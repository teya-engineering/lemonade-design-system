// url=<LEMONADE_COMPONENTS>?node-id=8302-10112
// source=swiftui/Sources/Lemonade/Components/LemonadeButton.swift
// component=Button
import figma from 'figma'

const instance = figma.selectedInstance

const label = instance.getString('✍️ Label')

const variant = instance.getEnum('◇ Variant', {
  Primary: 'primary',
  Secondary: 'secondary',
  Neutral: 'neutral',
  Critical: 'critical',
  'On Brand': 'onBrand',
  'On Color': 'onColor',
})

const type = instance.getEnum('◇ Type', {
  Solid: 'solid',
  Subtle: 'subtle',
  Ghost: 'ghost',
})

const size = instance.getEnum('↕ Size', {
  Large: 'large',
  Medium: 'medium',
  Small: 'small',
  XSmall: 'xSmall',
})

const loading = instance.getEnum('◉ Is Loading', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

const leadingSlot = instance.getBoolean('◉ Show Leading')
  ? instance.getSlot('↪ 🧩 Leading Slot')
  : undefined
const trailingSlot = instance.getBoolean('◉ Show Trailing')
  ? instance.getSlot('↪ 🧩 Trailing Slot')
  : undefined

// Swift rejects a trailing comma in an argument list, so every optional
// argument carries a leading comma instead.
export default {
  example: figma.swift`LemonadeUi.Button(
    label: "${label}",
    onClick: { }${
      leadingSlot ? figma.swift`,
    leadingSlot: { _ in ${leadingSlot} }` : ''
    }${
      trailingSlot ? figma.swift`,
    trailingSlot: { _ in ${trailingSlot} }` : ''
    },
    variant: .${variant},
    type: .${type},
    size: .${size}${disabled ? `,
    enabled: false` : ''}${loading ? `,
    loading: true` : ''}
)`,
  id: 'button',
  metadata: { nestable: true },
}
