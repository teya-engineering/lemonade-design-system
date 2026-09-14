// url=<LEMONADE_COMPONENTS>?node-id=19295-46881
// source=kmp/expressive/src/commonMain/kotlin/com/teya/lemonade/BottomSheet.kt
// component=BottomSheet
import figma from 'figma'
import { bottomSheet } from '../shared/bottom-sheet'

export default {
  ...bottomSheet(figma.selectedInstance, false),
  id: 'bottom-sheet-dynamic',
  metadata: { nestable: true },
}
