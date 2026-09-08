// url=<LEMONADE_COMPONENTS>?node-id=7115-77429
// source=swiftui/Sources/Lemonade/Components/LemonadeToast.swift
// component=Toast
import figma from 'figma'

const instance = figma.selectedInstance

// The message is a plain text layer, not a component property.
const labelLayer = instance.findText('Label')
const label = labelLayer && labelLayer.type === 'TEXT' ? labelLayer.textContent : ''

const voice = instance.getEnum('◉ Voice', {
  Success: 'success',
  Error: 'error',
  Neutral: 'neutral',
})

// Success and Error bake their own icon into the variant; only Neutral exposes a
// swappable one, so emitting the swap on the others would invent an icon.
const icon = voice === 'neutral' ? instance.getInstanceSwap('↪ 🧩 Icon') : null
let iconCode
if (icon && icon.type === 'INSTANCE') {
  iconCode = icon.executeTemplate().example
}

const actionLabel = instance.getBoolean('◉ Show Action')
  ? instance.getString('↪ ✍️ Action Label')
  : undefined

export default {
  example: figma.swift`LemonadeUi.Toast(
    label: "${label}",
    voice: .${voice}${iconCode ? figma.swift`,
    icon: ${iconCode}` : ''}${actionLabel ? `,
    actionLabel: "${actionLabel}",
    onAction: { }` : ''}
)`,
  id: 'toast',
  metadata: { nestable: true },
}
