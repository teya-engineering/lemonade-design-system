// @vitest-environment node
/// <reference types="vite/client" />
import kitCss from '../html-kit/kit.css?raw'
import tokensCss from '../styles/tokens.css?raw'
import typographyCss from '../styles/typography.css?raw'
import componentsCss from '../styles/components.css?raw'
import { kitLlmsTxt, kitScript, kitStylesheet, themedHues } from '../html-kit/build.mjs'
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

  // A radius without the corner scale, or without a corner-shape, draws a round corner beside smoothed ones.
  it('smooths every corner but a full one', () => {
    const blocks = [...withoutComments(kitCss).matchAll(/([^{}]+)\{([^{}]*)\}/g)]
    const unsmoothed = blocks.flatMap(([, selector, body]) => {
      const radius = body.match(/border-radius:\s*([^;]+);/)?.[1].trim()
      if (!radius || radius === '0') return []
      const shape = body.match(/corner-shape:\s*([^;]+);/)?.[1].trim()
      const full = /radius-full|^50%$/.test(radius)
      const ok = radius === 'inherit' ? shape === 'inherit'
        : full ? shape === 'round'
        : /^var\(--lmnd-kit-corner-shape(?:, round)?\)$/.test(shape ?? '') && !/var\(--lmnd-(?:kit-symbol-)?radius[\w-]*\)(?! \* var\(--lmnd-kit-corner-scale\))/.test(radius)
      return ok ? [] : [selector.trim()]
    })
    expect(unsmoothed).toEqual([])
  })

  // LDS-TOAST-DURATION: a default set by a more specific selector than the durations overrides them all.
  it('lets a toast duration override the default', () => {
    const setters = [...withoutComments(kitCss).matchAll(/([^{}]+)\{[^}]*--lmnd-kit-toast-time:/g)].map((m) => m[1].trim())
    expect(setters).toEqual(['.lmnd-kit-toast', '.lmnd-kit-toast--medium', '.lmnd-kit-toast--long'])
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

describe('themed hues', () => {
  it('gives every hue a rule that reads only declared tokens', async () => {
    const hues = await themedHues()
    expect(hues.length).toBeGreaterThan(10)
    const css = await kitStylesheet()
    for (const hue of hues) {
      const rule = css.match(new RegExp(`\\[data-hue="${hue}"\\]\\{([^}]*)\\}`))?.[1]
      expect(rule).toBeDefined()
      const read = [...rule!.matchAll(/var\((--lmnd-[\w-]+)\)/g)].map((m) => m[1])
      expect(read.filter((name) => !declared.has(name))).toEqual([])
    }
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

  it('names only themed hues that exist', async () => {
    const hues = new Set(await themedHues())
    const named = [...html.matchAll(/data-hue="([^"]+)"/g)].map((m) => m[1])
    expect(named.filter((hue) => !hues.has(hue))).toEqual([])
  })

  it('names only icons, flags and logos that exist', async () => {
    expect(inlinePage(html, await kitStylesheet())).not.toContain('lemonade-kit.css"')
  })

  // LHK-SCRIPT: the kit's script is the only one linked; a page's own script is inline. The gallery draws its own demos, so it is exempt.
  it.skipIf(path.endsWith('gallery.html'))('links the kit script and no other', () => {
    const linked = [...html.matchAll(/<script\b[^>]*\bsrc=[^>]*>/g)].map((m) => m[0])
    expect(linked).toEqual([expect.stringMatching(/src="[^"]*lemonade-kit\.js"/)])
  })

  it('inlines the kit script in its standalone copy', async () => {
    const standalone = inlinePage(html, await kitStylesheet(), await kitScript())
    expect(standalone).not.toContain('lemonade-kit.js"')
    expect(standalone).toContain(await kitScript())
  })
})

describe('the guidelines', () => {
  const raw = (files: Record<string, string>) =>
    new Map(Object.entries(files).map(([path, text]) => [path.split('/').pop() as string, text]))
  const kit = raw(import.meta.glob('../html-kit/guidelines/*.md', { query: '?raw', import: 'default', eager: true }))
  const lemonade = raw(import.meta.glob('../../guidelines/*.md', { query: '?raw', import: 'default', eager: true }))
  const screens = raw(import.meta.glob('../html-kit/screens/*.md', { query: '?raw', import: 'default', eager: true }))
  const skill = Object.values(import.meta.glob('../html-kit/SKILL.md', { query: '?raw', import: 'default', eager: true }))
  const index = kit.get('index.md') as string
  kit.delete('index.md')
  lemonade.delete('index.md')
  const defined = (text: string) => [...text.matchAll(/^#+ `((?:LDS|APP|LHK)-[A-Z0-9-]+)`/gm)].map((m) => m[1])
  const rules = new Map([...lemonade, ...kit])

  it('leaves every LDS- rule to Lemonade', () => {
    expect([...kit.values()].flatMap(defined).filter((id) => id.startsWith('LDS-'))).toEqual([])
    expect([...kit.keys()].filter((name) => lemonade.has(name))).toEqual([])
  })

  it('lists every rule file in the index, with the rules it holds', () => {
    const rows = new Map([...index.matchAll(/^\| \[([\w-]+\.md)\]\(\1\) \| (.*) \|$/gm)].map((m) => [m[1], m[2]]))
    for (const [name, text] of rules) {
      expect(rows.get(name), name).toBe(defined(text).map((id) => `\`${id}\``).join(', '))
    }
    expect([...rows.keys()].sort()).toEqual([...rules.keys()].sort())
  })

  it('names only rules that exist', () => {
    const leftToTheApp = ['APP-TOPBAR-COMPONENT', 'APP-SHEET-OVER-SHEET']
    const ids = new Set([...leftToTheApp, ...[...rules.values()].flatMap(defined)])
    const sources = [...Object.values(pages), index, ...rules.values(), ...screens.values(), ...skill] as string[]
    const named = sources.flatMap((text) => [...text.matchAll(/\b(?:LDS|APP|LHK)-[A-Z][A-Z0-9]*(?:-[A-Z0-9]+)*(?![\w-])/g)].map((m) => m[0]))
    expect([...new Set(named)].filter((id) => !ids.has(id))).toEqual([])
  })
})
