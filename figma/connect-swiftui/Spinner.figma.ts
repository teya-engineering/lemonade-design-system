// url=<LEMONADE_COMPONENTS>?node-id=7132-127
// source=swiftui/Sources/Lemonade/Components/LemonadeSpinner.swift
// component=Spinner
import figma from 'figma'

const instance = figma.selectedInstance

// Compose's Spinner takes a size; this one does not, so any size but the
// default cannot be expressed.
const size = instance.getEnum('Size', {
  Small: 'Small',
  Medium: 'Medium',
  Large: 'Large',
  XLarge: 'XLarge',
  '2XLarge': '2XLarge',
})

export default {
  example: figma.swift`LemonadeUi.Spinner()${size !== 'Medium' ? `
// NOTE: this design is ${size}; the SwiftUI Spinner has no size parameter` : ''}`,
  id: 'spinner',
  metadata: { nestable: true },
}
