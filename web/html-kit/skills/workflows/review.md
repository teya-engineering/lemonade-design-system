# Reviewing a prototype

You review an HTML prototype of a Teya app screen against the rules in `references/guidelines/`, and
report what breaks them. You do not restyle the page or judge taste; a finding is a broken rule.

## Files

| Path | What it is |
| --- | --- |
| `references/guidelines/` | The rules. `index.md` explains the three books: `LDS-` (Lemonade), `APP-` (the Teya app), `LHK-` (prototypes) |
| `assets/gallery.html` | The correct markup for every component, to compare against |
| `assets/examples/` | Finished screens that follow the rules: settings, sales, transactions, card machines, recipients, transaction details, add recipient |
| `assets/screens/`, `references/screens/` | The app's own screens as they are in production, and how they work |
| `references/llms.txt` | Every token that exists |
| `references/assets.txt` | Every icon, flag and logo name that exists |
| `references/classes.txt` | Every `lmnd-kit-` class that exists, by component |

## Workflow

1. **Read every rule.** Read `references/guidelines/index.md` and every file it lists. A rule's exception
   list is complete: anything it doesn't list is a violation.
2. **Read the page's source**, not only its rendering. If you were given a `.standalone.html`, ignore the
   inlined stylesheet and read the markup and the page's own `<style>`.
3. **Check every rule against the page.** Also check that:
   - every `var(--lmnd-…)` the page uses appears in `references/llms.txt`;
   - every `data-icon`, `data-flag` and `data-logo` name appears in `references/assets.txt`;
   - the page links only the kit's `lemonade-kit.js`, and any script of its own is inline, loads nothing, and
     builds or styles no component (`LHK-SCRIPT`);
   - every `lmnd-kit-` class appears in `references/classes.txt`, and every component's markup matches its
     markup in `assets/gallery.html` or the closest file in `assets/examples/`.
4. **Rules decide, examples illustrate.** A finding is a broken rule. Where the page and an example differ
   but no rule says which is right, it isn't a finding. Where an example the page copies seems to break a
   rule, say so under "Questions" rather than reporting the page. When the page under review is itself
   one of the kit's examples, report its broken rules as findings like any other page's.
5. **Honour exemptions.** A comment like `<!-- LDS-CARD-PADDING exempt: … -->` at the site covers that
   rule there, if its reason holds. Report a reason that doesn't hold as a finding.
6. **Review the change, not the app.** When the page is a change to one of the app's screens (it carries a
   baseline's comment, or matches a baseline in `assets/screens/`), compare it with that baseline. Findings
   are about what the change added or altered. A rule the baseline already breaks, marked "as in
   production", is not a finding: list it under "Already in production" instead, unless the change made it
   worse.

## Report

Start with the line "Reviewed with lemonade-prototype {{VERSION}}". Then lead with the verdict: how many findings, or that the page follows every rule. Then one row per finding,
most serious first:

| Rule | Where | What breaks it | Fix |
| --- | --- | --- | --- |
| `LDS-CARD-SURFACE` | Payouts, the page | A white screen holding grey `--elevated` cards | `lmnd-kit-screen` and default `lmnd-kit-card`s |

"Where" names the screen and the element a reader can find. "Fix" is the change, in kit markup. Don't
list rules the page follows. Under "Not checked", name any rule you couldn't judge from the source, such
as how the page renders, unless you rendered it. Under "Questions", note any place a rule and an example
disagree. Under "Already in production", list the baseline's own faults the change kept, one line each,
so they're known without drowning the change's findings. Offer to apply the fixes.
