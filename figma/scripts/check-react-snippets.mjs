// Type-checks the JSX the React templates emit against the components it calls.
//
// `npm run validate` only proves Figma can run a template, and needs a token; this runs
// offline, which is why CI can depend on it.

import { execFileSync } from 'node:child_process'
import { mkdirSync, mkdtempSync, readFileSync, readdirSync, rmSync, writeFileSync } from 'node:fs'
import { dirname, join, relative } from 'node:path'
import { fileURLToPath } from 'node:url'

const root = join(dirname(fileURLToPath(import.meta.url)), '..')
const repo = join(root, '..')
const web = join(repo, 'web')

const TEXT = 'Pay "now" & <later>'

/** Figma's `figma.code` tagged template, reduced to the string it would produce. */
const tag = (strings, ...values) =>
  Object.assign(new String(strings.reduce((o, s, i) => o + s + (i < values.length ? String(values[i]) : ''), '')), {
    language: 'typescript',
  })


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
    // Figma text is arbitrary, so the fixture is hostile: a benign label let a template
    // escape quotes the JavaScript way and still pass every combination, while the snippet
    // it published did not compile.
    getString: () => TEXT,
    getEnum: (name, map) => {
      if (!(name in props)) throw new Error(`template read an unexpected enum: ${name}`)
      return map[props[name]]
    },
    getBoolean: (name) => props[name] === true,
    getSlot: () => undefined,
  }
  const body = `const figma = arguments[0];\n${source}\nreturn globalThis.__result`
  // eslint-disable-next-line no-new-func
  const result = new Function(body)({ selectedInstance: instance, code: tag })
  return String(result.example)
}

const templates = readdirSync(join(root, 'connect-react'))
  .filter((file) => file.endsWith('.figma.ts'))
  .map((file) => join(root, 'connect-react', file))

// Reporting success over nothing would make deleting a template a green PR: this is the
// only React guard CI runs, since `validate` needs a token and stays local. The config
// existing is the claim that there are templates to check.
if (!templates.length) {
  console.error(
    'figma.react.config.json declares connect-react/**/*.figma.ts, but no template matched.\n' +
      'Add one, or remove the config and its scripts.',
  )
  process.exit(1)
}

// Inside web/, so the snippets resolve react/jsx-runtime and the React types the way a
// consumer's file would. node_modules/.cache is already ignored, so a crash cannot leave
// the tree dirty and fail a later web-check.
const cache = join(web, 'node_modules', '.cache')
mkdirSync(cache, { recursive: true }) // a fresh npm ci leaves no .cache, and mkdtemp will not create it
const scratch = mkdtempSync(join(cache, 'lmnd-react-snippets-'))
let total = 0
try {
  for (const template of templates) {
    const raw = readFileSync(template, 'utf8')
    // The React templates are self-contained: shared/render.ts accepts only Kotlin and
    // Swift, so a template reaching for it would throw inside Figma and publish an empty
    // snippet. Saying so here beats the stub that used to make it appear to work.
    if (raw.includes("from '../shared/render'")) {
      console.error(
        `${relative(root, template)} imports the shared renderer, which accepts only kotlin and swift.\n` +
          'Teach shared/render.ts the language first, or keep the template self-contained.',
      )
      process.exit(1)
    }
    const source = raw
      .replace(/^import figma from 'figma'$/m, '')
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
