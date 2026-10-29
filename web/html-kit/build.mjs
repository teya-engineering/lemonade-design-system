// Packages the Lemonade HTML kit into a folder and a zip that work with no repository, no install and no
// network: unzip, open a page, edit it. The kit is
//
//   lemonade-kit.css   tokens, type scale, package components, kit.css, Figtree and every asset
//   lemonade-kit.js    kit.js, minified: the optional behaviour pages link after the stylesheet
//   gallery.html       every component with its markup
//   llms.txt           every token and what it is for: the token sections of web/llms.txt
//   assets.txt         every icon, flag, logo and badge name
//   classes.txt        every lmnd-kit- class the kit defines
//   README.md          instructions for a person
//
//   node html-kit/build.mjs [--out <dir>]
//
// Output goes to web/dist/: lemonade-html-kit/ and its zip, because web/dist is the only place a build may
// write inside the repository.
import { cp, mkdir, readFile, readdir, rm, writeFile } from 'node:fs/promises'
import { dirname, join, relative, resolve } from 'node:path'
import { fileURLToPath } from 'node:url'
import { deflateRawSync } from 'node:zlib'
import { transform } from 'esbuild'
import { optimize } from 'svgo'
import ttf2woff2 from 'wawoff2'
import { artworkConfig, iconConfig } from '../svgo.config.mjs'

const KIT = dirname(fileURLToPath(import.meta.url))
const WEB = join(KIT, '..')
const FONTS = join(WEB, '../swiftui/Sources/Lemonade/Resources/Fonts')
const NAME = 'lemonade-html-kit'

const STYLESHEETS = [
  join(WEB, 'styles/tokens.css'),
  join(WEB, 'styles/typography.css'),
  join(WEB, 'styles/components.css'),
  join(KIT, 'kit.css'),
]

const FACES = [
  { file: 'Figtree-Regular.ttf', weight: '400' },
  { file: 'Figtree-Medium.ttf', weight: '500' },
  { file: 'Figtree-SemiBold.ttf', weight: '600 700' },
]

// Glyphs kit.css draws itself (the checkbox tick), exposed as --lmnd-kit-icon-<name>.
const KIT_ICONS = ['check-small', 'minus']

