#!/usr/bin/env python3
"""Fail if the four token loaders' shared DTCG helpers have diverged.

`scripts/{kmp,swiftui,flutter,web}-resource-file-loading.main.kts` each carry a copy
of the same DTCG parsing block. The duplication is deliberate — every converter
`@file:Import`s exactly one platform loader, and a shared module would rewire the
import graph of 20+ scripts — but nothing otherwise keeps the copies in step.

A silent divergence is the failure this guards: the same token would produce
different names, values or ordering depending on the platform, and each
platform's own verification would still pass.

Usage (from the repo root):
    python3 .claude/skills/generate-tokens/scripts/check-loader-parity.py

Exits 0 when every shared function is identical, 1 otherwise.
"""
import pathlib
import re
import sys

LOADERS = {
    "kmp": "scripts/kmp-resource-file-loading.main.kts",
    "swiftui": "scripts/swiftui-resource-file-loading.main.kts",
    "flutter": "scripts/flutter-resource-file-loading.main.kts",
    "web": "scripts/web-resource-file-loading.main.kts",
}

BLOCK_START = "Figma native (DTCG) support"

# Functions deliberately present in only some loaders, with the reason.
EXPECTED_PARTIAL = {
    "requireModes": ({"kmp", "swiftui", "web"}, "Flutter has no mode-based loading"),
    "readFileResourceFileByModeRaw": (
        {"swiftui", "web"},
        "the SwiftUI asset generator and the web converter need raw token names",
    ),
    "readFileResourceFileRaw": (
        {"web"},
        "only web emits kebab-case names for every token type",
    ),
}

# SwiftUI uses its own sanitizer names for the same operation.
NORMALISE = [("sanitizedSwiftClassName", "sanitizedClassName"),
             ("sanitizedSwiftValueName", "sanitizedValueName")]

FUN = re.compile(r"^(?:private )?fun (?:<[^>]+> )?([A-Za-z][A-Za-z0-9_]*)\s*\(")
# A new top-level declaration ends the previous one. Needed because several of
# these helpers are expression-bodied (`fun isDtcgDocument(...) = ...`) and so
# have no braces to count.
BOUNDARY = re.compile(r"^(?:private )?(?:fun\b|val\b|const\b)|^/\*\*|^// ---")


def functions(path):
    """Top-level `fun` declarations in the file's DTCG section, keyed by name."""
    text = pathlib.Path(path).read_text()
    if BLOCK_START not in text:
        sys.exit(f"error: {path} has no '{BLOCK_START}' section")
    section = text[text.index(BLOCK_START):]
    for old, new_ in NORMALISE:
        section = section.replace(old, new_)

    lines = section.splitlines()
    starts = [i for i, l in enumerate(lines) if FUN.match(l)]
    out = {}
    for idx, start in enumerate(starts):
        name = FUN.match(lines[start]).group(1)
        limit = len(lines)
        for j in range(start + 1, len(lines)):
            if BOUNDARY.match(lines[j]):
                limit = j
                break
        out[name] = "\n".join(lines[start:limit]).rstrip()
    return out


def main():
    found = {name: functions(path) for name, path in LOADERS.items()}
    every = set.intersection(*(set(f) for f in found.values()))
    problems = []

    for fn in sorted(every):
        bodies = {loader: fns[fn] for loader, fns in found.items()}
        if len(set(bodies.values())) > 1:
            differing = [l for l in bodies if bodies[l] != bodies["kmp"]]
            problems.append(f"{fn}(): differs between kmp and {', '.join(sorted(differing))}")

    for fn in sorted(set().union(*(set(f) for f in found.values())) - every):
        present = {l for l, fns in found.items() if fn in fns}
        expected, reason = EXPECTED_PARTIAL.get(fn, (None, None))
        if expected == present:
            print(f"  ok    {fn}() in {', '.join(sorted(present))} only — {reason}")
        else:
            problems.append(
                f"{fn}(): present in {', '.join(sorted(present))} only, which is not a "
                f"recorded exception. Either port it to the others or add it to "
                f"EXPECTED_PARTIAL with the reason.")

    for fn in sorted(every):
        print(f"  ok    {fn}() identical across all four")

    if problems:
        print("\nFAIL: the loaders' DTCG blocks have diverged.\n", file=sys.stderr)
        for p in problems:
            print(f"  - {p}", file=sys.stderr)
        print("\nThe same token must produce identical names, values and ordering on every "
              "platform. Reconcile the copies before merging.", file=sys.stderr)
        return 1

    print(f"\nPASS: {len(every)} shared DTCG functions identical across all four loaders.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
