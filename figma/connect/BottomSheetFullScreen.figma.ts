// url=<LEMONADE_COMPONENTS>?node-id=17985-27526
// source=kmp/expressive/src/commonMain/kotlin/com/teya/lemonade/BottomSheet.kt
// component=BottomSheet
import figma from 'figma'
import { bottomSheet } from '../shared/bottom-sheet'

export default {
  ...bottomSheet(figma.selectedInstance, true),
  id: 'bottom-sheet-full-screen',
  metadata: { nestable: false },
}
