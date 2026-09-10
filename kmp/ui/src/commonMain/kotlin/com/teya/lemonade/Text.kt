@file:Suppress("TooManyFunctions")

package com.teya.lemonade

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import com.teya.lemonade.core.LemonadeTextStyle

/**
 * Natural line-height multiplier for the font family.
 *
 * Derived from font metrics: (ascender - descender + lineGap) / unitsPerEm
 * = (950 - (-250) + 0) / 1000 = 1.20
 */
private const val NATURAL_LINE_HEIGHT_RATIO: Float = 1.20f

/**
 * Displays styled text using the Lemonade Design System typography tokens.
 *
 * @param text text to show
 * @param fontSize font size override; when [TextUnit.Unspecified] the size from [textStyle] is used
 * @param modifier [Modifier] applied to the text layout
 * @param textStyle Lemonade typography token to apply
 * @param textAlign alignment of the text within its container
 * @param color text color; when [Color.Unspecified] the color from [textStyle] is used
 * @param overflow how text overflow is handled
 * @param maxLines maximum number of lines to show
 * @param minLines minimum number of lines to show
 * @param autoSize auto-sizing configuration for the text
 * @param onTextLayout callback run with the [TextLayoutResult] once the text layout is computed
 * @param lineSpacing extra space added on top of the font's natural line height; it and the
 *   resolved font size must both be in sp
 */
@Composable
public fun LemonadeUi.Text(
    text: String,
    fontSize: TextUnit = TextUnit.Unspecified,
    modifier: Modifier = Modifier,
    textStyle: LemonadeTextStyle = LocalTextStyles.current,
    textAlign: TextAlign = TextAlign.Unspecified,
    color: Color = LocalColors.current.content.contentPrimary,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    autoSize: TextAutoSize? = null,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    lineSpacing: TextUnit = TextUnit.Unspecified,
) {
    val finalText = if (textStyle == LocalTypographies.current.bodyXSmallOverline) {
        text.uppercase()
    } else {
        text
    }

    BasicText(
        text = finalText,
        modifier = modifier,
        overflow = overflow,
        maxLines = maxLines,
        minLines = minLines,
        autoSize = autoSize,
        onTextLayout = onTextLayout,
        style = textStyle.resolveStyle(
            fontSize = fontSize,
            color = color,
            textAlign = textAlign,
            lineSpacing = lineSpacing,
        ),
    )
}

/**
 * Binary-compatibility shim for the pre-`lineSpacing` signature.
 *
 * Adding the `lineSpacing` value-class parameter re-mangled the symbol
 * (`Text-Zb-9Jy0` → `Text-hCnJeNg`), so consumers compiled against the old
 * symbol would `NoSuchMethodError` at runtime. This HIDDEN overload keeps the
 * exact old parameter list, regenerating the `Text-Zb-9Jy0` binary symbol.
 */
