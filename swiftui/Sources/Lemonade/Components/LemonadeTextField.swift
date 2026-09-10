import SwiftUI

// MARK: - TextField Component

public extension LemonadeUi {
    /// Text Field component allows users to enter or edit text.
    /// It supports multiple interaction states, label, support text, error states,
    /// and optional leading/trailing content.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.TextField(
    ///     input: $text,
    ///     onInputChanged: { newValue in text = newValue },
    ///     label: "Label",
    ///     supportText: "Support text",
    ///     placeholderText: "Enter text..."
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - input: The inputted text (Binding)
    ///   - onInputChanged: Callback when the user inputs content
    ///   - onSubmit: Callback when the keyboard's return key is pressed. The field
    ///     keeps focus (the keyboard stays up) so the caller decides what happens next.
    ///   - label: Label displayed above the text field
    ///   - optionalIndicator: Optional text displayed on the right of the label
    ///   - supportText: Support text displayed below the text field
    ///   - placeholderText: Placeholder text when the field is empty
    ///   - errorMessage: Error message displayed when error is true
    ///   - error: Whether the text field has an error
    ///   - enabled: Whether the text field is enabled
    /// - Returns: A styled TextField view
    @ViewBuilder
    static func TextField(
        input: Binding<String>,
        onInputChanged: ((String) -> Void)? = nil,
        onSubmit: (() -> Void)? = nil,
        label: String? = nil,
        optionalIndicator: String? = nil,
        supportText: String? = nil,
        placeholderText: String? = nil,
        errorMessage: String? = nil,
        error: Bool = false,
        enabled: Bool = true
    ) -> some View {
        LemonadeTextFieldView<EmptyView, EmptyView>(
            input: input,
            onInputChanged: onInputChanged,
            onSubmit: onSubmit,
            label: label,
            optionalIndicator: optionalIndicator,
            supportText: supportText,
            placeholderText: placeholderText,
            errorMessage: errorMessage,
            error: error,
            enabled: enabled,
            leadingContent: nil,
            trailingContent: nil
        )
    }

    /// Text Field component with custom leading and trailing content.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.TextField(
    ///     input: $text,
    ///     label: "Password",
    ///     placeholderText: "Enter password"
    /// ) {
    ///     LemonadeUi.Icon(icon: .padlock, contentDescription: nil)
    /// } trailingContent: {
    ///     LemonadeUi.Icon(icon: .eyeClosed, contentDescription: nil)
    /// }
    /// .secureTextEntry() // native masking for password input
    /// ```
    ///
    /// - Parameters:
    ///   - input: The inputted text (Binding)
    ///   - onInputChanged: Callback when the user inputs content
    ///   - onSubmit: Callback when the keyboard's return key is pressed. The field
    ///     keeps focus (the keyboard stays up) so the caller decides what happens next.
    ///   - label: Label displayed above the text field
    ///   - optionalIndicator: Optional text displayed on the right of the label
    ///   - supportText: Support text displayed below the text field
    ///   - placeholderText: Placeholder text when the field is empty
    ///   - errorMessage: Error message displayed when error is true
    ///   - error: Whether the text field has an error
    ///   - enabled: Whether the text field is enabled
    ///   - leadingContent: Content displayed on the left inside the text field
    ///   - trailingContent: Content displayed on the right inside the text field
    /// - Returns: A styled TextField view
    @ViewBuilder
    static func TextField<LeadingContent: View, TrailingContent: View>(
        input: Binding<String>,
        onInputChanged: ((String) -> Void)? = nil,
        onSubmit: (() -> Void)? = nil,
        label: String? = nil,
        optionalIndicator: String? = nil,
        supportText: String? = nil,
        placeholderText: String? = nil,
        errorMessage: String? = nil,
        error: Bool = false,
        enabled: Bool = true,
        @ViewBuilder leadingContent: @escaping () -> LeadingContent,
        @ViewBuilder trailingContent: @escaping () -> TrailingContent
    ) -> some View {
        LemonadeTextFieldView(
            input: input,
            onInputChanged: onInputChanged,
            onSubmit: onSubmit,
            label: label,
            optionalIndicator: optionalIndicator,
            supportText: supportText,
            placeholderText: placeholderText,
            errorMessage: errorMessage,
            error: error,
            enabled: enabled,
            leadingContent: leadingContent,
            trailingContent: trailingContent
        )
    }
}

// MARK: - TextField with TextFieldValue (Cursor Control)

#if canImport(UIKit)
import UIKit

