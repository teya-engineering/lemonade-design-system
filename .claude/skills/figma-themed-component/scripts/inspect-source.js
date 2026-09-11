// use_figma script: read-only survey of the source component set. Reports what the other scripts
// need to be configured: properties, layout, the colour bindings of one variant (including inside
// nested instances and slots, which add-themes.js skips unless told otherwise), and fonts.

const CONFIG = {
  sourceSetId: '0:0',
  // The variant to survey; the first one when null. Pick one with every optional layer visible.
  variantName: null,
};

const set = await figma.getNodeByIdAsync(CONFIG.sourceSetId);
if (!set || set.type !== 'COMPONENT_SET') throw new Error(`${CONFIG.sourceSetId} is not a component set`);
let page = set.parent;
while (page.type !== 'PAGE') page = page.parent;
await figma.setCurrentPageAsync(page);

const variant = CONFIG.variantName
  ? set.children.find((child) => child.name === CONFIG.variantName)
  : set.children[0];

const names = new Map();
async function variableName(id) {
  if (!names.has(id)) {
    const variable = await figma.variables.getVariableByIdAsync(id);
    names.set(id, variable ? variable.name : id);
  }
  return names.get(id);
}

const bindings = [];
const fonts = new Set();
async function walk(node, path, nestedIn) {
  for (const property of ['fills', 'strokes']) {
    const paints = node[property];
    if (paints === figma.mixed) {
      bindings.push({ path, property, variable: 'mixed (per-character fills)', nestedIn });
      continue;
    }
    if (!Array.isArray(paints)) continue;
    for (const paint of paints) {
      const binding = paint.boundVariables && paint.boundVariables.color;
      if (binding) bindings.push({ path, property, variable: await variableName(binding.id), nestedIn });
    }
  }
  if (node.type === 'TEXT') {
    for (const font of node.getRangeAllFontNames(0, node.characters.length)) fonts.add(`${font.family} ${font.style}`);
  }
  if ('children' in node) {
    for (const child of node.children) {
      const nested = child.type === 'INSTANCE' || child.type === 'SLOT' ? `${child.type} "${child.name}"` : nestedIn;
      await walk(child, `${path} > ${child.name}`, nested);
    }
  }
}
await walk(variant, variant.name, null);

const definitions = set.componentPropertyDefinitions;
return {
  page: page.name,
  parent: `${set.parent.type} ${set.parent.name}`,
  bounds: { x: set.x, y: set.y, width: set.width, height: set.height },
  layoutMode: set.layoutMode,
  variantCount: set.children.length,
  properties: Object.fromEntries(
    Object.entries(definitions).map(([key, definition]) => [
      key,
      definition.type === 'VARIANT' ? definition.variantOptions : definition.type,
    ]),
  ),
  surveyed: variant.name,
  bindings,
  fonts: [...fonts],
};
