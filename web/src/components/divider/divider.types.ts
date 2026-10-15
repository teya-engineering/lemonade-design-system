/** Mirrors `DividerVariant`. */
export type DividerVariant = 'solid' | 'dashed'

/**
 * What `dividerClasses` needs, and the whole vocabulary a non-React stack consumes.
 *
 * `orientation` mirrors no platform enum: KMP and SwiftUI each expose two entry points,
 * `HorizontalDivider` and `VerticalDivider`, and so does web. It is here because one class
 * builder serves both, and it is absent from the enum parity check for the same reason.
 *
 * Nothing here may reference React. The root export reaches these types, and both entries
 * share this module, so a React type here lands in a shared declaration chunk that the
 * framework-free root then has to import.
 */
export type DividerAppearance = {
  variant?: DividerVariant
  orientation?: 'horizontal' | 'vertical'
  /** The labelled form draws its lines as children, with the label between them. */
  labelled?: boolean
}
