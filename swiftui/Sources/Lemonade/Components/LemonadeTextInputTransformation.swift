import SwiftUI
import Foundation

/// One proposed edit to a text field, exactly as UIKit reports it.
public struct LemonadeTextEdit: Equatable {
    /// The field's text before this edit.
    public let currentText: String

    /// The UTF-16 range being replaced.
    public let range: NSRange

    /// The proposed replacement. Empty for a deletion, multi-character for a paste or AutoFill.
    public let replacement: String

    /// The selection before the edit, in UTF-16 offsets. Tells a backspace from a forward delete.
    public let selection: NSRange?

    public init(currentText: String, range: NSRange, replacement: String, selection: NSRange? = nil) {
        self.currentText = currentText
        self.range = range
        self.replacement = replacement
        self.selection = selection
    }
}

/// The text a field should hold after an edit, and where the caret goes.
public struct LemonadeTextEditResult: Equatable {
    public let text: String

    /// Caret position as a UTF-16 offset, clamped to `0...text.utf16.count`.
    public let cursorPosition: Int

    public init(text: String, cursorPosition: Int) {
        self.text = text
        self.cursorPosition = min(max(cursorPosition, 0), text.utf16.count)
    }
}

/// A synchronous per-keystroke filter for a Lemonade text field.
///
/// This is the only place an edit can be corrected without the field ever showing the uncorrected
/// version: it runs inside `UITextFieldDelegate.textField(_:shouldChangeCharactersIn:_:)`, in the
/// same call stack as the keystroke, before UIKit has applied anything.
public protocol LemonadeTextInputTransformation {
    /// Return `nil` to accept UIKit's own edit unchanged.
    ///
    /// This is the fast path and the one to prefer — it leaves undo, autocorrect, the predictive
    /// bar and multi-stage input alone. Return a result only when the edit must be rejected or
    /// rewritten.
    func transform(_ edit: LemonadeTextEdit) -> LemonadeTextEditResult?
}

/// The span of a field's text the caret and any selection may occupy.
///
/// This is what lets a currency symbol live in the field's own text: the symbol is real text, but
/// every offset outside the returned span is unreachable, so the user can neither put the caret
/// there nor select across it.
public protocol LemonadeTextSelectionConstraint {
    /// The UTF-16 offsets the caret may occupy in `text`. Return `nil` to leave the field free.
    func allowedRange(in text: String) -> ClosedRange<Int>?
}

/// Confines the caret to the digits of a formatted number, so that a currency symbol on either side
/// of them — `$2.80`, `2,80 kr.` — is part of the text yet can never be reached or deleted.
public struct LemonadeDigitSpanSelectionConstraint: LemonadeTextSelectionConstraint {
    public init() {}

    public func allowedRange(in text: String) -> ClosedRange<Int>? {
        let units = Array(text.utf16)
        guard let first = units.firstIndex(where: Self.isDigit),
              let last = units.lastIndex(where: Self.isDigit) else { return nil }
        return first...(last + 1)
    }

    private static func isDigit(_ unit: UTF16.CodeUnit) -> Bool {
        guard let scalar = Unicode.Scalar(unit) else { return false }
        return CharacterSet.decimalDigits.contains(scalar)
    }
}

/// Cosmetic decoration of a field's text.
///
/// **Must not change the character count** — index `i` of the returned string has to be index `i`
/// of `text`. Widen a character with kerning; never insert one. Keeping the buffer and the visible
/// text index-identical is what makes the caret need no restoring at all, which in turn keeps undo,
/// AutoFill and multi-stage input untouched.
public protocol LemonadeTextDisplayDecoration {
    func decorate(
        _ text: String,
        baseAttributes: [NSAttributedString.Key: Any]
    ) -> NSAttributedString
}

/// The smallest edit that turns `old` into `new`: the span to replace, and what to put there.
///
/// Overwriting a field's whole buffer would make a single keystroke undo the entire field, so an
/// override is applied as the narrowest possible replacement instead. Offsets are UTF-16, matching
/// `NSRange` and `UITextPosition`.
internal func lemonadeMinimalReplacement(
    from old: String,
    to new: String
) -> (range: NSRange, replacement: String) {
    let oldUnits = Array(old.utf16)
    let newUnits = Array(new.utf16)

    var prefix = 0
    while prefix < oldUnits.count, prefix < newUnits.count, oldUnits[prefix] == newUnits[prefix] {
        prefix += 1
    }

    var suffix = 0
    while suffix < oldUnits.count - prefix, suffix < newUnits.count - prefix,
          oldUnits[oldUnits.count - 1 - suffix] == newUnits[newUnits.count - 1 - suffix] {
        suffix += 1
    }

    let inserted = Array(newUnits[prefix ..< (newUnits.count - suffix)])
    return (
        range: NSRange(location: prefix, length: oldUnits.count - prefix - suffix),
        replacement: String(utf16CodeUnits: inserted, count: inserted.count)
    )
}

// MARK: - Environment

private struct LemonadeTextInputTransformationKey: EnvironmentKey {
    static let defaultValue: LemonadeTextInputTransformation? = nil
}

private struct LemonadeTextDisplayDecorationKey: EnvironmentKey {
    static let defaultValue: LemonadeTextDisplayDecoration? = nil
}

private struct LemonadeTextSelectionConstraintKey: EnvironmentKey {
    static let defaultValue: LemonadeTextSelectionConstraint? = nil
}

public extension EnvironmentValues {
    var lemonadeTextInputTransformation: LemonadeTextInputTransformation? {
        get { self[LemonadeTextInputTransformationKey.self] }
        set { self[LemonadeTextInputTransformationKey.self] = newValue }
    }

    var lemonadeTextDisplayDecoration: LemonadeTextDisplayDecoration? {
        get { self[LemonadeTextDisplayDecorationKey.self] }
        set { self[LemonadeTextDisplayDecorationKey.self] = newValue }
    }

    var lemonadeTextSelectionConstraint: LemonadeTextSelectionConstraint? {
        get { self[LemonadeTextSelectionConstraintKey.self] }
        set { self[LemonadeTextSelectionConstraintKey.self] = newValue }
    }
}

public extension View {
    /// Filters every edit to the text fields in this subtree, synchronously.
    func lemonadeTextInputTransformation(
        _ transformation: LemonadeTextInputTransformation?
    ) -> some View {
        environment(\.lemonadeTextInputTransformation, transformation)
    }

    /// Decorates the displayed text of the text fields in this subtree without changing it.
    func lemonadeTextDisplayDecoration(
        _ decoration: LemonadeTextDisplayDecoration?
    ) -> some View {
        environment(\.lemonadeTextDisplayDecoration, decoration)
    }

    /// Confines the caret of the text fields in this subtree to a span of their text.
    func lemonadeTextSelectionConstraint(
        _ constraint: LemonadeTextSelectionConstraint?
    ) -> some View {
        environment(\.lemonadeTextSelectionConstraint, constraint)
    }
}
