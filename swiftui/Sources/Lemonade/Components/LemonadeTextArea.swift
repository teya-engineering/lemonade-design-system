import SwiftUI

// MARK: - TextArea Component

public extension LemonadeUi {
    /// Text Area component for multi-line text input, with optional label, support text,
    /// character counter, and error state. The box keeps a fixed height and scrolls its
    /// content once the text outgrows it.
    ///
    /// The return key inserts a new line, so on iOS a "Done" bar above the keyboard closes it.
    /// Hide the bar with `.lemonadeKeyboardDoneButton(false)`.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.TextArea(
    ///     input: $text,
    ///     label: "What happened",
    ///     optionalIndicator: "Optional",
    ///     placeholderText: "For example: I didn't make this payment",
    ///     maxLength: 1000
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - input: The inputted text (Binding)
    ///   - onInputChanged: Callback when the user inputs content
    ///   - label: Label displayed above the text area
    ///   - optionalIndicator: Optional text displayed on the right of the label
    ///   - supportText: Support text displayed below the text area
    ///   - placeholderText: Placeholder text when the text area is empty
    ///   - errorMessage: Error message displayed when error is true
    ///   - maxLength: Rejects any edit that would take the text past this many UTF-16 code
    ///     units (an emoji can count as 2), and shows a `count/maxLength` counter below the text
    ///     area. Text already past the limit can still be shortened
    ///   - height: Height of the input box, excluding the label and the text below it
    ///   - error: Whether the text area has an error
    ///   - enabled: Whether the text area is enabled
    /// - Returns: A styled TextArea view
    static func TextArea(
        input: Binding<String>,
        onInputChanged: ((String) -> Void)? = nil,
        label: String? = nil,
        optionalIndicator: String? = nil,
        supportText: String? = nil,
        placeholderText: String? = nil,
        errorMessage: String? = nil,
        maxLength: Int? = nil,
        height: CGFloat = 180,
        error: Bool = false,
        enabled: Bool = true
    ) -> some View {
        LemonadeTextAreaView(
            input: input,
            onInputChanged: onInputChanged,
            label: label,
            optionalIndicator: optionalIndicator,
            supportText: supportText,
            placeholderText: placeholderText,
            errorMessage: errorMessage,
            maxLength: maxLength,
            height: height,
            error: error,
            enabled: enabled
        )
    }
}

// MARK: - Internal TextArea View

private struct LemonadeTextAreaView: View {
    @Binding var input: String
    let onInputChanged: ((String) -> Void)?
    let label: String?
    let optionalIndicator: String?
    let supportText: String?
    let placeholderText: String?
    let errorMessage: String?
    let maxLength: Int?
    let height: CGFloat
    let error: Bool
    let enabled: Bool

    @State private var localFocus = false
    @Environment(\.lemonadeTextFieldFocus) private var externalFocus

    private var focusBinding: Binding<Bool> { externalFocus ?? $localFocus }
    private var isFocused: Bool { focusBinding.wrappedValue }
    @State private var isHovered = false

    var body: some View {
        VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing50) {
            TextFieldLabelRow(label: label, optionalIndicator: optionalIndicator, enabled: enabled)

            fieldContainer

            TextFieldSupportText(
                supportText: supportText,
                errorMessage: errorMessage,
                error: error,
                enabled: enabled,
                counter: maxLength.map { limit in "\(input.utf16.count)/\(limit)" }
            )
        }
        .animation(.easeInOut(duration: 0.15), value: isFocused)
        .animation(.easeInOut(duration: 0.15), value: error)
    }

    private var fieldContainer: some View {
        ZStack(alignment: .topLeading) {
            if input.isEmpty, let placeholderText = placeholderText {
                LemonadeUi.Text(
                    placeholderText,
                    textStyle: LemonadeTypography.shared.bodyMediumRegular,
                    color: LemonadeTheme.colors.content.contentSecondary
                )
            }

            LemonadeTextAreaInput(
                input: $input,
                isFocused: focusBinding,
                enabled: enabled,
                maxLength: maxLength,
                onInputChanged: onInputChanged
            )
        }
        .padding(LemonadeTheme.spaces.spacing400)
        .frame(maxWidth: .infinity, minHeight: height, maxHeight: height, alignment: .topLeading)
        .modifier(TextFieldContainerModifier(
            backgroundColor: textFieldBackgroundColor(enabled: enabled, error: error, isFocused: isFocused, isHovered: isHovered),
            borderColor: textFieldBorderColor(enabled: enabled, isFocused: isFocused, error: error),
            enabled: enabled,
            isFocused: isFocused,
            cornerRadius: TextFieldConstants.cornerRadius,
            minHeight: height,
            isHovered: $isHovered
        ))
    }
}