@Deprecated(
    message = "Use the overload with a lineSpacing parameter.",
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.Text(
    text: String,
    fontSize: TextUnit = TextUnit.Unspecified,
    modifier: Modifier = Modifier,
    textStyle: LemonadeTextStyle = LocalTextStyles.current,
    textAlign: TextAlign = TextAlign.Unspecified,
    color: Color = LocalColors.current.content.contentPrimary,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    autoSize: TextAutoSize? = null,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
): Unit =
    Text(
        text = text,
        fontSize = fontSize,
        modifier = modifier,
        textStyle = textStyle,
        textAlign = textAlign,
        color = color,
        overflow = overflow,
        maxLines = maxLines,
        minLines = minLines,
        autoSize = autoSize,
        onTextLayout = onTextLayout,
        lineSpacing = TextUnit.Unspecified,
    )

/**
 * Displays styled [AnnotatedString] text using the Lemonade Design System typography tokens.
 *
 * Use this overload to render text with inline styling such as the output of
 * [String.toLemonadeMarkdown].
 *
 * @param text [AnnotatedString] to show
 * @param fontSize font size override; when [TextUnit.Unspecified] the size from [textStyle] is used
 * @param modifier [Modifier] applied to the text layout
 * @param textStyle Lemonade typography token to apply
 * @param textAlign alignment of the text within its container
 * @param color text color; when [Color.Unspecified] the color from [textStyle] is used
 * @param overflow how text overflow is handled
 * @param maxLines maximum number of lines to show
 * @param minLines minimum number of lines to show
 * @param autoSize auto-sizing configuration for the text
 * @param onTextLayout callback run with the [TextLayoutResult] once the text layout is computed
 * @param lineSpacing extra space added on top of the font's natural line height; it and the
 *   resolved font size must both be in sp
 */
@Composable
public fun LemonadeUi.Text(
    text: AnnotatedString,
    fontSize: TextUnit = TextUnit.Unspecified,
    modifier: Modifier = Modifier,
    textStyle: LemonadeTextStyle = LocalTextStyles.current,
    textAlign: TextAlign = TextAlign.Unspecified,
    color: Color = LocalColors.current.content.contentPrimary,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    autoSize: TextAutoSize? = null,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    lineSpacing: TextUnit = TextUnit.Unspecified,
) {
    BasicText(
        text = text,
        modifier = modifier,
        overflow = overflow,
        maxLines = maxLines,
        minLines = minLines,
        autoSize = autoSize,
        onTextLayout = onTextLayout,
        style = textStyle.resolveStyle(
            fontSize = fontSize,
            color = color,
            textAlign = textAlign,
            lineSpacing = lineSpacing,
        ),
    )
}

/**
 * Binary-compatibility shim for the pre-`lineSpacing` [AnnotatedString] signature.
 *
 * See the [String] shim above — keeps the old `Text-Zb-9Jy0` symbol alive for
 * consumers compiled before the `lineSpacing` parameter was added.
 */
@Deprecated(
    message = "Use the overload with a lineSpacing parameter.",
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.Text(
    text: AnnotatedString,
    fontSize: TextUnit = TextUnit.Unspecified,
    modifier: Modifier = Modifier,
    textStyle: LemonadeTextStyle = LocalTextStyles.current,
    textAlign: TextAlign = TextAlign.Unspecified,
    color: Color = LocalColors.current.content.contentPrimary,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    autoSize: TextAutoSize? = null,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
): Unit =
    Text(
        text = text,
        fontSize = fontSize,
        modifier = modifier,
        textStyle = textStyle,
        textAlign = textAlign,
        color = color,
        overflow = overflow,
        maxLines = maxLines,
        minLines = minLines,
        autoSize = autoSize,
        onTextLayout = onTextLayout,
        lineSpacing = TextUnit.Unspecified,
    )

/**
 * Displays styled text using a raw Compose [TextStyle].
 *
 * Use this overload when you need full control over the text style rather than
 * using Lemonade typography tokens.
 *
 * @param text text to show
 * @param modifier [Modifier] applied to the text layout
 * @param textStyle Compose [TextStyle] to apply
 * @param overflow how text overflow is handled
 * @param maxLines maximum number of lines to show
 * @param minLines minimum number of lines to show
 * @param autoSize auto-sizing configuration for the text
 * @param onTextLayout callback run with the [TextLayoutResult] once the text layout is computed
 */
@Composable
public fun LemonadeUi.Text(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    autoSize: TextAutoSize? = null,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = textStyle,
        overflow = overflow,
        maxLines = maxLines,
        minLines = minLines,
        autoSize = autoSize,
        onTextLayout = onTextLayout,
    )
}

/**
 * Displays styled [AnnotatedString] text using a raw Compose [TextStyle].
 *
 * Use this overload when you need full control over the text style and want to
 * render an [AnnotatedString] with inline styling.
 *
 * @param text [AnnotatedString] to show
 * @param modifier [Modifier] applied to the text layout
 * @param textStyle Compose [TextStyle] to apply
 * @param overflow how text overflow is handled
 * @param maxLines maximum number of lines to show
 * @param minLines minimum number of lines to show
 * @param autoSize auto-sizing configuration for the text
 * @param onTextLayout callback run with the [TextLayoutResult] once the text layout is computed
 */
@Composable
public fun LemonadeUi.Text(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    autoSize: TextAutoSize? = null,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = textStyle,
        overflow = overflow,
        maxLines = maxLines,
        minLines = minLines,
        autoSize = autoSize,
        onTextLayout = onTextLayout,
    )
}

@Composable
private fun LemonadeTextStyle.resolveStyle(
    fontSize: TextUnit,
    color: Color,
    textAlign: TextAlign,
    lineSpacing: TextUnit,
): TextStyle {
    val base = textStyle
    val resolvedFontSize = if (fontSize != TextUnit.Unspecified) {
        fontSize
    } else {
        base.fontSize
    }
    val resolvedColor = if (color != Color.Unspecified) {
        color
    } else {
        base.color
    }
    val resolvedLineHeight = if (lineSpacing.isSpecified) {
        require(lineSpacing.type == TextUnitType.Sp && resolvedFontSize.type == TextUnitType.Sp) {
            "lineSpacing and fontSize must be specified in sp"
        }
        (resolvedFontSize.value * NATURAL_LINE_HEIGHT_RATIO + lineSpacing.value).sp
    } else {
        base.lineHeight
    }
    return base.copy(
        fontSize = resolvedFontSize,
        lineHeight = resolvedLineHeight,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None,
        ),
        color = resolvedColor,
        textAlign = textAlign,
    )
}

