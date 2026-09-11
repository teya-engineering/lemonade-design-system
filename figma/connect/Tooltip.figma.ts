// url=<LEMONADE_COMPONENTS>?node-id=20809-38675
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Tooltip.kt
// component=Tooltip
import figma from 'figma'

const instance = figma.selectedInstance

const content = instance.getString('✍️ Content')
const title = instance.getBoolean('◉ Show Title') ? instance.getString('↪ ✍️ Title') : undefined

const placement = instance.getEnum('◇ Indicator Placement', {
  None: 'None',
  'Top Left': 'TopLeft',
  'Top Center': 'TopCenter',
  'Top Right': 'TopRight',
  'Right Top': 'RightTop',
  'Right Center': 'RightCenter',
  'Right Bottom': 'RightBottom',
  'Bottom Right': 'BottomRight',
  'Bottom Center': 'BottomCenter',
  'Bottom Left': 'BottomLeft',
  'Left Bottom': 'LeftBottom',
  'Left Center': 'LeftCenter',
  'Left Top': 'LeftTop',
})

const cover = instance.getBoolean('◉ Show Cover') ? instance.getSlot('↪ 🧩 Cover Slot') : undefined
const footer = instance.getBoolean('◉ Show Footer') ? instance.getSlot('↪ 🧩 Footer') : undefined
const showClose = instance.getBoolean('◉ Show Close Button')

export default {
  example: figma.kotlin`LemonadeUi.Tooltip(
    content = "${content}",${title ? `
    title = "${title}",` : ''}
    indicatorPlacement = TooltipIndicatorPlacement.${placement},${showClose ? `
    onCloseClick = { },` : ''}${
      cover ? figma.kotlin`
    cover = { /* cover */ },` : ''
    }${
      footer ? figma.kotlin`
    footer = { /* footer */ },` : ''
    }
)`,
  imports: [
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.Tooltip',
    'import com.teya.lemonade.core.TooltipIndicatorPlacement',
  ],
  id: 'tooltip',
  metadata: { nestable: true },
}
