// url=<LEMONADE_COMPONENTS>?node-id=17427-5933
// source=swiftui/Sources/Lemonade/Components/LemonadeIconButton.swift
// component=IconButton
import figma from 'figma'
import { swiftIconButton } from '../shared/icon-button'

export default {
  ...swiftIconButton(figma.selectedInstance, true),
  id: 'icon-button-circular',
  metadata: { nestable: true },
}
