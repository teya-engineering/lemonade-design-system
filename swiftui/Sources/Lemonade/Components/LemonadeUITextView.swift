import SwiftUI

#if canImport(UIKit)
import UIKit

/// Internal UIViewRepresentable wrapper around UITextView, backing ``LemonadeUi/TextArea``.
///
/// A UITextView rather than SwiftUI's `TextEditor`: on iOS 15 `TextEditor` paints its own opaque
/// background and has no placeholder, and `TextField(axis: .vertical)` needs iOS 16.
internal struct LemonadeUITextView: UIViewRepresentable {
    @Environment(\.lemonadeKeyboardType) private var keyboardType
    @Environment(\.lemonadeTextContentType) private var textContentType
    @Environment(\.lemonadeAutocapitalizationType) private var autocapitalizationType
    @Environment(\.lemonadeAutocorrectionType) private var autocorrectionType
    @Environment(\.lemonadeKeyboardDoneButton) private var showsDoneButton
    @Environment(\.lemonadeFieldAccessibilityLabel) private var accessibilityName
    @Environment(\.dynamicTypeSize) private var dynamicTypeSize

    @Binding var text: String
    @Binding var isFocused: Bool
    var isEnabled: Bool
    var textStyle: LemonadeTextStyle
    var textColor: Color
    var onTextChange: ((String) -> Void)?

    func makeUIView(context: Context) -> UITextView {
        let textView = UITextView()
        textView.delegate = context.coordinator
        textView.backgroundColor = .clear
        textView.textContainerInset = .zero
        textView.textContainer.lineFragmentPadding = 0
        textView.adjustsFontForContentSizeCategory = false

        // Fill whatever frame SwiftUI proposes instead of sizing to the text.
        textView.setContentHuggingPriority(.defaultLow, for: .horizontal)
        textView.setContentHuggingPriority(.defaultLow, for: .vertical)
        textView.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        textView.setContentCompressionResistancePriority(.defaultLow, for: .vertical)

        textView.attributedText = NSAttributedString(string: text, attributes: textAttributes)
        textView.typingAttributes = textAttributes
        return textView
    }

    func updateUIView(_ textView: UITextView, context: Context) {
        context.coordinator.parent = self
        applyAppearance(textView, coordinator: context.coordinator)
        syncInputTraits(textView)
        syncDoneButton(textView, coordinator: context.coordinator)

        // A change arriving on the binding carries no cursor position, so the caret lands at the end.
        // Assigning attributed text resets the typing attributes, so they are restored after it.
        if textView.text != text, textView.markedTextRange == nil {
            textView.attributedText = NSAttributedString(string: text, attributes: textAttributes)
            textView.typingAttributes = textAttributes
        }

        textView.scheduleFirstResponderUpdate(wantsFocus: isFocused)
    }

    func makeCoordinator() -> Coordinator {
        Coordinator(parent: self)
    }

    /// Matches the line pitch of ``LemonadeUi/Text``, which the placeholder is drawn with.
    private var textAttributes: [NSAttributedString.Key: Any] {
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = textStyle.lineSpacing
        return [
            .font: textStyle.scaledUIFont(for: dynamicTypeSize),
            .foregroundColor: UIColor(textColor),
            .paragraphStyle: paragraphStyle,
        ]
    }

    private func applyAppearance(_ textView: UITextView, coordinator: Coordinator) {
        if textView.accessibilityLabel != accessibilityName {
            textView.accessibilityLabel = accessibilityName
        }
        if textView.isEditable != isEnabled {
            textView.isEditable = isEnabled
            textView.isSelectable = isEnabled
        }

        let font = textStyle.scaledUIFont(for: dynamicTypeSize)
        guard coordinator.appliedColor != textColor || coordinator.appliedFont != font else { return }
        // Restyling cancels an in-progress IME composition, so the change waits, unrecorded, for the
        // update that follows the composition.
        guard textView.markedTextRange == nil else { return }
        coordinator.appliedColor = textColor
        coordinator.appliedFont = font

        let color = UIColor(textColor)
        textView.tintColor = color
        textView.inputAccessoryView?.tintColor = color
        let selection = textView.selectedRange
        textView.attributedText = NSAttributedString(string: textView.text ?? "", attributes: textAttributes)
        textView.selectedRange = selection
        textView.typingAttributes = textAttributes
    }

