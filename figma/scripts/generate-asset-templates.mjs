// Regenerates one Code Connect template per Lemonade asset, per platform.
//
//   node scripts/generate-asset-templates.mjs                  # everything
//   node scripts/generate-asset-templates.mjs icons            # one asset type
//   node scripts/generate-asset-templates.mjs flags swiftui    # one of each
//
// Every platform must be generated and published. Figma resolves a nested asset
// by node and falls back to another label when it has no template for that node,
// so a missing SwiftUI icon renders the Kotlin snippet inside a Swift call.

import { readFileSync, writeFileSync, mkdirSync, rmSync } from 'node:fs'
import { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'

const here = dirname(fileURLToPath(import.meta.url))
const root = join(here, '..')
const repo = join(root, '..')

const pascal = (name) =>
  name
    .split(/[-_ ]+/)
    .filter(Boolean)
    .map((part) => part[0].toUpperCase() + part.slice(1))
    .join('')

// Kotlin entries carry no raw value, so the member is derived from the Figma
// name. Swift cases carry it as their raw value, so the mapping is read off the
// enum instead.
const kotlinMembers = (src) =>
  new Map([...src.matchAll(/^\s+([A-Z][A-Za-z0-9]*)[,(]/gm)].map((m) => [pascal(m[1]), m[1]]))
const swiftMembers = (src) =>
  new Map([...src.matchAll(/^\s+case ([A-Za-z][A-Za-z0-9]*) = "([^"]+)"/gm)].map((m) => [m[2], m[1]]))

// Brand logos ship a -dark counterpart per brand, but the artwork comes from the
// theme, so both nodes reference the same enum entry.
const stripDark = (name) => name.replace(/-dark$/, '')

// Kotlin enums live in core as LemonadeIcons; the Swift enum in the same-named
// file is singular, LemonadeIcon.
const PLATFORMS = {
  compose: {
    dir: 'connect',
    tag: 'kotlin',
    members: kotlinMembers,
    keyFor: pascal,
    enumPath: (enumName) => `kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/${enumName}.kt`,
    type: (enumName) => enumName,
    imports: (enumName) => [`import com.teya.lemonade.core.${enumName}`],
  },
  swiftui: {
    dir: 'connect-swiftui',
    tag: 'swift',
    members: swiftMembers,
    keyFor: (name) => name,
    enumPath: (enumName) => `swiftui/Sources/Lemonade/${enumName}.swift`,
    type: (enumName) => enumName.replace(/s$/, ''),
    imports: () => [],
  },
}

const ASSETS = {
  icons: {
    manifest: 'icons.manifest.json',
    entries: 'icons',
    urlToken: '<LEMONADE_ICONS>',
    outSub: 'icons',
    enumName: 'LemonadeIcons',
  },
  flags: {
    manifest: 'flags.manifest.json',
    entries: 'flags',
    urlToken: '<LEMONADE_FLAGS>',
    outSub: 'flags',
    enumName: 'LemonadeCountryFlags',
  },
  brandLogos: {
    manifest: 'brand-logos.manifest.json',
    entries: 'brandLogos',
    urlToken: '<LEMONADE_COMPONENTS>',
    outSub: 'brand-logos',
    enumName: 'LemonadeBrandLogos',
    normalise: stripDark,
  },
}

const args = process.argv.slice(2)
const assetNames = args.filter((a) => a in ASSETS)
const platformNames = args.filter((a) => a in PLATFORMS)
const unknown = args.filter((a) => !(a in ASSETS) && !(a in PLATFORMS))
if (unknown.length) {
  console.error(`Unknown argument(s): ${unknown.join(', ')}`)
  console.error(`Assets: ${Object.keys(ASSETS).join(', ')}   Platforms: ${Object.keys(PLATFORMS).join(', ')}`)
  process.exit(1)
}
const assets = assetNames.length ? assetNames : Object.keys(ASSETS)
const platforms = platformNames.length ? platformNames : Object.keys(PLATFORMS)

let failed = false

// Nothing is written until every asset and platform validates. A half-written
// run would leave one label's templates refreshed and the other's stale, which
// renders the wrong language's snippet.
const plans = []

for (const assetName of assets) {
  const asset = ASSETS[assetName]
  const manifest = JSON.parse(readFileSync(join(root, asset.manifest), 'utf8'))
  const items = manifest[asset.entries]
  // Entries the code enum has but Figma does not, stored as Figma names so each
  // platform maps them into its own key space below.
  const knownUnmapped = manifest.knownUnmapped ?? []
  const normalise = asset.normalise ?? ((name) => name)

  for (const platformName of platforms) {
    const platform = PLATFORMS[platformName]
    const enumPath = platform.enumPath(asset.enumName)
    const type = platform.type(asset.enumName)
    const imports = platform.imports(asset.enumName)
    const members = platform.members(readFileSync(join(repo, enumPath), 'utf8'))
    const allowed = new Set(knownUnmapped.map(platform.keyFor))
    const label = `[${assetName}/${platformName}]`

    // Checked both ways: manifest -> enum alone catches a deleted asset but stays
    // silent on an added one, which is the direction that actually happens.
    const missingFromEnum = []
    const memberFor = new Map()
    for (const name of Object.keys(items)) {
      const member = members.get(platform.keyFor(normalise(name)))
      if (!member) missingFromEnum.push(name)
      else memberFor.set(name, member)
    }
    const mapped = new Set(memberFor.values())
    const missingFromManifest = [...members.entries()]
      .filter(([key, member]) => !mapped.has(member) && !allowed.has(key))
      .map(([, member]) => member)

    if (missingFromEnum.length) {
      failed = true
      console.error(`${label} ${missingFromEnum.length} Figma component(s) have no enum entry:`)
      for (const n of missingFromEnum) console.error(`  ${n}`)
      console.error(`  → run the svg-asset-converter to add them to ${enumPath}`)
    }
    if (missingFromManifest.length) {
      failed = true
      console.error(`${label} ${missingFromManifest.length} enum entr(ies) have no mapping:`)
      for (const m of missingFromManifest) console.error(`  ${m}`)
      console.error(`  → refresh ${asset.manifest} from Figma, or add the Figma name to its knownUnmapped list`)
    }

    // pascal() collapses separators, so two manifest names can land on one file
    // and the second would silently win.
    const files = new Map()
    for (const [name, nodeId] of Object.entries(items)) {
      const file = `${pascal(name)}.figma.ts`
      const taken = files.get(file)
      if (taken) {
        failed = true
        console.error(`${label} ${name} and ${taken.name} both write ${file}; rename one in ${asset.manifest}`)
        continue
      }
      files.set(file, { name, nodeId })
    }
    if (failed) continue

    plans.push({ label, outDir: join(root, platform.dir, asset.outSub), files, memberFor, platform, asset, assetName, enumPath, type, imports })
  }
}

if (failed) process.exit(1)

for (const plan of plans) {
  rmSync(plan.outDir, { recursive: true, force: true })
  mkdirSync(plan.outDir, { recursive: true })

  for (const [file, { name, nodeId }] of plan.files) {
    // A bare enum reference, not a call: every consumer takes the enum.
    const body = `// DO NOT MODIFY THIS FILE MANUALLY — generated by scripts/generate-asset-templates.mjs
// url=${plan.asset.urlToken}?node-id=${nodeId.replace(':', '-')}
// source=${plan.enumPath}
// component=${plan.type}
import figma from 'figma'

export default {
  example: figma.${plan.platform.tag}\`${plan.type}.${plan.memberFor.get(name)}\`,${
    plan.imports.length ? `\n  imports: [${plan.imports.map((i) => `'${i}'`).join(', ')}],` : ''
  }
  id: '${plan.assetName.replace(/s$/, '')}-${name}',
  metadata: { nestable: true },
}
`
    writeFileSync(join(plan.outDir, file), body)
  }

  console.log(`${plan.label} generated ${plan.files.size} templates in ${plan.platform.dir}/${plan.asset.outSub}/`)
}
