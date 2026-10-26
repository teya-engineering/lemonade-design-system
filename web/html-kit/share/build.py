"""Turns a kit page into one self-contained HTML file: the linked lemonade-kit.css goes inline, keeping
only the icons, flags, logos and badges the page names. The same as build.mjs, for machines without Node.

    python3 build.py <page.html> [more.html ...]

Each page is written next to itself as <name>.standalone.html.
"""
import re
import sys
from pathlib import Path

LINK = re.compile(r'<link\b[^>]*\bhref="([^"]*lemonade-kit\.css)"[^>]*>')
SCRIPT = re.compile(r'<script\b[^>]*\bsrc="([^"]*lemonade-kit\.js)"[^>]*></script>')
ASSET = re.compile(r'data-(icon|flag|logo|badge)="([a-zA-Z0-9-]+)"')
# lemonade-kit.css carries one asset per line, so a page keeps exactly the lines it names.
ASSET_LINE = re.compile(r'\[data-(icon|flag|logo|badge)="([a-zA-Z0-9-]+)"\]')


def inline_page(html: str, css: str, js=None) -> str:
    if not LINK.search(html):
        raise ValueError('link lemonade-kit.css with <link rel="stylesheet" href="lemonade-kit.css">')
    # The gallery lists every icon from script, so it keeps them all.
    keep_all = 'data-kit-all-assets' in html
    used = {f'{kind}:{name}' for kind, name in ASSET.findall(html)}
    known = set()
    kept = []
    for line in css.split('\n'):
        asset = ASSET_LINE.search(line)
        if asset:
            key = f'{asset.group(1)}:{asset.group(2)}'
            known.add(key)
            if not (keep_all or key in used):
                continue
        kept.append(line)
    unknown = sorted(used - known)
    if unknown:
        raise ValueError('\n'.join(f'unknown {a.replace(":", " " + chr(34))}"' for a in unknown))
    styled = LINK.sub(lambda _: '<style>\n' + '\n'.join(kept) + '\n</style>', html, count=1)
    # The kit's script, when the page links it and it's given, goes inline too. Without it the link stays.
    if js is None:
        return styled
    return SCRIPT.sub(lambda _: '<script>\n' + js.replace('</script', '<\\/script') + '\n</script>', styled, count=1)


def main(pages):
    if not pages:
        sys.exit('usage: python3 build.py <page.html> [more.html ...]')
    failed = False
    for page in pages:
        source = Path(page).resolve()
        try:
            html = source.read_text(encoding='utf-8')
            link = LINK.search(html)
            css = (source.parent / (link.group(1) if link else 'lemonade-kit.css')).read_text(encoding='utf-8')
            script = SCRIPT.search(html)
            js = (source.parent / script.group(1)).read_text(encoding='utf-8') if script else None
            target = source.with_name(source.stem + '.standalone.html')
            target.write_text(inline_page(html, css, js), encoding='utf-8')
            print(f'✓ {target}')
        except (OSError, ValueError) as error:
            failed = True
            print(f'✗ {page}\n  ' + str(error).replace('\n', '\n  '), file=sys.stderr)
    if failed:
        sys.exit(1)


if __name__ == '__main__':
    main(sys.argv[1:])
