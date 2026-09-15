// url=<LEMONADE_COMPONENTS>?node-id=7132-127
// source=swiftui/Sources/Lemonade/Components/LemonadeSpinner.swift
// component=Spinner
import figma from 'figma'

const instance = figma.selectedInstance

const size = instance.getEnum('Size', {
  Small: 'small',
  Medium: 'medium',
  Large: 'large',
  XLarge: 'xLarge',
  '2XLarge': 'xxLarge',
})

export default {
  example: figma.swift`LemonadeUi.Spinner(
    size: .${size}
)`,
  id: 'spinner',
  metadata: { nestable: true },
}
