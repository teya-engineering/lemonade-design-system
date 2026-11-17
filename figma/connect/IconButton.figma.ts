// url=<LEMONADE_COMPONENTS>?node-id=17383-2038
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/IconButton.kt
// component=IconButton
import figma from 'figma'
import { kotlinIconButton } from '../shared/icon-button'

export default {
  ...kotlinIconButton(figma.selectedInstance, false),
  id: 'icon-button',
  metadata: { nestable: true },
}