    private func syncDoneButton(_ textView: UITextView, coordinator: Coordinator) {
        guard showsDoneButton != (textView.inputAccessoryView != nil) else { return }
        textView.inputAccessoryView = showsDoneButton ? doneToolbar(coordinator: coordinator) : nil
        textView.inputAccessoryView?.tintColor = UIColor(textColor)
        if textView.isFirstResponder {
            textView.reloadInputViews()
        }
    }

    /// On iOS 26 the Done item is a glass button taller than the bar `sizeToFit()` reports, so the
    /// toolbar sits in a taller container or the button overlaps the keyboard. Earlier versions
    /// draw an opaque bar, where the same gap would show the page through it.
    private func doneToolbar(coordinator: Coordinator) -> UIView {
        let toolbar = UIToolbar()
        toolbar.items = [
            UIBarButtonItem(systemItem: .flexibleSpace),
            UIBarButtonItem(
                barButtonSystemItem: .done,
                target: coordinator,
                action: #selector(Coordinator.dismissKeyboard)
            ),
        ]
        toolbar.sizeToFit()
        guard #available(iOS 26.0, *) else { return toolbar }
        toolbar.autoresizingMask = [.flexibleWidth]

        let container = UIView(frame: CGRect(
            x: 0,
            y: 0,
            width: toolbar.frame.width,
            height: toolbar.frame.height + LemonadeTheme.spaces.spacing200
        ))
        container.backgroundColor = .clear
        container.addSubview(toolbar)
        return container
    }

    private func syncInputTraits(_ textView: UITextView) {
        if textView.keyboardType != keyboardType {
            textView.keyboardType = keyboardType
            if textView.isFirstResponder {
                textView.reloadInputViews()
            }
        }
        if textView.textContentType != textContentType {
            textView.textContentType = textContentType
        }
        if textView.autocapitalizationType != autocapitalizationType {
            textView.autocapitalizationType = autocapitalizationType
        }
        if textView.autocorrectionType != autocorrectionType {
            textView.autocorrectionType = autocorrectionType
        }
    }

    final class Coordinator: NSObject, UITextViewDelegate {
        var parent: LemonadeUITextView
        var appliedColor: Color?
        var appliedFont: UIFont?
        private weak var textView: UITextView?
        private weak var enclosingScrollView: UIScrollView?

        init(parent: LemonadeUITextView) {
            self.parent = parent
        }

        @objc func dismissKeyboard() {
            textView?.resignFirstResponder()
        }

        /// The page scroll view never brings a UITextView above the keyboard on its own: the text
        /// view scrolls internally to keep the caret on screen, which can leave the caret under the
        /// keyboard. So the enclosing scroll view is asked to reveal the box, then the caret.
        @objc private func keyboardDidShow() {
            guard let textView else { return }
            reveal(textView.bounds, of: textView)
        }

        private func revealCaret(of textView: UITextView) {
            guard let caret = textView.selectedTextRange?.end else { return }
            reveal(textView.caretRect(for: caret), of: textView)
        }

        private func reveal(_ rect: CGRect, of textView: UITextView) {
            guard let scrollView = enclosingScrollView else { return }
            let margin = LemonadeTheme.spaces.spacing400
            let target = textView.convert(rect, to: scrollView).insetBy(dx: 0, dy: -margin)
            scrollView.scrollRectToVisible(target, animated: true)
        }

        func textViewDidChange(_ textView: UITextView) {
            revealCaret(of: textView)
            let newText = textView.text ?? ""
            guard newText != parent.text else { return }
            parent.text = newText
            parent.onTextChange?(newText)
        }

        func textViewDidBeginEditing(_ textView: UITextView) {
            self.textView = textView
            var ancestor = textView.superview
            while let view = ancestor, !(view is UIScrollView) {
                ancestor = view.superview
            }
            enclosingScrollView = ancestor as? UIScrollView
            NotificationCenter.default.addObserver(
                self,
                selector: #selector(keyboardDidShow),
                name: UIResponder.keyboardDidShowNotification,
                object: nil
            )
            // The keyboard may already be up when focus moves here from another field, in which
            // case keyboardDidShow never fires.
            DispatchQueue.main.async { [weak self, weak textView] in
                guard let self, let textView else { return }
                self.reveal(textView.bounds, of: textView)
            }
            if !parent.isFocused { parent.isFocused = true }
        }

        func textViewDidEndEditing(_ textView: UITextView) {
            NotificationCenter.default.removeObserver(
                self,
                name: UIResponder.keyboardDidShowNotification,
                object: nil
            )
            if parent.isFocused { parent.isFocused = false }
        }
    }
}
#endif
