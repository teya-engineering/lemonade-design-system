const HUES = ['Yellow', 'Amber', 'Orange', 'Red', 'Rose', 'Pink', 'Fuchsia', 'Purple', 'Violet',
  'Indigo', 'Blue', 'Cyan', 'Teal', 'Green', 'Green Lime', 'Yellow Lime']

// `◇ Theme` value -> code, given how a platform spells a hue's solid palette.
export const themeMap = (solid) =>
  Object.fromEntries(HUES.flatMap((hue) => {
    const entry = solid(hue.split(' '))
    return [[hue, entry], [`${hue} Subtle`, `${entry}.subtle`]]
  }))
