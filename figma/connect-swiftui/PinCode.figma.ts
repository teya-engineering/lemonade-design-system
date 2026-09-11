// url=<LEMONADE_COMPONENTS>?node-id=21794-78507
// source=swiftui/Sources/Lemonade/Components/LemonadePinCode.swift
// component=PinCode
import figma from 'figma'

const instance = figma.selectedInstance

const length = instance.getEnum('↔ Length', { '4': 4, '6': 6 })
const error = instance.getEnum('◉ Has Error', { True: true, False: false })

// Figma offers a disabled state that neither platform implements.
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

export default {
  example: figma.swift`LemonadeUi.PinCode(${disabled ? `
    // NOTE: this design is disabled, which the component does not support` : ''}
    value: .constant("")${length !== 6 ? `,
    length: ${length}` : ''}${error ? `,
    error: true` : ''}
)`,
  id: 'pin-code',
  metadata: { nestable: true },
}
