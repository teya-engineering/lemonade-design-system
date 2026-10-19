/// <reference types="vite/client" />
import tokensCss from '../styles/tokens.css?raw'
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

const CSS_GROUP: Record<string, string> = {
  spacing: 'spacing',
  radius: 'radius',
  size: 'size',
  borderWidth: 'border-width',
  opacity: 'opacity',
  lineHeight: 'line-height',
  shadow: 'shadow',
}
const CSS_FONT_GROUP: Record<string, string> = { family: 'font-family', size: 'font-size', weight: 'font-weight' }

const cssNameOf = (path: string[]) => {
  const group = path[0] === 'font' ? CSS_FONT_GROUP[path[1]] : CSS_GROUP[path[0]]
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
