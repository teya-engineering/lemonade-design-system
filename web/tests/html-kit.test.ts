// @vitest-environment node
/// <reference types="vite/client" />
import kitCss from '../html-kit/kit.css?raw'
import tokensCss from '../styles/tokens.css?raw'
import typographyCss from '../styles/typography.css?raw'
import componentsCss from '../styles/components.css?raw'
import { kitLlmsTxt, kitScript, kitStylesheet } from '../html-kit/build.mjs'
import { inlinePage } from '../html-kit/share/build.mjs'
import { describe, expect, it } from 'vitest'

const pages = import.meta.glob(['../html-kit/gallery.html', '../html-kit/examples/*.html', '../html-kit/screens/*.html'], {
  query: '?raw',
  import: 'default',
  eager: true,
}) as Record<string, string>

const declared = new Set([...tokensCss.matchAll(/(--lmnd-[\w-]+):/g)].map((m) => m[1]))
const withoutComments = (css: string) => css.replace(/\/\*[\s\S]*?\*\//g, '')
const rules = withoutComments([kitCss, typographyCss, componentsCss].join('\n'))

describe('kit.css', () => {
  // A var() naming a token that doesn't exist drops the whole declaration without a word.
  it('reads only tokens that tokens.css declares', () => {
    // The kit's own variables (--lmnd-kit-*) are declared in kit.css, not tokens.css.
    const used = new Set([...kitCss.matchAll(/var\((--lmnd-(?!kit-)[\w-]+)/g)].map((m) => m[1]))
    expect([...used].filter((name) => !declared.has(name))).toEqual([])
  })

  it('carries no colour of its own', () => {
    const literal = withoutComments(kitCss).match(/#[0-9a-f]{3,8}\b|\brgba?\(|\bhsla?\(/gi)
    expect(literal).toBeNull()
  })
})

describe('the packaged stylesheet', () => {
  it('puts each asset on its own line, which the page builder prunes by', async () => {
    const lines = (await kitStylesheet()).split('\n')
    const assetLines = lines.filter((line) => /\[data-(icon|flag|logo|badge)="/.test(line))
    expect(assetLines.length).toBeGreaterThan(500)
    assetLines.forEach((line) => expect(line.match(/\[data-(icon|flag|logo|badge)="/g)).toHaveLength(1))
  })
})

describe("the kit's llms.txt", () => {
  // The package's component markup serves icons as files, which renders nothing in the kit.
  it('keeps the token sections and drops the package-only ones', async () => {
    const llms = await kitLlmsTxt()
    expect(llms).toContain('## Colours')
    expect(llms).toContain('## Text styles')
    expect(llms).not.toContain('## Components')
    expect(llms).not.toContain('## JavaScript')
    expect(llms).not.toContain('/assets/icons')
  })
})

describe.each(Object.keys(pages))('%s', (path) => {
  const html = pages[path]

  it('uses only classes that have a rule', () => {
    const classes = new Set(
      [...html.matchAll(/class="([^"]+)"/g)].flatMap((m) => m[1].split(/\s+/)).filter((c) => /^lmnd-/.test(c)),
    )
    const missing = [...classes].filter((name) => !new RegExp(`\\.${name}(?![\\w-])`).test(rules))
    expect(missing).toEqual([])
  })

  // LDS-TOP-BAR-DESCRIPTION: a large title's description goes inside the title block, not after it.
  it('keeps a large title and its description together', () => {
    expect(html).not.toMatch(/class="lmnd-kit-large-title"[^>]*>[\s\S]*?<\/div>\s*<p\b[^>]*class="[^"]*lmnd-kit-intro/)
  })

  // LHK-SCRIPT: a reset that names no element resets nothing, silently.
  it('resets only elements that exist', () => {
    const ids = new Set([...html.matchAll(/\sid="([^"]+)"/g)].map((m) => m[1]))
    const missing = [...html.matchAll(/data-lmnd-kit-reset="([^"]+)"/g)].map((m) => m[1]).filter((id) => !ids.has(id))
    expect(missing).toEqual([])
  })

  // LHK-SELECT-OPENS: a select field's box is a link to what it opens, or holds a native select.
  it('opens every select field with a link or a native select', () => {
    const boxes = [...html.matchAll(/class="lmnd-kit-field lmnd-kit-select[^"]*"[^>]*>[\s\S]*?<(\w+)[^>]*class="lmnd-kit-field__box"[^>]*>(?:\s*<(\w+))?/g)]
    expect(boxes.filter(([, box, child]) => box !== 'a' && !(box === 'span' && child === 'select'))).toEqual([])
    const ids = new Set([...html.matchAll(/\sid="([^"]+)"/g)].map((m) => m[1]))
    const targets = [...html.matchAll(/<a href="#([^"]+)" class="lmnd-kit-field__box"/g)].map((m) => m[1])
    expect(targets.filter((id) => !ids.has(id))).toEqual([])
  })

  // APP-SEGMENTED-SIZE: the segmented control that switches a whole screen is Large.
  it('keeps the segmented control that switches a screen Large', () => {
    expect(html).not.toMatch(/<main class="lmnd-kit-content">\s*<div class="lmnd-kit-segmented lmnd-kit-segmented--/)
  })

  // LDS-TOP-BAR-CRITICAL: a destructive icon button takes the critical variant, not a tinted glyph.
  it('marks a destructive icon button with its critical variant', () => {
    expect(html).not.toMatch(/class="lmnd-kit-icon-button\b[^"]*"[^>]*>\s*<span class="[^"]*lmnd-kit-critical/)
  })

  it('names only icons, flags and logos that exist', async () => {
    expect(inlinePage(html, await kitStylesheet())).not.toContain('lemonade-kit.css"')
  })

  // LHK-SCRIPT: the kit's script and nothing else. The gallery draws its own demos, so it is exempt.
  it.skipIf(path.endsWith('gallery.html'))('links the kit script and no other', () => {
    const scripts = [...html.matchAll(/<script\b[^>]*>/g)].map((m) => m[0])
    expect(scripts).toEqual([expect.stringMatching(/src="[^"]*lemonade-kit\.js"/)])
  })

  it('inlines the kit script in its standalone copy', async () => {
    const standalone = inlinePage(html, await kitStylesheet(), await kitScript())
    expect(standalone).not.toContain('lemonade-kit.js"')
    expect(standalone).toContain(await kitScript())
  })
})