/**
 * Inline style markers for Lemonade text formatting.
 *
 * Style markers use symmetric delimiters:
 * - `**text**` for [SemiBold]
 * - `***text***` for [Bold]
 * - `__text__` for [Underline]
 * - `___text___` for [StrikeThrough]
 * - `~~text~~` for [Italic]
 *
 * Color markers are resolved dynamically using `{color-name}text{/color-name}` syntax,
 * where `color-name` maps to a semantic content color token (e.g. `critical`, `positive`,
 * `info`, `caution`, `brand`, `secondary`, `tertiary`).
 *
 * Use [String.toLemonadeMarkdown] to parse a string containing these markers
 * into an [AnnotatedString] with the corresponding styles applied.
 *
 * @param key delimiter that opens and closes a formatted span
 */
public sealed class LemonadeMarkdown(
    public val key: String,
) {
    /** Applies semi-bold font weight. Marker: `**` */
    public data object SemiBold : LemonadeMarkdown(key = "**")

    /** Applies bold font weight. Marker: `***` */
    public data object Bold : LemonadeMarkdown(key = "***")

    /** Applies underline text decoration. Marker: `__` */
    public data object Underline : LemonadeMarkdown(key = "__")

    /** Applies strikethrough text decoration. Marker: `___` */
    public data object StrikeThrough : LemonadeMarkdown(key = "___")

    /** Applies italic font style. Marker: `~~` */
    public data object Italic : LemonadeMarkdown(key = "~~")

    internal companion object {
        internal val values = listOf(
            Bold,
            StrikeThrough,
            SemiBold,
            Underline,
            Italic,
        )
    }
}

/**
 * Parses this string for [LemonadeMarkdown] style markers and `{color}...{/color}` color tags,
 * returning an [AnnotatedString] with markers removed and styles applied.
 *
 * Style markers are matched in pairs — an unpaired marker is left as plain text.
 * Longer markers are matched first to avoid partial matches (e.g. `***` before `**`).
 *
 * Color tags use the `{name}...{/name}` syntax, where `name` maps to a semantic content
 * color token (e.g. `critical`, `positive`, `info`, `caution`, `brand`, `secondary`,
 * `tertiary`, `primary`, `neutral`, and others). Unrecognized tags are left as plain text.
 * Color markers resolve against the current Lemonade theme.
 *
 * Example:
 * ```
 * "Hello **world** and {critical}error{/critical}".toLemonadeMarkdown()
 * // Returns "Hello world and error" with semi-bold on "world" and critical color on "error"
 * ```
 */
@Composable
public fun String.toLemonadeMarkdown(): AnnotatedString {
    val colorMap = resolveContentColorMap()
    val state = MarkdownParseState()
    state.parseColorTags(
        source = this,
        colorMap = colorMap,
    )
    state.parseStyleMarkers(source = this)
    return state.buildAnnotatedString(source = this)
}

private class MarkdownParseState {
    val markerPositions: MutableSet<Int> = mutableSetOf()
    val spanStarts: MutableList<Int> = mutableListOf()
    val spanEnds: MutableList<Int> = mutableListOf()
    val spanStyles: MutableList<SpanStyle> = mutableListOf()
}

private fun MarkdownParseState.parseColorTags(
    source: String,
    colorMap: Map<String, Color>,
) {
    var searchFrom = 0
    while (searchFrom < source.length) {
        val nextSearchFrom = parseNextColorTag(
            source = source,
            colorMap = colorMap,
            searchFrom = searchFrom,
        )
            ?: return
        searchFrom = nextSearchFrom
    }
}

@Suppress("ReturnCount")
private fun MarkdownParseState.parseNextColorTag(
    source: String,
    colorMap: Map<String, Color>,
    searchFrom: Int,
): Int? {
    val openBrace = source.indexOf(
        char = '{',
        startIndex = searchFrom,
    )
    if (openBrace == -1) return null

    val closeBrace = source.indexOf(
        char = '}',
        startIndex = openBrace + 1,
    )
    if (closeBrace == -1) return null

    val tagName = source.substring(
        startIndex = openBrace + 1,
        endIndex = closeBrace,
    )

    if (tagName.startsWith('/') || tagName !in colorMap) {
        return openBrace + 1
    }

    val openTag = "{$tagName}"
    val closeTag = "{/$tagName}"
    val contentStart = openBrace + openTag.length
    val closeTagIdx = source.indexOf(
        string = closeTag,
        startIndex = contentStart,
    )
    if (closeTagIdx == -1) {
        return openBrace + 1
    }

    val openRange = openBrace until openBrace + openTag.length
    val closeRange = closeTagIdx until closeTagIdx + closeTag.length

    openRange.forEach { index -> markerPositions.add(index) }
    closeRange.forEach { index -> markerPositions.add(index) }

    spanStarts.add(contentStart)
    spanEnds.add(closeTagIdx)
    spanStyles.add(
        SpanStyle(color = colorMap.getValue(tagName)),
    )

    return closeTagIdx + closeTag.length
}

