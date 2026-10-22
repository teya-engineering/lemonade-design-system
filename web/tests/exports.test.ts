/// <reference types="vite/client" />
import { describe, expect, it } from 'vitest'
import * as root from '../src/index'
import { iconNames, flagNames, brandLogoNames } from '../src/index'

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
