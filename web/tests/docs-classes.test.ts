/// <reference types="vite/client" />
import componentsCss from '../styles/components.css?raw'
import tokensCss from '../styles/tokens.css?raw'
import typographyCss from '../styles/typography.css?raw'
import docs from '../llms-components.md?raw'
import { describe, expect, it } from 'vitest'

// llms-components.md is hand-written, and llms.txt hands it to AI tools as the way to build
// Lemonade markup without loading the JavaScript. A class name that drifts from the
// stylesheet produces a page that renders unstyled, and every other test still passes —
// they check the component against the CSS, never the documentation against either.
const published = `${componentsCss}\n${typographyCss}`

// Not preceded by a dash, so the tail of a custom property is not read as a class name:
// `--lmnd-symbol-container-bg` is one name, not a `lmnd-symbol-container-bg` class.
const documented = [...new Set(docs.match(/(?<![-\w])lmnd-[a-z0-9-]+/g) ?? [])]

/**
 * Custom properties the docs tell a consumer to set or to read. A themed container is styled
 * through these rather than through a class, so a name that drifts leaves the surface
 * transparent — and the class checks above would all still pass.
 */
const properties = [...new Set(docs.match(/--lmnd-[a-z0-9-]+/g) ?? [])]

/** `IconButton` documents the `lmnd-icon-button` block. */
const blockOf = (heading: string) =>
  `lmnd-${heading.replace(/[A-Z]/g, (char, at) => (at === 0 ? char.toLowerCase() : `-${char.toLowerCase()}`))}`

/**
 * Modifiers are written relative to the block of the section they sit in, so each one is
 * paired with that block rather than checked against the file as a whole.
 */
const shorthands = docs
  .split(/^### /m)
  .slice(1)
  .flatMap((section) => {
    const block = blockOf(section.slice(0, section.indexOf('\n')).trim())
    const found = new Set(
      [...section.matchAll(/`(--[a-z0-9-]+)`/g)]
        .map((match) => match[1])
        // `--lmnd-…` is a custom property, checked as one below, not a modifier of this block.
        .filter((shorthand) => !shorthand.startsWith('--lmnd-')),
    )
    return [...found].map((shorthand) => [block, shorthand] as [string, string])
  })

describe('the class names the docs tell AI tools to write', () => {
  it('finds classes to check', () => {
    expect(documented.length).toBeGreaterThan(10)
    expect(shorthands.length).toBeGreaterThan(0)
    expect(properties.length).toBeGreaterThan(0)
    // More than one block documents modifiers this way. A split that stopped matching the
    // headings would collapse to one block and still find shorthands to check.
    expect(new Set(shorthands.map(([block]) => block)).size).toBeGreaterThan(1)
  })

  // A class name ends where a CSS identifier can no longer continue: another class, a
  // pseudo, a combinator, a comma or the brace. Substring matching would let `.lmnd-icon`
  // be satisfied by `.lmnd-icon--medium`, so deleting the base rule would still pass.
  const hasRule = (className: string) => new RegExp(`\\.${className}(?![\\w-])`).test(published)

  it.each(documented)('%s has a rule in the published CSS', (className) => {
    expect(hasRule(className)).toBe(true)
  })

  // Accepting any block would let IconButton's `--ghost` pass on Button's rule, and a
  // shorthand documented under the wrong heading is exactly the drift this file catches.
  it.each(shorthands)('%s%s resolves against the block that documents it', (block, shorthand) => {
    expect(hasRule(`${block}${shorthand}`)).toBe(true)
  })

  // A property the component sets, one it reads, and one the tokens define all appear in the
  // CSS the package ships. A documented name that appears nowhere is one a consumer would
  // set to no effect.
  it.each(properties)('%s is a property the published CSS knows', (property) => {
    expect(`${published}\n${tokensCss}`).toContain(property)
  })
})
