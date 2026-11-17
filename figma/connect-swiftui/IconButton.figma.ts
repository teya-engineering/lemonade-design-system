// url=<LEMONADE_COMPONENTS>?node-id=17383-2038
// source=swiftui/Sources/Lemonade/Components/LemonadeIconButton.swift
// component=IconButton
import figma from 'figma'
import { swiftIconButton } from '../shared/icon-button'

export default {
  ...swiftIconButton(figma.selectedInstance, false),
  id: 'icon-button',
  metadata: { nestable: true },
}
