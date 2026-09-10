import SwiftUI

// MARK: - SelectField Component

public extension LemonadeUi {
    /// Select Field allows users to trigger an options list or picker without typing.
    /// Visually identical to TextField but the entire field is clickable (no text input).
    /// Ideal for country selection, category pickers, or any dropdown trigger.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.SelectField(
    ///     onClick: { showPicker = true },
    ///     selectedValue: selectedOption,
    ///     label: "Category",
    ///     placeholderText: "Select a category"
    /// )
    /// ```
    @ViewBuilder
    static func SelectField(
        onClick: @escaping () -> Void,
        selectedValue: String?,
        placeholderText: String? = nil,
        label: String? = nil,
        optionalIndicator: String? = nil,
        supportText: String? = nil,
        errorMessage: String? = nil,
        error: Bool = false,
        enabled: Bool = true
    ) -> some View {
        LemonadeSelectFieldView<EmptyView>(
            onClick: onClick,
            selectedValue: selectedValue,
            placeholderText: placeholderText,
            label: label,
            optionalIndicator: optionalIndicator,
            supportText: supportText,
            errorMessage: errorMessage,
            error: error,
            enabled: enabled,
            leadingContent: nil
        )
    }

    /// Select Field with optional leading content (e.g., an icon).
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.SelectField(
    ///     onClick: { showPicker = true },
    ///     selectedValue: "Favourites",
    ///     label: "Collection"
    /// ) {
    ///     LemonadeUi.Icon(icon: .heart, contentDescription: nil)
    /// }
    /// ```
    @ViewBuilder
    static func SelectField<LeadingContent: View>(
        onClick: @escaping () -> Void,
        selectedValue: String?,
        placeholderText: String? = nil,
        label: String? = nil,
        optionalIndicator: String? = nil,
        supportText: String? = nil,
        errorMessage: String? = nil,
        error: Bool = false,
        enabled: Bool = true,
        @ViewBuilder leadingContent: @escaping () -> LeadingContent
    ) -> some View {
        LemonadeSelectFieldView(
            onClick: onClick,
            selectedValue: selectedValue,
            placeholderText: placeholderText,
            label: label,
            optionalIndicator: optionalIndicator,
            supportText: supportText,
            errorMessage: errorMessage,
            error: error,
            enabled: enabled,
            leadingContent: leadingContent
        )
    }
}

// MARK: - Internal SelectField View

private struct LemonadeSelectFieldView<LeadingContent: View>: View {
    let onClick: () -> Void
    let selectedValue: String?
    let placeholderText: String?
    let label: String?
    let optionalIndicator: String?
    let supportText: String?
    let errorMessage: String?
    let error: Bool
    let enabled: Bool
    let leadingContent: (() -> LeadingContent)?

    @State private var isHovered = false

    var body: some View {
        VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing50) {
            TextFieldLabelRow(label: label, optionalIndicator: optionalIndicator, enabled: enabled)

            fieldContainer

            TextFieldSupportText(supportText: supportText, errorMessage: errorMessage, error: error, enabled: enabled)
        }
        .animation(.easeInOut(duration: 0.15), value: error)
    }

    private var fieldContainer: some View {
        SwiftUI.Button(action: onClick) {
            HStack(spacing: LemonadeTheme.spaces.spacing300) {
                if let leadingContent = leadingContent {
                    leadingContent()
                }

                if let text = selectedValue.flatMap({ $0.trimmingCharacters(in: .whitespaces).isEmpty ? nil : $0 }) {
                    LemonadeUi.Text(
                        text,
                        textStyle: LemonadeTypography.shared.bodyMediumRegular,
                        color: LemonadeTheme.colors.content.contentPrimary
                    )
                    .lineLimit(1)
                } else if let placeholderText = placeholderText {
                    LemonadeUi.Text(
                        placeholderText,
                        textStyle: LemonadeTypography.shared.bodyMediumRegular,
                        color: LemonadeTheme.colors.content.contentSecondary
                    )
                    .lineLimit(1)
                }

                Spacer(minLength: 0)

                LemonadeUi.Icon(
                    icon: .chevronDown,
                    contentDescription: nil,
                    tint: LemonadeTheme.colors.content.contentSecondary
                )
            }
            .padding(.horizontal, TextFieldConstants.horizontalPadding)
            .padding(.vertical, TextFieldConstants.verticalPadding)
            // Hit-test the whole field, not just the opaque text + chevron. Without this the
            // empty space around the Spacer falls through and taps there don't register.
            .contentShape(Rectangle())
        }
        .buttonStyle(PlainButtonStyle())
        .disabled(!enabled)
        .modifier(SelectFieldContainerModifier(
            backgroundColor: selectFieldBackgroundColor(enabled: enabled, error: error, isHovered: isHovered),
            borderColor: selectFieldBorderColor(enabled: enabled, error: error),
            enabled: enabled,
            cornerRadius: TextFieldConstants.cornerRadius,
            isHovered: $isHovered
        ))
        .accessibilityAddTraits(.isButton)
        .accessibilityRemoveTraits(.isStaticText)
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeSelectField_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: LemonadeTheme.spaces.spacing600) {
            LemonadeUi.SelectField(
                onClick: {},
                selectedValue: nil,
                placeholderText: "Select an option",
                label: "Category"
            )

            LemonadeUi.SelectField(
                onClick: {},
                selectedValue: "English",
                label: "Language"
            )

            LemonadeUi.SelectField(
                onClick: {},
                selectedValue: nil,
                placeholderText: "Select an option",
                label: "Required",
                errorMessage: "Please select an option",
                error: true
            )

            LemonadeUi.SelectField(
                onClick: {},
                selectedValue: "Locked value",
                label: "Disabled",
                enabled: false
            )
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
