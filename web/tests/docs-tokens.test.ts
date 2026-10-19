/// <reference types="vite/client" />
import readme from '../README.md?raw'
import installing from '../stories/Installing.mdx?raw'
import llms from '../llms.txt?raw'
import { describe, expect, it } from 'vitest'
import { textStyles, tokens, tokenValues } from '../src/index'

const exports: Record<string, unknown> = { tokens, tokenValues, textStyles }

const referenced = [readme, installing, llms].flatMap((doc) =>
  [...doc.matchAll(/\b(tokens|tokenValues|textStyles)((?:\.[A-Za-z]\w*|\[\d+\])+)/g)]
    .map((match) => match[0])
    .filter((reference) => !/^tokens\.(css|json)\b/.test(reference)),
)

const resolve = (reference: string) =>
  [...reference.matchAll(/\.?(\w+)|\[(\d+)\]/g)]
    .map((match) => match[1] ?? match[2])
    .reduce<unknown>((node, key, index) => (index === 0 ? exports[key] : (node as Record<string, unknown>)?.[key]), undefined)

describe('the token references in the docs', () => {
  it('finds references to check', () => {
    expect(referenced.length).toBeGreaterThan(10)
  })

  it.each([...new Set(referenced)])('%s exists', (reference) => {
    expect(resolve(reference)).toBeDefined()
  })
})
