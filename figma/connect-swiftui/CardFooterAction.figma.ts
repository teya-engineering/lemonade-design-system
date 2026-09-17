// url=<LEMONADE_COMPONENTS>?node-id=15153-702
// source=swiftui/Sources/Lemonade/Components/LemonadeCard.swift
// component=CardFooterActionConfig
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { quote } = renderer(instance, figma.swift)

const text = instance.findText('Action')
const label = text && text.type === 'TEXT' ? text.textContent : 'Action'

export default {
  example: figma.swift`CardFooterActionConfig(label: "${quote(label)}", onClick: { })`,
  id: 'card-footer-action',
  metadata: { nestable: true },
}
