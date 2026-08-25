package com.teya.lemonade.docs.content

import androidx.compose.runtime.Composable
import com.teya.lemonade.core.NoticeVoice
import com.teya.lemonade.docs.DocRoute

/**
 * A run of text inside a paragraph, list item or table cell.
 *
 * [Link] takes a [DocRoute] rather than a URL, so an internal link to a page that does not exist
 * cannot be written down.
 */
internal sealed interface DocSpan {
    data class Plain(
        val text: String,
    ) : DocSpan

    data class Strong(
        val text: String,
    ) : DocSpan

    data class Emphasis(
        val text: String,
    ) : DocSpan

    data class Mono(
        val text: String,
    ) : DocSpan

    data class Link(
        val text: String,
        val route: DocRoute,
    ) : DocSpan

    data class Href(
        val text: String,
        val url: String,
    ) : DocSpan
}

internal enum class CodeLanguage(val label: String) {
    Kotlin(label = "Kotlin"),
    Swift(label = "Swift"),
    Toml(label = "TOML"),
    Html(label = "HTML"),
    Plain(label = "Text"),
}

/** One element of a page, in the order it is read. */
internal sealed interface DocBlock {
    data class Heading(
        val level: Int,
        val text: String,
    ) : DocBlock

    data class Paragraph(
        val spans: List<DocSpan>,
    ) : DocBlock

    data class Bullets(
        val items: List<List<DocSpan>>,
    ) : DocBlock

    data class Steps(
        val items: List<List<DocSpan>>,
    ) : DocBlock

    data class Table(
        val headers: List<String>,
        val rows: List<List<List<DocSpan>>>,
    ) : DocBlock

    data class Code(
        val language: CodeLanguage,
        val source: String,
    ) : DocBlock

    data class Callout(
        val voice: NoticeVoice,
        val title: String?,
        val body: List<DocBlock>,
    ) : DocBlock

    data class NextSteps(
        val routes: List<DocRoute>,
    ) : DocBlock

    /**
     * A live Lemonade rendering — a token gallery, a component specimen.
     *
     * This is the seam that makes the site a running proof of the design system rather than a
     * description of it.
     */
    data class Sample(
        val content: @Composable () -> Unit,
    ) : DocBlock
}

internal data class DocPage(
    val route: DocRoute,
    val title: String,
    val description: String,
    val blocks: List<DocBlock>,
)
