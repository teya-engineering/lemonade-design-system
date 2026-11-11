// The stepper's step count is fixed at three in Figma; with titles off, or when
// no item instance is found, callers emit three empty items.
export function progressStepperData(instance): {
  completedSteps: number
  items: { label?: string; subheading?: string }[] | null
} {
  const completedSteps = instance.getEnum('value', { '1/3': 1, '2/3': 2, '3/3': 3 })
  const hasStepTitle = instance.getEnum('hasStepTitle?', { yes: true, no: false })
  if (!hasStepTitle) return { completedSteps, items: null }
  const read = (item, layer) => {
    const node = item.findText(layer)
    return node && node.type === 'TEXT' ? node.textContent : undefined
  }
  const items = instance
    .findLayers((node) => node.type === 'INSTANCE' && node.name === '.Stepper Item')
    .map((item) => ({ label: read(item, 'Step title'), subheading: read(item, 'Subheading') }))
  return { completedSteps, items: items.length > 0 ? items : null }
}
