export const iconConfig = {
  multipass: true,
  plugins: [
    { name: 'preset-default', params: { overrides: { removeViewBox: false } } },
    // currentColor was set deliberately in Task 8; svgo must not "simplify" it away.
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
