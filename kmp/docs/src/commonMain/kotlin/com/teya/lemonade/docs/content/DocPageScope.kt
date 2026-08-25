package com.teya.lemonade.docs.content

import androidx.compose.runtime.Composable
import com.teya.lemonade.core.NoticeVoice
import com.teya.lemonade.docs.DocRoute

/**
 * Builds a page. The shape deliberately reads close to the Markdown it replaces, so porting prose
 * is transcription rather than translation.
 */
internal fun docPage(
    route: DocRoute,
    title: String,
    description: String,
    build: DocPageScope.() -> Unit,
): DocPage {
    val scope = DocPageScope()
    scope.build()
    return DocPage(
        route = route,
        title = title,
        description = description,
        blocks = scope.blocks(),
    )
}

internal class DocPageScope {
    private val blocks: MutableList<DocBlock> = mutableListOf()

    fun blocks(): List<DocBlock> = blocks.toList()

    fun h2(text: String) {
        blocks += DocBlock.Heading(level = 2, text = text)
    }

    fun h3(text: String) {
        blocks += DocBlock.Heading(level = 3, text = text)
    }

    fun h4(text: String) {
        blocks += DocBlock.Heading(level = 4, text = text)
    }

    fun p(build: DocSpanScope.() -> Unit) {
        blocks += DocBlock.Paragraph(spans = spansOf(build = build))
    }

    fun p(text: String) {
        blocks += DocBlock.Paragraph(spans = listOf(DocSpan.Plain(text = text)))
    }

    fun bullets(build: DocListScope.() -> Unit) {
        blocks += DocBlock.Bullets(items = itemsOf(build = build))
    }

    fun steps(build: DocListScope.() -> Unit) {
        blocks += DocBlock.Steps(items = itemsOf(build = build))
    }

    fun table(
        headers: List<String>,
        build: DocTableScope.() -> Unit,
    ) {
        val scope = DocTableScope()
        scope.build()
        blocks += DocBlock.Table(headers = headers, rows = scope.rows())
    }

    fun code(
        language: CodeLanguage,
        source: String,
    ) {
        blocks += DocBlock.Code(language = language, source = source.trimIndent())
    }

    fun callout(
        voice: NoticeVoice,
        title: String? = null,
        build: DocPageScope.() -> Unit,
    ) {
        val scope = DocPageScope()
        scope.build()
        blocks += DocBlock.Callout(voice = voice, title = title, body = scope.blocks())
    }

    fun nextSteps(vararg routes: DocRoute) {
        blocks += DocBlock.NextSteps(routes = routes.toList())
    }

    fun sample(content: @Composable () -> Unit) {
        blocks += DocBlock.Sample(content = content)
    }
}

internal class DocListScope {
    private val items: MutableList<List<DocSpan>> = mutableListOf()

    fun items(): List<List<DocSpan>> = items.toList()

    fun item(build: DocSpanScope.() -> Unit) {
        items += spansOf(build = build)
    }

    fun item(text: String) {
        items += listOf(DocSpan.Plain(text = text))
    }
}

internal class DocTableScope {
    private val rows: MutableList<List<List<DocSpan>>> = mutableListOf()

    fun rows(): List<List<List<DocSpan>>> = rows.toList()

    fun row(vararg cells: DocSpanScope.() -> Unit) {
        rows += cells.map { cell ->
            spansOf(build = cell)
        }
    }
}

internal class DocSpanScope {
    private val spans: MutableList<DocSpan> = mutableListOf()

    fun spans(): List<DocSpan> = spans.toList()

    operator fun String.unaryPlus() {
        spans += DocSpan.Plain(text = this)
    }

    fun b(text: String) {
        spans += DocSpan.Strong(text = text)
    }

    fun i(text: String) {
        spans += DocSpan.Emphasis(text = text)
    }

    fun c(text: String) {
        spans += DocSpan.Mono(text = text)
    }

    fun link(
        text: String,
        route: DocRoute,
    ) {
        spans += DocSpan.Link(text = text, route = route)
    }

    fun href(
        text: String,
        url: String,
    ) {
        spans += DocSpan.Href(text = text, url = url)
    }
}

private fun spansOf(build: DocSpanScope.() -> Unit): List<DocSpan> {
    val scope = DocSpanScope()
    scope.build()
    return scope.spans()
}

private fun itemsOf(build: DocListScope.() -> Unit): List<List<DocSpan>> {
    val scope = DocListScope()
    scope.build()
    return scope.items()
}
