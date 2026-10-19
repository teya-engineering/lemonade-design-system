import { describe, expect, it } from 'vitest'
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
