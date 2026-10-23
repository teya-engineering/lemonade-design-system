/// <reference types="vite/client" />
import llms from '../llms.txt?raw'
import { describe, expect, it } from 'vitest'
import { brandLogoNames, flagNames, iconNames } from '../src/index'

const listedUnder = (heading: string) => {
  const section = llms.split(`\n## ${heading}\n`)[1]?.split('\n## ')[0] ?? ''
  return [...section.matchAll(/^- `([^`]+)`$/gm)].map((match) => match[1])
}

describe('the asset names in llms.txt', () => {
  it.each([
    ['Icons', iconNames],
    ['Flags', flagNames],
    ['Brand logos', brandLogoNames],
  ] as const)('lists every name under %s', (heading, names) => {
    expect(names.length).toBeGreaterThan(0)
    expect(listedUnder(heading)).toEqual([...names])
  })
})