public extension LemonadeUi {
    /// Text Field component with TextFieldValue for cursor position control.
    /// Use this overload when you need to programmatically control cursor position,
    /// such as when formatting phone numbers or other structured input.
    ///
    /// - Note: Cursor position control is fully supported on iOS. On macOS, the text field
    ///   functions normally but cursor position changes are not applied (no-op).
    ///
    /// ## Usage
    /// ```swift
    /// @State var textFieldValue = LemonadeTextFieldValue(text: "")
    ///
    /// LemonadeUi.TextField(
    ///     value: $textFieldValue,
    ///     onValueChange: { newValue in
    ///         // Format and move cursor to end
    ///         let formatted = formatPhoneNumber(newValue.text)
    ///         textFieldValue = LemonadeTextFieldValue(text: formatted)
    ///     },
    ///     label: "Phone Number",
    ///     placeholderText: "Enter phone number"
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - value: The text field value including text and cursor position (Binding)
    ///   - onValueChange: Callback when the value changes
    ///   - label: Label displayed above the text field
    ///   - optionalIndicator: Optional text displayed on the right of the label
    ///   - supportText: Support text displayed below the text field
    ///   - placeholderText: Placeholder text when the field is empty
    ///   - errorMessage: Error message displayed when error is true
    ///   - error: Whether the text field has an error
    ///   - enabled: Whether the text field is enabled
    /// - Returns: A styled TextField view with cursor control
    @ViewBuilder
    static func TextField(
        value: Binding<LemonadeTextFieldValue>,
        onValueChange: ((LemonadeTextFieldValue) -> Void)? = nil,
        label: String? = nil,
        optionalIndicator: String? = nil,
        supportText: String? = nil,
        placeholderText: String? = nil,
        errorMessage: String? = nil,
        error: Bool = false,
        enabled: Bool = true,
        keyboardType: UIKeyboardType? = nil
    ) -> some View {
        LemonadeTextFieldValueView<EmptyView, EmptyView>(
            value: value,
            onValueChange: onValueChange,
            label: label,
            optionalIndicator: optionalIndicator,
            supportText: supportText,
            placeholderText: placeholderText,
            errorMessage: errorMessage,
            error: error,
            enabled: enabled,
            keyboardType: keyboardType,
            leadingContent: nil,
            trailingContent: nil
        )
    }

    /// Text Field component with TextFieldValue and custom leading/trailing content.
    ///
    /// - Parameters:
    ///   - value: The text field value including text and cursor position (Binding)
    ///   - onValueChange: Callback when the value changes
    ///   - label: Label displayed above the text field
    ///   - optionalIndicator: Optional text displayed on the right of the label
    ///   - supportText: Support text displayed below the text field
    ///   - placeholderText: Placeholder text when the field is empty
    ///   - errorMessage: Error message displayed when error is true
    ///   - error: Whether the text field has an error
    ///   - enabled: Whether the text field is enabled
    ///   - leadingContent: Content displayed on the left inside the text field
    ///   - trailingContent: Content displayed on the right inside the text field
    /// - Returns: A styled TextField view with cursor control
    @ViewBuilder
    static func TextField<LeadingContent: View, TrailingContent: View>(
        value: Binding<LemonadeTextFieldValue>,
        onValueChange: ((LemonadeTextFieldValue) -> Void)? = nil,
        label: String? = nil,
        optionalIndicator: String? = nil,
        supportText: String? = nil,
        placeholderText: String? = nil,
        errorMessage: String? = nil,
        error: Bool = false,
        enabled: Bool = true,
        keyboardType: UIKeyboardType? = nil,
        @ViewBuilder leadingContent: @escaping () -> LeadingContent,
        @ViewBuilder trailingContent: @escaping () -> TrailingContent
    ) -> some View {
        LemonadeTextFieldValueView(
            value: value,
            onValueChange: onValueChange,
            label: label,
            optionalIndicator: optionalIndicator,
            supportText: supportText,
            placeholderText: placeholderText,
            errorMessage: errorMessage,
            error: error,
            enabled: enabled,
            keyboardType: keyboardType,
            leadingContent: leadingContent,
            trailingContent: trailingContent
        )
    }
}
#endif

// MARK: - TextFieldWithSelector Component

public extension LemonadeUi {
    /// A text input combined with a selectable element, allowing users to choose a prefix
    /// before entering text. Ideal for structured inputs like phone numbers.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.TextFieldWithSelector(
    ///     input: $text,
    ///     onInputChanged: { newValue in text = newValue },
    ///     leadingAction: { /* show selector */ },
    ///     leadingContent: {
    ///         HStack {
    ///             Text("+1")
    ///             Image(systemName: "chevron.down")
    ///         }
    ///     }
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - input: The inputted text (Binding)
    ///   - onInputChanged: Callback when the user inputs content
    ///   - leadingAction: Action triggered when the leading content is clicked
    ///   - leadingContent: Content displayed on the left as selector
    ///   - label: Label displayed above the text field
    ///   - optionalIndicator: Optional text displayed on the right of the label
    ///   - supportText: Support text displayed below the text field
    ///   - placeholderText: Placeholder text when the field is empty
    ///   - errorMessage: Error message displayed when error is true
    ///   - error: Whether the text field has an error
    ///   - enabled: Whether the text field is enabled
    /// - Returns: A styled TextFieldWithSelector view
    @ViewBuilder
    static func TextFieldWithSelector<LeadingContent: View>(
        input: Binding<String>,
        onInputChanged: ((String) -> Void)? = nil,
        leadingAction: @escaping () -> Void,
        @ViewBuilder leadingContent: @escaping () -> LeadingContent,
        label: String? = nil,
        optionalIndicator: String? = nil,
        supportText: String? = nil,
        placeholderText: String? = nil,
        errorMessage: String? = nil,
        error: Bool = false,
        enabled: Bool = true
    ) -> some View {
        LemonadeTextFieldWithSelectorView<LeadingContent, EmptyView>(
            input: input,
            onInputChanged: onInputChanged,
            leadingAction: leadingAction,
            leadingContent: leadingContent,
            label: label,
            optionalIndicator: optionalIndicator,
            supportText: supportText,
            placeholderText: placeholderText,
            errorMessage: errorMessage,
            error: error,
            enabled: enabled,
            trailingContent: nil
        )
    }

