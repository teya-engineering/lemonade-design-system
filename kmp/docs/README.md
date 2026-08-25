# Lemonade documentation site

The guidance site for Lemonade, written with Lemonade and compiled to the browser with
Compose Multiplatform. Deployed to GitHub Pages at
<https://saltpay.github.io/lemonade-design-system/>.

## Running it

From `kmp/`:

| Command | What it does |
|---|---|
| `./gradlew :docs:run` | Desktop window, for authoring pages against Compose Hot Reload |
| `./gradlew :docs:wasmJsBrowserDevelopmentRun` | Dev server on <http://localhost:8080/> |
| `./gradlew :docs:wasmJsBrowserDistribution` | Production bundle in `docs/build/dist/wasmJs/productionExecutable` |
| `./gradlew :docs:desktopTest` | Navigation invariants |

The desktop target exists so the prose can be written without a webpack rebuild between
edits. It is not deployed.

## How a page is written

Pages are Kotlin, not Markdown. A page is a `DocPage` built with the `docPage` DSL in
`content/DocPageScope.kt`, and rendered by `content/DocBlockRenderer.kt` through Lemonade
components.

```kotlin
internal val examplePage: DocPage = docPage(
    route = DocRoute.Example,
    title = "Example",
    description = "One sentence, shown under the title.",
) {
    p {
        +"Prose, with "
        b("emphasis")
        +", a "
        c("code span")
        +", and a "
        link(text = "link to another page", route = DocRoute.Colour)
        +"."
    }
    h2("A heading")
    table(headers = listOf("Column", "Column")) {
        row({ +"a" }, { +"b" })
    }
    code(language = CodeLanguage.Kotlin, source = """LemonadeTheme { App() }""")
    callout(voice = NoticeVoice.Warning, title = "Careful") { p("…") }
    sample { SomeLiveComposable() }
    nextSteps(DocRoute.Colour, DocRoute.Typography)
}
```

Adding a page means adding a `DocRoute`, adding the page to `docPages`, and putting the
route in `docSections`. `DocNavigationTest` fails if you miss any of the three.

Internal links take a `DocRoute` rather than a string, so a link to a page that does not
exist will not compile. That is why there is no link checker here.

## Tokens

`tokens/TokenCatalog.kt` is generated — do not edit it. It comes from the Figma exports in
the repository's `tokens/` directory via `scripts/docs-token-catalog.main.kts`, which runs
as part of `.claude/skills/generate-tokens/scripts/run-converters.sh`. Change the export,
not the page.

Colour entries reference `LemonadeLightTheme` and `LemonadeDarkTheme` directly, so a
renamed or unpublished token breaks this module's compile rather than showing a stale value.
