/// <reference types="vite/client" />
import componentsCss from '../styles/components.css?raw'
import previewSource from '../.storybook/preview.ts?raw'
import { describe, expect, it } from 'vitest'

/** Every component stylesheet, by path, read at build time so this needs no filesystem API. */
const stylesheets = import.meta.glob('../src/components/**/*.css', {
  query: '?raw',
  import: 'default',
  eager: true,
}) as Record<string, string>

const classesIn = (css: string) => [...css.matchAll(/^\.([\w-]+)/gm)].map((match) => match[1])

describe('the generated components.css', () => {
  it('finds the component stylesheets at all', () => {
    expect(Object.keys(stylesheets).length).toBeGreaterThan(0)
  })

  // Regenerating is what CI diffs, but a stale barrel locally means every story and every
  // consumer sees the old classes while the component emits new ones.
  it.each(Object.keys(stylesheets))('carries every class in %s', (path) => {
    const classes = classesIn(stylesheets[path])
    expect(classes.length).toBeGreaterThan(0)
    classes.forEach((className) => expect(componentsCss).toContain(`.${className}`))
  })
})

describe('Storybook', () => {
  // Storybook has to load what the package ships, not a list of stylesheets kept in step by
  // hand. A component whose CSS never reaches the preview renders as nothing, and the tests
  // that read the CSS file directly all still pass.
  it('loads the barrel rather than naming component stylesheets', () => {
    expect(previewSource).toContain("import '../styles/components.css'")
    expect(previewSource).not.toMatch(/import '\.\.\/src\/components\/.*\.css'/)
  })
})
