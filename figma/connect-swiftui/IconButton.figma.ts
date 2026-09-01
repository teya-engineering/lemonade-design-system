// url=<LEMONADE_COMPONENTS>?node-id=17383-2038
// source=swiftui/Sources/Lemonade/Components/LemonadeIconButton.swift
// component=IconButton
import figma from 'figma'

const instance = figma.selectedInstance

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
})

const loading = instance.getEnum('◉ Is Loading', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

const icon = instance.getInstanceSwap('🧩 Icon')
let iconCode
if (icon && icon.type === 'INSTANCE') {
  iconCode = icon.executeTemplate().example
}

export default {
  example: figma.swift`LemonadeUi.IconButton(${iconCode ? figma.swift`
    icon: ${iconCode},` : ''}
    contentDescription: nil, // TODO: this button has no visible label — describe the action
    onClick: { },
    variant: .${variant},
    type: .${type},
    size: .${size}${disabled ? `,
    enabled: false` : ''}${loading ? `,
    loading: true` : ''}
)`,
  id: 'icon-button',
  metadata: { nestable: true },
}
