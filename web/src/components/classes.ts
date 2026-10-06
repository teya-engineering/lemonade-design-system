/** The vocabularies are camelCase to match the platforms; every stylesheet here is kebab. */
export function kebab(value: string): string {
  return value.replace(/[A-Z]/g, (char) => `-${char.toLowerCase()}`)
}
