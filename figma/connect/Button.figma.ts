// url=<LEMONADE_COMPONENTS>?node-id=8302-10112
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Button.kt
// component=Button
import figma from 'figma'

const instance = figma.selectedInstance

const label = instance.getString('✍️ Label')

const variant = instance.getEnum('◇ Variant', {
  Primary: 'Primary',
  Secondary: 'Secondary',
  Neutral: 'Neutral',
  Critical: 'Critical',
  'On Brand': 'OnBrand',
  'On Color': 'OnColor',
})

const type = instance.getEnum('◇ Type', {
  Solid: 'Solid',
  Subtle: 'Subtle',
  Ghost: 'Ghost',
})

const size = instance.getEnum('↕ Size', {
  Large: 'Large',
  Medium: 'Medium',
  Small: 'Small',
  XSmall: 'XSmall',
})

const loading = instance.getEnum('◉ Is Loading', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

// The Figma slots are only meaningful when their matching toggle is on.
const leadingSlot = instance.getBoolean('◉ Show Leading')
  ? instance.getSlot('↪ 🧩 Leading Slot')
  : undefined
const trailingSlot = instance.getBoolean('◉ Show Trailing')
  ? instance.getSlot('↪ 🧩 Trailing Slot')
  : undefined

export default {
  example: figma.kotlin`LemonadeUi.Button(
    label = "${label}",
    onClick = { },
    variant = LemonadeButtonVariant.${variant},
    type = LemonadeButtonType.${type},
    size = LemonadeButtonSize.${size},${
      leadingSlot ? figma.kotlin`
    leadingSlot = { ${leadingSlot} },` : ''
    }${
      trailingSlot ? figma.kotlin`
    trailingSlot = { ${trailingSlot} },` : ''
    }${disabled ? `
    enabled = false,` : ''}${loading ? `
    loading = true,` : ''}
)`,
  imports: [
    'import com.teya.lemonade.Button',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeButtonSize',
    'import com.teya.lemonade.core.LemonadeButtonType',
    'import com.teya.lemonade.core.LemonadeButtonVariant',
  ],
  id: 'button',
  metadata: { nestable: true },
}
