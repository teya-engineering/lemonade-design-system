// url=<LEMONADE_COMPONENTS>?node-id=18177-1701
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Chip.kt
// component=Chip
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { slotIcon } = renderer(instance, figma.kotlin)

const label = instance.getString('✍️ Label')
const selected = instance.getEnum('◉ Is Selected', { True: true, False: false })
const error = instance.getEnum('◉ Has Error', { True: true, False: false })

// Disabled lives in Interaction State rather than its own flag.
const disabled = instance.getEnum('◇ Interaction State', {
  Rest: false,
  Pressed: false,
  Disabled: true,
})

const counter = instance.getBoolean('◉ Shown Counter')
  ? instance.getString('↪ ✍️ Counter')
  : undefined

const leading = instance.getBoolean('◉ Show Leading')
const leadingIcon = leading ? slotIcon('↪ 🧩 Leading') : undefined

export default {
  example: figma.kotlin`LemonadeUi.Chip(
    label = "${label}",
    selected = ${selected},
    onChipClicked = { },${leadingIcon ? figma.kotlin`
    leadingIcon = ${leadingIcon},` : ''}${counter ? `
    counter = ${counter},` : ''}${error ? `
    error = true,` : ''}${disabled ? `
    enabled = false,` : ''}
)`,
  imports: [
    'import com.teya.lemonade.Chip',
    'import com.teya.lemonade.LemonadeUi',
    ...(leadingIcon ? ['import com.teya.lemonade.core.LemonadeIcons'] : []),
  ],
  id: 'chip',
  metadata: { nestable: true },
}
