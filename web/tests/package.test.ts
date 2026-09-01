import { describe, expect, it } from 'vitest'
import pkg from '../package.json'

/** Every file path in an exports map, at any nesting depth. */
function exportedPaths(node: unknown, found: string[] = []): string[] {
  if (typeof node === 'string') {
    found.push(node)
  } else if (node && typeof node === 'object') {
    for (const value of Object.values(node as Record<string, unknown>)) {
      exportedPaths(value, found)
    }
  }
  return found
}

// The exports map and the files array are edited by different PRs as this package
// grows, and they drift apart silently: a subpath can be exported but left out of
// files, in which case it is simply absent from the published tarball and the
// consumer gets a module-not-found. Nothing else in the build catches that.
describe('package manifest', () => {
  it('packages every path it exports', () => {
    const targets = exportedPaths(pkg.exports)
    expect(targets.length).toBeGreaterThan(0)
    for (const target of targets) {
      const top = target.replace(/^\.\//, '').split('/')[0]
      expect(pkg.files, `${target} is exported but missing from "files"`).toContain(top)
    }
  })

  it('declares no runtime dependencies', () => {
    // v0 is framework-agnostic on purpose: a Vue app, a plain HTML prototype or an
    // existing React app must all be able to consume it. Anything that appears here
    // becomes a requirement for every one of them.
    expect(pkg).not.toHaveProperty('dependencies')
    expect(pkg).not.toHaveProperty('peerDependencies')
  })

  it('pins the Node floor that CI reproduces', () => {
    expect(pkg.engines.node).toBe('>=20')
  })
})
