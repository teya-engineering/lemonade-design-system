import type { Meta, StoryObj } from '@storybook/react'
import { useEffect, useState } from 'react'

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

function Card({ name }: { name: string }) {
  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        gap: 'var(--lmnd-spacing-400)',
        padding: 'var(--lmnd-spacing-800)',
        background: 'var(--lmnd-color-bg-elevated)',
        borderRadius: 'var(--lmnd-radius-300)',
      }}
    >
      <div
        style={{
          width: 'var(--lmnd-size-2000)',
          height: 'var(--lmnd-size-2000)',
          background: 'var(--lmnd-color-bg-default)',
          borderRadius: 'var(--lmnd-radius-200)',
          boxShadow: `var(${name})`,
        }}
      />
      <code className="lmnd-text-body-small-regular" style={{ color: 'var(--lmnd-color-content-primary)' }}>
        {name}
      </code>
    </div>
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
        <Card key={name} name={name} />
      ))}
    </div>
  )
}
