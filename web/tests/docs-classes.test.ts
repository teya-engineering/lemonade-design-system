/// <reference types="vite/client" />
import componentsCss from '../styles/components.css?raw'
import typographyCss from '../styles/typography.css?raw'
import docs from '../llms-components.md?raw'
import { describe, expect, it } from 'vitest'

// llms-components.md is hand-written, and llms.txt hands it to AI tools as the way to build
// Lemonade markup without loading the JavaScript. A class name that drifts from the
// stylesheet produces a page that renders unstyled, and every other test still passes —
// they check the component against the CSS, never the documentation against either.
const published = `${componentsCss}\n${typographyCss}`

const documented = [...new Set(docs.match(/\blmnd-[a-z0-9-]+/g) ?? [])]

/** Modifiers are written relative to the block above them, e.g. `--subtle` under Button. */
const shorthands = [...new Set([...docs.matchAll(/`(--[a-z0-9-]+)`/g)].map((match) => match[1]))]
const BLOCKS = ['button', 'icon', 'spinner', 'text']

describe('the class names the docs tell AI tools to write', () => {
  it('finds classes to check', () => {
    expect(documented.length).toBeGreaterThan(10)
    expect(shorthands.length).toBeGreaterThan(0)
  })

  it.each(documented)('%s has a rule in the published CSS', (className) => {
    expect(published).toContain(`.${className}`)
  })

  it.each(shorthands)('%s resolves against one of the documented blocks', (shorthand) => {
    expect(BLOCKS.some((block) => published.includes(`.lmnd-${block}${shorthand}`))).toBe(true)
  })
})
