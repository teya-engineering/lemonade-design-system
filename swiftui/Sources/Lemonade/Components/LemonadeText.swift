import SwiftUI

// MARK: - Text Component

public extension LemonadeUi {
    /// Text component for displaying styled text following the Lemonade Design System.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Text(
    ///     text: "Hello, World!",
    ///     textStyle: LemonadeTypography.shared.bodyMediumRegular
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - text: The string to display
    ///   - fontSize: Optional font size override. If not specified, uses the textStyle's fontSize.
    ///   - textStyle: The LemonadeTextStyle to apply. Defaults to bodyMediumRegular.
    ///   - textAlign: Text alignment. Defaults to leading.
    ///   - color: Text color. Defaults to contentPrimary.
    ///   - overflow: How to handle text overflow. Defaults to truncation.
    ///   - maxLines: Maximum number of lines. Defaults to unlimited.
    ///   - minLines: Minimum number of lines. Defaults to 1.
    /// - Returns: A styled Text view
    @ViewBuilder
    static func Text(
        _ text: String,
        fontSize: CGFloat? = nil,
        textStyle: LemonadeTextStyle = LemonadeTypography.shared.bodyMediumRegular,
        textAlign: TextAlignment = .leading,
        color: Color = LemonadeTheme.colors.content.contentPrimary,
        overflow: Text.TruncationMode = .tail,
        maxLines: Int? = nil,
        minLines: Int = 1
    ) -> some View {
        LemonadeTextView(
            text: text,
            fontSize: fontSize,
            textStyle: textStyle,
            textAlign: textAlign,
            color: color,
            overflow: overflow,
            maxLines: maxLines,
            minLines: minLines
        )
    }

    /// Text component with a raw TextStyle for advanced customization.
    ///
    /// - Parameters:
    ///   - text: The string to display
    ///   - font: The Font to use
    ///   - overflow: How to handle text overflow. Defaults to truncation.
    ///   - maxLines: Maximum number of lines. Defaults to unlimited.
    ///   - minLines: Minimum number of lines. Defaults to 1.
    /// - Returns: A styled Text view
    @ViewBuilder
    static func Text(
        _ text: String,
        font: Font,
        overflow: Text.TruncationMode = .tail,
        maxLines: Int? = nil,
        minLines: Int = 1
    ) -> some View {
        LemonadeTextView(
            text: text,
            font: font,
            overflow: overflow,
            maxLines: maxLines,
            minLines: minLines
        )
    }
}

// MARK: - Internal Text View

private struct LemonadeTextView: View {
    /// The family is resolved per environment so a subtree can be drawn in another face.
    @Environment(\.lemonadeFontFamily) private var fontFamily

    let text: String
    let fontSize: CGFloat?
    let textStyle: LemonadeTextStyle?
    let font: Font?
    let textAlign: TextAlignment
    let color: Color
    let overflow: Text.TruncationMode
    let maxLines: Int?
    let minLines: Int

    init(
        text: String,
        fontSize: CGFloat? = nil,
        textStyle: LemonadeTextStyle = LemonadeTypography.shared.bodyMediumRegular,
        textAlign: TextAlignment = .leading,
        color: Color = LemonadeTheme.colors.content.contentPrimary,
        overflow: Text.TruncationMode = .tail,
        maxLines: Int? = nil,
        minLines: Int = 1
    ) {
        self.text = text
        self.fontSize = fontSize
        self.textStyle = textStyle
        self.font = nil
        self.textAlign = textAlign
        self.color = color
        self.overflow = overflow
        self.maxLines = maxLines
        self.minLines = minLines
    }

    init(
        text: String,
        font: Font,
        overflow: Text.TruncationMode = .tail,
        maxLines: Int? = nil,
        minLines: Int = 1
    ) {
        self.text = text
        self.fontSize = nil
        self.textStyle = nil
        self.font = font
        self.textAlign = .leading
        self.color = LemonadeTheme.colors.content.contentPrimary
        self.overflow = overflow
        self.maxLines = maxLines
        self.minLines = minLines
    }

