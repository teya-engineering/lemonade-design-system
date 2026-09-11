// url=<LEMONADE_COMPONENTS>?node-id=7115-77429
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Toast.kt
// component=Toast
import figma from 'figma'

const instance = figma.selectedInstance

const labelLayer = instance.findText('Label')
const label = labelLayer && labelLayer.type === 'TEXT' ? labelLayer.textContent : ''

const voice = instance.getEnum('◉ Voice', {
  Success: 'Success',
  Error: 'Error',
  Neutral: 'Neutral',
})

// Success and Error bake their icon into the variant; only Neutral's is
// swappable.
const icon = voice === 'Neutral' ? instance.getInstanceSwap('↪ 🧩 Icon') : null
let iconCode
if (icon && icon.type === 'INSTANCE') {
  iconCode = icon.executeTemplate().example
}

const actionLabel = instance.getBoolean('◉ Show Action')
  ? instance.getString('↪ ✍️ Action Label')
  : undefined

export default {
  example: figma.kotlin`LemonadeUi.Toast(
    label = "${label}",
    voice = ToastVoice.${voice},${iconCode ? figma.kotlin`
    icon = ${iconCode},` : ''}${actionLabel ? `
    actionLabel = "${actionLabel}",
    onAction = { },` : ''}
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.Toast',
    'import com.teya.lemonade.ToastVoice',
  ],
  id: 'toast',
  metadata: { nestable: true },
}
