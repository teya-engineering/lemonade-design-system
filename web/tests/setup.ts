import '@testing-library/jest-dom/vitest'
import { cleanup } from '@testing-library/react'
import { afterEach } from 'vitest'

// Testing Library only auto-cleans under a global afterEach it can detect; Vitest
// without globals is not one, so a mounted tree would leak into the next test.
afterEach(cleanup)