    var body: some View {
        let displayText = isOverlineStyle ? text.uppercased() : text
        let resolvedFont = resolveFont()

        if #available(iOS 16.0, macOS 13.0, *) {
            SwiftUI.Text(displayText)
                .font(resolvedFont)
                .foregroundStyle(color)
                .multilineTextAlignment(textAlign)
                .lineLimit(maxLines)
                .truncationMode(overflow)
                .lineSpacing(textStyle?.lineSpacing(in: fontFamily) ?? 0)
                .tracking(textStyle?.letterSpacing ?? 0)
                .frame(minHeight: textStyle?.lineHeight)
        } else {
            SwiftUI.Text(displayText)
                .font(resolvedFont)
                .foregroundStyle(color)
                .multilineTextAlignment(textAlign)
                .lineLimit(maxLines)
                .truncationMode(overflow)
                .lineSpacing(textStyle?.lineSpacing(in: fontFamily) ?? 0)
                .frame(minHeight: textStyle?.lineHeight)
        }
    }
    

    private var isOverlineStyle: Bool {
        guard let style = textStyle else { return false }
        // Check if it's the overline style by checking for letter spacing
        return style.letterSpacing != nil && style.letterSpacing! > 0
    }

    private func resolveFont() -> Font {
        if let font = font {
            return font
        }

        guard let style = textStyle else {
            return .body
        }

        // A caller-supplied `fontSize` overrides the size but not the face.
        let size = fontSize ?? style.fontSize
        return .custom(fontFamily.fontName(for: style.fontWeight), size: size, relativeTo: .body)
    }
}

// MARK: - AttributedString Text Component

public extension LemonadeUi {
    /// Text component for displaying styled `AttributedString` following the Lemonade Design System.
    ///
    /// Use this overload to render text with inline styling such as the output of
    /// `String.toLemonadeMarkdown()`.
    ///
    /// - Parameters:
    ///   - text: The `AttributedString` to display
    ///   - textStyle: The LemonadeTextStyle to apply as the base style. Defaults to bodyMediumRegular.
    ///   - textAlign: Text alignment. Defaults to leading.
    ///   - color: Base text color. Defaults to contentPrimary. Inline color attributes override this.
    ///   - overflow: How to handle text overflow. Defaults to truncation.
    ///   - maxLines: Maximum number of lines. Defaults to unlimited.
    ///   - minLines: Minimum number of lines. Defaults to 1.
    /// - Returns: A styled Text view
    @ViewBuilder
    static func Text(
        _ text: AttributedString,
        textStyle: LemonadeTextStyle = LemonadeTypography.shared.bodyMediumRegular,
        textAlign: TextAlignment = .leading,
        color: Color = LemonadeTheme.colors.content.contentPrimary,
        overflow: SwiftUI.Text.TruncationMode = .tail,
        maxLines: Int? = nil,
        minLines: Int = 1
    ) -> some View {
        LemonadeAttributedTextView(
            text: text,
            textStyle: textStyle,
            textAlign: textAlign,
            color: color,
            overflow: overflow,
            maxLines: maxLines,
            minLines: minLines
        )
    }

    /// Text component for displaying styled `AttributedString` with a raw Font.
    ///
    /// - Parameters:
    ///   - text: The `AttributedString` to display
    ///   - font: The Font to use as the base font
    ///   - overflow: How to handle text overflow. Defaults to truncation.
    ///   - maxLines: Maximum number of lines. Defaults to unlimited.
    ///   - minLines: Minimum number of lines. Defaults to 1.
    /// - Returns: A styled Text view
    @ViewBuilder
    static func Text(
        _ text: AttributedString,
        font: Font,
        overflow: SwiftUI.Text.TruncationMode = .tail,
        maxLines: Int? = nil,
        minLines: Int = 1
    ) -> some View {
        LemonadeAttributedTextView(
            text: text,
            font: font,
            overflow: overflow,
            maxLines: maxLines,
            minLines: minLines
        )
    }
}

// MARK: - Internal Attributed Text View

private struct LemonadeAttributedTextView: View {
    /// See the note on `LemonadeTextView`.
    @Environment(\.lemonadeFontFamily) private var fontFamily

    let text: AttributedString
    let textStyle: LemonadeTextStyle?
    let font: Font?
    let textAlign: TextAlignment
    let color: Color
    let overflow: SwiftUI.Text.TruncationMode
    let maxLines: Int?
    let minLines: Int

