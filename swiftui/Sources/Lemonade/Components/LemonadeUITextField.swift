import SwiftUI

#if canImport(UIKit)
import UIKit

/// Internal UIViewRepresentable wrapper around UITextField for cursor position control.
/// This enables LemonadeTextFieldValue support on iOS.
///
/// - Note: Cursor positions are measured in UTF-16 code units, the units UIKit indexes text
///   positions in.
internal struct LemonadeUITextField: UIViewRepresentable {
    /// Read from the environment rather than passed in, so a single change here covers every
    /// public TextField overload without touching any of their signatures.
    @Environment(\.lemonadeTextInputTransformation) private var inputTransformation
    @Environment(\.lemonadeTextDisplayDecoration) private var displayDecoration
    @Environment(\.lemonadeTextSelectionConstraint) private var selectionConstraint

    @Binding var value: LemonadeTextFieldValue
    @Binding var isFocused: Bool
    var isEnabled: Bool
    var textStyle: LemonadeTextStyle
    var textColor: Color
    var keyboardType: UIKeyboardType = .default
    /// Drives iOS AutoFill (username/password/one-time-code/…) via
    /// `UITextField.textContentType`. `nil` leaves it unset.
    var textContentType: UITextContentType?
    var autocapitalizationType: UITextAutocapitalizationType = .sentences
    var autocorrectionType: UITextAutocorrectionType = .default
    /// Enables native secure text entry (character masking). Note: UIKit clears
    /// the field's text and selection when this flips, so `updateUIView` applies
    /// it before re-synchronizing text and cursor to keep them stable on toggle.
    var isSecure: Bool = false
    var onValueChange: ((LemonadeTextFieldValue) -> Void)?
    var onEditingChanged: ((Bool) -> Void)?
    /// Called when the keyboard's return key is pressed. When set, the field keeps
    /// first responder (the keyboard stays up) so the caller drives what happens
    /// next; when `nil`, the return key dismisses the keyboard.
    var onReturnKey: (() -> Void)?

    /// Clamps a cursor position to the text's UTF-16 range, and then into the constrained span.
    private func clampedCursorPosition(_ position: Int, for text: String) -> Int {
        let utf16Length = text.utf16.count
        let inText = min(max(position, 0), utf16Length)
        guard let allowed = selectionConstraint?.allowedSpan(in: text) else { return inText }
        return min(max(inText, allowed.lowerBound), allowed.upperBound)
    }

    func makeUIView(context: Context) -> LemonadeClampingTextField {
        let textField = LemonadeClampingTextField()
        textField.selectionConstraint = selectionConstraint
        textField.delegate = context.coordinator
        textField.font = textStyle.uiFont
        textField.textColor = UIColor(textColor)
        textField.borderStyle = .none
        textField.backgroundColor = .clear
        textField.tintColor = UIColor(textColor)
        textField.isEnabled = isEnabled
        textField.keyboardType = keyboardType
        textField.textContentType = textContentType
        textField.autocapitalizationType = autocapitalizationType
        textField.autocorrectionType = autocorrectionType
        textField.isSecureTextEntry = isSecure
        textField.text = value.text

        // Let SwiftUI compress the field to the available width instead of growing it to the
        // text's intrinsic width. Without this, long input pushes the layout past the screen edge.
        textField.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        textField.setContentHuggingPriority(.defaultLow, for: .horizontal)

        // Pin the field to its intrinsic (single-line) height. A UITextField defaults to low
        // vertical hugging, so when SwiftUI offers unbounded height (e.g. inside a flexible
        // VStack) the field grows to fill it. SwiftUI's own TextField never does this.
        textField.setContentHuggingPriority(.required, for: .vertical)
        textField.setContentCompressionResistancePriority(.required, for: .vertical)

        textField.setCaret(toUTF16Offset: clampedCursorPosition(value.cursorPosition, for: value.text))

        textField.addTarget(
            context.coordinator,
            action: #selector(Coordinator.textFieldDidChange(_:)),
            for: .editingChanged
        )

        return textField
    }

