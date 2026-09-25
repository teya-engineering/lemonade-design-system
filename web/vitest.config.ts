import { defineConfig } from 'vitest/config'

export default defineConfig({
  test: {
    // Node is the default, and it has no document — a component test cannot render
    // without this. The manifest and token tests are plain assertions and do not care.
    environment: 'jsdom',
    setupFiles: ['./tests/setup.ts'],
  },
})