    /// A text input combined with a selectable element and trailing content.
    ///
    /// - Parameters:
    ///   - input: The inputted text (Binding)
    ///   - onInputChanged: Callback when the user inputs content
    ///   - leadingAction: Action triggered when the leading content is clicked
    ///   - leadingContent: Content displayed on the left as selector
    ///   - label: Label displayed above the text field
    ///   - optionalIndicator: Optional text displayed on the right of the label
    ///   - supportText: Support text displayed below the text field
    ///   - placeholderText: Placeholder text when the field is empty
    ///   - errorMessage: Error message displayed when error is true
    ///   - error: Whether the text field has an error
    ///   - enabled: Whether the text field is enabled
    ///   - trailingContent: Content displayed on the right inside the text field
    /// - Returns: A styled TextFieldWithSelector view
    @ViewBuilder
    static func TextFieldWithSelector<LeadingContent: View, TrailingContent: View>(
        input: Binding<String>,
        onInputChanged: ((String) -> Void)? = nil,
        leadingAction: @escaping () -> Void,
        @ViewBuilder leadingContent: @escaping () -> LeadingContent,
        label: String? = nil,
        optionalIndicator: String? = nil,
        supportText: String? = nil,
        placeholderText: String? = nil,
        errorMessage: String? = nil,
        error: Bool = false,
        enabled: Bool = true,
        @ViewBuilder trailingContent: @escaping () -> TrailingContent
    ) -> some View {
        LemonadeTextFieldWithSelectorView(
            input: input,
            onInputChanged: onInputChanged,
            leadingAction: leadingAction,
            leadingContent: leadingContent,
            label: label,
            optionalIndicator: optionalIndicator,
            supportText: supportText,
            placeholderText: placeholderText,
            errorMessage: errorMessage,
            error: error,
            enabled: enabled,
            trailingContent: trailingContent
        )
    }
}

// MARK: - TextFieldWithSelector with TextFieldValue (Cursor Control)

#if canImport(UIKit)
public extension LemonadeUi {
    /// A text input with selector using TextFieldValue for cursor position control.
    /// Use this for structured input like phone numbers where you need to format
    /// the input while maintaining correct cursor position.
    ///
    /// - Note: Cursor position control is fully supported on iOS. On macOS, the text field
    ///   functions normally but cursor position changes are not applied (no-op).
    ///
    /// ## Usage
    /// ```swift
    /// @State var phoneValue = LemonadeTextFieldValue(text: "")
    ///
    /// LemonadeUi.TextFieldWithSelector(
    ///     value: $phoneValue,
    ///     onValueChange: { newValue in
    ///         let formatted = formatPhoneNumber(newValue.text)
    ///         phoneValue = LemonadeTextFieldValue(text: formatted)
    ///     },
    ///     leadingAction: { showCountryPicker() },
    ///     leadingContent: {
    ///         HStack {
    ///             Text("+1")
    ///             Image(systemName: "chevron.down")
    ///         }
    ///     },
    ///     label: "Phone Number",
    ///     placeholderText: "Enter phone number"
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - value: The text field value including text and cursor position (Binding)
    ///   - onValueChange: Callback when the value changes
    ///   - leadingAction: Action triggered when the leading content is clicked
    ///   - leadingContent: Content displayed on the left as selector
    ///   - label: Label displayed above the text field
    ///   - optionalIndicator: Optional text displayed on the right of the label
    ///   - supportText: Support text displayed below the text field
    ///   - placeholderText: Placeholder text when the field is empty
    ///   - errorMessage: Error message displayed when error is true
    ///   - error: Whether the text field has an error
    ///   - enabled: Whether the text field is enabled
    /// - Returns: A styled TextFieldWithSelector view with cursor control
    @ViewBuilder
    static func TextFieldWithSelector<LeadingContent: View>(
        value: Binding<LemonadeTextFieldValue>,
        onValueChange: ((LemonadeTextFieldValue) -> Void)? = nil,
        leadingAction: @escaping () -> Void,
        @ViewBuilder leadingContent: @escaping () -> LeadingContent,
        label: String? = nil,
        optionalIndicator: String? = nil,
        supportText: String? = nil,
        placeholderText: String? = nil,
        errorMessage: String? = nil,
        error: Bool = false,
        enabled: Bool = true,
        keyboardType: UIKeyboardType? = nil
    ) -> some View {
        LemonadeTextFieldWithSelectorValueView<LeadingContent, EmptyView>(
            value: value,
            onValueChange: onValueChange,
            leadingAction: leadingAction,
            leadingContent: leadingContent,
            label: label,
            optionalIndicator: optionalIndicator,
            supportText: supportText,
            placeholderText: placeholderText,
            errorMessage: errorMessage,
            error: error,
            enabled: enabled,
            keyboardType: keyboardType,
            trailingContent: nil
        )
    }