    func updateUIView(_ textField: LemonadeClampingTextField, context: Context) {
        context.coordinator.parent = self
        textField.selectionConstraint = selectionConstraint

        // Our own callbacks write back through the binding, which re-enters here; without the
        // guard the two chase each other.
        guard !context.coordinator.isUpdating else { return }
        context.coordinator.isUpdating = true
        defer { context.coordinator.isUpdating = false }

        // Toggle secure entry *before* synchronizing text/cursor: UIKit clears a
        // field's contents and selection when isSecureTextEntry flips, so the text +
        // cursor sync below must run afterwards to restore them.
        if textField.isSecureTextEntry != isSecure {
            textField.isSecureTextEntry = isSecure
        }

        syncText(textField)
        syncCaret(textField)
        applyAppearance(textField)
        syncInputTraits(textField)
        applyDisplayDecoration(textField)
        updateFocus(textField)
    }

    private func syncText(_ textField: UITextField) {
        guard (textField.text ?? "") != value.text else { return }
        textField.text = value.text
    }

    private func syncCaret(_ textField: UITextField) {
        let clampedPosition = clampedCursorPosition(value.cursorPosition, for: textField.text ?? "")
        let currentOffset = textField.selectedTextRange.map {
            textField.offset(from: textField.beginningOfDocument, to: $0.start)
        } ?? -1

        // Writing an unchanged selection back jumps the caret, so only a real move is applied.
        guard currentOffset != clampedPosition else { return }
        textField.setCaret(toUTF16Offset: clampedPosition)
    }

    private func applyAppearance(_ textField: UITextField) {
        textField.isEnabled = isEnabled
        textField.font = textStyle.uiFont
        textField.textColor = UIColor(textColor)
        textField.tintColor = UIColor(textColor)
    }

    /// Each trait is reassigned only when it actually changed, to avoid needless per-keystroke
    /// churn on the focused field. `keyboardType` is the only one that needs `reloadInputViews()`
    /// to take effect while the field is up.
    private func syncInputTraits(_ textField: UITextField) {
        if textField.keyboardType != keyboardType {
            textField.keyboardType = keyboardType
            if textField.isFirstResponder {
                textField.reloadInputViews()
            }
        }
        if textField.textContentType != textContentType {
            textField.textContentType = textContentType
        }
        if textField.autocapitalizationType != autocapitalizationType {
            textField.autocapitalizationType = autocapitalizationType
        }
        if textField.autocorrectionType != autocorrectionType {
            textField.autocorrectionType = autocorrectionType
        }
    }

    /// Redraws the field's text through the decoration, if one is set.
    ///
    /// The decoration cannot change the character count, so the caret index is still valid
    /// afterwards — but assigning `attributedText` moves the caret to the end regardless, so it is
    /// captured and restored around the write.
    private func applyDisplayDecoration(_ textField: UITextField) {
        guard let decoration = displayDecoration else { return }

        // Assigning attributedText cancels an in-flight multi-stage composition.
        guard textField.markedTextRange == nil else { return }

        let text = textField.text ?? ""
        let decorated = decoration.decorate(
            text,
            baseAttributes: [
                .font: textStyle.uiFont,
                .foregroundColor: UIColor(textColor),
            ]
        )
        guard textField.attributedText != decorated else { return }

        let caret = textField.selectedTextRange.map {
            textField.offset(from: textField.beginningOfDocument, to: $0.start)
        }
        textField.attributedText = decorated
        if let caret {
            textField.setCaret(toUTF16Offset: caret)
        }
    }

    private func updateFocus(_ textField: UITextField) {
        if isFocused && !textField.isFirstResponder {
            textField.becomeFirstResponder()
        } else if !isFocused && textField.isFirstResponder {
            textField.resignFirstResponder()
        }
    }

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    class Coordinator: NSObject, UITextFieldDelegate {
        var parent: LemonadeUITextField
        var isUpdating = false

        init(_ parent: LemonadeUITextField) {
            self.parent = parent
        }

        @objc func textFieldDidChange(_ textField: UITextField) {
            guard !isUpdating else { return }

            let text = textField.text ?? ""
            let cursorPosition = getCursorPosition(textField)

            let newValue = LemonadeTextFieldValue(
                text: text,
                cursorPosition: cursorPosition
            )
            parent.value = newValue
            parent.onValueChange?(newValue)
        }

        func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
            // A disabled field must never enter editing, even if a focus attempt
            // reaches it — otherwise it would gain first responder and a focus ring.
            parent.isEnabled
        }

        func textFieldDidBeginEditing(_ textField: UITextField) {
            parent.isFocused = true
            parent.onEditingChanged?(true)
        }

