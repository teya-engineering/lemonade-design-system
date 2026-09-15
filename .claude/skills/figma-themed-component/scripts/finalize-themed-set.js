// use_figma script: deletes the __base variants and the template, lays the variants out as a
// grid, and sets the description. The first cell of the grid is the set's default variant.

const CONFIG = {
  setId: '0:0',
  templateId: '0:0',
  description: '',
  // Outer property first. Each lists its values in display order; the first combination of
  // rows x columns is the top-left cell, which Figma uses as the default variant.
  rows: [
    ['◇ Theme', ['Yellow', 'Amber']],
  ],
  columns: [
    ['↕ Size', ['Small', 'Medium', 'Large']],
  ],
  // Used only when the set is not a GRID auto-layout.
  cell: { width: 104, height: 104, padding: 32 },
};

const BASE = '__base';

const set = await figma.getNodeByIdAsync(CONFIG.setId);
let page = set.parent;
while (page.type !== 'PAGE') page = page.parent;
await figma.setCurrentPageAsync(page);

const template = await figma.getNodeByIdAsync(CONFIG.templateId);
if (template && !template.removed) template.remove();
for (const variant of [...set.children]) {
  if (Object.values(variant.variantProperties).includes(BASE)) variant.remove();
}

const combos = (axes) => axes.reduce(
  (acc, [property, values]) => acc.flatMap((combo) => values.map((value) => ({ ...combo, [property]: value }))),
  [{}],
);
const rows = combos(CONFIG.rows);
const columns = combos(CONFIG.columns);
const matches = (variant, combo) => Object.entries(combo).every(([property, value]) => variant.variantProperties[property] === value);

const variants = [...set.children];
const placement = variants.map((variant) => ({
  variant,
  row: rows.findIndex((combo) => matches(variant, combo)),
  column: columns.findIndex((combo) => matches(variant, combo)),
}));
const unplaced = placement.filter(({ row, column }) => row < 0 || column < 0).map(({ variant }) => variant.name);
if (unplaced.length) throw new Error(`No grid cell for: ${unplaced.slice(0, 5).join(' | ')}`);

if (set.layoutMode === 'GRID') {
  // Park every variant below the grid first so each target cell is free when it is assigned.
  set.gridRowCount = rows.length + Math.ceil(variants.length / columns.length);
  set.gridColumnCount = Math.max(set.gridColumnCount, columns.length);
  placement.forEach(({ variant }, index) => {
    variant.setGridChildPosition(rows.length + Math.floor(index / columns.length), index % columns.length);
  });
  for (const { variant, row, column } of placement) variant.setGridChildPosition(row, column);
  set.gridRowCount = rows.length;
  set.gridColumnCount = columns.length;
} else {
  const { width, height, padding } = CONFIG.cell;
  for (const { variant, row, column } of placement) {
    variant.x = padding + column * width + (width - variant.width) / 2;
    variant.y = padding + row * height + (height - variant.height) / 2;
  }
  set.resizeWithoutConstraints(padding * 2 + columns.length * width, padding * 2 + rows.length * height);
}

placement.sort((a, b) => a.row - b.row || a.column - b.column);
placement.forEach(({ variant }, index) => set.insertChild(index, variant));
if (CONFIG.description) set.description = CONFIG.description;

const definitions = set.componentPropertyDefinitions;
return {
  variants: set.children.length,
  expected: rows.length * columns.length,
  size: [set.width, set.height],
  defaultVariant: set.children[0].name,
  gridTracks: set.layoutMode === 'GRID'
    ? [...new Set([...set.gridRowSizes, ...set.gridColumnSizes].map((track) => track.type))]
    : null,
  properties: Object.fromEntries(
    Object.entries(definitions).map(([key, definition]) => [
      key,
      definition.type === 'VARIANT' ? definition.variantOptions : definition.type,
    ]),
  ),
};
