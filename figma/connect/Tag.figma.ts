// url=<LEMONADE_COMPONENTS>?node-id=2199-1320
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Tag.kt
// component=Tag
import figma from 'figma'

const instance = figma.selectedInstance

const label = instance.getString('✍️ Label')

const voice = instance.getEnum('Voice', {
  Neutral: 'Neutral',
  Critical: 'Critical',
  Warning: 'Warning',
  Info: 'Info',
  Positive: 'Positive',
  'Neutral On Color': 'NeutralOnColor',
  Featured: 'Featured',
})

const icon = instance.getBoolean('Show Icon') ? instance.getInstanceSwap('↪ 🧩 Icon') : null
let iconCode
if (icon && icon.type === 'INSTANCE') {
  iconCode = icon.executeTemplate().example
}

export default {
  example: figma.kotlin`LemonadeUi.Tag(
    label = "${label}",
    voice = TagVoice.${voice},${iconCode ? figma.kotlin`
    icon = ${iconCode},` : ''}
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.Tag',
    'import com.teya.lemonade.core.TagVoice',
  ],
  id: 'tag',
  metadata: { nestable: true },
}