    init(
        text: AttributedString,
        textStyle: LemonadeTextStyle = LemonadeTypography.shared.bodyMediumRegular,
        textAlign: TextAlignment = .leading,
        color: Color = LemonadeTheme.colors.content.contentPrimary,
        overflow: SwiftUI.Text.TruncationMode = .tail,
        maxLines: Int? = nil,
        minLines: Int = 1
    ) {
        self.text = text
        self.textStyle = textStyle
        self.font = nil
        self.textAlign = textAlign
        self.color = color
        self.overflow = overflow
        self.maxLines = maxLines
        self.minLines = minLines
    }

    init(
        text: AttributedString,
        font: Font,
        overflow: SwiftUI.Text.TruncationMode = .tail,
        maxLines: Int? = nil,
        minLines: Int = 1
    ) {
        self.text = text
        self.textStyle = nil
        self.font = font
        self.textAlign = .leading
        self.color = LemonadeTheme.colors.content.contentPrimary
        self.overflow = overflow
        self.maxLines = maxLines
        self.minLines = minLines
    }

    var body: some View {
        let resolvedFont = resolveFont()

        if #available(iOS 16.0, macOS 13.0, *) {
            SwiftUI.Text(text)
                .font(resolvedFont)
                .foregroundStyle(color)
                .multilineTextAlignment(textAlign)
                .lineLimit(maxLines)
                .truncationMode(overflow)
                .lineSpacing(textStyle?.lineSpacing(in: fontFamily) ?? 0)
                .tracking(textStyle?.letterSpacing ?? 0)
                .frame(minHeight: textStyle?.lineHeight)
        } else {
            SwiftUI.Text(text)
                .font(resolvedFont)
                .foregroundStyle(color)
                .multilineTextAlignment(textAlign)
                .lineLimit(maxLines)
                .truncationMode(overflow)
                .lineSpacing(textStyle?.lineSpacing(in: fontFamily) ?? 0)
                .frame(minHeight: textStyle?.lineHeight)
        }
    }

    private func resolveFont() -> Font {
        if let font = font {
            return font
        }

        guard let style = textStyle else {
            return .body
        }

        return style.font(in: fontFamily)
    }
}

// MARK: - LemonadeMarkdown

/// Defines the supported inline style markers for Lemonade text formatting.
///
/// Style markers use symmetric delimiters:
/// - `**text**` for ``semiBold``
/// - `***text***` for ``bold``
/// - `__text__` for ``underline``
/// - `___text___` for ``strikeThrough``
/// - `~~text~~` for ``italic``
///
/// Color markers are resolved dynamically using `{color-name}text{/color-name}` syntax,
/// where `color-name` maps to a semantic content color token (e.g. `critical`, `positive`,
/// `info`, `caution`, `brand`, `secondary`, `tertiary`).
///
/// Use `String.toLemonadeMarkdown()` to parse a string containing these markers
/// into an `AttributedString` with the corresponding styles applied.
public enum LemonadeMarkdown {
    /// Applies semi-bold font weight. Marker: `**`
    case semiBold
    /// Applies bold font weight. Marker: `***`
    case bold
    /// Applies underline text decoration. Marker: `__`
    case underline
    /// Applies strikethrough text decoration. Marker: `___`
    case strikeThrough
    /// Applies italic font style. Marker: `~~`
    case italic

    /// The symmetric delimiter used to open and close a formatted span.
    public var key: String {
        switch self {
        case .semiBold: return "**"
        case .bold: return "***"
        case .underline: return "__"
        case .strikeThrough: return "___"
        case .italic: return "~~"
        }
    }

    static let values: [LemonadeMarkdown] = [
        .bold, .strikeThrough, .semiBold, .underline, .italic
    ]

    func toAttributes(
        baseFontSize: CGFloat,
        family: LemonadeFontFamily
    ) -> AttributeContainer {
        var container = AttributeContainer()
        switch self {
        case .semiBold:
            container.font = .custom(family.semibold, size: baseFontSize, relativeTo: .body)
        case .bold:
            container.font = .custom(family.semibold, size: baseFontSize, relativeTo: .body).bold()
        case .underline:
            container.underlineStyle = .single
        case .strikeThrough:
            container.strikethroughStyle = .single
        case .italic:
            container.font = .custom(family.regular, size: baseFontSize, relativeTo: .body).italic()
        }
        return container
    }
}

