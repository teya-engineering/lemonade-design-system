import SwiftUI

#if canImport(UIKit)
import UIKit
#endif

/// Input modes for ``LemonadeUi/PinCode(value:variant:length:error:submitting:autoFocus:accessibilityLabel:oneTimeCodeAutofill:onComplete:)``.
/// Selects which system keyboard is requested.
public enum LemonadePinCodeVariant {
    /// Requests a numeric keyboard.
    case numeric
    /// Requests the full keyboard.
    case alphanumeric
}

// MARK: - PinCode Component

public extension LemonadeUi {
    /// A PIN code entry component rendering the entered characters as a row of boxes.
    ///
    /// Each box carries the same border, focus ring, error and disabled treatment as a text
    /// field, at the same font and height.
    ///
    /// Input always comes from the device's system keyboard, surfaced through a hidden field
    /// overlaid on the boxes. The `variant` only selects which keyboard appears:
    /// - `.numeric` requests a numeric keyboard.
    /// - `.alphanumeric` requests the full keyboard.
    ///
    /// The indicator renders `length` boxes; each typed character is shown in its box. The
    /// component appends to and removes from `value` internally, never letting it grow past
    /// `length`.
    ///
    /// The hidden field uses the accessibility identifier `pin_code_field`, a stable contract for
    /// E2E tests.
    ///
    /// ## Usage
    /// ```swift
    /// @State private var pin = ""
    ///
    /// LemonadeUi.PinCode(
    ///     value: $pin,
    ///     onComplete: { code in /* validate */ }
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - value: The current entry. The component keeps it clamped to `length`.
    ///   - variant: Which system keyboard to request for input.
    ///   - length: The number of characters to enter. Defaults to 6.
    ///   - error: When true the boxes turn critical and shake. Re-triggers on each rising edge.
    ///   - submitting: When true the boxes show the disabled style and input is disabled.
    ///   - autoFocus: When true the field requests focus, opening the keyboard without a tap. Focus
    ///     is requested when it appears and again whenever the field becomes enabled (e.g. after
    ///     `submitting` clears). Use for a screen whose only purpose is entering this code.
    ///   - accessibilityLabel: Label for the input, announced by VoiceOver. The boxes carry no
    ///     visible label, so set this to what the code is for (e.g. "Verification code").
    ///   - oneTimeCodeAutofill: When true the field offers the OS one-time-code suggestion (the
    ///     "From Messages" QuickType item). Set false to suppress it on flows where it's unwanted.
    ///   - onComplete: Called once when `value` reaches `length`.
    /// - Returns: A styled PinCode view.
    static func PinCode(
        value: Binding<String>,
        variant: LemonadePinCodeVariant = .numeric,
        length: Int = 6,
        error: Bool = false,
        submitting: Bool = false,
        autoFocus: Bool = false,
        accessibilityLabel: String? = nil,
        oneTimeCodeAutofill: Bool = true,
        onComplete: ((String) -> Void)? = nil
    ) -> some View {
        precondition(length > 0, "PinCode length must be greater than zero.")
        return LemonadePinCodeView(
            value: value,
            variant: variant,
            length: length,
            error: error,
            submitting: submitting,
            autoFocus: autoFocus,
            accessibilityLabel: accessibilityLabel,
            oneTimeCodeAutofill: oneTimeCodeAutofill,
            onComplete: onComplete
        )
    }
}

// MARK: - Internal PinCode View

private struct LemonadePinCodeView: View {
    @Binding var value: String
    let variant: LemonadePinCodeVariant
    let length: Int
    let error: Bool
    let submitting: Bool
    let autoFocus: Bool
    let accessibilityLabel: String?
    let oneTimeCodeAutofill: Bool
    let onComplete: ((String) -> Void)?

    @State private var shakeTrigger: CGFloat = 0
    // Focus is owned here and read directly by the indicator (for the active ring) and bound
    // into the hidden field — no @State/@FocusState sync loop, so a tap focuses immediately.
    @FocusState private var focused: Bool

