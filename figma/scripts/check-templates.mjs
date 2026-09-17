// Checks that the snippets the templates emit would compile against the code.
// `npm run validate` only proves Figma can run a template; this proves the
// Kotlin and Swift it prints resolve. Runs offline, no token needed.
//
//   node scripts/check-templates.mjs

import { readFileSync, readdirSync, existsSync } from 'node:fs'
import { dirname, join, relative } from 'node:path'
import { fileURLToPath } from 'node:url'

const root = join(dirname(fileURLToPath(import.meta.url)), '..')
const repo = join(root, '..')

const files = (dir, test) =>
  readdirSync(join(repo, dir), { recursive: true })
    .filter((f) => test(f))
    .map((f) => join(repo, dir, f))

// Index of the characters between an opening paren and its match.
const closing = (src, open) => {
  let depth = 0
  for (let i = open; i < src.length; i += 1) {
    if (src[i] === '(') depth += 1
    else if (src[i] === ')' && (depth -= 1) === 0) return i
  }
  return -1
}

// Top-level comma-separated parameters of a declaration's parameter list.
// `<` and `>` are not brackets here: `->` would unbalance them.
const splitParams = (list) => {
  const parts = []
  let depth = 0
  let current = ''
  for (const ch of list) {
    if ('([{'.includes(ch)) depth += 1
    if (')]}'.includes(ch)) depth -= 1
    if (ch === ',' && depth === 0) {
      parts.push(current)
      current = ''
    } else current += ch
  }
  parts.push(current)
  return parts.map((p) => p.replace(/@\w+(\([^)]*\))?\s*/g, '').trim()).filter(Boolean)
}

// name -> list of parameter-label lists, one per overload or constructor.
const declarations = (sources, pattern, label) => {
  const found = new Map()
  for (const file of sources) {
    const src = readFileSync(file, 'utf8')
    for (const m of src.matchAll(pattern)) {
      const open = m.index + m[0].length - 1
      const end = closing(src, open)
      if (end === -1) continue
      const labels = splitParams(src.slice(open + 1, end)).map(label).filter(Boolean)
      const name = m.groups.name ?? enclosingType(src, m.index)
      if (!name) continue
      if (!found.has(name)) found.set(name, [])
      found.get(name).push(labels)
    }
  }
  return found
}

const enclosingType = (src, at) => {
  const types = [...src.slice(0, at).matchAll(/^(?:public\s+)?(?:struct|extension)\s+(\w+)/gm)]
  return types.length ? types[types.length - 1][1] : undefined
}