// MARK: - Markdown Parsing

public extension String {
    /// Parses this string for `LemonadeMarkdown` style markers and `{color}...{/color}` color tags,
    /// returning an `AttributedString` with markers removed and styles applied.
    ///
    /// Style markers are matched in pairs — an unpaired marker is left as plain text.
    /// Longer markers are matched first to avoid partial matches (e.g. `***` before `**`).
    ///
    /// Color tags use the `{name}...{/name}` syntax, where `name` maps to a semantic content
    /// color token (e.g. `critical`, `positive`, `info`, `caution`, `brand`, `secondary`,
    /// `tertiary`, `primary`, `neutral`, and others). Unrecognized tags are left as plain text.
    ///
    /// - Parameter baseFontSize: The base font size used for font-related style markers
    ///   (semiBold, bold, italic). Defaults to the body medium regular font size.
    /// - Returns: An `AttributedString` with markers removed and corresponding styles applied.
    func toLemonadeMarkdown(
        baseFontSize: CGFloat = LemonadeTypography.shared.bodyMediumRegular.fontSize,
        family: LemonadeFontFamily = .figtree
    ) -> AttributedString {
        let colorMap = resolveContentColorMap()
        let state = MarkdownParseState()
        state.parseColorTags(source: self, colorMap: colorMap)
        state.parseStyleMarkers(source: self, baseFontSize: baseFontSize, family: family)
        return state.buildAttributedString(source: self)
    }
}

// MARK: - Markdown Parse State

private class MarkdownParseState {
    var markerPositions: Set<Int> = []
    var spanStarts: [Int] = []
    var spanEnds: [Int] = []
    var spanAttributes: [AttributeContainer] = []

    func parseColorTags(source: String, colorMap: [String: Color]) {
        let nsSource = source as NSString
        var searchFrom = 0

        while searchFrom < nsSource.length {
            let openBrace = nsSource.range(
                of: "{",
                range: NSRange(location: searchFrom, length: nsSource.length - searchFrom)
            )
            guard openBrace.location != NSNotFound else { break }

            let closeBrace = nsSource.range(
                of: "}",
                range: NSRange(location: openBrace.location + 1, length: nsSource.length - openBrace.location - 1)
            )
            guard closeBrace.location != NSNotFound else { break }

            let tagName = nsSource.substring(
                with: NSRange(
                    location: openBrace.location + 1,
                    length: closeBrace.location - openBrace.location - 1
                )
            )

            guard !tagName.hasPrefix("/"), let color = colorMap[tagName] else {
                searchFrom = openBrace.location + 1
                continue
            }

            let openTag = "{\(tagName)}"
            let closeTag = "{/\(tagName)}"
            let contentStart = openBrace.location + openTag.count
            let closeTagRange = nsSource.range(
                of: closeTag,
                range: NSRange(location: contentStart, length: nsSource.length - contentStart)
            )
            guard closeTagRange.location != NSNotFound else {
                searchFrom = openBrace.location + 1
                continue
            }

            let openRange = openBrace.location..<(openBrace.location + openTag.count)
            let closeRange = closeTagRange.location..<(closeTagRange.location + closeTag.count)

            for index in openRange { markerPositions.insert(index) }
            for index in closeRange { markerPositions.insert(index) }

            spanStarts.append(contentStart)
            spanEnds.append(closeTagRange.location)

            var container = AttributeContainer()
            container.foregroundColor = color
            spanAttributes.append(container)

            searchFrom = closeTagRange.location + closeTag.count
        }
    }

