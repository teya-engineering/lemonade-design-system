// A Steps timeline has no current-index property in Figma; each row's indicator
// says Completed, Current, Error or Next. This folds those back into the code's
// currentIndex, where a reached Critical row is the error and an index past the
// last row means every step is done.
export function stepsCurrentIndex(steps: (string | undefined)[]): { currentIndex: number | null; unmatched: boolean } {
  const current = steps.indexOf('Current')
  if (current !== -1) return { currentIndex: current, unmatched: false }
  const firstNext = steps.indexOf('Next')
  const lastReached = (firstNext === -1 ? steps.length : firstNext) - 1
  if (lastReached < 0) return { currentIndex: null, unmatched: false }
  if (lastReached === steps.length - 1) return { currentIndex: steps.length, unmatched: false }
  if (steps[lastReached] === 'Error') return { currentIndex: lastReached, unmatched: false }
  return { currentIndex: lastReached + 1, unmatched: true }
}
