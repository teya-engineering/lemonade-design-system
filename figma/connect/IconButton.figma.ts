// url=<LEMONADE_COMPONENTS>?node-id=17383-2038
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/IconButton.kt
// component=IconButton
import figma from 'figma'

const instance = figma.selectedInstance

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

// Figma exposes three sizes here; LemonadeButtonSize also has XSmall, unused by this set.
const size = instance.getEnum('↕ Size', {
  Large: 'Large',
  Medium: 'Medium',
  Small: 'Small',
})

const loading = instance.getEnum('◉ Is Loading', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })

const icon = instance.getInstanceSwap('🧩 Icon')
let iconCode
if (icon && icon.type === 'INSTANCE') {
  iconCode = icon.executeTemplate().example
}

export default {
  example: figma.kotlin`LemonadeUi.IconButton(${iconCode ? figma.kotlin`
    icon = ${iconCode},` : ''}
    contentDescription = null, // TODO: this button has no visible label — describe the action
    onClick = { },
    variant = LemonadeButtonVariant.${variant},
    type = LemonadeButtonType.${type},
    size = LemonadeButtonSize.${size},${disabled ? `
    enabled = false,` : ''}${loading ? `
    loading = true,` : ''}
)`,
  imports: [
    'import com.teya.lemonade.IconButton',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.core.LemonadeButtonSize',
    'import com.teya.lemonade.core.LemonadeButtonType',
    'import com.teya.lemonade.core.LemonadeButtonVariant',
  ],
  id: 'icon-button',
  metadata: { nestable: true },
}
