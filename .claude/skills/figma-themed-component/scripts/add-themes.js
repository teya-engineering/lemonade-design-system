// use_figma script: adds one batch of themes to the themed set. For each theme it clones the
// template set, rebinds every colour to the theme's variables, moves the variants into the
// themed set, and names them there. Run it once per batch of about 150 new variants
// (150 / variants per theme, rounded down).
//
// Colours are mapped by what they are bound to, not by layer name:
//   Background/*  or Themed .../background     -> background
//   Border/*      or Themed .../border         -> border
//   Content/*     or Themed .../on-background  -> on-background
// Nested instances and slots are left alone unless their layer name is in recolourInstances;
// the names of the ones left alone come back in `skippedInstances`. Anything bound to a colour
// the mapping does not cover comes back in `unmapped` and is not changed.

const CONFIG = {
  setId: '0:0',
  templateId: '0:0',
  themeProperty: '◇ Theme',
  // Layer names of nested instances whose colours should follow the theme too, e.g. ['Icon'].
  recolourInstances: [],
  // A slice of find-themed-variables.js output: { 'Green Lime': { background, border, 'on-background' } }
  themes: {},
};

const BASE = '__base';
const SLOT_BY_GROUP = { Background: 'background', Border: 'border', Content: 'on-background' };

const set = await figma.getNodeByIdAsync(CONFIG.setId);
const template = await figma.getNodeByIdAsync(CONFIG.templateId);
let page = set.parent;
while (page.type !== 'PAGE') page = page.parent;
await figma.setCurrentPageAsync(page);

const existingThemes = new Set(set.children.map((variant) => variant.variantProperties[CONFIG.themeProperty]));

const variableNames = new Map();
async function slotFor(binding) {
  if (!variableNames.has(binding.id)) {
    const variable = await figma.variables.getVariableByIdAsync(binding.id);
    variableNames.set(binding.id, variable ? variable.name : '');
  }
  const name = variableNames.get(binding.id);
  const themed = name.match(/^Themed[^/]*\/.+\/(background|border|on-background)$/i);
  if (themed) return { name, slot: themed[1].toLowerCase() };
  return { name, slot: SLOT_BY_GROUP[name.split('/')[0]] || null };
}

// Layers owned by the variant itself: stops at nested instances and slots it was not told to enter.
const skippedInstances = new Set();
function ownLayers(node, out = []) {
  out.push(node);
  if ('children' in node) {
    for (const child of node.children) {
      const nested = child.type === 'INSTANCE' || child.type === 'SLOT';
      if (nested && !CONFIG.recolourInstances.includes(child.name)) skippedInstances.add(`${child.type} "${child.name}"`);
      else ownLayers(child, out);
    }
  }
  return out;
}

async function rebind(paints, variables, path, unmapped) {
  const next = [];
  for (const paint of paints) {
    const binding = paint.boundVariables && paint.boundVariables.color;
    if (!binding) {
      next.push(paint);
      continue;
    }
    const { name, slot } = await slotFor(binding);
    if (!slot) {
      unmapped.push(`${path}: ${name}`);
      next.push(paint);
      continue;
    }
    next.push(figma.variables.setBoundVariableForPaint(paint, 'color', variables[slot]));
  }
  return next;
}

const created = {};
const unmapped = [];
for (const [theme, keys] of Object.entries(CONFIG.themes)) {
  if (existingThemes.has(theme)) {
    created[theme] = 'skipped, already in the set';
    continue;
  }
  const variables = {};
  for (const [slot, key] of Object.entries(keys)) variables[slot] = await figma.variables.importVariableByKeyAsync(key);

  const batch = template.clone();
  created[theme] = 0;
  for (const variant of [...batch.children]) {
    const name = variant.name.replace(`${CONFIG.themeProperty}=${BASE}`, `${CONFIG.themeProperty}=${theme}`);
    for (const layer of ownLayers(variant)) {
      const path = `${name} > ${layer.name}`;
      if (layer.type === 'TEXT') {
        const fonts = layer.getRangeAllFontNames(0, layer.characters.length);
        for (const font of fonts) await figma.loadFontAsync(font);
      }
      if ('fills' in layer && layer.fills === figma.mixed) unmapped.push(`${path}: mixed text fills`);
      if ('fills' in layer && Array.isArray(layer.fills)) layer.fills = await rebind(layer.fills, variables, path, unmapped);
      if ('strokes' in layer && Array.isArray(layer.strokes)) layer.strokes = await rebind(layer.strokes, variables, path, unmapped);
    }
    set.appendChild(variant);
    // A variant that leaves its set is renamed "<set name>, <values>", so name it after the move.
    variant.name = name;
    created[theme]++;
  }
  // A component set deletes itself once its last variant moves out.
  if (!batch.removed) batch.remove();
}

return {
  total: set.children.length,
  created,
  unmapped: [...new Set(unmapped)].slice(0, 30),
  skippedInstances: [...skippedInstances],
};