        func textFieldDidEndEditing(_ textField: UITextField) {
            parent.isFocused = false
            parent.onEditingChanged?(false)
        }

        func textFieldDidChangeSelection(_ textField: UITextField) {
            guard !isUpdating else { return }

            isUpdating = true
            clampSelection(textField)
            isUpdating = false

            let cursorPosition = getCursorPosition(textField)
            if parent.value.cursorPosition != cursorPosition {
                isUpdating = true
                defer { isUpdating = false }
                let newValue = LemonadeTextFieldValue(
                    text: parent.value.text,
                    cursorPosition: cursorPosition
                )
                parent.value = newValue
                parent.onValueChange?(newValue)
            }
        }

        func textField(
            _ textField: UITextField,
            shouldChangeCharactersIn range: NSRange,
            replacementString string: String
        ) -> Bool {
            // Multi-stage input (kana, pinyin, Devanagari) is mid-composition: the provisional
            // text lives in the field's marked range, and assigning `.text` cancels it. Never
            // intercept while that is in flight.
            if textField.markedTextRange != nil { return true }

            // AutoFill probes an empty field with a lone space before offering its suggestion.
            // Reject the probe and the suggestion never appears at all.
            if textField.textContentType != nil,
               (textField.text ?? "").isEmpty,
               range.location == 0, range.length == 0, string == " " {
                return true
            }

            // The selection clamp keeps the caret out of the symbol, but not the *edit*: with the
            // caret pinned just after a leading "$", UIKit still proposes (0, 1) for a backspace.
            if let allowed = allowedSpan(for: textField),
               range.location < allowed.lowerBound || range.location + range.length > allowed.upperBound {
                return false
            }

            guard let transformation = parent.inputTransformation,
                  let oldText = textField.text else {
                return applySecureEntryEdit(textField, range: range, replacement: string)
            }

            let edit = LemonadeTextEdit(
                currentText: oldText,
                range: range,
                replacement: string
            )
            guard let result = transformation.transform(edit) else {
                return applySecureEntryEdit(textField, range: range, replacement: string)
            }

            // The transformation lands on exactly what UIKit would have produced. Take the native
            // path: every `false` we return instead is a hole in undo, autocorrect and the
            // predictive bar.
            let naive = (oldText as NSString).replacingCharacters(in: range, with: string)
            let naiveCaret = range.location + (string as NSString).length
            if result.text == naive, result.cursorPosition == naiveCaret {
                return applySecureEntryEdit(textField, range: range, replacement: string)
            }

            // `replace(_:withText:)` routes through the text-input system and so may fire
            // `.editingChanged` itself; suppress that and notify once, deliberately, below.
            isUpdating = true
            applyOverride(to: textField, newText: result.text, caret: result.cursorPosition)
            isUpdating = false

            textFieldDidChange(textField)
            return false
        }

        /// Overwrites only the span that actually differs.
        ///
        /// Assigning `.text` wholesale would bypass the field's own undo manager, so one keystroke
        /// undoes the entire field — and a rejected paste can be left on the undo stack, where
        /// shake-to-undo then crashes on it.
        private func applyOverride(to textField: UITextField, newText: String, caret: Int) {
            let edit = lemonadeMinimalReplacement(from: textField.text ?? "", to: newText)

            if let start = textField.position(
                from: textField.beginningOfDocument,
                offset: edit.range.location
            ),
               let end = textField.position(from: start, offset: edit.range.length),
               let uiRange = textField.textRange(from: start, to: end) {
                // Going through the text-input system, rather than assigning `.text`, is what
                // registers the edit with the field's own undo manager.
                textField.replace(uiRange, withText: edit.replacement)
            } else {
                textField.text = newText
            }

            textField.setCaret(toUTF16Offset: clampedCaret(caret, for: newText))
        }

        /// A secure `UITextField` wipes all of its text on the first edit after the text was
        /// assigned programmatically — which we do when restoring content across a secure-entry
        /// toggle. Applying the edit ourselves keeps the field stable when the user carries on
        /// typing after a show/hide toggle.
        private func applySecureEntryEdit(
            _ textField: UITextField,
            range: NSRange,
            replacement: String
        ) -> Bool {
            guard textField.isSecureTextEntry else { return true }
            guard let oldText = textField.text,
                  let replaceRange = Range(range, in: oldText) else { return true }

            textField.text = oldText.replacingCharacters(in: replaceRange, with: replacement)
            textField.setCaret(toUTF16Offset: range.location + (replacement as NSString).length)

            // Programmatic text changes don't fire `.editingChanged`, so propagate manually.
            textFieldDidChange(textField)
            return false
        }