    func parseStyleMarkers(source: String, baseFontSize: CGFloat, family: LemonadeFontFamily) {
        let nsSource = source as NSString
        let sortedMarkdowns = LemonadeMarkdown.values.sorted { $0.key.count > $1.key.count }

        for markdown in sortedMarkdowns {
            let key = markdown.key
            var searchFrom = 0

            while searchFrom < nsSource.length {
                let openRange = nsSource.range(
                    of: key,
                    range: NSRange(location: searchFrom, length: nsSource.length - searchFrom)
                )
                guard openRange.location != NSNotFound else { break }

                let openIndices = openRange.location..<(openRange.location + openRange.length)
                if openIndices.contains(where: { markerPositions.contains($0) }) {
                    searchFrom = openRange.location + 1
                    continue
                }

                let contentStart = openRange.location + key.count
                let closeRange = nsSource.range(
                    of: key,
                    range: NSRange(location: contentStart, length: nsSource.length - contentStart)
                )
                guard closeRange.location != NSNotFound else { break }

                let closeIndices = closeRange.location..<(closeRange.location + closeRange.length)
                if closeIndices.contains(where: { markerPositions.contains($0) }) {
                    searchFrom = openRange.location + 1
                    continue
                }

                for index in openIndices { markerPositions.insert(index) }
                for index in closeIndices { markerPositions.insert(index) }

                spanStarts.append(contentStart)
                spanEnds.append(closeRange.location)
                spanAttributes.append(markdown.toAttributes(baseFontSize: baseFontSize, family: family))

                searchFrom = closeRange.location + key.count
            }
        }
    }

    func buildAttributedString(source: String) -> AttributedString {
        var cleanString = ""
        var indexMapping = [Int](repeating: 0, count: source.count)
        var newIndex = 0

        for (i, character) in source.enumerated() {
            indexMapping[i] = newIndex
            if !markerPositions.contains(i) {
                cleanString.append(character)
                newIndex += 1
            }
        }

        var result = AttributedString(cleanString)

        for i in 0..<spanStarts.count {
            let start = indexMapping[spanStarts[i]]
            let end = indexMapping[spanEnds[i]]

            guard start < end else { continue }

            let startIdx = result.characters.index(result.startIndex, offsetBy: start)
            let endIdx = result.characters.index(result.startIndex, offsetBy: end)
            result[startIdx..<endIdx].mergeAttributes(spanAttributes[i])
        }

        return result
    }
}

// MARK: - Content Color Map

private func resolveContentColorMap() -> [String: Color] {
    let content = LemonadeTheme.colors.content
    return [
        "primary": content.contentPrimary,
        "secondary": content.contentSecondary,
        "tertiary": content.contentTertiary,
        "critical": content.contentCritical,
        "positive": content.contentPositive,
        "info": content.contentInfo,
        "caution": content.contentCaution,
        "brand": content.contentBrand,
        "neutral": content.contentNeutral,
        "brand-high": content.contentBrandHigh,
        "on-brand-low": content.contentOnBrandLow,
        "on-brand-high": content.contentOnBrandHigh,
        "always-light": content.contentAlwaysLight,
        "always-dark": content.contentAlwaysDark,
        "primary-inverse": content.contentPrimaryInverse,
        "secondary-inverse": content.contentSecondaryInverse,
        "tertiary-inverse": content.contentTertiaryInverse,
        "brand-inverse": content.contentBrandInverse,
        "critical-on-color": content.contentCriticalOnColor,
        "caution-on-color": content.contentCautionOnColor,
        "info-on-color": content.contentInfoOnColor,
        "positive-on-color": content.contentPositiveOnColor,
        "neutral-on-color": content.contentNeutralOnColor,
    ]
}

// MARK: - Previews

#if DEBUG
struct LemonadeText_Previews: PreviewProvider {
    static var previews: some View {
        VStack(alignment: .leading, spacing: 16) {
            LemonadeUi.Text(
                "Display Large",
                textStyle: LemonadeTypography.shared.displayLarge
            )

            LemonadeUi.Text(
                "Heading Medium",
                textStyle: LemonadeTypography.shared.headingMedium
            )

            LemonadeUi.Text(
                "Body Medium Regular",
                textStyle: LemonadeTypography.shared.bodyMediumRegular
            )

            LemonadeUi.Text(
                "Body Small SemiBold",
                textStyle: LemonadeTypography.shared.bodySmallSemiBold,
                color: LemonadeTheme.colors.content.contentSecondary
            )

            LemonadeUi.Text(
                "Overline Text",
                textStyle: LemonadeTypography.shared.bodyXSmallOverline
            )
            
            LemonadeUi.Text("This text allows multiple lines but is limited to 2 lines maximum. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore.", textStyle: LemonadeTypography.shared.bodyMediumRegular, maxLines: 2)
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