// An SVG as a data URI, escaping only what a quoted url() needs: a third the size of encodeURIComponent.
const svgDataUri = (svg) => {
  if (svg.includes("'")) return `data:image/svg+xml,${encodeURIComponent(svg)}`
  const escaped = svg.replaceAll('"', "'").replace(/[%#<>\n\r]/g, (c) => encodeURIComponent(c))
  return `data:image/svg+xml,${escaped}`
}

const flagCode = (fileName) => {
  const [country, region] = fileName.split('-')
  return /^[A-Z0-9]{1,3}$/.test(region ?? '') ? `${country}-${region}` : country
}

// The Teya app's transaction status badges, copied from its shared-assets/source/vectors: the kit's own
// assets, since they aren't Lemonade's.
const BADGES = join(KIT, 'badges')

// Optimised with the package's own svgo settings: aggressive for icons, conservative for artwork.
async function readSvgs(dir, config) {
  const names = (await readdir(dir)).filter((n) => n.endsWith('.svg')).sort()
  const entries = await Promise.all(
    names.map(async (n) => {
      const path = join(dir, n)
      const { data } = optimize(await readFile(path, 'utf8'), { path, ...config })
      return [n.slice(0, -4), svgDataUri(data)]
    }),
  )
  return new Map(entries)
}

// An asset with dark artwork swaps to it under the dark theme, the way BrandLogo does natively.
function themedRules(attr, svgs) {
  const light = [...svgs].filter(([name]) => !name.endsWith('-dark'))
  const dark = light
    .filter(([name]) => svgs.has(`${name}-dark`))
    .flatMap(([name]) => {
      const rule = `[data-${attr}="${name}"]{--lmnd-kit-asset:url("${svgs.get(`${name}-dark`)}")}`
      return [
        `@media (prefers-color-scheme: dark){:root:not([data-lmnd-theme="light"]) ${rule}}`,
        `[data-lmnd-theme="dark"] ${rule}`,
      ]
    })
  return [...light.map(([name, uri]) => `[data-${attr}="${name}"]{--lmnd-kit-asset:url("${uri}")}`), ...dark]
}

let stylesheet
let script

// The kit's optional behaviour (kit.js), minified. Pages work without it.
export function kitScript() {
  script ??= readFile(join(KIT, 'kit.js'), 'utf8').then(async (source) => (await transform(source, { loader: 'js', minify: true })).code.trim())
  return script
}

// One asset per line: share/build.mjs prunes lemonade-kit.css line by line to the assets a page names.
export function kitStylesheet() {
  stylesheet ??= buildStylesheet()
  return stylesheet
}

async function buildStylesheet() {
  const [icons, flagFiles, logos, badges] = await Promise.all([
    readSvgs(join(WEB, 'assets', 'icons'), iconConfig),
    readSvgs(join(WEB, 'assets', 'flags'), artworkConfig),
    readSvgs(join(WEB, 'assets', 'brand-logos'), artworkConfig),
    readSvgs(BADGES, artworkConfig),
  ])
  // Flag files are named `GB-united-kingdom` or, for a region, `GB-SCT-scotland`; pages name a country by
  // its ISO code and a region by country and region code, `GB` and `GB-SCT`.
  const flags = new Map()
  for (const [name, uri] of flagFiles) if (!flags.has(flagCode(name))) flags.set(flagCode(name), uri)

  const fonts = await Promise.all(
    FACES.map(async ({ file, weight }) => {
      const woff2 = Buffer.from(await ttf2woff2.compress(await readFile(join(FONTS, file)))).toString('base64')
      return `@font-face{font-family:'Figtree';src:url(data:font/woff2;base64,${woff2}) format('woff2');font-weight:${weight};font-style:normal;font-display:block}`
    }),
  )
  const source = (await Promise.all(STYLESHEETS.map((path) => readFile(path, 'utf8')))).join('\n')
  const { code: css } = await transform(source, { loader: 'css', minify: true })
  const kitIcons = KIT_ICONS.map((name) => `--lmnd-kit-icon-${name}:url("${icons.get(name)}");`).join('')

  return [
    '/* Lemonade HTML kit — generated by web/html-kit/build.mjs from the Lemonade tokens. Do not edit. */',
    ...fonts,
    css.trim(),
    `:root{${kitIcons}}`,
    '/* Assets: one per line. */',
    ...[...icons].map(([name, uri]) => `[data-icon="${name}"]{--lmnd-icon:url("${uri}")}`),
    ...[...flags].map(([name, uri]) => `[data-flag="${name}"]{--lmnd-kit-asset:url("${uri}")}`),
    ...themedRules('logo', logos),
    ...themedRules('badge', badges),
    '',
  ].join('\n')
}

export async function kitPages() {
  return ['gallery.html']
}

// The token sections of web/llms.txt. The rest of that file is about the npm package — installing it,
// JavaScript, React, and component markup that serves icons as files — and the kit does each of those
// differently.
const LLMS_SECTIONS = ['Colours', 'Spacing', 'Radius', 'Size', 'Border width', 'Text styles', 'Shadows']

export async function kitLlmsTxt() {
  const source = await readFile(join(WEB, 'llms.txt'), 'utf8')
  const sections = new Map(
    source
      .split(/^(?=## )/m)
      .filter((part) => part.startsWith('## '))
      .map((part) => [part.slice(3, part.indexOf('\n')).trim(), part.trimEnd()]),
  )
  const missing = LLMS_SECTIONS.filter((title) => !sections.has(title))
  if (missing.length) throw new Error(`web/llms.txt has no section for: ${missing.join(', ')}`)
  return [
    '# Lemonade Design System — tokens',
    '',
    "Every token the kit's stylesheet carries, from the Lemonade web package.",
    'Light theme is the default; dark applies from the OS, or with `data-lmnd-theme="dark"` on `<html>`.',
    'Always prefer a semantic token over a literal value.',
    '',
    'Component markup is in gallery.html. Icon, flag, logo and badge names are in assets.txt.',
    '',
    ...LLMS_SECTIONS.map((title) => `${sections.get(title)}\n`),
  ].join('\n')
}

async function svgNames(dir) {
  return (await readdir(dir)).filter((n) => n.endsWith('.svg')).map((n) => n.slice(0, -4)).sort()
}

const iconNames = () => svgNames(join(WEB, 'assets', 'icons'))

// The names lemonade-kit.css carries, without the artwork, so an assistant can look one up cheaply.
async function assetList() {
  const seen = new Set()
  const flags = (await svgNames(join(WEB, 'assets', 'flags')))
    .filter((name) => !seen.has(flagCode(name)) && seen.add(flagCode(name)))
    .map((name) => {
      const code = flagCode(name)
      return `${code}  (${name.slice(code.length + 1).replaceAll('-', ' ')})`
    })
  const logos = (await svgNames(join(WEB, 'assets', 'brand-logos'))).filter((name) => !name.endsWith('-dark'))
  const badges = (await svgNames(BADGES)).filter((name) => !name.endsWith('-dark'))
  const section = (title, usage, names) => `## ${title}\n\n${usage}\n\n${names.map((n) => `- ${n}`).join('\n')}\n`
  return [
    '# Lemonade HTML kit — assets',
    '',
    'Every name lemonade-kit.css carries. A name not listed here does not exist.',
    '',
    section('Icons', 'Take currentColor. `<span class="lmnd-icon" data-icon="NAME" aria-hidden="true"></span>`', await iconNames()),
    section('Flags', 'By ISO code, or country and region code for a region. `<span class="lmnd-kit-flag" data-flag="CODE" aria-hidden="true"></span>`', flags),
    section(
      'Brand logos',
      'Switch to their dark artwork in dark mode where one exists. `<span class="lmnd-kit-logo" data-logo="NAME" role="img" aria-label="…"></span>`',
      logos,
    ),
    section(
      'Transaction status badges',
      "The Teya app's, for a money row's symbol (APP-FEED-ROWS). `sale-transaction-*` for sales and refunds; `money-in`, `money-out` and `transaction-*` for an account's activity. `<span class=\"lmnd-kit-symbol__status\" data-badge=\"NAME\" aria-hidden=\"true\"></span>`",
      badges,
    ),
  ].join('\n')
}

// A packaged page loses the source-file notice, which only makes sense in the repository, and the
// gallery's icon list gets the names.
async function pageForKit(page) {
  const html = (await readFile(join(KIT, page), 'utf8')).replace(/<p class="lmnd-kit-source-notice"[^>]*>.*?<\/p>\n/s, '')
  return page === 'gallery.html' ? html.replace('/*ICONS*/', JSON.stringify(await iconNames())) : html
}

const CRC_TABLE = Array.from({ length: 256 }, (_, n) => {
  let c = n
  for (let k = 0; k < 8; k++) c = c & 1 ? 0xedb88320 ^ (c >>> 1) : c >>> 1
  return c >>> 0
})

const crc32 = (buffer) => {
  let c = 0xffffffff
  for (const byte of buffer) c = CRC_TABLE[(c ^ byte) & 0xff] ^ (c >>> 8)
  return (c ^ 0xffffffff) >>> 0
}

// A plain deflate zip, so packaging needs no zip tool on the machine.
function zip(files) {
  const now = new Date()
  const time = (now.getHours() << 11) | (now.getMinutes() << 5) | (now.getSeconds() >> 1)
  const date = ((now.getFullYear() - 1980) << 9) | ((now.getMonth() + 1) << 5) | now.getDate()
  const local = []
  const central = []
  let offset = 0
  for (const { path, data } of files) {
    const name = Buffer.from(path)
    const packed = deflateRawSync(data)
    const crc = crc32(data)
    const header = Buffer.alloc(30)
    header.writeUInt32LE(0x04034b50, 0)
    header.writeUInt16LE(20, 4)
    header.writeUInt16LE(0x0800, 6)
    header.writeUInt16LE(8, 8)
    header.writeUInt16LE(time, 10)
    header.writeUInt16LE(date, 12)
    header.writeUInt32LE(crc, 14)
    header.writeUInt32LE(packed.length, 18)
    header.writeUInt32LE(data.length, 22)
    header.writeUInt16LE(name.length, 26)
    local.push(header, name, packed)

    const entry = Buffer.alloc(46)
    entry.writeUInt32LE(0x02014b50, 0)
    entry.writeUInt16LE(20, 4)
    entry.writeUInt16LE(20, 6)
    entry.writeUInt16LE(0x0800, 8)
    entry.writeUInt16LE(8, 10)
    entry.writeUInt16LE(time, 12)
    entry.writeUInt16LE(date, 14)
    entry.writeUInt32LE(crc, 16)
    entry.writeUInt32LE(packed.length, 20)
    entry.writeUInt32LE(data.length, 24)
    entry.writeUInt16LE(name.length, 28)
    entry.writeUInt32LE(offset, 42)
    central.push(entry, name)
    offset += header.length + name.length + packed.length
  }
  const directory = Buffer.concat(central)
  const end = Buffer.alloc(22)
  end.writeUInt32LE(0x06054b50, 0)
  end.writeUInt16LE(files.length, 8)
  end.writeUInt16LE(files.length, 10)
  end.writeUInt32LE(directory.length, 12)
  end.writeUInt32LE(offset, 16)
  return Buffer.concat([...local, directory, end])
}

async function filesUnder(dir) {
  const entries = await readdir(dir, { withFileTypes: true, recursive: true })
  return entries.filter((e) => e.isFile()).map((e) => join(e.parentPath, e.name)).sort()
}

async function zipFolder(dir, name, target) {
  const files = await filesUnder(dir)
  const archive = zip(
    await Promise.all(files.map(async (path) => ({ path: `${name}/${relative(dir, path)}`, data: await readFile(path) }))),
  )
  await writeFile(target, archive)
  console.log(`✓ ${target} • ${files.length} files • ${(archive.length / 1024 / 1024).toFixed(1)} MB`)
}

async function writePages(dir) {
  await mkdir(dir, { recursive: true })
  for (const page of await kitPages()) await writeFile(join(dir, page), await pageForKit(page))
}

// Every lmnd-kit- class kit.css defines, grouped by component, so a name not listed is known not to exist.
export async function classList() {
  const css = (await readFile(join(KIT, 'kit.css'), 'utf8')).replace(/\/\*[\s\S]*?\*\//g, '')
  const classes = [...new Set([...css.matchAll(/\.(lmnd-kit-[\w-]+)/g)].map((m) => m[1]))].sort()
  const blocks = new Map()
  for (const name of classes) {
    const block = name.split(/__|--/)[0]
    blocks.set(block, [...(blocks.get(block) ?? []), name])
  }
  return [
    '# Lemonade HTML kit — classes',
    '',
    'Every lmnd-kit- class the kit defines, by component. A class not listed here does not exist. The lmnd- classes',
    '(lmnd-button, lmnd-icon, lmnd-spinner, lmnd-text-*) are the Lemonade package\'s own; see gallery.html.',
    '',
    ...[...blocks].map(([block, names]) => `- ${block}: ${names.filter((n) => n !== block).join(', ') || '(no parts)'}`),
    '',
  ].join('\n')
}

async function writeReferences(dir) {
  await writeFile(join(dir, 'llms.txt'), await kitLlmsTxt())
  await writeFile(join(dir, 'assets.txt'), await assetList())
  await writeFile(join(dir, 'classes.txt'), await classList())
}

// The kit as a folder anyone can open: the gallery, the stylesheet and the references.
async function packageKit(dist, css) {
  const out = join(dist, NAME)
  await rm(out, { recursive: true, force: true })
  await writePages(out)
  await writeFile(join(out, 'lemonade-kit.css'), css)
  await writeFile(join(out, 'lemonade-kit.js'), await kitScript())
  await writeReferences(out)
  await cp(join(KIT, 'share/README.md'), join(out, 'README.md'))
  await zipFolder(out, NAME, join(dist, `${NAME}.zip`))
}

async function main(argv) {
  const outFlag = argv.indexOf('--out')
  const dist = outFlag >= 0 ? resolve(argv[outFlag + 1]) : join(WEB, 'dist')
  await packageKit(dist, await kitStylesheet())
}

if (process.argv[1] === fileURLToPath(import.meta.url)) await main(process.argv.slice(2))
