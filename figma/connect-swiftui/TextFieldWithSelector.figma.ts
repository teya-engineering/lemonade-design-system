// url=<LEMONADE_COMPONENTS>?node-id=7815-106370
// source=swiftui/Sources/Lemonade/Components/LemonadeTextField.swift
// component=TextFieldWithSelector
import figma from 'figma'

const instance = figma.selectedInstance

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
    if (config === 'image') asset = figma.swift`/* image */`
    else if (code && config === 'flag') asset = figma.swift`LemonadeUi.CountryFlag(flag: ${code})`
    else if (code && config === 'icon') asset = figma.swift`LemonadeUi.Icon(icon: ${code}, contentDescription: nil)`
    else if (code && config === 'brand') asset = figma.swift`LemonadeUi.BrandLogo(logo: ${code})`
  }
}

// `input` is a Binding; .constant keeps the designed text and compiles as-is.
export default {
  example: figma.swift`LemonadeUi.TextFieldWithSelector(
    input: .constant("${input}"),
    leadingAction: { },
    leadingContent: {
        HStack(spacing: LemonadeTheme.spaces.spacing200) {${asset ? figma.swift`
            ${asset}` : ''}
            LemonadeUi.Text("${selectorText}")
            LemonadeUi.Icon(icon: .chevronDown, contentDescription: nil)
        }
        .padding(LemonadeTheme.spaces.spacing400)
    }${label ? `,
    label: "${label}"` : ''}${optional ? `,
    optionalIndicator: "Optional"` : ''}${supportText ? `,
    supportText: "${supportText}"` : ''},
    placeholderText: "${placeholder}"${errorMessage ? `,
    errorMessage: "${errorMessage}"` : ''}${hasError ? `,
    error: true` : ''}${disabled ? `,
    enabled: false` : ''}
)`,
  id: 'text-field-with-selector',
  metadata: { nestable: true },
}