// MARK: - Limit

/// Lengths are UTF-16 code units, as in `NSRange` and the KMP TextArea, so both platforms count the
/// same text alike. An edit that doesn't lengthen the text always fits, so text already past the
/// limit can still be shortened.
enum TextAreaLimit {
    static func accepts(oldLength: Int, newLength: Int, maxLength: Int?) -> Bool {
        guard let maxLength else { return true }
        return newLength <= max(maxLength, oldLength)
    }
}

// MARK: - Input

#if canImport(UIKit)
private struct LemonadeTextAreaInput: View {
    @Binding var input: String
    @Binding var isFocused: Bool
    let enabled: Bool
    let maxLength: Int?
    let onInputChanged: ((String) -> Void)?

    var body: some View {
        LemonadeUITextView(
            text: $input,
            isFocused: $isFocused,
            isEnabled: enabled,
            maxLength: maxLength,
            textStyle: LemonadeTypography.shared.bodyMediumRegular,
            textColor: LemonadeTheme.colors.content.contentPrimary,
            onTextChange: onInputChanged
        )
    }
}
#else
private struct LemonadeTextAreaInput: View {
    @Binding var input: String
    @Binding var isFocused: Bool
    let enabled: Bool
    let maxLength: Int?
    let onInputChanged: ((String) -> Void)?

    @FocusState private var editorFocused: Bool
    @State private var acceptedInput = ""

    var body: some View {
        textEditor
            .font(LemonadeTypography.shared.bodyMediumRegular.font)
            .foregroundStyle(LemonadeTheme.colors.content.contentPrimary)
            .tint(LemonadeTheme.colors.content.contentPrimary)
            .focused($editorFocused)
            .disabled(!enabled)
            .onAppear { acceptedInput = input }
            .onChange(of: input) { newValue in
                guard newValue != acceptedInput else { return }
                let fits = TextAreaLimit.accepts(
                    oldLength: acceptedInput.utf16.count,
                    newLength: newValue.utf16.count,
                    maxLength: maxLength
                )
                if !fits {
                    input = acceptedInput
                    return
                }
                acceptedInput = newValue
                onInputChanged?(newValue)
            }
            .onChange(of: editorFocused) { isFocused = $0 }
            .onChange(of: isFocused) { newValue in
                if editorFocused != newValue { editorFocused = newValue }
            }
    }

    @ViewBuilder
    private var textEditor: some View {
        if #available(macOS 13.0, *) {
            TextEditor(text: $input)
                .scrollContentBackground(.hidden)
        } else {
            TextEditor(text: $input)
        }
    }
}
#endif

// MARK: - Previews

#if DEBUG
struct LemonadeTextArea_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: LemonadeTheme.spaces.spacing600) {
            LemonadeUi.TextArea(
                input: .constant(""),
                label: "What happened",
                optionalIndicator: "Optional",
                supportText: "Anything you add helps us look into it.",
                placeholderText: "For example: I didn't make this payment",
                maxLength: 1000
            )

            LemonadeUi.TextArea(
                input: .constant("I didn't make this payment."),
                label: "What happened",
                errorMessage: "Tell us a bit more",
                maxLength: 1000,
                error: true
            )
        }
        .padding()
    }
}
#endif
