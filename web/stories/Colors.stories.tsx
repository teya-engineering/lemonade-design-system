import type { Meta, StoryObj } from '@storybook/react'
import { useEffect, useState } from 'react'

const GROUPS = ['content', 'bg', 'border'] as const

function useTokenNames(prefix: string) {
  const [names, setNames] = useState<string[]>([])
  useEffect(() => {
    // Read the custom properties actually declared on :root, so the gallery lists
    // whatever the converter emitted — no second copy of the token list.
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
          if (prop.startsWith(`--lmnd-color-${prefix}-`)) found.add(prop)
        }
      }
    }
    setNames([...found].sort())
  }, [prefix])
  return names
}

function Swatch({ name }: { name: string }) {
  const value = getComputedStyle(document.documentElement).getPropertyValue(name).trim()
  return (
    <div style={{ display: 'flex', gap: 'var(--lmnd-spacing-300)', alignItems: 'center' }}>
      <div
        style={{
          width: 'var(--lmnd-size-1000)',
          height: 'var(--lmnd-size-1000)',
          background: `var(${name})`,
          borderRadius: 'var(--lmnd-radius-200)',
          border: '1px solid var(--lmnd-color-border-neutral-low)',
        }}
      />
      <code className="lmnd-text-body-small-regular" style={{ color: 'var(--lmnd-color-content-primary)' }}>
        {name}
      </code>
      <span className="lmnd-text-body-xsmall-regular" style={{ color: 'var(--lmnd-color-content-secondary)' }}>
        {value}
      </span>
    </div>
  )
}

const meta: Meta = { title: 'Foundations/Colors' }
export default meta

export const All: StoryObj = {
  render: () => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-200)' }}>
      {GROUPS.map((group) => (
        <Group key={group} prefix={group} />
      ))}
    </div>
  ),
}

function Group({ prefix }: { prefix: string }) {
  const names = useTokenNames(prefix)
  return (
    <section>
      <h2 className="lmnd-text-heading-small" style={{ color: 'var(--lmnd-color-content-primary)' }}>
        {prefix} ({names.length})
      </h2>
      {names.map((name) => (
        <Swatch key={name} name={name} />
      ))}
    </section>
  )
}
