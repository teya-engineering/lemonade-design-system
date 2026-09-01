export const iconConfig = {
  multipass: true,
  plugins: [
    { name: 'preset-default', params: { overrides: { removeViewBox: false } } },
    // svgo never rewrites an existing `currentColor` (it is not in its colour tables), so the
    // Task 8 recolouring is safe by default -- this is NOT what protects it. The param is pinned
    // to its default only to stop anyone flipping it to `true`, which would convert every colour
    // INTO currentColor and destroy the flag/brand-logo artwork.
    { name: 'convertColors', params: { currentColor: false } },
  ],
}

export const artworkConfig = {
  multipass: false,
  plugins: [
    {
      name: 'preset-default',
      params: {
        overrides: {
          removeViewBox: false,
          mergePaths: false, // merging distorts multi-colour artwork
          convertPathData: false, // rounding shifts flag geometry visibly
          removeHiddenElems: false,
        },
      },
    },
  ],
}
