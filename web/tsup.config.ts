import { defineConfig } from 'tsup'

export default defineConfig({
  // Named, so the React entry emits dist/react.* rather than dist/react/index.*, which
  // is what the ./react export subpath points at.
  entry: { index: 'src/index.ts', react: 'src/react/index.ts' },
  format: ['esm', 'cjs'],
  dts: true,
  clean: false, // dist/fonts is written by build-fonts.mjs before tsup runs
  sourcemap: true,
  // tsup derives externals from dependencies and peerDependencies, and jsx-runtime is
  // in neither. Bundling any of these ships a second copy of React, which breaks the
  // host app's hook dispatcher.
  external: ['react', 'react-dom', 'react/jsx-runtime'],
})
