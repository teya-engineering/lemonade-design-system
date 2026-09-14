import figma from 'figma'
import { renderer } from './render'

// Both Figma sheet sets map to the one composable; full screen skips the
// half-expanded state.
export const bottomSheet = (instance, fullScreen) => {
  const { render, snippets, imports } = renderer(instance, figma.kotlin)

  const subtle = instance.getEnum('◇ Background', { Default: false, Subtle: true })
  const actions = instance.getBoolean('◉ Show Bottom Actions')
  const actionCode = actions ? snippets('↪ 🧩 Actions', '    ') : undefined

  // The code sheet has no header: its title goes in the content, as in its KDoc.
  const bar = instance.findInstance('Bottom Sheet Top Bar')
  const hasBar = bar && bar.type === 'INSTANCE'
  const title = hasBar ? bar.getString('✍️ Title') : undefined
  const subheading = hasBar && bar.getBoolean('◉ Show Subheading') ? bar.getString('↪ ✍️ Subheading') : undefined
  const grabber = hasBar ? bar.getBoolean('◉ Show Grabber') : true

  const search = instance.findInstance('Search Field')
  const searchCode = search && search.type === 'INSTANCE' ? render(search, '    ') : undefined

  return {
    example: figma.kotlin`LemonadeUi.BottomSheet(
    expanded = true,
    onDismissRequest = { },${grabber ? '' : `
    showDragHandle = false,`}${fullScreen ? `
    skipPartiallyExpanded = true,` : ''}${subtle ? `
    background = LemonadeBottomSheetVariant.Subtle,` : ''}
) {${title ? `
    LemonadeUi.Text(text = "${title}", textStyle = LemonadeTheme.typography.headingSmall)` : ''}${subheading ? `
    LemonadeUi.Text(text = "${subheading}")` : ''}${searchCode ? figma.kotlin`
    ${searchCode}` : ''}
    /* sheet content */${actions ? actionCode ?? `
    /* bottom actions */` : ''}
}`,
    imports: [
      'import com.teya.lemonade.BottomSheet',
      'import com.teya.lemonade.LemonadeTheme',
      'import com.teya.lemonade.LemonadeUi',
      'import com.teya.lemonade.Text',
      ...(subtle ? ['import com.teya.lemonade.core.LemonadeBottomSheetVariant'] : []),
      ...imports,
    ],
  }
}
