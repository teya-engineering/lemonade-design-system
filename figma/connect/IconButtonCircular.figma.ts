// url=<LEMONADE_COMPONENTS>?node-id=17427-5933
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/IconButton.kt
// component=IconButton
import figma from 'figma'
import { kotlinIconButton } from '../shared/icon-button'

export default {
  ...kotlinIconButton(figma.selectedInstance, true),
  id: 'icon-button-circular',
  metadata: { nestable: true },
}
