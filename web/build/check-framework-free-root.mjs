// The root entry must typecheck for a consumer with no React types installed: a Vue app, or
// a plain page that only wants the tokens. Both entries share component type modules, so
// rollup-dts puts the shared ones in a chunk that dist/index.d.ts imports — which is how a
// React type reaches the root without anyone writing an import there. Checking the source
// cannot see that; only the emitted declarations can.
//
//   node build/check-framework-free-root.mjs
import { readFileSync, existsSync } from 'node:fs'
import { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'

const dist = join(dirname(fileURLToPath(import.meta.url)), '..', 'dist')
const entries = ['index.d.ts', 'index.d.cts']

/** Every declaration file the entry pulls in, following the shared chunks it imports. */
function reachable(file, seen = new Set()) {
  if (seen.has(file) || !existsSync(join(dist, file))) return seen
  seen.add(file)
  const source = readFileSync(join(dist, file), 'utf8')
  const ext = file.endsWith('.d.cts') ? '.d.cts' : '.d.ts'
  for (const [, specifier] of source.matchAll(/from ['"](\.\/[^'"]+)['"]/g)) {
    reachable(specifier.replace(/^\.\//, '').replace(/\.(js|cjs|mjs)$/, ext), seen)
  }
  return seen
}

const offenders = []
for (const entry of entries) {
  if (!existsSync(join(dist, entry))) {
    console.error(`✗ ${entry} is missing — run the build first`)
    process.exit(1)
  }
  for (const file of reachable(entry)) {
    const source = readFileSync(join(dist, file), 'utf8')
    for (const [match] of source.matchAll(/(?:from|import\()\s*['"](react[^'"]*)['"]/g)) {
      offenders.push(`${file}: ${match.trim()}`)
    }
  }
}

if (offenders.length) {
  console.error('✗ the framework-free root reaches React types:')
  offenders.forEach((line) => console.error(`    ${line}`))
  console.error('\n  Move the React-typed declaration into a module only src/react/index.ts reaches.')
  process.exit(1)
}

const checked = entries.flatMap((entry) => [...reachable(entry)])
console.log(`✓ the root declarations reach no React types (${new Set(checked).size} files followed)`)