    var body: some View {
        ZStack {
            PinCodeIndicator(
                value: value,
                length: length,
                error: error,
                enabled: !submitting,
                focused: focused
            )
            .equatable()
            .modifier(ShakeEffect(animatableData: shakeTrigger))

            PinCodeHiddenField(
                value: $value,
                variant: variant,
                length: length,
                enabled: !submitting,
                accessibilityLabel: accessibilityLabel,
                oneTimeCodeAutofill: oneTimeCodeAutofill,
                focused: $focused
            )
        }
        .animation(.easeInOut(duration: 0.15), value: focused)
        .animation(.easeInOut(duration: 0.15), value: error)
        .onChange(of: error) { isError in
            guard isError else { return }
            #if canImport(UIKit)
            UINotificationFeedbackGenerator().notificationOccurred(.error)
            #endif
            withAnimation(.linear(duration: 0.3)) { shakeTrigger += 1 }
        }
        .onAppear {
            clampAndReport(value)
            requestAutoFocusIfNeeded()
        }
        .onChange(of: value) { clampAndReport($0) }
        // (Re)request focus when the field becomes enabled (e.g. `submitting` clears) or when
        // `autoFocus` turns on, not just on appear.
        .onChange(of: submitting) { _ in requestAutoFocusIfNeeded() }
        .onChange(of: autoFocus) { _ in requestAutoFocusIfNeeded() }
    }

    private func requestAutoFocusIfNeeded() {
        guard autoFocus, !submitting else { return }
        // Defer a frame so the field is in the hierarchy; setting @FocusState synchronously (e.g.
        // from onAppear) is dropped on first appearance.
        DispatchQueue.main.async { focused = true }
    }

    /// Enforces the "`value` stays clamped to `length`" contract for any value — including one set
    /// externally (e.g. restoring state) — then reports completion off the clamped result.
    private func clampAndReport(_ newValue: String) {
        let clamped = String(newValue.prefix(length))
        if clamped != newValue {
            // The write-back re-enters via .onChange and reports completion there; returning here
            // avoids firing onComplete twice for a single externally-set oversized value.
            value = clamped
            return
        }
        if clamped.count == length { onComplete?(clamped) }
    }
}

// MARK: - Indicator

// `Equatable` lets SwiftUI skip the whole indicator (and its `length` boxes) when none of
// its inputs changed — e.g. while another PinCode on the same screen is being edited.
private struct PinCodeIndicator: View, Equatable {
    let value: String
    let length: Int
    let error: Bool
    let enabled: Bool
    let focused: Bool

    private var filledCount: Int { min(value.count, length) }

    var body: some View {
        // Index the string once per render rather than re-walking it for every box.
        let characters = Array(value)
        // The row stretches to fill the width it is given and the boxes split it evenly.
        HStack(spacing: LemonadeTheme.spaces.spacing200) {
            ForEach(0 ..< length, id: \.self) { index in
                boxCell(character: index < characters.count ? characters[index] : nil, index: index)
            }
        }
        .frame(maxWidth: .infinity)
    }

    private func boxCell(character: Character?, index: Int) -> some View {
        // The "active" cell — the next one to fill — shows the focus ring while the
        // keyboard is up, matching a focused text field.
        let isActive = enabled && focused && index == filledCount
        return Color.clear
            .frame(maxWidth: .infinity)
            .overlay {
                if let character {
                    LemonadeUi.Text(
                        String(character),
                        textStyle: LemonadeTypography.shared.bodyMediumMedium,
                        color: enabled
                            ? LemonadeTheme.colors.content.contentPrimary
                            : LemonadeTheme.colors.content.contentTertiary
                    )
                }
            }
            .modifier(TextFieldContainerModifier(
                backgroundColor: textFieldBackgroundColor(enabled: enabled, error: error, isFocused: isActive, isHovered: false),
                // Keep a muted but visible outline when disabled — the boxes have no label/content
                // to fall back on, so the text field's transparent disabled border would make them
                // merge into the page. Skip the container's disabled opacity for the same reason.
                borderColor: enabled
                    ? textFieldBorderColor(enabled: enabled, isFocused: isActive, error: error)
                    : LemonadeTheme.colors.border.borderNeutralLow,
                enabled: enabled,
                isFocused: isActive,
                cornerRadius: TextFieldConstants.cornerRadius,
                applyOpacity: false,
                isHovered: .constant(false)
            ))
    }
}

