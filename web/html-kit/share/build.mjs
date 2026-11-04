// Turns a kit page into one self-contained HTML file: the linked lemonade-kit.css goes inline, keeping
// only the icons, flags, logos and badges the page names. The result opens anywhere — an email attachment, a
// chat, a phone — with nothing beside it.
//
//   node build.mjs <page.html> [more.html ...]
//
// Each page is written next to itself as <name>.standalone.html. Needs Node 20 or later and nothing else.
import { readFile, writeFile } from 'node:fs/promises'
import { dirname, resolve } from 'node:path'
import { fileURLToPath } from 'node:url'

const LINK = /<link\b[^>]*\bhref="([^"]*lemonade-kit\.css)"[^>]*>/
const SCRIPT = /<script\b[^>]*\bsrc="([^"]*lemonade-kit\.js)"[^>]*><\/script>/
const ASSET = /data-(icon|flag|logo|badge)="([a-zA-Z0-9-]+)"/g

// lemonade-kit.css carries one asset per line, so a page keeps exactly the lines it names.
const assetOf = (line) => line.match(/\[data-(icon|flag|logo|badge)="([a-zA-Z0-9-]+)"\]/)

// The kit's script, when the page links it and it's given, goes inline too. Without it the link stays.
export function inlinePage(html, css, js) {
  if (!LINK.test(html)) throw new Error('link lemonade-kit.css with <link rel="stylesheet" href="lemonade-kit.css">')

  // The gallery lists every icon from script, so it keeps them all.
  const keepAll = html.includes('data-kit-all-assets')
  const used = new Set([...html.matchAll(ASSET)].map(([, kind, name]) => `${kind}:${name}`))
  const known = new Set()
  const kept = css.split('\n').filter((line) => {
    const asset = assetOf(line)
    if (!asset) return true
    known.add(`${asset[1]}:${asset[2]}`)
    return keepAll || used.has(`${asset[1]}:${asset[2]}`)
  })

  const unknown = [...used].filter((asset) => !known.has(asset))
  if (unknown.length) {
    throw new Error(unknown.map((asset) => `unknown ${asset.replace(':', ' "')}"`).join('\n'))
  }
  const styled = html.replace(LINK, () => `<style>\n${kept.join('\n')}\n</style>`)
  return js === undefined ? styled : styled.replace(SCRIPT, () => `<script>\n${js.replaceAll('</script', '<\\/script')}\n</script>`)
}

async function main(pages) {
  if (!pages.length) {
    console.error('usage: node build.mjs <page.html> [more.html ...]')
    process.exit(1)
  }
  let failed = false
  for (const page of pages) {
    const source = resolve(page)
    try {
      const html = await readFile(source, 'utf8')
      const css = await readFile(resolve(dirname(source), html.match(LINK)?.[1] ?? 'lemonade-kit.css'), 'utf8')
      const scriptPath = html.match(SCRIPT)?.[1]
      const js = scriptPath ? await readFile(resolve(dirname(source), scriptPath), 'utf8') : undefined
      const target = source.replace(/\.html$/, '.standalone.html')
      await writeFile(target, inlinePage(html, css, js))
      console.log(`✓ ${target}`)
    } catch (error) {
      failed = true
      console.error(`✗ ${page}\n  ${error.message.replaceAll('\n', '\n  ')}`)
    }
  }
  if (failed) process.exit(1)
}

if (process.argv[1] === fileURLToPath(import.meta.url)) await main(process.argv.slice(2))
