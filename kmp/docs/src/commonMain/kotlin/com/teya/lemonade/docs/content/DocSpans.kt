package com.teya.lemonade.docs.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import com.teya.lemonade.LemonadeTheme
import com.teya.lemonade.docs.DocRoute
import com.teya.lemonade.docs.DocRouter

@Composable
internal fun List<DocSpan>.toAnnotatedString(router: DocRouter): AnnotatedString {
    val monoBackground = LemonadeTheme.colors.background.bgSubtle
    val linkColor = LemonadeTheme.colors.content.contentBrand
    val linkStyles = TextLinkStyles(
        style = SpanStyle(
            color = linkColor,
            textDecoration = TextDecoration.Underline,
        ),
    )
    return buildAnnotatedString {
        this@toAnnotatedString.forEach { span ->
            when (span) {
                is DocSpan.Plain -> append(span.text)

                is DocSpan.Strong -> withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(span.text)
                }

                is DocSpan.Emphasis -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(span.text)
                }

                is DocSpan.Mono -> withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = monoBackground,
                    ),
                ) {
                    append(span.text)
                }

                is DocSpan.Link -> withLink(
                    LinkAnnotation.Clickable(
                        tag = span.route.path,
                        styles = linkStyles,
                        linkInteractionListener = {
                            router.navigate(span.route)
                        },
                    ),
                ) {
                    append(span.text)
                }

                is DocSpan.Href -> withLink(
                    LinkAnnotation.Url(
                        url = span.url,
                        styles = linkStyles,
                    ),
                ) {
                    append(span.text)
                }
            }
        }
    }
}

/** Every route this page links to, so navigation can be checked without rendering. */
internal fun List<DocSpan>.linkedRoutes(): List<DocRoute> =
    filterIsInstance<DocSpan.Link>().map { span ->
        span.route
    }