// Calls a snippet makes, with the labels on their own 4-space-indented lines.
// An argument on a deeper line belongs to a nested call, and a call written on
// one line has no labels here, so it is skipped rather than misread.
const calls = (src, separator) => {
  const out = []
  for (const m of src.matchAll(/(LemonadeUi\.)?\b([A-Z]\w+)\(/g)) {
    const open = m.index + m[0].length - 1
    const end = closing(src, open)
    if (end === -1) continue
    const body = src.slice(open, end)
    const labels = [...body.matchAll(new RegExp(`\\n {4}(\\w+) ?${separator}`, 'g'))].map((l) => l[1])
    if (labels.length) out.push({ name: m[2], labels, ours: Boolean(m[1]) })
  }
  return out
}

const problems = []
const report = (file, message) => problems.push(`${relative(root, file)}: ${message}`)

const compose = files('figma/connect', (f) => f.endsWith('.figma.ts'))
const swiftui = files('figma/connect-swiftui', (f) => f.endsWith('.figma.ts'))
const shared = files('figma/shared', (f) => f.endsWith('.ts'))

// 1. Every template's `// source=` link points at a real file.
for (const file of [...compose, ...swiftui]) {
  const source = readFileSync(file, 'utf8').match(/^\/\/ source=(.+)$/m)?.[1]
  if (!source) report(file, 'has no // source= header')
  else if (!existsSync(join(repo, source))) report(file, `source ${source} does not exist`)
}

// 2. Compose imports resolve, and Lemonade names the snippet uses are imported.
const kotlinSources = files('kmp', (f) => f.endsWith('.kt') && f.includes('commonMain'))
const packages = new Map()
for (const file of kotlinSources) {
  const src = readFileSync(file, 'utf8')
  const pkg = src.match(/^package\s+([\w.]+)/m)?.[1]
  if (!pkg) continue
  const names = [
    ...src.matchAll(/^(?:public\s+)?(?:data\s+|enum\s+|sealed\s+|abstract\s+|open\s+|value\s+)*(?:class|object|interface|typealias)\s+(\w+)/gm),
    ...src.matchAll(/^(?:public\s+)?(?:@\w+\s+)*fun\s+(?:<[^>]*>\s*)?(?:LemonadeUi\.)?(\w+)\s*\(/gm),
  ].map((m) => m[1])
  for (const name of names) {
    if (!packages.has(name)) packages.set(name, new Set())
    packages.get(name).add(pkg)
  }
}
const snippetText = (src) => [...src.matchAll(/`([^`]*)`/g)].map((m) => m[1]).join('\n')
for (const file of [...compose, ...shared]) {
  const src = readFileSync(file, 'utf8')
  const imports = new Set([...src.matchAll(/'import ([\w.]+)'/g)].map((m) => m[1]))
  for (const imp of imports) {
    if (!imp.startsWith('com.teya.')) continue
    const cut = imp.lastIndexOf('.')
    if (!packages.get(imp.slice(cut + 1))?.has(imp.slice(0, cut))) report(file, `import ${imp} does not resolve`)
  }
  const text = snippetText(src)
  const used = new Set([
    ...[...text.matchAll(/LemonadeUi\.(\w+)\(/g)].map((m) => m[1]),
    ...[...text.matchAll(/\b([A-Z]\w+)\.[A-Z]/g)].map((m) => m[1]),
    ...[...text.matchAll(/\b(remember\w+|[A-Z]\w+(?:Config|Item|Properties))\(/g)].map((m) => m[1]),
  ])
  for (const name of used) {
    const pkgs = packages.get(name)
    if (!pkgs || name === 'LemonadeUi') continue
    if (![...pkgs].some((p) => imports.has(`${p}.${name}`))) report(file, `${name} is used but not imported`)
  }
}

// 3. Compose named arguments exist on some overload of the function called.
const kotlinDecls = declarations(
  kotlinSources,
  /^(?:public\s+)?(?:@\w+\s+)*(?:fun\s+(?:<[^>]*>\s*)?(?:LemonadeUi\.)?|(?:data\s+)?class\s+)(?<name>\w+)(?:<[^>]*>)?\s*\(/gm,
  (p) => p.match(/^(?:va[lr]\s+)?(\w+)\s*:/)?.[1],
)
for (const file of [...compose, ...shared]) {
  for (const call of calls(readFileSync(file, 'utf8'), '=')) {
    const overloads = kotlinDecls.get(call.name)
    if (!overloads) {
      if (call.ours) report(file, `LemonadeUi.${call.name} is not a composable in kmp/`)
      continue
    }
    if (!overloads.some((labels) => call.labels.every((l) => labels.includes(l)))) {
      report(file, `${call.name}(${call.labels.join(', ')}) matches no overload`)
    }
  }
}

// 4. Swift labelled arguments appear in declaration order on some overload.
const swiftDecls = declarations(
  files('swiftui/Sources', (f) => f.endsWith('.swift')),
  /(?:static func\s+(?<name>\w+)\s*(?:<[^>]*>)?|\binit)\s*\(/g,
  (p) => p.match(/^(\w+)(?:\s+\w+)?\s*:/)?.[1],
)
for (const file of swiftui) {
  for (const call of calls(readFileSync(file, 'utf8'), ':')) {
    const overloads = swiftDecls.get(call.name)
    if (!overloads) {
      if (call.ours) report(file, `LemonadeUi.${call.name} is not a function in swiftui/Sources`)
      continue
    }
    const fits = overloads.some((labels) => {
      const at = call.labels.map((l) => labels.indexOf(l))
      return !at.includes(-1) && at.every((v, i) => i === 0 || v > at[i - 1])
    })
    if (!fits) report(file, `${call.name}(${call.labels.join(', ')}) is out of order or matches no overload`)
  }
}

for (const p of problems) console.error(p)
console.log(problems.length ? `${problems.length} problem(s)` : 'Templates check out.')
process.exit(problems.length ? 1 : 0)
