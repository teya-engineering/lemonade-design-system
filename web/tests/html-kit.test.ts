/// <reference types="vite/client" />
import kitCss from '../html-kit/kit.css?raw'
import tokensCss from '../styles/tokens.css?raw'
import typographyCss from '../styles/typography.css?raw'
import componentsCss from '../styles/components.css?raw'
import { kitLlmsTxt, kitStylesheet } from '../html-kit/build.mjs'
import { inlinePage } from '../html-kit/share/build.mjs'
import { describe, expect, it } from 'vitest'

const pages = import.meta.glob(['../html-kit/gallery.html', '../html-kit/examples/*.html'], {
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
    const assetLines = lines.filter((line) => /\[data-(icon|flag|logo)="/.test(line))
    expect(assetLines.length).toBeGreaterThan(500)
    assetLines.forEach((line) => expect(line.match(/\[data-(icon|flag|logo)="/g)).toHaveLength(1))
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

  it('names only icons, flags and logos that exist', async () => {
    expect(inlinePage(html, await kitStylesheet())).not.toContain('lemonade-kit.css"')
  })
})