private fun MarkdownParseState.parseStyleMarkers(source: String) {
    val sortedMarkdowns = LemonadeMarkdown.values
        .sortedByDescending { markdown ->
            markdown.key.length
        }

    for (markdown in sortedMarkdowns) {
        parseStyleMarker(
            source = source,
            markdown = markdown,
        )
    }
}

private fun MarkdownParseState.parseStyleMarker(
    source: String,
    markdown: LemonadeMarkdown,
) {
    var searchFrom = 0

    while (searchFrom < source.length) {
        val nextSearchFrom = parseNextStyleMarker(
            source = source,
            markdown = markdown,
            searchFrom = searchFrom,
        )
            ?: return
        searchFrom = nextSearchFrom
    }
}

@Suppress("ReturnCount")
private fun MarkdownParseState.parseNextStyleMarker(
    source: String,
    markdown: LemonadeMarkdown,
    searchFrom: Int,
): Int? {
    val key = markdown.key
    val openIdx = source.indexOf(
        string = key,
        startIndex = searchFrom,
    )
    if (openIdx == -1) return null

    val openRange = openIdx until openIdx + key.length
    if (openRange.any { index -> index in markerPositions }) {
        return openIdx + 1
    }

    val contentStart = openIdx + key.length
    val closeIdx = source.indexOf(
        string = key,
        startIndex = contentStart,
    )
    if (closeIdx == -1) return null

    val closeRange = closeIdx until closeIdx + key.length
    if (closeRange.any { index -> index in markerPositions }) {
        return openIdx + 1
    }

    openRange.forEach { index -> markerPositions.add(index) }
    closeRange.forEach { index -> markerPositions.add(index) }

    spanStarts.add(contentStart)
    spanEnds.add(closeIdx)
    spanStyles.add(markdown.toSpanStyle())

    return closeIdx + key.length
}

private fun MarkdownParseState.buildAnnotatedString(source: String): AnnotatedString =
    buildAnnotatedString {
        val indexMapping = IntArray(size = source.length)
        var newIndex = 0

        for (i in source.indices) {
            indexMapping[i] = newIndex
            if (i !in markerPositions) {
                append(source[i])
                newIndex++
            }
        }

        for (i in spanStarts.indices) {
            addStyle(
                style = spanStyles[i],
                start = indexMapping[spanStarts[i]],
                end = indexMapping[spanEnds[i]],
            )
        }
    }

@Composable
private fun resolveContentColorMap(): Map<String, Color> {
    val content = LocalColors.current.content
    return mapOf(
        "primary" to content.contentPrimary,
        "secondary" to content.contentSecondary,
        "tertiary" to content.contentTertiary,
        "critical" to content.contentCritical,
        "positive" to content.contentPositive,
        "info" to content.contentInfo,
        "caution" to content.contentCaution,
        "brand" to content.contentBrand,
        "neutral" to content.contentNeutral,
        "brand-high" to content.contentBrandHigh,
        "on-brand-low" to content.contentOnBrandLow,
        "on-brand-high" to content.contentOnBrandHigh,
        "always-light" to content.contentAlwaysLight,
        "always-dark" to content.contentAlwaysDark,
        "primary-inverse" to content.contentPrimaryInverse,
        "secondary-inverse" to content.contentSecondaryInverse,
        "tertiary-inverse" to content.contentTertiaryInverse,
        "brand-inverse" to content.contentBrandInverse,
        "critical-on-color" to content.contentCriticalOnColor,
        "caution-on-color" to content.contentCautionOnColor,
        "info-on-color" to content.contentInfoOnColor,
        "positive-on-color" to content.contentPositiveOnColor,
        "neutral-on-color" to content.contentNeutralOnColor,
    )
}

private fun LemonadeMarkdown.toSpanStyle(): SpanStyle {
    val markdown = this
    return when (markdown) {
        is LemonadeMarkdown.SemiBold -> SpanStyle(
            fontWeight = FontWeight.SemiBold,
        )
        is LemonadeMarkdown.Bold -> SpanStyle(
            fontWeight = FontWeight.Bold,
        )
        is LemonadeMarkdown.Underline -> SpanStyle(
            textDecoration = TextDecoration.Underline,
        )
        is LemonadeMarkdown.StrikeThrough -> SpanStyle(
            textDecoration = TextDecoration.LineThrough,
        )
        is LemonadeMarkdown.Italic -> SpanStyle(
            fontStyle = FontStyle.Italic,
        )
    }
}
