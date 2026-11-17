// Fails when an SVG in svg/ has no generated output on one of the platforms.
//
//   node scripts/check-asset-parity.mjs
//
// A presence check, not a regeneration: the SVG converter rewrites every PDF with a new
// timestamp in a clean checkout, so diffing its output would fail on every run.
import { existsSync, readdirSync, readFileSync } from 'node:fs'
import { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'

const repo = join(dirname(fileURLToPath(import.meta.url)), '..')
const at = (...parts) => join(repo, ...parts)

const packs = [
  { name: 'icons', svg: 'svg/icons', web: 'web/assets/icons', swiftEnum: 'LemonadeIcons.swift', dark: false },
  { name: 'flags', svg: 'svg/flags', web: 'web/assets/flags', swiftEnum: 'LemonadeCountryFlags.swift', dark: false },
  { name: 'brand logos', svg: 'svg/brandLogos', web: 'web/assets/brand-logos', swiftEnum: 'LemonadeBrandLogos.swift', dark: true },
]

const drawables = 'kmp/ui/src/commonMain/composeResources/drawable'
const imagesets = 'swiftui/Sources/Lemonade/Resources/Assets.xcassets'

const rawValues = (file) =>
  new Set([...readFileSync(at('swiftui/Sources/Lemonade', file), 'utf8').matchAll(/^\s+case \w+ = "([^"]+)"/gm)].map((m) => m[1]))

const problems = []

for (const pack of packs) {
  const swiftNames = rawValues(pack.swiftEnum)
  const names = readdirSync(at(pack.svg))
    .filter((f) => f.endsWith('.svg'))
    .map((f) => f.slice(0, -'.svg'.length))
    .filter((n) => !(pack.dark && n.endsWith('-dark')))
    .sort()

  for (const name of names) {
    const missing = []
    if (!existsSync(at(drawables, `gen_${name.replaceAll('-', '_')}.xml`))) missing.push('Android drawable')
    if (!existsSync(at(imagesets, `${name}.imageset`, `${name}.pdf`))) missing.push('SwiftUI imageset')
    if (!swiftNames.has(name)) missing.push('SwiftUI enum entry')
    if (!existsSync(at(pack.web, `${name}.svg`))) missing.push('web asset')
    if (missing.length) problems.push(`${pack.name}: ${name} is missing its ${missing.join(', ')}`)
  }
}

if (problems.length) {
  console.error(`::error::${problems.length} asset(s) in svg/ are missing generated output:`)
  for (const p of problems) console.error(`  ${p}`)
  console.error('')
  console.error('Icons: run /export-icons. Flags and brand logos: follow svg/README.md.')
  console.error('Every new asset also needs its Figma Code Connect mapping (see "Connect it to Figma" in svg/README.md).')
  process.exit(1)
}

console.log('Every SVG in svg/ has its generated output.')
