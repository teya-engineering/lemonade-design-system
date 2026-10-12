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

/** Modifiers are written relative to the block above them, which today is always Button. */
const shorthands = [...new Set([...docs.matchAll(/`(--[a-z0-9-]+)`/g)].map((match) => match[1]))]

describe('the class names the docs tell AI tools to write', () => {
  it('finds classes to check', () => {
    expect(documented.length).toBeGreaterThan(10)
    expect(shorthands.length).toBeGreaterThan(0)
  })

  // A class name ends where a CSS identifier can no longer continue: another class, a
  // pseudo, a combinator, a comma or the brace. Substring matching would let `.lmnd-icon`
  // be satisfied by `.lmnd-icon--medium`, so deleting the base rule would still pass.
  const hasRule = (className: string) => new RegExp(`\\.${className}(?![\\w-])`).test(published)

  it.each(documented)('%s has a rule in the published CSS', (className) => {
    expect(hasRule(className)).toBe(true)
  })

  // Every shorthand in the file documents Button, so it is the block they must resolve
  // against: accepting any block would let Button's `--small` pass on Icon's rule.
  it.each(shorthands)('%s resolves against the block that documents it', (shorthand) => {
    expect(hasRule(`lmnd-button${shorthand}`)).toBe(true)
  })
})
