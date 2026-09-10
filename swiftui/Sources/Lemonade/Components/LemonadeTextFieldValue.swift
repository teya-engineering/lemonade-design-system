import Foundation

/// Represents a text field value with cursor position control.
///
/// ## Usage
/// ```swift
/// @State var textFieldValue = LemonadeTextFieldValue(text: "Hello", cursorPosition: 5)
///
/// LemonadeUi.TextField(
///     value: $textFieldValue,
///     onValueChange: { newValue in
///         // Format text and adjust cursor
///         textFieldValue = LemonadeTextFieldValue(
///             text: formatPhoneNumber(newValue.text),
///             cursorPosition: calculateNewCursorPosition(newValue)
///         )
///     }
/// )
/// ```
///
/// - Note: `cursorPosition` is measured in UTF-16 code units, the same units `NSRange` and
///   `UITextPosition` use. For most ASCII text this equals the character count; for emoji and
///   combined characters, use `text.utf16.count` to calculate positions.
public struct LemonadeTextFieldValue: Equatable {
    /// The text content of the text field
    public var text: String

    /// The cursor position as a UTF-16 code unit offset from the start of the text.
    /// Valid range is 0 to text.utf16.count (inclusive).
    public var cursorPosition: Int

    /// Creates a TextFieldValue with text and optional cursor position.
    /// - Parameters:
    ///   - text: The text content
    ///   - cursorPosition: The cursor position in UTF-16 code units. If nil, defaults to end of text.
    public init(text: String = "", cursorPosition: Int? = nil) {
        self.text = text
        let utf16Length = text.utf16.count
        let position = cursorPosition ?? utf16Length
        self.cursorPosition = min(max(position, 0), utf16Length)
    }

    /// Creates a TextFieldValue with cursor at the end of the text.
    /// - Parameter text: The text content
    public static func atEnd(_ text: String) -> LemonadeTextFieldValue {
        LemonadeTextFieldValue(text: text, cursorPosition: text.utf16.count)
    }

    /// Creates a TextFieldValue with cursor at the start of the text.
    /// - Parameter text: The text content
    public static func atStart(_ text: String) -> LemonadeTextFieldValue {
        LemonadeTextFieldValue(text: text, cursorPosition: 0)
    }
}
