import { readdir, rm } from 'node:fs/promises'
import { join } from 'node:path'

/**
 * Removes the files tsup writes, and only those.
 *
 * tsup's own `clean` always empties the whole output directory — an array of globs is
 * cleaned in addition to `**` + `/*`, not instead of it — which would take dist/fonts.css
 * and dist/assets with it, since those are written before tsup runs. Left uncleaned,
 * though, every past build's shared declaration chunk stays behind and ships, because
 * package.json's files[] takes all of dist.
 */
const DIST = new URL('../dist/', import.meta.url).pathname
const BUNDLE = /\.(js|cjs|mjs|d\.ts|d\.cts|map)$/

const entries = await readdir(DIST, { withFileTypes: true }).catch(() => [])
const stale = entries.filter((entry) => entry.isFile() && BUNDLE.test(entry.name))

await Promise.all(stale.map((entry) => rm(join(DIST, entry.name))))
console.log(`✓ cleared ${stale.length} bundle file${stale.length === 1 ? '' : 's'} from dist`)
