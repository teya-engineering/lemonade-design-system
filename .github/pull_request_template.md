# Summary
<!--Please provide a short summary, describing the changes made by the PR if necessary. -->

## Figma component
<!-- Provide figma link (if any) to the component, it makes it easy for reviewers to find it -->

## Assets
<!-- Added or changed an icon, flag or brand logo? Follow svg/README.md. Otherwise delete this section. -->

## Screenshot
<!-- Provide at least one image of your change if applicable -->
<!-- <img src="drawing.jpg" alt="drawing" width="200"/> -->
<!-- You can define the width/height to resize the image if needed -->

## API Dump
<!--
Required when any kmp/<module>/api/*.api or *.klib.api file changed.
Run `.claude/skills/binary-compatibility/scripts/bcv-check.sh --ci` to get the verdict.
If nothing in api/ changed, write "No public API changes" and delete the rest.
-->

**Verdict:** <!-- NO_CHANGES / ADDITIONS_ONLY / BREAKING -->

**Changes that are not a concern, and why:**
<!--
- Interface addition — the interface is local-only, no external implementers.
- Enum entry addition — config enum; the component owns the `when`.
- `@Deprecated(HIDDEN)` overload — the `-` line is only the `synthetic` flag,
  the JVM descriptor is unchanged, so already-compiled consumers keep linking.
-->

**Genuine breaking changes (if any):**
<!--
List each one, who needs to approve (@caiqueslp / @williankl / @DevSrSouza),
and why the break is acceptable. Leave empty if the verdict is not BREAKING.
-->
