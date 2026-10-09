/**
 * The ARIA attributes for a component whose `contentDescription` is required as
 * `string | null`: a name and a role when it carries meaning, `aria-hidden` when it repeats
 * something adjacent.
 *
 * Returned as an object rather than written at the call site so it can be spread *before*
 * the caller's own props. Writing `role={…}` after `{...rest}` sets the attribute even when
 * the expression is `undefined`, which silently discards a `role` or `aria-label` the
 * caller passed.
 */
export function labelling(contentDescription: string | null, role: 'img' | 'status' = 'img') {
  if (contentDescription === null) {
    return { 'aria-hidden': true } as const
  }
  return { role, 'aria-label': contentDescription } as const
}
