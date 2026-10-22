/// <reference types="vite/client" />
import typographyCss from '../styles/typography.css?raw'
import { describe, expect, it } from 'vitest'
import { textStyles, tokens } from '../src/index'

const ruleFor = (className: string) => {
  const match = typographyCss.match(new RegExp(`^\\.${className} \\{\\n([\\s\\S]*?)\\n\\}`, 'm'))
  if (!match) throw new Error(`no rule for .${className}`)
  return new Map([...match[1].matchAll(/^\s+([\w-]+): (.+);$/gm)].map(([, name, value]) => [name, value]))
}

const tokenFor = (value: string | undefined, path: string[]) => {
  const match = value?.match(/^var\(--lmnd-[a-z-]+?-(\w+)\)$/)
  if (!match) throw new Error(`'${value}' is not a token reference`)
  return [...path, match[1]].reduce<unknown>((node, key) => (node as Record<string, unknown>)[key], tokens)
}

describe('textStyles', () => {
  it.each(Object.keys(textStyles) as (keyof typeof textStyles)[])('%s renders like its class', (name) => {
    const { className, style }: { className: string; style: Record<string, string | number> } = textStyles[name]
    const rule = ruleFor(className)

    expect(style.fontFamily).toBe(tokenFor(rule.get('font-family'), ['font', 'family']))
    expect(style.fontSize).toBe(tokenFor(rule.get('font-size'), ['font', 'size']))
    expect(style.lineHeight).toBe(tokenFor(rule.get('line-height'), ['lineHeight']))
    expect(style.fontWeight).toBe(tokenFor(rule.get('font-weight'), ['font', 'weight']))
    expect(style.letterSpacing).toBe(rule.get('letter-spacing'))
    expect(style.textTransform).toBe(rule.get('text-transform'))
    expect(style.WebkitFontSmoothing).toBe(rule.get('-webkit-font-smoothing'))
    expect(style.MozOsxFontSmoothing).toBe(rule.get('-moz-osx-font-smoothing'))
  })

  it('keeps the class name beside a spreadable style', () => {
    expect(textStyles.displayXSmall.className).toBe('lmnd-text-display-xsmall')
    expect('className' in textStyles.displayXSmall.style).toBe(false)
    expect(textStyles.displayXSmall.style).toMatchObject({
      fontSize: '1.5rem',
      fontWeight: 600,
      letterSpacing: '-0.25px',
    })
  })
})
