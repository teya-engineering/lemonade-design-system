import type { Meta, StoryObj } from '@storybook/react'
import { useEffect, useState } from 'react'
import { Tile } from './Tile'

function useShadowNames() {
  const [names, setNames] = useState<string[]>([])
  useEffect(() => {
    // Read whatever shadow custom properties tokens.css actually declares, so this
    // gallery cannot drift or hardcode a stale list of sizes.
    const found = new Set<string>()
    for (const sheet of Array.from(document.styleSheets)) {
      let rules: CSSRuleList
      try {
        rules = sheet.cssRules
      } catch {
        continue // cross-origin sheets throw
      }
      for (const rule of Array.from(rules)) {
        if (!(rule instanceof CSSStyleRule)) continue
        for (const prop of Array.from(rule.style)) {
          if (prop.startsWith('--lmnd-shadow-')) found.add(prop)
        }
      }
    }
    const order = ['xs', 'sm', 'md', 'lg', 'xl']
    setNames(
      [...found].sort((a, b) => order.indexOf(a.replace('--lmnd-shadow-', '')) - order.indexOf(b.replace('--lmnd-shadow-', ''))),
    )
  }, [])
  return names
}

function ShadowTile({ name }: { name: string }) {
  return (
    <Tile label={name}>
      <div
        style={{
          width: 'var(--lmnd-size-2000)',
          height: 'var(--lmnd-size-2000)',
          background: 'var(--lmnd-color-bg-default)',
          borderRadius: 'var(--lmnd-radius-200)',
          boxShadow: `var(${name})`,
        }}
      />
    </Tile>
  )
}

const meta: Meta = { title: 'Foundations/Shadows' }
export default meta

export const All: StoryObj = {
  render: () => <ShadowGrid />,
}

function ShadowGrid() {
  const names = useShadowNames()
  return (
    <div
      style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fill, minmax(12rem, 1fr))',
        gap: 'var(--lmnd-spacing-400)',
      }}
    >
      {names.map((name) => (
        <ShadowTile key={name} name={name} />
      ))}
    </div>
  )
}
