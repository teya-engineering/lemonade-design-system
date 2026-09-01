#!/usr/bin/env python3
"""Diff and export the Lemonade icon set from Figma into svg/icons.

Usage (from the repo root):
    .claude/skills/export-icons/scripts/figma-icons.py diff
    .claude/skills/export-icons/scripts/figma-icons.py export [name ...]

`diff` reports which icons exist in Figma but not in svg/icons (and the reverse).
`export` with no names writes every icon missing from svg/icons; with names it
re-exports exactly those, which is how updated artwork is pulled in.

Needs a Figma personal access token with the `file_content:read` scope, in
FIGMA_TOKEN or FIGMA_CODE_CONNECT_TOKEN.

The SVG is written exactly as Figma renders it, byte for byte. That is not
laziness: every file already in svg/icons is an unmodified Figma render, so any
normalisation here would show up as a diff on the next re-export of an icon
nobody touched.
"""

import argparse
import json
import os
import sys
import urllib.error
import urllib.request
from pathlib import Path

API = "https://api.figma.com/v1"

# The "🎲 Lemonade DS - Icons" file, Icons page. The components live in two
# sibling frames under this node (outline icons and the smaller solid set), so
# the whole subtree is walked rather than one frame's children.
DEFAULT_FILE_KEY = "f7zokCdnayXejxc2y7r1Qt"
DEFAULT_NODE_ID = "32:185"
DEFAULT_DIR = "svg/icons"

# Depth that reaches the COMPONENT nodes (page > Main > Stack > frame > card > component).
TREE_DEPTH = 6

# Ids per /v1/images call. Figma renders them in one batch; a few hundred at
# once is where the endpoint starts timing out.
BATCH = 50


def die(message):
    sys.exit(f"error: {message}")


def token():
    for name in ("FIGMA_TOKEN", "FIGMA_CODE_CONNECT_TOKEN"):
        value = os.environ.get(name)
        if value:
            return value
    die(
        "no Figma token found. Export FIGMA_TOKEN — a personal access token from\n"
        "       Figma > Settings > Security > Personal access tokens, with the\n"
        "       'file_content:read' scope. That one scope covers both calls made here."
    )


def api_get(url, tok):
    request = urllib.request.Request(url, headers={"X-Figma-Token": tok})
    try:
        with urllib.request.urlopen(request) as response:
            return json.load(response)
    except urllib.error.HTTPError as error:
        body = error.read().decode("utf-8", "replace")[:500]
        die(f"Figma API returned {error.code} for {url.split('?')[0]}\n       {body}")
    except urllib.error.URLError as error:
        die(f"could not reach the Figma API: {error.reason}")


def figma_icons(file_key, node_id, tok):
    """Every COMPONENT under `node_id`, as {name: node id}.

    Walking the tree rather than calling /v1/files/:key/components, which is the
    tidier endpoint but needs the `library_content:read` scope; this reads the
    file like every other call here, so `file_content:read` is enough.
    """
    url = f"{API}/files/{file_key}/nodes?ids={node_id}&depth={TREE_DEPTH}"
    payload = api_get(url, tok)
    node = payload.get("nodes", {}).get(node_id.replace("-", ":"))
    if not node:
        die(f"node {node_id} not found in file {file_key}")

    found = {}
    duplicates = []

    def walk(current):
        if current.get("type") == "COMPONENT":
            name = current["name"]
            if name in found:
                duplicates.append(name)
            found[name] = current["id"]
        for child in current.get("children", ()):
            walk(child)

    walk(node["document"])

    if duplicates:
        die(
            "two Figma components share a name, so one would overwrite the other: "
            + ", ".join(sorted(set(duplicates)))
        )
    if not found:
        die(f"no components found under node {node_id} — is that the Icons page?")
    return found


def repo_icons(icons_dir):
    return {path.stem for path in icons_dir.glob("*.svg")}


def render_urls(file_key, ids, tok):
    """Ask Figma to render `ids` as SVG, returning {node id: url}."""
    urls = {}
    for start in range(0, len(ids), BATCH):
        batch = ids[start : start + BATCH]
        url = f"{API}/images/{file_key}?ids={','.join(batch)}&format=svg"
        payload = api_get(url, tok)
        if payload.get("err"):
            die(f"Figma could not render the icons: {payload['err']}")
        urls.update(payload.get("images", {}))
    return urls


def download(url):
    with urllib.request.urlopen(url) as response:
        return response.read()


def cmd_diff(args, tok):
    icons_dir = Path(args.dir)
    figma = figma_icons(args.file_key, args.node, tok)
    repo = repo_icons(icons_dir)

    new = sorted(set(figma) - repo)
    stale = sorted(repo - set(figma))

    print(f"Figma: {len(figma)} components    {icons_dir}: {len(repo)} files")
    print()
    if new:
        print(f"In Figma, not in the repo ({len(new)}):")
        for name in new:
            print(f"  + {name}")
    else:
        print("In Figma, not in the repo: none")
    print()
    if stale:
        # Not automatically an error: an icon may have been removed from the
        # Figma page while its enum entry has to stay for binary compatibility.
        print(f"In the repo, not in Figma ({len(stale)}):")
        for name in stale:
            print(f"  - {name}")
    else:
        print("In the repo, not in Figma: none")


def cmd_export(args, tok):
    icons_dir = Path(args.dir)
    if not icons_dir.is_dir():
        die(f"{icons_dir} does not exist — run this from the repo root")

    figma = figma_icons(args.file_key, args.node, tok)

    if args.names:
        unknown = [name for name in args.names if name not in figma]
        if unknown:
            die("not a component in the Figma icons page: " + ", ".join(unknown))
        wanted = list(dict.fromkeys(args.names))
    else:
        wanted = sorted(set(figma) - repo_icons(icons_dir))
        if not wanted:
            print("Nothing to export — every Figma icon is already in the repo.")
            return

    print(f"Exporting {len(wanted)} icon(s): {', '.join(wanted)}")
    urls = render_urls(args.file_key, [figma[name] for name in wanted], tok)

    for name in wanted:
        node_id = figma[name]
        url = urls.get(node_id)
        if not url:
            die(f"Figma returned no render for '{name}' ({node_id})")
        svg = download(url)
        if not svg.lstrip().startswith(b"<svg"):
            die(f"the render for '{name}' is not an SVG")
        # Every committed icon is 24x24. A different box means the component was
        # drawn on the wrong frame, and it would ship at the wrong scale.
        if b'viewBox="0 0 24 24"' not in svg:
            print(f"  ! {name}: not a 24x24 viewBox — check the component in Figma")
        target = icons_dir / f"{name}.svg"
        verb = "updated" if target.exists() else "added"
        target.write_bytes(svg)
        print(f"  {verb} {target}")

    print()
    print("Next: .claude/skills/export-icons/scripts/generate-assets.sh")


def main():
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--file-key", default=DEFAULT_FILE_KEY, help="Figma file key")
    parser.add_argument("--node", default=DEFAULT_NODE_ID, help="node id of the Icons page")
    parser.add_argument("--dir", default=DEFAULT_DIR, help="destination directory")
    sub = parser.add_subparsers(dest="command", required=True)
    sub.add_parser("diff", help="compare Figma against the repo")
    export = sub.add_parser("export", help="write icons into the repo")
    export.add_argument("names", nargs="*", help="icon names; default is everything missing")

    args = parser.parse_args()
    tok = token()
    if args.command == "diff":
        cmd_diff(args, tok)
    else:
        cmd_export(args, tok)


if __name__ == "__main__":
    main()
