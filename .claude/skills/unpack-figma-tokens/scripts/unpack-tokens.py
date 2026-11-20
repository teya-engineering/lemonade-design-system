#!/usr/bin/env -S python3 -I
import json
import re
import subprocess
import sys
import zipfile
from pathlib import Path

COLLECTIONS = {
    "Border Width": {"Default": "border-width.tokens.json"},
    "Opacity": {"Default": "opacity.tokens.json"},
    "Radius": {"Default": "radius.tokens.json"},
    ".Shadow": {"Default": "shadow.tokens.json"},
    "Sizing": {"Default": "size.tokens.json"},
    "Spacing": {"Default": "spacing.tokens.json"},
    "Typography": {"Default": "typography.tokens.json"},
    "Theme": {
        "Light": "theme-colors.light.tokens.json",
        "Dark": "theme-colors.dark.tokens.json",
    },
    "Primitive Colors": {"Default": "primitive-colors.tokens.json"},
}
EXPECTED = {name for modes in COLLECTIONS.values() for name in modes.values()}
MODE_SUFFIX = ".tokens.json"

REPO = Path(__file__).resolve().parents[4]
TOKENS = REPO / "tokens"


def fail(message):
    sys.exit(f"error: {message}\nNothing was written to tokens/.")


def is_junk(entry):
    name = entry.rsplit("/", 1)[-1]
    return entry.startswith("__MACOSX/") or name == ".DS_Store" or name.startswith("._")


def collection_of(zip_path):
    # Browsers save a second download of Theme.zip as "Theme (1).zip".
    name = re.sub(r" \(\d+\)$", "", zip_path.stem)
    if name not in COLLECTIONS:
        fail(f"{zip_path.name}: unknown collection '{name}'. Known: {', '.join(COLLECTIONS)}. "
             "A new or renamed collection needs its entry in COLLECTIONS in this script.")
    return name


def read_collection(zip_path, collection):
    modes = COLLECTIONS[collection]
    found = {}
    with zipfile.ZipFile(zip_path) as archive:
        for entry in archive.namelist():
            if entry.endswith("/") or is_junk(entry):
                continue
            if "/" in entry or not entry.endswith(MODE_SUFFIX):
                fail(f"{zip_path.name}: unexpected entry '{entry}'. Expected only "
                     f"{', '.join(m + MODE_SUFFIX for m in modes)} at the top level.")
            mode = entry[: -len(MODE_SUFFIX)]
            if mode not in modes:
                fail(f"{zip_path.name}: unknown mode '{mode}' in {collection}. Known: {', '.join(modes)}. "
                     "A renamed mode needs its entry in COLLECTIONS in this script.")
            data = archive.read(entry)
            try:
                json.loads(data)
            except ValueError as error:
                fail(f"{zip_path.name}/{entry} is not valid JSON: {error}")
            found[modes[mode]] = data if data.endswith(b"\n") else data + b"\n"
    missing = [m for m, target in modes.items() if target not in found]
    if missing:
        fail(f"{zip_path.name}: missing mode(s) {', '.join(missing)}.")
    return found


def tokens_dir_extras(inputs):
    allowed = EXPECTED | {".DS_Store"} | {p.name for p in inputs if p.parent == TOKENS}
    return sorted(p.name for p in TOKENS.iterdir() if p.name not in allowed)


def walk(node, path=""):
    if isinstance(node, dict) and "$value" in node:
        yield path, node
    elif isinstance(node, dict):
        for key, child in node.items():
            yield from walk(child, f"{path}/{key}" if path else key)


def stale_aliases():
    primitives = dict(walk(json.loads((TOKENS / "primitive-colors.tokens.json").read_text())))
    for theme in ("theme-colors.light.tokens.json", "theme-colors.dark.tokens.json"):
        for path, token in walk(json.loads((TOKENS / theme).read_text())):
            alias = token.get("$extensions", {}).get("com.figma.aliasData", {})
            if alias.get("targetVariableSetName") != "Primitive Colors":
                continue
            target = primitives.get(alias["targetVariableName"])
            if target is None:
                yield f"{theme} {path} -> {alias['targetVariableName']} (no such primitive)"
                continue
            stored, actual = token["$value"], target["$value"]
            if (stored["hex"], stored.get("alpha")) != (actual["hex"], actual.get("alpha")):
                yield f"{theme} {path}: {stored['hex']} but {alias['targetVariableName']} is {actual['hex']}"


def main():
    if len(sys.argv) < 2:
        sys.exit("usage: unpack-tokens.py <Collection>.zip [<Collection>.zip ...]")
    inputs = [Path(arg).resolve() for arg in sys.argv[1:]]

    files = {}
    seen = set()
    for zip_path in inputs:
        if not zip_path.is_file():
            fail(f"{zip_path} does not exist.")
        collection = collection_of(zip_path)
        if collection in seen:
            fail(f"{collection} was given more than once.")
        seen.add(collection)
        files.update(read_collection(zip_path, collection))

    extras = tokens_dir_extras(inputs)
    if extras:
        fail(f"tokens/ holds files that are not token files: {', '.join(extras)}. Move them out first.")

    for name, data in files.items():
        (TOKENS / name).write_bytes(data)
    (TOKENS / ".DS_Store").unlink(missing_ok=True)
    for zip_path in inputs:
        if zip_path.parent == TOKENS:
            zip_path.unlink()

    print(f"Unpacked {', '.join(sorted(seen))}.")
    if "Primitive Colors" in seen and "Theme" not in seen:
        print("warning: Primitive Colors unpacked without Theme. Theme tokens store the resolved hex "
              "of each primitive they alias; export and unpack Theme too.")
    stale = list(stale_aliases())
    if stale:
        print(f"warning: {len(stale)} theme colour(s) store a hex that no longer matches their primitive. "
              "Export Theme and Primitive Colors together and unpack both in one run:")
        for line in stale[:20]:
            print(f"  {line}")
        if len(stale) > 20:
            print(f"  ... and {len(stale) - 20} more")

    status = subprocess.run(["git", "status", "--short", "tokens/"], cwd=REPO,
                            capture_output=True, text=True, check=True).stdout
    print("\nChanged in tokens/:\n" + (status or "  (nothing)\n"))
    if status:
        print("Next: .claude/skills/generate-tokens/scripts/run-converters.sh --changed")


if __name__ == "__main__":
    main()
