import { readdir, readFile, writeFile } from 'node:fs/promises'
import { join } from 'node:path'
import { optimize } from 'svgo'
import { iconConfig, artworkConfig } from '../svgo.config.mjs'

const FAMILIES = [
  { dir: 'assets/icons', config: iconConfig },
  { dir: 'assets/flags', config: artworkConfig },
  { dir: 'assets/brand-logos', config: artworkConfig },
]

for (const { dir, config } of FAMILIES) {
  const files = (await readdir(dir)).filter((f) => f.endsWith('.svg'))
  let before = 0
  let after = 0
  for (const file of files) {
    const path = join(dir, file)
    const input = await readFile(path, 'utf8')
    const { data } = optimize(input, { path, ...config })
    before += input.length
    after += data.length
    await writeFile(path, data)
  }
  const saved = before === 0 ? 0 : Math.round((1 - after / before) * 100)
  console.log(`✓ ${dir}: ${files.length} files, ${saved}% smaller`)
}
