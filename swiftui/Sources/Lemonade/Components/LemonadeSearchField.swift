import SwiftUI

// MARK: - SearchField Component

public extension LemonadeUi {
    /// Input field designated to use for search and querying.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.SearchField(
    ///     input: $searchText,
    ///     onInputChanged: { newValue in searchText = newValue },
    ///     placeholder: "Search..."
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - input: Binding to the search text
    ///   - onInputChanged: Callback when the input changes
    ///   - placeholder: Optional placeholder text
    ///   - onInputClear: Callback when user requests input to be cleared
    ///   - dismissible: Flag controlling the trailing cancel button, on by default. The button shows
    ///     while the field is focused, and tapping it dismisses the keyboard and drops the focus,
    ///     leaving the input untouched. Turn it off for hosts that already provide their own
    ///     dismissal affordance.
    ///   - onCancel: Callback invoked after the search has been dismissed through the cancel button.
    ///     The input is left as typed — clearing it stays with the inner clear icon and
    ///     `onInputClear` — so use this to react to the dismissal itself, or to reset the binding
    ///     when the host wants dismissal to also drop the query.
    ///   - cancelContentDescription: Optional content description for the cancel button, for
    ///     accessibility. The component leaves it unset by default so the label can be localised by
    ///     the consumer; supply one whenever the field is `dismissible`.
    ///   - enabled: Flag to enable or disable the component
    ///   - isFocused: Optional two-way binding to the field's focus, for hosts that need to read it
    ///     or drive it — moving the field into a toolbar when a search begins, say, or handing focus
    ///     to a replacement field. Writing `true` focuses the field and raises the keyboard; the
    ///     field writes back whenever the user focuses or dismisses it. Left unset the field keeps
    ///     its focus entirely to itself and behaves exactly as before.
    /// - Returns: A styled SearchField view
    @ViewBuilder
    static func SearchField(
        input: Binding<String>,
        onInputChanged: ((String) -> Void)? = nil,
        placeholder: String? = nil,
        onInputClear: (() -> Void)? = nil,
        dismissible: Bool = true,
        onCancel: (() -> Void)? = nil,
        cancelContentDescription: String? = nil,
        enabled: Bool = true,
        isFocused: Binding<Bool>? = nil
    ) -> some View {
        LemonadeSearchFieldView(
            input: input,
            onInputChanged: onInputChanged,
            placeholder: placeholder,
            onInputClear: onInputClear,
            dismissible: dismissible,
            onCancel: onCancel,
            cancelContentDescription: cancelContentDescription,
            enabled: enabled,
            externalFocus: isFocused
        )
    }
}

// MARK: - Internal SearchField View

private struct LemonadeSearchFieldView: View {
    @Binding var input: String
    let onInputChanged: ((String) -> Void)?
    let placeholder: String?
    let onInputClear: (() -> Void)?
    let dismissible: Bool
    let onCancel: (() -> Void)?
    let cancelContentDescription: String?
    let enabled: Bool

    /// The host's mirror of the focus, when it supplied one. The field still owns the real
    /// `@FocusState`: `.focused(_:)` takes nothing else, and it is what invalidates this view when
    /// the *user* focuses or dismisses the field — a change the host's binding only learns about
    /// once we write it back. Everything keyed on focus, the cancel button above all, hangs off it.
    let externalFocus: Binding<Bool>?
    @FocusState private var isFocused: Bool

    private let height: CGFloat = LemonadeTheme.sizes.size1100
    private let horizontalPadding: CGFloat = LemonadeTheme.spaces.spacing300

    /// The cancel button pops in from — and collapses back to — slightly under its full size, so the
    /// entrance reads as the button growing into the gap the field gives up rather than blinking in.
    private let cancelCollapsedScale: CGFloat = 0.8

    /// Shared by the focus styling and the cancel button, so the pill and the button settle together.
    private let animationDuration: TimeInterval = 0.15

    private var backgroundColor: Color {
        isFocused
            ? LemonadeTheme.colors.background.bgDefault
            : LemonadeTheme.colors.background.bgElevated
    }

    private var borderColor: Color {
        isFocused
            ? LemonadeTheme.colors.border.borderSelected
            : .clear
    }

    /// Dismissal only drops the focus, so the button only earns its space while there is focus to
    /// drop.
    private var shouldShowCancel: Bool {
        dismissible && enabled && isFocused
    }

