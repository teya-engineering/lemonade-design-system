---
applyTo: "svg/**,kmp/core/src/commonMain/kotlin/com/teya/lemonade/core/{LemonadeIcons,LemonadeCountryFlags,LemonadeBrandLogos}.kt,kmp/ui/src/commonMain/composeResources/drawable/gen_*.xml,kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Lemonade{Icons,CountryFlags,BrandLogos}Extension.kt,swiftui/Sources/Lemonade/Lemonade{Icons,CountryFlags,BrandLogos}.swift,swiftui/Sources/Lemonade/Resources/Assets.xcassets/**,figma/*.manifest.json,figma/connect*/{icons,flags,brand-logos}/**"
---

# Asset review guidelines

Icons, country flags and brand logos are authored as SVG in `svg/` and everything downstream is generated. The steps are in `svg/README.md`. Flag the cases below.

- **A generated file was edited by hand.** The enums, `gen_*.xml` drawables, imagesets, Code Connect templates and `*Extension.kt` files must come from the converters. If one changed without a matching change under `svg/`, or its content does not match what the converter would write, ask the author to regenerate it.
- **An SVG was added or changed without its generated output.** A new file in `svg/icons/`, `svg/flags/` or `svg/brandLogos/` needs its enum entry, drawable, imageset, web asset and Code Connect manifest entry in the same PR. For icons, the `export-icons` skill produces all of it.
- **The SVG was edited after export.** Exported SVGs are committed exactly as Figma renders them. Hand-tidied paths, added attributes or reformatting are a problem; fixes belong in Figma.
- **The SVG has more than paths.** The Android converter reads `<path>` elements only. `<rect>`, `<circle>`, `<text>`, gradients and strokes are dropped on Android while still rendering on iOS.
- **An asset was removed or renamed.** Deleting an SVG or enum entry is a binary-compatibility break. See the binary-compatibility guidelines.
- **A count in `web/tests/exports.test.ts` was not updated** after assets were added.
