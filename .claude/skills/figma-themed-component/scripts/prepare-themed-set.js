// use_figma script: clones the source component set, trims it to the variants to keep, removes
// the variant properties listed in dropProperties and the component properties nothing references
// any more, turns the colour property into Theme, and parks a pristine copy as the template
// add-themes.js clones from.
//
// The kept variants are renamed to Theme=__base. They are the only variants until the themes
// are added, and finalize-themed-set.js deletes them at the end.

const CONFIG = {
  sourceSetId: '0:0',
  name: 'Component (Themed)',
  position: { x: 0, y: 0 },
  // A variant is kept when, for every property listed, its value is in the list.
  // Keep exactly one value of the colour property: its variants become the base.
  keep: {
    '◇ Voice': ['Neutral'],
  },
  // Variant properties to remove once `keep` has pinned them to a single value.
  dropProperties: [],
  // The variant property the themes replace, or null when the source has none.
  colourProperty: '◇ Voice',
  themeProperty: '◇ Theme',
};

const BASE = '__base';

const source = await figma.getNodeByIdAsync(CONFIG.sourceSetId);
if (!source || source.type !== 'COMPONENT_SET') throw new Error(`${CONFIG.sourceSetId} is not a component set`);
let page = source.parent;
while (page.type !== 'PAGE') page = page.parent;
await figma.setCurrentPageAsync(page);

const existing = source.parent.children.find((node) => node.name === CONFIG.name);
if (existing) throw new Error(`"${CONFIG.name}" already exists (${existing.id})`);

const set = source.clone();
if (set.parent !== source.parent) source.parent.appendChild(set);
set.name = CONFIG.name;
set.x = CONFIG.position.x;
set.y = CONFIG.position.y;

for (const variant of [...set.children]) {
  const values = variant.variantProperties;
  const keep = Object.entries(CONFIG.keep).every(([property, allowed]) => allowed.includes(values[property]));
  if (!keep) variant.remove();
}
if (set.children.length === 0) throw new Error('CONFIG.keep matched no variants');

for (const property of CONFIG.dropProperties) {
  const values = new Set(set.children.map((variant) => variant.variantProperties[property]));
  if (values.size !== 1) {
    throw new Error(`Pin ${property} to one value in CONFIG.keep before dropping it, found: ${[...values].join(', ')}`);
  }
  for (const variant of set.children) {
    variant.name = variant.name.split(', ').filter((pair) => !pair.startsWith(`${property}=`)).join(', ');
  }
}

const referenced = new Set();
for (const node of set.findAll(() => true)) {
  for (const key of Object.values(node.componentPropertyReferences || {})) referenced.add(key);
}
const dropped = [];
for (const [key, definition] of Object.entries(set.componentPropertyDefinitions)) {
  if (definition.type !== 'VARIANT' && !referenced.has(key)) {
    set.deleteComponentProperty(key);
    dropped.push(key);
  }
}

if (CONFIG.colourProperty) {
  const colours = new Set(set.children.map((variant) => variant.variantProperties[CONFIG.colourProperty]));
  if (colours.size !== 1) {
    throw new Error(`Keep exactly one ${CONFIG.colourProperty} value, found: ${[...colours].join(', ')}`);
  }
  set.editComponentProperty(CONFIG.colourProperty, { name: CONFIG.themeProperty });
  const escaped = CONFIG.themeProperty.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  const pattern = new RegExp(`${escaped}=[^,]+`);
  for (const variant of set.children) {
    variant.name = variant.name.replace(pattern, `${CONFIG.themeProperty}=${BASE}`);
  }
} else {
  for (const variant of set.children) variant.name = `${variant.name}, ${CONFIG.themeProperty}=${BASE}`;
}

const template = set.clone();
if (template.parent !== set.parent) set.parent.appendChild(template);
template.name = `__template ${CONFIG.name}`;
template.x = CONFIG.position.x;
template.y = CONFIG.position.y - 10000;

return {
  setId: set.id,
  templateId: template.id,
  baseVariants: set.children.length,
  droppedProperties: dropped,
  properties: Object.fromEntries(
    Object.entries(set.componentPropertyDefinitions).map(([key, definition]) => [
      key,
      definition.type === 'VARIANT' ? definition.variantOptions : definition.type,
    ]),
  ),
};
