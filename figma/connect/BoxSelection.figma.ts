// url=<LEMONADE_COMPONENTS>?node-id=20918-229
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/BoxSelection.kt
// component=BoxSelection
import figma from 'figma'

const instance = figma.selectedInstance

const variant = instance.getEnum('◇ Variant', {
  Filled: 'Filled',
  Outlined: 'Outlined',
})

// "N/A" is the Outlined variant, where the background does not apply — emitting
// a value there would imply a choice the design does not make.
const background = instance.getEnum('◇ Background', {
  Default: 'Default',
  Elevated: 'Elevated',
  'N/A': undefined,
})

const selected = instance.getEnum('◉ Is Selected', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

const content = instance.getSlot('🧩 Content Slot')

export default {
  example: figma.kotlin`LemonadeUi.BoxSelection(
    variant = LemonadeBoxSelectionVariant.${variant},${background ? `
    background = LemonadeBoxSelectionBackground.${background},` : ''}${selected ? `
    isSelected = true,` : ''}${disabled ? `
    enabled = false,` : ''}
    onClick = { },
) {
    /* content */
}`,
  imports: [
    'import com.teya.lemonade.BoxSelection',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeBoxSelectionBackground',
    'import com.teya.lemonade.core.LemonadeBoxSelectionVariant',
  ],
  id: 'box-selection',
  metadata: { nestable: false },
}