    /// A text input with selector and trailing content using TextFieldValue for cursor control.
    ///
    /// - Parameters:
    ///   - value: The text field value including text and cursor position (Binding)
    ///   - onValueChange: Callback when the value changes
    ///   - leadingAction: Action triggered when the leading content is clicked
    ///   - leadingContent: Content displayed on the left as selector
    ///   - label: Label displayed above the text field
    ///   - optionalIndicator: Optional text displayed on the right of the label
    ///   - supportText: Support text displayed below the text field
    ///   - placeholderText: Placeholder text when the field is empty
    ///   - errorMessage: Error message displayed when error is true
    ///   - error: Whether the text field has an error
    ///   - enabled: Whether the text field is enabled
    ///   - trailingContent: Content displayed on the right inside the text field
    /// - Returns: A styled TextFieldWithSelector view with cursor control
    @ViewBuilder
    static func TextFieldWithSelector<LeadingContent: View, TrailingContent: View>(
        value: Binding<LemonadeTextFieldValue>,
        onValueChange: ((LemonadeTextFieldValue) -> Void)? = nil,
        leadingAction: @escaping () -> Void,
        @ViewBuilder leadingContent: @escaping () -> LeadingContent,
        label: String? = nil,
        optionalIndicator: String? = nil,
        supportText: String? = nil,
        placeholderText: String? = nil,
        errorMessage: String? = nil,
        error: Bool = false,
        enabled: Bool = true,
        keyboardType: UIKeyboardType? = nil,
        @ViewBuilder trailingContent: @escaping () -> TrailingContent
    ) -> some View {
        LemonadeTextFieldWithSelectorValueView(
            value: value,
            onValueChange: onValueChange,
            leadingAction: leadingAction,
            leadingContent: leadingContent,
            label: label,
            optionalIndicator: optionalIndicator,
            supportText: supportText,
            placeholderText: placeholderText,
            errorMessage: errorMessage,
            error: error,
            enabled: enabled,
            keyboardType: keyboardType,
            trailingContent: trailingContent
        )
    }
}
#endif

// MARK: - Internal TextField View

/// The inner editable control shared by the string-based text field variants,
/// switching between secure (masked) and plain entry.
///
/// On iOS it is backed by a `UITextField` (via `LemonadeUITextField`) that
/// toggles `isSecureTextEntry` *in place*. Flipping secure mode therefore keeps
/// the same field instance — and so the keyboard and first responder stay alive,
/// letting a show/hide toggle run mid-edit without interruption. A SwiftUI
/// `SecureField`/`TextField` swap would instead rebuild the field and dismiss the
/// keyboard. macOS (no UIKit) falls back to that SwiftUI pair.
#if canImport(UIKit)
private struct LemonadeTextInputField: View {
    @Binding var input: String
    let isSecure: Bool
    let enabled: Bool
    @Binding var isFocused: Bool
    let onInputChanged: ((String) -> Void)?
    let onSubmit: (() -> Void)?

    // The String-binding overloads expose no `keyboardType:` parameter, so the
    // `.lemonadeKeyboardType()` modifier (read from the environment here) is the
    // only way to drive their keyboard. The same applies to the text-input traits
    // below.
    @Environment(\.lemonadeKeyboardType) private var keyboardType
    @Environment(\.lemonadeTextContentType) private var textContentType
    @Environment(\.lemonadeAutocapitalizationType) private var autocapitalizationType
    @Environment(\.lemonadeAutocorrectionType) private var autocorrectionType

    // `input` (the public String binding) stays the source of truth; this local
    // value only carries the live cursor position that LemonadeUITextField needs.
    @State private var fieldValue: LemonadeTextFieldValue

    init(
        input: Binding<String>,
        isSecure: Bool,
        enabled: Bool,
        isFocused: Binding<Bool>,
        onInputChanged: ((String) -> Void)?,
        onSubmit: (() -> Void)?
    ) {
        _input = input
        self.isSecure = isSecure
        self.enabled = enabled
        _isFocused = isFocused
        self.onInputChanged = onInputChanged
        self.onSubmit = onSubmit
        _fieldValue = State(initialValue: LemonadeTextFieldValue(text: input.wrappedValue))
    }