    var body: some View {
        HStack(spacing: LemonadeTheme.spaces.spacing200) {
            searchField

            if shouldShowCancel {
                LemonadeUi.IconButton(
                    icon: .times,
                    contentDescription: cancelContentDescription,
                    onClick: {
                        isFocused = false
                        onCancel?()
                    },
                    variant: .neutral,
                    type: .solid,
                    size: .small,
                    shape: .circular
                )
                .transition(.scale(scale: cancelCollapsedScale).combined(with: .opacity))
            }
        }
        .animation(.easeInOut(duration: animationDuration), value: shouldShowCancel)
        .onChange(of: isFocused) { newValue in
            guard let externalFocus, externalFocus.wrappedValue != newValue else { return }
            externalFocus.wrappedValue = newValue
        }
        .onChange(of: externalFocus?.wrappedValue) { newValue in
            guard let newValue, newValue != isFocused else { return }
            isFocused = newValue
        }
        .onAppear {
            // A host can hand the field focus before it ever renders, so adopt what it asked for on
            // the way in. Nothing is focused yet at this point, so only a requested `true` is worth
            // acting on, and it has to wait a frame: a `@FocusState` set synchronously from
            // `onAppear` lands before the field is in the hierarchy and is dropped.
            guard externalFocus?.wrappedValue == true else { return }
            DispatchQueue.main.async { isFocused = true }
        }
    }

    private var searchIcon: some View {
        LemonadeUi.Icon(
            icon: .search,
            contentDescription: nil,
            size: .medium,
            tint: LemonadeTheme.colors.content.contentPrimary
        )
    }

    private var textInput: some View {
        ZStack(alignment: .leading) {
            if let placeholder = placeholder, input.isEmpty {
                LemonadeUi.Text(
                    placeholder,
                    textStyle: LemonadeTypography.shared.bodyMediumRegular,
                    color: LemonadeTheme.colors.content.contentTertiary
                )
            }

            SwiftUI.TextField("", text: $input)
                .font(LemonadeTypography.shared.bodyMediumRegular.font)
                .foregroundStyle(LemonadeTheme.colors.content.contentPrimary)
                .tint(LemonadeTheme.colors.content.contentPrimary)
                .focused($isFocused)
                .disabled(!enabled)
                .onChange(of: input) { newValue in
                    onInputChanged?(newValue)
                }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }

    @ViewBuilder
    private var clearButton: some View {
        if !input.isEmpty && enabled {
            SwiftUI.Button(action: {
                if let onInputClear = onInputClear {
                    onInputClear()
                } else {
                    input = ""
                }
            }) {
                LemonadeUi.Icon(
                    icon: .circleXSolid,
                    contentDescription: "Clear",
                    size: .medium,
                    tint: LemonadeTheme.colors.content.contentSecondary
                )
            }
            .buttonStyle(PlainButtonStyle())
        }
    }

    private var searchField: some View {
        HStack(spacing: LemonadeTheme.spaces.spacing200) {
            searchIcon

            textInput

            clearButton
        }
        .padding(.horizontal, horizontalPadding)
        .frame(height: height)
        .background(Capsule().fill(backgroundColor))
        .overlay(
            Capsule()
                .stroke(borderColor, lineWidth: LemonadeTheme.borderWidth.state.borderSelected)
        )
        .overlay(
            Capsule()
                .stroke(
                    isFocused ? LemonadeTheme.colors.background.bgElevatedHigh : .clear,
                    lineWidth: LemonadeTheme.borderWidth.base.border50
                )
                .padding(-2)
        )
        .opacity(enabled ? 1.0 : LemonadeTheme.opacity.state.opacityDisabled)
        .animation(.easeInOut(duration: animationDuration), value: isFocused)
    }
}

// MARK: - Previews

#if DEBUG
struct LemonadeSearchField_Previews: PreviewProvider {
    private static var hostDrivenFocusField: some View {
        StatefulPreviewWrapper(false) { focus in
            StatefulPreviewWrapper("") { input in
                VStack(alignment: .leading, spacing: 8) {
                    LemonadeUi.SearchField(
                        input: input,
                        placeholder: "Host-driven focus...",
                        isFocused: focus
                    )

                    SwiftUI.Button("Focus from the host") { focus.wrappedValue = true }
                }
            }
        }
    }

    static var previews: some View {
        VStack(spacing: 24) {
            StatefulPreviewWrapper("") { input in
                LemonadeUi.SearchField(
                    input: input,
                    placeholder: "Search..."
                )
            }

            StatefulPreviewWrapper("Sample text") { input in
                LemonadeUi.SearchField(
                    input: input,
                    placeholder: "Search..."
                )
            }

            StatefulPreviewWrapper("Cancel callback") { input in
                LemonadeUi.SearchField(
                    input: input,
                    placeholder: "Search...",
                    onCancel: { input.wrappedValue = "" },
                    cancelContentDescription: "Cancel search"
                )
            }

            StatefulPreviewWrapper("Not dismissible") { input in
                LemonadeUi.SearchField(
                    input: input,
                    placeholder: "Search...",
                    dismissible: false
                )
            }

            StatefulPreviewWrapper("") { input in
                LemonadeUi.SearchField(
                    input: input,
                    placeholder: "Disabled search",
                    enabled: false
                )
            }

            hostDrivenFocusField
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
