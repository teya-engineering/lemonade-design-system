/// <reference types="vite/client" />
import { describe, expect, it } from 'vitest'
import * as root from '../src/index'
import { tokens, iconNames, flagNames, brandLogoNames } from '../src/index'

describe('token export', () => {
  it('exposes raw pixel numbers, matching the native platforms', () => {
    // 8, not "0.5rem" — the CSS carries rem, the TS export carries parity.
    expect(tokens.spacing.spacing200).toBe(8)
    expect(tokens.radius.radius200).toBe(8)
  })

  it('converts opacity from the authored 0-100 scale to CSS 0-1', () => {
    expect(tokens.opacity.opacity50).toBe(0.5)
  })

  it('keeps sub-pixel border widths', () => {
    expect(tokens.borderWidth.borderWidth40).toBe(1.5)
  })
})

describe('asset manifests', () => {
  it('lists every asset', () => {
    expect(iconNames).toHaveLength(295)
    expect(flagNames).toHaveLength(265)
    expect(brandLogoNames).toHaveLength(39)
  })

  it('uses kebab-case icon names', () => {
    expect(iconNames).toContain('arrow-corner-down-left')
  })
})

// A class builder is how a stack that never loads the React components gets the same class
// list. One left off the root export is reachable only by deep import, which `exports`
// blocks — and no other test notices, because the component's own suite imports it directly.
describe('the framework-free root', () => {
  const builders = Object.keys(import.meta.glob('../src/components/*/*.classes.ts', { eager: true }))
    .map((path) => path.replace(/.*\/([^/]+)\.classes\.ts$/, '$1'))
    .map((component) => `${component.replace(/-(\w)/g, (_, char: string) => char.toUpperCase())}Classes`)

  it('finds the class builders to check', () => {
    expect(builders.length).toBeGreaterThan(2)
  })

  it.each(builders)('exports %s', (name) => {
    expect(Object.keys(root)).toContain(name)
  })
})