    var body: some View {
        LemonadeUITextField(
            value: $fieldValue,
            isFocused: $isFocused,
            isEnabled: enabled,
            textStyle: LemonadeTypography.shared.bodyMediumRegular,
            textColor: LemonadeTheme.colors.content.contentPrimary,
            keyboardType: keyboardType,
            textContentType: textContentType,
            autocapitalizationType: autocapitalizationType,
            autocorrectionType: autocorrectionType,
            isSecure: isSecure,
            onValueChange: { newValue in
                if newValue.text != input { input = newValue.text }
                onInputChanged?(newValue.text)
            },
            onReturnKey: onSubmit
        )
        .onChange(of: input) { newText in
            // A change arriving on the public String binding carries no cursor position, so the
            // caret lands at the end of the new text.
            if newText != fieldValue.text {
                fieldValue = LemonadeTextFieldValue(text: newText)
            }
        }
    }
}
#else
private struct LemonadeTextInputField: View {
    @Binding var input: String
    let isSecure: Bool
    let enabled: Bool
    @Binding var isFocused: Bool
    let onInputChanged: ((String) -> Void)?
    let onSubmit: (() -> Void)?

    @FocusState private var fieldFocused: Bool

    var body: some View {
        Group {
            if isSecure {
                SwiftUI.SecureField("", text: $input)
            } else {
                SwiftUI.TextField("", text: $input)
            }
        }
        .font(LemonadeTypography.shared.bodyMediumRegular.font)
        .foregroundStyle(LemonadeTheme.colors.content.contentPrimary)
        .tint(LemonadeTheme.colors.content.contentPrimary)
        .focused($fieldFocused)
        .disabled(!enabled)
        .onSubmit { onSubmit?() }
        .onChange(of: input) { newValue in
            onInputChanged?(newValue)
        }
        .onChange(of: fieldFocused) { isFocused = $0 }
        .onChange(of: isFocused) { newValue in
            if fieldFocused != newValue { fieldFocused = newValue }
        }
    }
}
#endif

private struct LemonadeTextFieldView<LeadingContent: View, TrailingContent: View>: View {
    @Binding var input: String
    let onInputChanged: ((String) -> Void)?
    let onSubmit: (() -> Void)?
    let label: String?
    let optionalIndicator: String?
    let supportText: String?
    let placeholderText: String?
    let errorMessage: String?
    let error: Bool
    let enabled: Bool
    let leadingContent: (() -> LeadingContent)?
    let trailingContent: (() -> TrailingContent)?

    @State private var isFocused = false
    @State private var isHovered = false
    @Environment(\.lemonadeSecureTextEntry) private var isSecure

    var body: some View {
        VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing50) {
            TextFieldLabelRow(label: label, optionalIndicator: optionalIndicator, enabled: enabled)

            fieldContainer

            TextFieldSupportText(supportText: supportText, errorMessage: errorMessage, error: error, enabled: enabled)
        }
        .animation(.easeInOut(duration: 0.15), value: isFocused)
        .animation(.easeInOut(duration: 0.15), value: error)
    }

    private var fieldContainer: some View {
        HStack(spacing: LemonadeTheme.spaces.spacing300) {
            if let leadingContent = leadingContent {
                leadingContent()
            }

            ZStack(alignment: .leading) {
                if input.isEmpty, let placeholderText = placeholderText {
                    LemonadeUi.Text(
                        placeholderText,
                        textStyle: LemonadeTypography.shared.bodyMediumRegular,
                        color: LemonadeTheme.colors.content.contentSecondary
                    )
                }

                LemonadeTextInputField(
                    input: $input,
                    isSecure: isSecure,
                    enabled: enabled,
                    isFocused: $isFocused,
                    onInputChanged: onInputChanged,
                    onSubmit: onSubmit
                )
            }

            if let trailingContent = trailingContent {
                trailingContent()
            }
        }
        .padding(.horizontal, TextFieldConstants.horizontalPadding)
        .padding(.vertical, TextFieldConstants.verticalPadding)
        .modifier(TextFieldContainerModifier(
            backgroundColor: textFieldBackgroundColor(enabled: enabled, error: error, isFocused: isFocused, isHovered: isHovered),
            borderColor: textFieldBorderColor(enabled: enabled, isFocused: isFocused, error: error),
            enabled: enabled,
            isFocused: isFocused,
            cornerRadius: TextFieldConstants.cornerRadius,
            isHovered: $isHovered
        ))
    }
}

// MARK: - Internal TextField With Selector View

private struct LemonadeTextFieldWithSelectorView<LeadingContent: View, TrailingContent: View>: View {
    @Binding var input: String
    let onInputChanged: ((String) -> Void)?
    let leadingAction: () -> Void
    let leadingContent: () -> LeadingContent
    let label: String?
    let optionalIndicator: String?
    let supportText: String?
    let placeholderText: String?
    let errorMessage: String?
    let error: Bool
    let enabled: Bool
    let trailingContent: (() -> TrailingContent)?

