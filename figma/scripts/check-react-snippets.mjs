// Checks that the JSX the React templates emit compiles against the components it calls.
// `npm run validate` only proves Figma can run a template; this renders every property
// combination and type-checks the result, which is what catches a prop the component does
// not take, an enum value spelled the platform's way, or a placeholder that is not a valid
// expression. Runs offline, no token needed.
//
//   node scripts/check-react-snippets.mjs
//
// The Kotlin and Swift equivalent is check-templates.mjs; that one parses declarations,
// this one hands the snippets to tsc, because the web package has a compiler already.

import { execFileSync } from 'node:child_process'
import { mkdtempSync, readFileSync, readdirSync, rmSync, writeFileSync } from 'node:fs'
import { dirname, join, relative } from 'node:path'
import { fileURLToPath } from 'node:url'

const root = join(dirname(fileURLToPath(import.meta.url)), '..')
const repo = join(root, '..')
const web = join(repo, 'web')

/** Figma's tagged template, reduced to the string it would produce. */
const tag = (strings, ...values) =>
  Object.assign(new String(strings.reduce((o, s, i) => o + s + (i < values.length ? String(values[i]) : ''), '')), {
    language: 'typescript',
  })

/** The subset of shared/render.ts a template can reach with no Figma document behind it. */
const renderer = (instance, t) => {
  const language = t``.language
  if (language !== 'typescript') throw new Error(`expected typescript, got ${language}`)
  const quote = (value) =>
    String(value ?? '')
      .replace(/\\/g, '\\\\')
      .replace(/"/g, '\\"')
      .replace(/\r?\n/g, '\\n')
  // No document, so no slot has connected instances: every icon prop takes its placeholder,
  // which is the branch a published snippet hits anyway while web has no asset templates.
  return { imports: new Set(), quote, snippets: () => undefined, slot: () => '{}', slotIcon: () => undefined }
}

/**
 * Every combination of the enum and boolean properties a template reads. Derived from the
 * template's own `getEnum` maps, so a vocabulary added in Figma is covered without editing
 * this script.
 */
function combinations(source) {
  const enums = [...source.matchAll(/getEnum\('([^']+)',\s*\{([^}]*)\}/g)].map(([, name, body]) => ({
    name,
    keys: [...body.matchAll(/(?:'([^']+)'|(\w+)):/g)].map((m) => m[1] ?? m[2]),
  }))
  const booleans = [...source.matchAll(/getBoolean\('([^']+)'\)/g)].map(([, name]) => ({ name, keys: [true, false] }))
  return [...enums, ...booleans].reduce(
    (rows, { name, keys }) => rows.flatMap((row) => keys.map((key) => ({ ...row, [name]: key }))),
    [{}],
  )
}

function render(source, props) {
  const instance = {
    getString: () => 'Add item',
    getEnum: (name, map) => {
      if (!(name in props)) throw new Error(`template read an unexpected enum: ${name}`)
      return map[props[name]]
    },
    getBoolean: (name) => props[name] === true,
    getSlot: () => undefined,
  }
  const body = `const figma = arguments[0]; const renderer = arguments[1];\n${source}\nreturn globalThis.__result`
  // eslint-disable-next-line no-new-func
  const result = new Function(body)({ selectedInstance: instance, typescript: tag }, renderer)
  return String(result.example)
}

const templates = readdirSync(join(root, 'connect-react'))
  .filter((file) => file.endsWith('.figma.ts'))
  .map((file) => join(root, 'connect-react', file))

if (!templates.length) {
  console.log('No React templates to check.')
  process.exit(0)
}

// Inside web/, so the snippets resolve react/jsx-runtime and the React types the way a
// consumer's file would. node_modules/.cache is already ignored, so a crash cannot leave
// the tree dirty and fail a later web-check.
const scratch = mkdtempSync(join(web, 'node_modules', '.cache', 'lmnd-react-snippets-'))
let total = 0
try {
  for (const template of templates) {
    const raw = readFileSync(template, 'utf8')
    const source = raw
      .replace(/^import figma from 'figma'$/m, '')
      .replace(/^import \{ renderer \} from '\.\.\/shared\/render'$/m, '')
      .replace(/^export default /m, 'globalThis.__result = ')

    const imports = [...raw.matchAll(/"import \{ ([^}]+)\} from '([^']+)'"/g)].map(([, names, from]) => ({
      names: names.trim(),
      from,
    }))
    if (!imports.length) throw new Error(`${relative(root, template)} declares no imports to resolve the snippet with`)
    // The snippet imports from the published package; the checker points those at the
    // source, so it verifies the component in this checkout rather than an installed copy.
    const header = imports
      .map(({ names, from }) =>
        from.startsWith('@teya/lemonade-mobile-ds')
          ? `import { ${names} } from '${join(web, 'src', from.endsWith('/react') ? 'react/index' : 'index')}'`
          : `import { ${names} } from '${from}'`,
      )
      .join('\n')

    const cases = combinations(raw).map((props, index) => `export const case${index} = (\n${render(source, props)}\n)`)
    total += cases.length
    const file = join(scratch, `${relative(root, template).replace(/[/\\.]/g, '_')}.tsx`)
    writeFileSync(file, `${header}\n\n${cases.join('\n\n')}\n`)
  }

  execFileSync(
    'npx',
    [
      'tsc',
      '--noEmit',
      '--jsx',
      'react-jsx',
      '--strict',
      '--esModuleInterop',
      '--moduleResolution',
      'bundler',
      '--module',
      'esnext',
      '--target',
      'es2022',
      '--skipLibCheck',
      ...readdirSync(scratch).map((file) => join(scratch, file)),
    ],
    { cwd: web, stdio: 'inherit' },
  )
  console.log(`React snippets check out (${total} combinations across ${templates.length} template(s)).`)
} finally {
  rmSync(scratch, { recursive: true, force: true })
}
