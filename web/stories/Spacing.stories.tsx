import type { Meta, StoryObj } from '@storybook/react'
import { useEffect, useState } from 'react'

function useCustomPropertyNames(prefix: string) {
  const [names, setNames] = useState<string[]>([])
  useEffect(() => {
    // Read whatever custom properties the converter actually declared on :root,
    // so this list cannot drift from tokens.css.
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
          if (prop.startsWith(prefix)) found.add(prop)
        }
      }
    }
    setNames(
      [...found].sort((a, b) => {
        const numA = Number(a.match(/-(\d+)$/)?.[1] ?? 0)
        const numB = Number(b.match(/-(\d+)$/)?.[1] ?? 0)
        return numA - numB
      }),
    )
  }, [prefix])
  return names
}

function Bar({ name }: { name: string }) {
  const value = getComputedStyle(document.documentElement).getPropertyValue(name).trim()
  return (
    <div style={{ display: 'flex', gap: 'var(--lmnd-spacing-300)', alignItems: 'center' }}>
      <code
        className="lmnd-text-body-small-regular"
        style={{ color: 'var(--lmnd-color-content-primary)', width: '12rem' }}
      >
        {name}
      </code>
      <div
        style={{
          height: 'var(--lmnd-size-400)',
          width: `var(${name})`,
          minWidth: 2,
          background: 'var(--lmnd-color-bg-brand)',
          borderRadius: 'var(--lmnd-radius-50)',
        }}
      />
      <span className="lmnd-text-body-xsmall-regular" style={{ color: 'var(--lmnd-color-content-secondary)' }}>
        {value}
      </span>
    </div>
  )
}

const meta: Meta = { title: 'Foundations/Spacing' }
export default meta

export const All: StoryObj = {
  render: () => {
    return <SpacingList />
  },
}

function SpacingList() {
  const names = useCustomPropertyNames('--lmnd-spacing-')
  return (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-200)' }}>
      {names.map((name) => (
        <Bar key={name} name={name} />
      ))}
    </div>
  )
}