    @State private var isFocused = false
    @State private var isHovered = false
    @Environment(\.lemonadeSecureTextEntry) private var isSecure

    var body: some View {
        VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing50) {
            TextFieldLabelRow(label: label, optionalIndicator: optionalIndicator, enabled: enabled)

            fieldContainer

            TextFieldSupportText(supportText: supportText, errorMessage: errorMessage, error: error, enabled: enabled)
        }
        .animation(.easeInOut(duration: 0.15), value: isFocused)
        .animation(.easeInOut(duration: 0.15), value: error)
    }

    private var fieldContainer: some View {
        HStack(spacing: 0) {
            TextFieldSelectorButton(
                leadingAction: leadingAction,
                leadingContent: leadingContent,
                enabled: enabled
            )

            TextFieldSelectorDivider()

            textInputArea
        }
        .modifier(TextFieldContainerModifier(
            backgroundColor: textFieldBackgroundColor(enabled: enabled, error: error, isFocused: isFocused, isHovered: isHovered),
            borderColor: textFieldBorderColor(enabled: enabled, isFocused: isFocused, error: error),
            enabled: enabled,
            isFocused: isFocused,
            cornerRadius: TextFieldConstants.cornerRadius,
            applyOpacity: false,
            isHovered: $isHovered
        ))
    }

    private var textInputArea: some View {
        HStack(spacing: LemonadeTheme.spaces.spacing300) {
            ZStack(alignment: .leading) {
                if input.isEmpty, let placeholderText = placeholderText {
                    LemonadeUi.Text(
                        placeholderText,
                        textStyle: LemonadeTypography.shared.bodyMediumRegular,
                        color: LemonadeTheme.colors.content.contentSecondary
                    )
                }

                LemonadeTextInputField(
                    input: $input,
                    isSecure: isSecure,
                    enabled: enabled,
                    isFocused: $isFocused,
                    onInputChanged: onInputChanged,
                    onSubmit: nil
                )
            }

            if let trailingContent = trailingContent {
                trailingContent()
            }
        }
        .padding(.horizontal, TextFieldConstants.horizontalPadding)
        .padding(.vertical, TextFieldConstants.verticalPadding)
        .opacity(enabled ? 1.0 : LemonadeTheme.opacity.state.opacityDisabled)
    }
}

// MARK: - Internal TextField Value View (Platform-specific)

#if canImport(UIKit)
private struct LemonadeTextFieldValueView<LeadingContent: View, TrailingContent: View>: View {
    @Binding var value: LemonadeTextFieldValue
    let onValueChange: ((LemonadeTextFieldValue) -> Void)?
    let label: String?
    let optionalIndicator: String?
    let supportText: String?
    let placeholderText: String?
    let errorMessage: String?
    let error: Bool
    let enabled: Bool
    // An explicit per-call keyboard type; `nil` falls back to the environment value
    // set by `.lemonadeKeyboardType()`.
    let keyboardType: UIKeyboardType?
    let leadingContent: (() -> LeadingContent)?
    let trailingContent: (() -> TrailingContent)?

    @State private var isFocused = false
    @State private var isHovered = false
    @Environment(\.lemonadeSecureTextEntry) private var isSecure
    @Environment(\.lemonadeKeyboardType) private var environmentKeyboardType
    @Environment(\.lemonadeTextContentType) private var textContentType
    @Environment(\.lemonadeAutocapitalizationType) private var autocapitalizationType
    @Environment(\.lemonadeAutocorrectionType) private var autocorrectionType

    // The explicit per-call type wins; otherwise fall back to the environment value.
    private var resolvedKeyboardType: UIKeyboardType { keyboardType ?? environmentKeyboardType }

