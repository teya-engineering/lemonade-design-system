import { defineConfig } from 'tsup'

export default defineConfig({
  // Named, so the React entry emits dist/react.* rather than dist/react/index.*, which
  // is what the ./react export subpath points at.
  entry: { index: 'src/index.ts', react: 'src/react/index.ts' },
  format: ['esm', 'cjs'],
  dts: true,
  // build-fonts.mjs and optimize-svg.mjs write dist/fonts.css and dist/assets before tsup
  // runs, and tsup's clean always empties outDir — an array of globs is cleaned in addition
  // to '**/*', not instead of it. clean-bundle-output.mjs removes tsup's own files instead.
  clean: false,
  sourcemap: true,
  // tsup derives externals from dependencies and peerDependencies, and jsx-runtime is
  // in neither. Bundling any of these ships a second copy of React, which breaks the
  // host app's hook dispatcher.
  external: ['react', 'react-dom', 'react/jsx-runtime'],
})
