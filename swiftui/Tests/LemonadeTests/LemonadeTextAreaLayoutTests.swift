#if canImport(AppKit) && !targetEnvironment(macCatalyst)
import AppKit
import SwiftUI
import XCTest
@testable import Lemonade

@MainActor
final class LemonadeTextAreaLayoutTests: XCTestCase {
    private let width: CGFloat = 320

    func testTheBoxGrowsLineByLineUpToTheLastLimitThenStops() {
        let heights = ["", "one\ntwo", "one\ntwo\nthree", "one\ntwo\nthree\nfour\nfive\nsix"].map { text in
            host(LemonadeUi.TextArea(input: .constant(text), lineLimits: 1...3)).fittingSize.height
        }

        XCTAssertGreaterThan(heights[1], heights[0], "\(heights)")
        XCTAssertGreaterThan(heights[2], heights[1], "\(heights)")
        XCTAssertEqual(heights[3], heights[2], accuracy: 0.5, "\(heights)")
    }

    func testTheBoxStartsAtTheFirstLimit() {
        let oneLine = host(LemonadeUi.TextArea(input: .constant(""), lineLimits: 1...4)).fittingSize.height
        let twoLines = host(LemonadeUi.TextArea(input: .constant(""), lineLimits: 2...4)).fittingSize.height

        XCTAssertGreaterThan(twoLines, oneLine)
    }

    func testALongPlaceholderDoesNotGrowTheEmptyBox() {
        let placeholder = Array(repeating: "placeholder", count: 40).joined(separator: " ")
        let withoutPlaceholder = host(LemonadeUi.TextArea(input: .constant(""), lineLimits: 1...5)).fittingSize.height
        let withPlaceholder = host(
            LemonadeUi.TextArea(input: .constant(""), lineLimits: 1...5, placeholderText: placeholder)
        ).fittingSize.height

        XCTAssertEqual(withPlaceholder, withoutPlaceholder, accuracy: 0.5)
    }

    func testWithoutLineLimitsTheBoxKeepsItsFixedHeight() {
        let height = host(LemonadeUi.TextArea(input: .constant(""))).fittingSize.height

        XCTAssertGreaterThanOrEqual(height, 180)
    }

    /// Without the editor's line padding the measuring text wraps later than the editor, so at
    /// some length the editor needs one more line than the box has and its text overflows.
    func testTheEditorTextFitsTheBoxAtEveryLengthUpToTheLastLimit() throws {
        let words = Array(repeating: "Teya wraps", count: 40).joined(separator: " ").split(separator: " ")
        for count in 1...words.count {
            let text = words.prefix(count).joined(separator: " ")
            let view = host(LemonadeUi.TextArea(input: .constant(text), lineLimits: 1...5))
            view.frame.size = view.fittingSize
            view.layoutSubtreeIfNeeded()
            let editor = try XCTUnwrap(firstTextView(in: view))
            let layoutManager = try XCTUnwrap(editor.layoutManager)
            let container = try XCTUnwrap(editor.textContainer)
            layoutManager.ensureLayout(for: container)
            var lines = 0
            layoutManager.enumerateLineFragments(forGlyphRange: layoutManager.glyphRange(for: container)) { _, _, _, _, _ in
                lines += 1
            }
            guard lines <= 5 else { return }
            let visible = try XCTUnwrap(editor.enclosingScrollView).contentView.bounds.height
            let used = layoutManager.usedRect(for: container).height + editor.textContainerInset.height * 2
            XCTAssertLessThanOrEqual(used, visible + 0.5, "\(count) words, \(lines) lines")
        }
    }

    private func host(_ view: some View) -> NSHostingView<some View> {
        let hosting = NSHostingView(rootView: view.frame(width: width))
        hosting.frame = NSRect(x: 0, y: 0, width: width, height: 600)
        let window = NSWindow(contentRect: hosting.frame, styleMask: [.titled], backing: .buffered, defer: false)
        window.contentView = hosting
        hosting.layoutSubtreeIfNeeded()
        return hosting
    }

    private func firstTextView(in view: NSView) -> NSTextView? {
        if let textView = view as? NSTextView { return textView }
        for subview in view.subviews {
            if let textView = firstTextView(in: subview) { return textView }
        }
        return nil
    }
}
#endif
