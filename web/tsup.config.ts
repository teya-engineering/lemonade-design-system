import { defineConfig } from 'tsup'

export default defineConfig({
  entry: ['src/index.ts'],
  format: ['esm', 'cjs'],
  dts: true,
  clean: false, // dist/fonts is written by build-fonts.mjs before tsup runs
  sourcemap: true,
})