// MARK: - Hidden field

private struct PinCodeHiddenField: View {
    @Binding var value: String
    let variant: LemonadePinCodeVariant
    let length: Int
    let enabled: Bool
    let accessibilityLabel: String?
    let oneTimeCodeAutofill: Bool
    @FocusState.Binding var focused: Bool

    // Local mirror of the entry that the field edits directly. Clamping inside a binding to `value`
    // leaves the overflow characters in the field's own buffer with the cursor parked beyond them,
    // because an unchanged `value` pushes no update back. Reassigning this @State to the clamped
    // text forces that push, dropping the overflow and collapsing the cursor to the end.
    @State private var text: String = ""

    var body: some View {
        // Full-bleed transparent field: a tap anywhere on the boxes lands here and focuses it
        // natively, so the keyboard opens on the first tap with no sync round-trip.
        TextField("", text: $text)
            .focused($focused)
            .autocorrectionDisabled(true)
            .foregroundColor(.clear)
            .tint(.clear)
            .disabled(!enabled)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .contentShape(Rectangle())
            .accessibilityIdentifier("pin_code_field")
            .modifier(OptionalAccessibilityLabel(label: accessibilityLabel))
            .modifier(SystemKeyboardTraits(variant: variant, oneTimeCodeAutofill: oneTimeCodeAutofill))
            .onAppear { text = String(value.prefix(length)) }
            .onChange(of: text) { newText in
                let clamped = String(newText.prefix(length))
                // Force the field back to the clamped text (drops overflow, collapses the cursor to
                // the end) whenever raw input exceeded `length` — including typing while it is full.
                if clamped != newText { text = clamped }
                if clamped != value { value = clamped }
            }
            // Keep the field in sync when the entry changes from outside (external writes, or the
            // parent clamping an oversized value).
            .onChange(of: value) { newValue in
                let clamped = String(newValue.prefix(length))
                if text != clamped { text = clamped }
            }
    }
}

private struct SystemKeyboardTraits: ViewModifier {
    let variant: LemonadePinCodeVariant
    let oneTimeCodeAutofill: Bool

    func body(content: Content) -> some View {
        #if os(iOS)
        // Deliberately NOT `nil`: an unset contentType does NOT stop iOS surfacing the
        // "From Messages" OTC suggestion. `.username` (any non-OTC, non-nil type) overrides
        // that heuristic. Verified on-device; do not change to nil without re-verifying.
        let contentType: UITextContentType = oneTimeCodeAutofill ? .oneTimeCode : .username
        Group {
            switch variant {
            case .numeric:
                content
                    .keyboardType(.numberPad)
            case .alphanumeric:
                content
                    .keyboardType(.asciiCapable)
                    .textInputAutocapitalization(.never)
            }
        }
        .textContentType(contentType)
        #else
        content
        #endif
    }
}

private struct OptionalAccessibilityLabel: ViewModifier {
    let label: String?

    func body(content: Content) -> some View {
        if let label {
            content.accessibilityLabel(label)
        } else {
            content
        }
    }
}

// MARK: - Shake

private struct ShakeEffect: GeometryEffect {
    var amount: CGFloat = 10
    var shakesPerUnit: CGFloat = 3
    var animatableData: CGFloat

    func effectValue(size: CGSize) -> ProjectionTransform {
        ProjectionTransform(
            CGAffineTransform(translationX: amount * sin(animatableData * .pi * shakesPerUnit), y: 0)
        )
    }
}


// MARK: - Previews

#if DEBUG
struct LemonadePinCode_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 32) {
            LemonadeUi.PinCode(value: .constant("123"))

            LemonadeUi.PinCode(
                value: .constant("123"),
                error: true
            )

            LemonadeUi.PinCode(
                value: .constant("12"),
                submitting: true
            )

            LemonadeUi.PinCode(
                value: .constant("aB3"),
                variant: .alphanumeric
            )
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
