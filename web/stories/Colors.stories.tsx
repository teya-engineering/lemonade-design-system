import type { Meta, StoryObj } from '@storybook/react'
import { useEffect, useState } from 'react'

const GROUP_PATTERN = /^--lmnd-color-([a-z]+)-/

function useColorGroups() {
  const [groups, setGroups] = useState<Record<string, string[]>>({})
  useEffect(() => {
    // Read the custom properties actually declared on :root, so the gallery lists
    // whatever the converter emitted, grouped by their own naming — no hardcoded
    // group list to drift out of sync (e.g. missing `shadow` if it were listed by hand).
    const found = new Map<string, Set<string>>()
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
          const match = GROUP_PATTERN.exec(prop)
          if (!match) continue
          const group = match[1]
          if (!found.has(group)) found.set(group, new Set())
          found.get(group)?.add(prop)
        }
      }
    }
    const result: Record<string, string[]> = {}
    for (const [group, names] of found) {
      result[group] = [...names].sort()
    }
    setGroups(result)
  }, [])
  return groups
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
  render: () => {
    const groups = useColorGroups()
    return (
      <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-200)' }}>
        {Object.keys(groups)
          .sort()
          .map((group) => (
            <Group key={group} prefix={group} names={groups[group]} />
          ))}
      </div>
    )
  },
}

function Group({ prefix, names }: { prefix: string; names: string[] }) {
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
