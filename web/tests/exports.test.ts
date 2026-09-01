import { describe, expect, it } from 'vitest'
import { tokens } from '../src/index'

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
