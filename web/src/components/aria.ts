/**
 * The ARIA a component contributes for a required `contentDescription: string | null`: a
 * name and a role when it carries meaning, `aria-hidden` when it repeats adjacent text.
 *
 * Only keys the caller left undefined are filled in, and the result is spread *after* their
 * props. Spreading it before looks equivalent and is not — a wrapper forwarding
 * `aria-label={undefined}` means "no opinion", and spreading that over the default would
 * leave the element unnamed.
 *
 * Both components render a fixed element, so the role is always theirs to set. That is what
 * keeps this simple: an element the caller could choose would bring semantics of its own,
 * and the component would have to decide which of them survive.
 */
export function ariaFor(
  contentDescription: string | null,
  role: 'img' | 'status',
  callerProps: Record<string, unknown>,
): Record<string, unknown> {
  const aria: Record<string, unknown> = {}

  if (contentDescription === null) {
    if (callerProps['aria-hidden'] === undefined) aria['aria-hidden'] = true
    return aria
  }

  if (callerProps.role === undefined) aria.role = role
  if (callerProps['aria-label'] === undefined) aria['aria-label'] = contentDescription
  return aria
}