    var body: some View {
        VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing50) {
            TextFieldLabelRow(label: label, optionalIndicator: optionalIndicator, enabled: enabled)

            fieldContainer

            TextFieldSupportText(supportText: supportText, errorMessage: errorMessage, error: error, enabled: enabled)
        }
        .animation(.easeInOut(duration: 0.15), value: isFocused)
        .animation(.easeInOut(duration: 0.15), value: error)
    }

    private var fieldContainer: some View {
        HStack(spacing: LemonadeTheme.spaces.spacing300) {
            if let leadingContent = leadingContent {
                leadingContent()
            }

            ZStack(alignment: .leading) {
                if value.text.isEmpty, let placeholderText = placeholderText {
                    LemonadeUi.Text(
                        placeholderText,
                        textStyle: LemonadeTypography.shared.bodyMediumRegular,
                        color: LemonadeTheme.colors.content.contentSecondary
                    )
                }

                LemonadeUITextField(
                    value: $value,
                    isFocused: $isFocused,
                    isEnabled: enabled,
                    textStyle: LemonadeTypography.shared.bodyMediumRegular,
                    textColor: LemonadeTheme.colors.content.contentPrimary,
                    keyboardType: resolvedKeyboardType,
                    textContentType: textContentType,
                    autocapitalizationType: autocapitalizationType,
                    autocorrectionType: autocorrectionType,
                    isSecure: isSecure,
                    onValueChange: onValueChange
                )
            }

            if let trailingContent = trailingContent {
                trailingContent()
            }
        }
        .padding(.horizontal, TextFieldConstants.horizontalPadding)
        .padding(.vertical, TextFieldConstants.verticalPadding)
        .modifier(TextFieldContainerModifier(
            backgroundColor: textFieldBackgroundColor(enabled: enabled, error: error, isFocused: isFocused, isHovered: isHovered),
            borderColor: textFieldBorderColor(enabled: enabled, isFocused: isFocused, error: error),
            enabled: enabled,
            isFocused: isFocused,
            cornerRadius: TextFieldConstants.cornerRadius,
            isHovered: $isHovered
        ))
    }
}
#else
// MARK: - macOS Fallback (cursor control is no-op)
private struct LemonadeTextFieldValueView<LeadingContent: View, TrailingContent: View>: View {
    @Binding var value: LemonadeTextFieldValue
    let onValueChange: ((LemonadeTextFieldValue) -> Void)?
    let label: String?
    let optionalIndicator: String?
    let supportText: String?
    let placeholderText: String?
    let errorMessage: String?
    let error: Bool
    let enabled: Bool
    let leadingContent: (() -> LeadingContent)?
    let trailingContent: (() -> TrailingContent)?

    private var textBinding: Binding<String> {
        Binding(
            get: { value.text },
            set: { newText in
                let newValue = LemonadeTextFieldValue(text: newText)
                value = newValue
                onValueChange?(newValue)
            }
        )
    }

    var body: some View {
        LemonadeTextFieldView(
            input: textBinding,
            onInputChanged: nil,
            onSubmit: nil,
            label: label,
            optionalIndicator: optionalIndicator,
            supportText: supportText,
            placeholderText: placeholderText,
            errorMessage: errorMessage,
            error: error,
            enabled: enabled,
            leadingContent: leadingContent,
            trailingContent: trailingContent
        )
    }
}
#endif

// MARK: - Internal TextField With Selector Value View (Platform-specific)

#if canImport(UIKit)
import UIKit

private struct LemonadeTextFieldWithSelectorValueView<LeadingContent: View, TrailingContent: View>: View {
    @Binding var value: LemonadeTextFieldValue
    let onValueChange: ((LemonadeTextFieldValue) -> Void)?
    let leadingAction: () -> Void
    let leadingContent: () -> LeadingContent
    let label: String?
    let optionalIndicator: String?
    let supportText: String?
    let placeholderText: String?
    let errorMessage: String?
    let error: Bool
    let enabled: Bool
    // An explicit per-call keyboard type; `nil` falls back to the environment value
    // set by `.lemonadeKeyboardType()`.
    let keyboardType: UIKeyboardType?
    let trailingContent: (() -> TrailingContent)?

    @State private var isFocused = false
    @State private var isHovered = false
    @Environment(\.lemonadeSecureTextEntry) private var isSecure
    @Environment(\.lemonadeKeyboardType) private var environmentKeyboardType
    @Environment(\.lemonadeTextContentType) private var textContentType
    @Environment(\.lemonadeAutocapitalizationType) private var autocapitalizationType
    @Environment(\.lemonadeAutocorrectionType) private var autocorrectionType

    // The explicit per-call type wins; otherwise fall back to the environment value.
    private var resolvedKeyboardType: UIKeyboardType { keyboardType ?? environmentKeyboardType }

