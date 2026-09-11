// use_figma script: lists the themed palette variables this file can import, keyed by the
// Theme variant value each hue becomes ("green-lime" -> "Green Lime"). Read-only.
// Feed the result to add-themes.js in batches.

const CONFIG = {
  // 'subtle' for the pale palette, '' for the solid one.
  palette: 'subtle',
};

const collections = await figma.teamLibrary.getAvailableLibraryVariableCollectionsAsync();
const themeCollections = collections.filter((collection) => collection.name === 'Theme');
if (themeCollections.length === 0) {
  throw new Error('No "Theme" library collection. Enable the Lemonade DS - Foundations library in this file.');
}

const group = CONFIG.palette ? `/${CONFIG.palette}` : '';
const pattern = new RegExp(`^Themed[^/]*/([^/]+)${group}/(background|border|on-background)$`, 'i');
const toTitle = (slug) => slug.split('-').map((word) => word[0].toUpperCase() + word.slice(1)).join(' ');

const themes = {};
for (const collection of themeCollections) {
  const variables = await figma.teamLibrary.getVariablesInLibraryCollectionAsync(collection.key);
  for (const variable of variables) {
    const match = variable.name.match(pattern);
    if (!match) continue;
    const theme = toTitle(match[1]);
    themes[theme] = themes[theme] || {};
    themes[theme][match[2].toLowerCase()] = variable.key;
  }
}

const incomplete = Object.keys(themes).filter((theme) => Object.keys(themes[theme]).length !== 3);
return { count: Object.keys(themes).length, incomplete, themes };
