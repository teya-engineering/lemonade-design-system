/// <reference types="vite/client" />
import tokensCss from '../styles/tokens.css?raw'
import tokensJson from '../tokens.json'
import { describe, expect, it } from 'vitest'
import { tokens, tokenValues } from '../src/index'

type Leaf = string | number
type Tree = { readonly [key: string]: Leaf | Tree }

const declarations = (selector: string) => {
  const found = new Map<string, string>()
  for (const [, blockSelector, body] of tokensCss.matchAll(/^(.+?) \{\n([\s\S]*?)\n\}/gm)) {
    if (blockSelector !== selector) continue
    for (const [, name, value] of body.matchAll(/^\s+(--[\w-]+): (.+);$/gm)) found.set(name, value)
  }
  return found
}

const root = declarations(':root')

const kebab = (key: string) => key.replace(/[A-Z]/g, (letter) => `-${letter.toLowerCase()}`)

const flatten = (tree: Tree, path: string[] = []): [string[], Leaf][] =>
  Object.entries(tree).flatMap(([key, value]) =>
    typeof value === 'object' ? flatten(value, [...path, key]) : [[[...path, key], value] as [string[], Leaf]],
  )

const cssNameOf = (path: string[]) => {
  const group = path[0] === 'font' ? `font-${path[1]}` : kebab(path[0])
  return `--lmnd-${group}-${kebab(path[path.length - 1])}`
}

const tokenAt = (tree: Tree, path: string[]) =>
  path.reduce<Leaf | Tree>((node, key) => (node as Tree)[key], tree) as Leaf

const nonColour = (tree: Tree) => flatten(tree).filter(([path]) => path[0] !== 'color')

describe('tokens', () => {
  it('matches tokens.css for every non-colour token', () => {
    const entries = nonColour(tokens)
    expect(entries.length).toBeGreaterThan(100)
    entries.forEach(([path, value]) => expect([path.join('.'), String(value)]).toEqual([path.join('.'), root.get(cssNameOf(path))]))
  })

  it('carries style-ready values', () => {
    expect(tokens.spacing[400]).toBe('1rem')
    expect(tokens.spacing[0]).toBe('0')
    expect(tokens.radius.full).toBe('62.4375rem')
    expect(tokens.borderWidth.focusRing).toBe('2px')
    expect(tokens.opacity.disabled).toBe(0.5)
    expect(tokens.font.weight.semibold).toBe(600)
  })
})

describe('tokenValues', () => {
  it('gives plain px for every dimension', () => {
    nonColour(tokenValues).forEach(([path, value]) => {
      const style = tokenAt(tokens, path)
      const expected =
        style === '0' ? 0
          : typeof style === 'string' && style.endsWith('rem') ? parseFloat(style) * 16
            : typeof style === 'string' && style.endsWith('px') ? parseFloat(style)
              : style
      expect([path.join('.'), value]).toEqual([path.join('.'), expected])
    })
  })

  it('is what tokens.json carries', () => {
    expect(tokensJson).toEqual(tokenValues)
  })

  it('has no shadow', () => {
    expect('shadow' in tokenValues).toBe(false)
  })

  it('carries numbers for code', () => {
    expect(tokenValues.spacing[400]).toBe(16)
    expect(tokenValues.spacing[0]).toBe(0)
    expect(tokenValues.radius.full).toBe(999)
    expect(tokenValues.lineHeight[650]).toBe(26)
  })
})

const channels = (colour: string) => {
  const [r, g, b, a = '1'] = colour.match(/[\d.]+/g) ?? []
  return [r, g, b, a].map(Number)
}

describe('colours', () => {
  const dark = declarations('[data-lmnd-theme="dark"]')
  const colours = flatten(tokens.color)

  it('references a declared custom property for every colour', () => {
    expect(colours.length).toBeGreaterThan(300)
    colours.forEach(([path, value]) => {
      const name = `--lmnd-color-${path.map(kebab).join('-')}`
      expect(value).toBe(`var(${name})`)
      expect(root.has(name)).toBe(true)
    })
  })

  it('gives light and dark the same keys as tokens.color', () => {
    const keys = (tree: Tree) => flatten(tree).map(([path]) => path.join('.'))
    expect(keys(tokenValues.color.light)).toEqual(keys(tokens.color))
    expect(keys(tokenValues.color.dark)).toEqual(keys(tokens.color))
  })

  it('matches tokens.css per theme', () => {
    const check = (theme: Tree, css: Map<string, string>) =>
      flatten(theme).forEach(([path, value]) => {
        const name = `--lmnd-color-${path.map(kebab).join('-')}`
        expect([name, channels(String(value))]).toEqual([name, channels(css.get(name)!)])
      })
    check(tokenValues.color.light, root)
    check(tokenValues.color.dark, dark)
  })

  it('exposes theme-following references and per-theme values', () => {
    expect(tokens.color.content.primary).toBe('var(--lmnd-color-content-primary)')
    expect(tokenValues.color.light.content.primary).toBe('rgba(9, 8, 6, 0.925)')
    expect(tokenValues.color.dark.content.primary).toBe('rgba(255, 255, 255, 0.9)')
    expect(tokenValues.color.light.bg.brand).toBe('rgba(225, 229, 26, 1)')
    expect(tokens.color.themed.greenLime.subtle.onBackground).toBe(
      'var(--lmnd-color-themed-green-lime-subtle-on-background)',
    )
  })

  it('leaves out colours hidden from publishing', () => {
    expect('accent' in tokens.color.content).toBe(false)
  })
})