    var body: some View {
        VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing50) {
            TextFieldLabelRow(label: label, optionalIndicator: optionalIndicator, enabled: enabled)

            fieldContainer

            TextFieldSupportText(supportText: supportText, errorMessage: errorMessage, error: error, enabled: enabled)
        }
        .animation(.easeInOut(duration: 0.15), value: isFocused)
        .animation(.easeInOut(duration: 0.15), value: error)
    }

    private var fieldContainer: some View {
        HStack(spacing: 0) {
            TextFieldSelectorButton(
                leadingAction: leadingAction,
                leadingContent: leadingContent,
                enabled: enabled
            )

            TextFieldSelectorDivider()

            textInputArea
        }
        .modifier(TextFieldContainerModifier(
            backgroundColor: textFieldBackgroundColor(enabled: enabled, error: error, isFocused: isFocused, isHovered: isHovered),
            borderColor: textFieldBorderColor(enabled: enabled, isFocused: isFocused, error: error),
            enabled: enabled,
            isFocused: isFocused,
            cornerRadius: TextFieldConstants.cornerRadius,
            applyOpacity: false,
            isHovered: $isHovered
        ))
    }

    private var textInputArea: some View {
        HStack(spacing: LemonadeTheme.spaces.spacing300) {
            ZStack(alignment: .leading) {
                if value.text.isEmpty, let placeholderText = placeholderText {
                    LemonadeUi.Text(
                        placeholderText,
                        textStyle: LemonadeTypography.shared.bodyMediumRegular,
                        color: LemonadeTheme.colors.content.contentSecondary
                    )
                }

                LemonadeUITextField(
                    value: $value,
                    isFocused: $isFocused,
                    isEnabled: enabled,
                    textStyle: LemonadeTypography.shared.bodyMediumRegular,
                    textColor: LemonadeTheme.colors.content.contentPrimary,
                    keyboardType: resolvedKeyboardType,
                    textContentType: textContentType,
                    autocapitalizationType: autocapitalizationType,
                    autocorrectionType: autocorrectionType,
                    isSecure: isSecure,
                    onValueChange: onValueChange
                )
            }

            if let trailingContent = trailingContent {
                trailingContent()
            }
        }
        .padding(.horizontal, TextFieldConstants.horizontalPadding)
        .padding(.vertical, TextFieldConstants.verticalPadding)
        .opacity(enabled ? 1.0 : LemonadeTheme.opacity.state.opacityDisabled)
    }
}
#else
// MARK: - macOS Fallback (cursor control is no-op)
private struct LemonadeTextFieldWithSelectorValueView<LeadingContent: View, TrailingContent: View>: View {
    @Binding var value: LemonadeTextFieldValue
    let onValueChange: ((LemonadeTextFieldValue) -> Void)?
    let leadingAction: () -> Void
    let leadingContent: () -> LeadingContent
    let label: String?
    let optionalIndicator: String?
    let supportText: String?
    let placeholderText: String?
    let errorMessage: String?
    let error: Bool
    let enabled: Bool
    let trailingContent: (() -> TrailingContent)?

    private var textBinding: Binding<String> {
        Binding(
            get: { value.text },
            set: { newText in
                let newValue = LemonadeTextFieldValue(text: newText)
                value = newValue
                onValueChange?(newValue)
            }
        )
    }

    var body: some View {
        LemonadeTextFieldWithSelectorView(
            input: textBinding,
            onInputChanged: nil,
            leadingAction: leadingAction,
            leadingContent: leadingContent,
            label: label,
            optionalIndicator: optionalIndicator,
            supportText: supportText,
            placeholderText: placeholderText,
            errorMessage: errorMessage,
            error: error,
            enabled: enabled,
            trailingContent: trailingContent
        )
    }
}
#endif

// MARK: - Previews

#if DEBUG
struct LemonadeTextField_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 24) {
            basicField
            fieldWithError
            disabledField
            fieldWithLeadingAndTrailingContent
            secureField
            fieldWithSelector
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }

    private static var basicField: some View {
        StatefulPreviewWrapper("") { input in
            LemonadeUi.TextField(
                input: input,
                label: "Label",
                optionalIndicator: "Optional",
                supportText: "Support text",
                placeholderText: "Enter text..."
            )
        }
    }

    private static var fieldWithError: some View {
        StatefulPreviewWrapper("Invalid input") { input in
            LemonadeUi.TextField(
                input: input,
                label: "Email",
                placeholderText: "Enter email",
                errorMessage: "Please enter a valid email",
                error: true
            )
        }
    }

    private static var disabledField: some View {
        StatefulPreviewWrapper("Disabled text") { input in
            LemonadeUi.TextField(
                input: input,
                label: "Disabled",
                enabled: false
            )
        }
    }

    private static var fieldWithLeadingAndTrailingContent: some View {
        StatefulPreviewWrapper("") { input in
            LemonadeUi.TextField(
                input: input,
                label: "Password",
                placeholderText: "Enter password"
            ) {
                LemonadeUi.Icon(
                    icon: .padlock,
                    contentDescription: nil,
                    size: .medium,
                    tint: LemonadeTheme.colors.content.contentSecondary
                )
            } trailingContent: {
                LemonadeUi.Icon(
                    icon: .eyeClosed,
                    contentDescription: nil,
                    size: .medium,
                    tint: LemonadeTheme.colors.content.contentSecondary
                )
            }
        }
    }

    private static var secureField: some View {
        StatefulPreviewWrapper("hunter2") { input in
            LemonadeUi.TextField(
                input: input,
                label: "Password",
                placeholderText: "Enter password"
            )
            .secureTextEntry()
        }
    }

    private static var fieldWithSelector: some View {
        StatefulPreviewWrapper("") { input in
            LemonadeUi.TextFieldWithSelector(
                input: input,
                leadingAction: { print("Selector tapped") },
                leadingContent: {
                    HStack(spacing: LemonadeTheme.spaces.spacing200) {
                        LemonadeUi.Text(
                            "+1",
                            textStyle: LemonadeTypography.shared.bodyMediumMedium
                        )
                        LemonadeUi.Icon(
                            icon: .chevronDown,
                            contentDescription: nil,
                            size: .small,
                            tint: LemonadeTheme.colors.content.contentPrimary
                        )
                    }
                },
                label: "Phone Number",
                placeholderText: "Enter phone number"
            )
        }
    }
}

#endif