        func textFieldShouldReturn(_ textField: UITextField) -> Bool {
            if let onReturnKey = parent.onReturnKey {
                onReturnKey()
            } else {
                textField.resignFirstResponder()
            }
            return true
        }

        /// Pulls the caret — or both ends of a selection — back into the constrained span.
        ///
        /// Assigning `selectedTextRange` re-enters this delegate synchronously, so the write is made
        /// under `isUpdating` and only when the range actually changes. Doing it synchronously is
        /// what keeps the caret from being *drawn* in the symbol: deferring the correction to the
        /// next runloop turn paints a handful of frames with the caret in the forbidden region.
        private func clampSelection(_ textField: UITextField) {
            guard let allowed = allowedSpan(for: textField),
                  let selection = textField.selectedTextRange else { return }

            let document = textField.beginningOfDocument
            let start = textField.offset(from: document, to: selection.start)
            let end = textField.offset(from: document, to: selection.end)
            let clampedStart = min(max(start, allowed.lowerBound), allowed.upperBound)
            let clampedEnd = min(max(end, allowed.lowerBound), allowed.upperBound)
            guard clampedStart != start || clampedEnd != end else { return }

            guard let startPosition = textField.position(from: document, offset: clampedStart),
                  let endPosition = textField.position(from: document, offset: clampedEnd),
                  let clamped = textField.textRange(from: startPosition, to: endPosition) else { return }
            textField.selectedTextRange = clamped
        }

        private func allowedSpan(for textField: UITextField) -> ClosedRange<Int>? {
            parent.selectionConstraint?.allowedSpan(in: textField.text ?? "")
        }

        private func clampedCaret(_ caret: Int, for text: String) -> Int {
            guard let allowed = parent.selectionConstraint?.allowedSpan(in: text) else { return caret }
            return min(max(caret, allowed.lowerBound), allowed.upperBound)
        }

        /// The caret's offset in UTF-16 code units, the units UIKit indexes text positions in.
        private func getCursorPosition(_ textField: UITextField) -> Int {
            guard let selectedRange = textField.selectedTextRange else { return 0 }
            return textField.offset(from: textField.beginningOfDocument, to: selectedRange.start)
        }
    }
}

/// A field that never resolves a touch to a position outside the constrained span.
///
/// `closestPosition(to:)` is the one function UIKit calls to turn a touch point into a text
/// position, so clamping it constrains the tap, the drag and the loupe at the source, and the
/// selection-change clamp never has to correct them. It does not see keyboard-driven selection
/// (arrow keys, ⌘A) or word selection, which is why the delegate clamp remains the backstop.
internal final class LemonadeClampingTextField: UITextField {
    var selectionConstraint: LemonadeTextSelectionConstraint?

    override func closestPosition(to point: CGPoint) -> UITextPosition? {
        guard let closest = super.closestPosition(to: point) else { return nil }
        guard let allowed = selectionConstraint?.allowedSpan(in: text ?? "") else { return closest }

        let offset = self.offset(from: beginningOfDocument, to: closest)
        let clamped = min(max(offset, allowed.lowerBound), allowed.upperBound)
        guard clamped != offset else { return closest }
        return position(from: beginningOfDocument, offset: clamped) ?? closest
    }
}

internal extension LemonadeTextSelectionConstraint {
    /// The allowed span for `text`, clamped to its UTF-16 length so both bounds are always valid
    /// positions in the field.
    func allowedSpan(in text: String) -> ClosedRange<Int>? {
        guard let allowed = allowedRange(in: text) else { return nil }
        let length = text.utf16.count
        let lower = min(max(allowed.lowerBound, 0), length)
        let upper = min(max(allowed.upperBound, lower), length)
        return lower...upper
    }
}

private extension UITextField {
    /// Places the caret (a collapsed selection) at the given UTF-16 code unit offset.
    func setCaret(toUTF16Offset offset: Int) {
        guard let position = position(from: beginningOfDocument, offset: offset) else { return }
        selectedTextRange = textRange(from: position, to: position)
    }
}
#endif
