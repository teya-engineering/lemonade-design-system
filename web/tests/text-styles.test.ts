/// <reference types="vite/client" />
import typographyCss from '../styles/typography.css?raw'
import { describe, expect, it } from 'vitest'
import { textStyles, tokens } from '../src/index'

const ruleFor = (className: string) => {
  const match = typographyCss.match(new RegExp(`^\\.${className} \\{\\n([\\s\\S]*?)\\n\\}`, 'm'))
  if (!match) throw new Error(`no rule for .${className}`)
  return new Map([...match[1].matchAll(/^\s+([\w-]+): (.+);$/gm)].map(([, name, value]) => [name, value]))
}

const step = (value: string | undefined, prefix: string) => {
  const match = value?.match(new RegExp(`^var\\(--lmnd-${prefix}-([\\w-]+)\\)$`))
  if (!match) throw new Error(`'${value}' is not a --lmnd-${prefix} reference`)
  return match[1]
}

describe('textStyles', () => {
  it.each(Object.keys(textStyles) as (keyof typeof textStyles)[])('%s renders like its class', (name) => {
    const style: Record<string, string | number> = textStyles[name]
    const rule = ruleFor(style.className as string)

    expect(style.fontFamily).toBe(tokens.font.family[step(rule.get('font-family'), 'font-family') as 'base'])
    expect(style.fontSize).toBe(tokens.font.size[step(rule.get('font-size'), 'font-size') as unknown as 400])
    expect(style.lineHeight).toBe(tokens.lineHeight[step(rule.get('line-height'), 'line-height') as unknown as 400])
    expect(style.fontWeight).toBe(tokens.font.weight[step(rule.get('font-weight'), 'font-weight') as 'regular'])
    expect(style.letterSpacing).toBe(rule.get('letter-spacing'))
    expect(style.textTransform).toBe(rule.get('text-transform'))
    expect(style.WebkitFontSmoothing).toBe(rule.get('-webkit-font-smoothing'))
    expect(style.MozOsxFontSmoothing).toBe(rule.get('-moz-osx-font-smoothing'))
  })

  it('spreads into a style object', () => {
    expect(textStyles.displayXSmall).toMatchObject({
      className: 'lmnd-text-display-xsmall',
      fontSize: '1.5rem',
      fontWeight: 600,
      letterSpacing: '-0.25px',
    })
  })
})
