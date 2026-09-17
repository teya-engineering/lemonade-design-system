// url=<LEMONADE_COMPONENTS>?node-id=7815-106370
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/TextField.kt
// component=TextFieldWithSelector
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { quote } = renderer(instance, figma.kotlin)

const filled = instance.getEnum('◉ Is Filled', { True: true, False: false })
const input = filled ? instance.getString('✍️ Value') : ''

const placeholder = instance.getString('✍️ Placeholder')
const hasError = instance.getEnum('◉ Has Error', { True: true, False: false })
const disabled = instance.getEnum('◉ Is Disabled', { True: true, False: false })
const optional = instance.getBoolean('◉ Is Optional')

const label = instance.getBoolean('◉ Show Label') ? instance.getString('↪ ✍️ Label') : undefined
const supportText = instance.getBoolean('◉ Show Footer')
  ? instance.getString('↪ ✍️ Support Text')
  : undefined
const errorMessage = hasError ? instance.getString('↪ ✍️ Error Message') : undefined

// The selector is built from nested parts that the code leaves to leadingContent.
const selector = instance.findInstance('Selector Type', { traverseInstances: true })
let selectorText = ''
let asset
let assetImport
if (selector && selector.type === 'INSTANCE') {
  selectorText = selector.getString('✍️ Text')
  const holder = selector.getBoolean('◉ Show Asset') ? selector.findInstance('Asset') : undefined
  if (holder && holder.type === 'INSTANCE') {
    const config = holder.getEnum('Config', {
      'Country Flag': 'flag',
      Icon: 'icon',
      Brand: 'brand',
      Image: 'image',
    })
    const swapName = { flag: '↪ 🧩 Flag', icon: '↪ 🧩 Icon', brand: '↪ 🧩 Brand' }[config]
    const swap = swapName ? holder.getInstanceSwap(swapName) : undefined
    const code = swap && swap.type === 'INSTANCE' ? swap.executeTemplate().example : undefined
    if (config === 'image') asset = figma.kotlin`/* image */`
    else if (code && config === 'flag') {
      asset = figma.kotlin`LemonadeUi.CountryFlag(flag = ${code})`
      assetImport = 'import com.teya.lemonade.CountryFlag'
    } else if (code && config === 'icon') {
      asset = figma.kotlin`LemonadeUi.Icon(icon = ${code}, contentDescription = null)`
    } else if (code && config === 'brand') {
      asset = figma.kotlin`LemonadeUi.BrandLogo(logo = ${code})`
      assetImport = 'import com.teya.lemonade.BrandLogo'
    }
  }
}

export default {
  example: figma.kotlin`LemonadeUi.TextFieldWithSelector(
    input = "${quote(input)}",
    onInputChanged = { },
    leadingAction = { },
    leadingContent = {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = LemonadeTheme.spaces.spacing200),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(all = LemonadeTheme.spaces.spacing400),
        ) {${asset ? figma.kotlin`
            ${asset}` : ''}
            LemonadeUi.Text(text = "${quote(selectorText)}")
            LemonadeUi.Icon(icon = LemonadeIcons.ChevronDown, contentDescription = null)
        }
    },${label ? `
    label = "${quote(label)}",` : ''}
    placeholderText = "${quote(placeholder)}",${supportText ? `
    supportText = "${quote(supportText)}",` : ''}${errorMessage ? `
    errorMessage = "${quote(errorMessage)}",` : ''}${hasError ? `
    error = true,` : ''}${optional ? `
    optionalIndicator = "Optional",` : ''}${disabled ? `
    enabled = false,` : ''}
)`,
  imports: [
    'import androidx.compose.foundation.layout.Arrangement',
    'import androidx.compose.foundation.layout.Row',
    'import androidx.compose.foundation.layout.padding',
    'import androidx.compose.ui.Alignment',
    'import androidx.compose.ui.Modifier',
    'import com.teya.lemonade.Icon',
    'import com.teya.lemonade.LemonadeTheme',
    'import com.teya.lemonade.LemonadeUi',
    'import com.teya.lemonade.Text',
    'import com.teya.lemonade.TextFieldWithSelector',
    'import com.teya.lemonade.core.LemonadeIcons',
    ...(assetImport ? [assetImport] : []),
  ],
  id: 'text-field-with-selector',
  metadata: { nestable: true },
}
