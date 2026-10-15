/**
 * Mirrors `ThemedHue`, the seventeen hues the themed palettes are authored in. Each resolves
 * to the `--lmnd-color-themed-<hue>-*` properties the token converter generates.
 *
 * Nothing here may reference React. The framework-free root reaches this module, and a React
 * type in it would land in a shared declaration chunk the root then has to import.
 */
export type ThemedHue =
  | 'amber'
  | 'blue'
  | 'cyan'
  | 'fuchsia'
  | 'green'
  | 'greenLime'
  | 'indigo'
  | 'neutral'
  | 'orange'
  | 'pink'
  | 'purple'
  | 'red'
  | 'rose'
  | 'teal'
  | 'violet'
  | 'yellow'
  | 'yellowLime'

/**
 * A hue and which of its two palettes to read, mirroring SwiftUI's `ThemedStyle` — KMP
 * spells the same pair as `ThemedHue.Violet` and `ThemedHue.Violet.subtle`.
 */
export type ThemedStyle = {
  hue: ThemedHue
  subtle?: boolean
}

/** The three themed properties a surface needs, as CSS `var()` references for its hue. */
export function themedSurface({ hue, subtle = false }: ThemedStyle): {
  background: string
  border: string
  content: string
} {
  const family = `--lmnd-color-themed-${hue.replace(/[A-Z]/g, (char) => `-${char.toLowerCase()}`)}${subtle ? '-subtle' : ''}`
  return {
    background: `var(${family}-background)`,
    border: `var(${family}-border)`,
    content: `var(${family}-on-background)`,
  }
}
