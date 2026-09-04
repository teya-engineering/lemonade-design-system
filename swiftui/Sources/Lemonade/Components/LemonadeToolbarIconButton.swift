import SwiftUI

// MARK: - Toolbar Icon Prominence

/// Fill treatment for a navigation bar's icon button.
public enum LemonadeToolbarIconProminence {
    /// The bar draws its own chrome. Correct for almost every action.
    case plain
    /// Filled with `tint` — for the single primary action in a bar, if any.
    case filled(tint: Color)
}

// MARK: - Toolbar Icon Button

public extension LemonadeUi {
    /// An icon button for a navigation bar's `ToolbarItem`.
    ///
    /// Use this rather than a hand-rolled `Button`, and never put a ``LemonadeUi/IconButton`` in a
    /// toolbar: that is a control inside the bar's own control, and it renders as a pill.
    ///
    /// ## Why this exists
    ///
    /// SwiftUI turns a `ToolbarItem` into a real `UIBarButtonItem` only when it recognises the
    /// button's label. Anything else is wrapped in a `UIKitBarItemHost`, which UIKit caps at 36x36
    /// with only a 24x36 interactive view inside it — so roughly 6pt of each edge is dead while the
    /// control still looks like a full circle. Measured on an iPhone:
    ///
    /// ```
    /// plain image label            UIKitBarItemHost    {{337, 63}, {36, 36}}
    ///   live area within           _UIScrollPocket     {{343, 63}, {24, 36}}
    /// wrapped in a Label           _UIButtonBarButton  {{337, 63}, {36, 36}}
    /// ```
    ///
    /// A `Label` is the only shape that bridges. An `Image` does not, not even a bare
    /// `Image(systemName:)` with no modifiers — measured, so do not reach for one. Nor does
    /// anything applied to the label help: a larger `.frame`, a `.contentShape` and a button style
    /// were each measured and left the interactive view at 24pt, because UIKit clips hit-testing to
    /// the bar item's bounds.
    ///
    /// The `Label` is not a trick. A `UIBarButtonItem` genuinely is a title plus an image;
    /// `.iconOnly` hides the title visually and leaves it to assistive technology.
    ///
    /// - Parameters:
    ///   - icon: The icon to display.
    ///   - contentDescription: Describes the action. Becomes the accessibility label.
    ///   - prominence: Fill treatment. Defaults to `.plain`.
    ///   - action: Invoked on tap.
    @ViewBuilder
    static func ToolbarIconButton(
        icon: LemonadeIcon,
        contentDescription: String,
        prominence: LemonadeToolbarIconProminence = .plain,
        action: @escaping () -> Void
    ) -> some View {
        LemonadeToolbarIconButtonView(
            icon: icon,
            contentDescription: contentDescription,
            prominence: prominence,
            action: action
        )
    }

    /// The menu counterpart of ``LemonadeUi/ToolbarIconButton(icon:contentDescription:prominence:action:)``,
    /// for a bar item that opens a menu rather than firing an action.
    @ViewBuilder
    static func ToolbarIconMenu<Content: View>(
        icon: LemonadeIcon,
        contentDescription: String,
        prominence: LemonadeToolbarIconProminence = .plain,
        @ViewBuilder content: @escaping () -> Content
    ) -> some View {
        LemonadeToolbarIconMenuView(
            icon: icon,
            contentDescription: contentDescription,
            prominence: prominence,
            content: content
        )
    }
}

// MARK: - Internal Views

private struct LemonadeToolbarIconButtonView: View {
    let icon: LemonadeIcon
    let contentDescription: String
    let prominence: LemonadeToolbarIconProminence
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            LemonadeToolbarIconLabel(icon: icon, title: contentDescription)
        }
        .lemonadeToolbarProminence(prominence)
    }
}

private struct LemonadeToolbarIconMenuView<Content: View>: View {
    let icon: LemonadeIcon
    let contentDescription: String
    let prominence: LemonadeToolbarIconProminence
    @ViewBuilder let content: () -> Content

    var body: some View {
        menu.lemonadeToolbarProminence(prominence)
    }

    @ViewBuilder
    private var menu: some View {
        if #available(iOS 16.0, *) {
            rawMenu
                // Without this a `Menu` ignores `.buttonStyle` entirely, so a prominent tint lands
                // on the glyph instead of filling the button.
                .menuStyle(.button)
        } else {
            rawMenu
        }
    }

    private var rawMenu: some View {
        Menu {
            // A prominent tint is an environment value, so it reaches the menu's own rows and
            // recolours their glyphs. Reset it for the content.
            content()
                .tint(LemonadeTheme.colors.content.contentPrimary)
        } label: {
            LemonadeToolbarIconLabel(icon: icon, title: contentDescription)
        }
    }
}

/// The label shape a bar button needs. Shared by the button and the menu so both spell it the same
/// way, and so the reason survives someone refactoring one of them.
private struct LemonadeToolbarIconLabel: View {
    let icon: LemonadeIcon
    let title: String

    var body: some View {
        Label {
            Text(title)
        } icon: {
            LemonadeUi.Icon(icon: icon, contentDescription: nil, size: .medium)
        }
        .labelStyle(.iconOnly)
    }
}

private extension View {
    @ViewBuilder
    func lemonadeToolbarProminence(_ prominence: LemonadeToolbarIconProminence) -> some View {
        switch prominence {
        case .plain:
            self
        case let .filled(tint):
            // `.borderedProminent`, not `.glassProminent`: a `Menu` loses the glass-prominent style
            // across the bar-item bridge, and its tint then colours the glyph rather than the fill.
            self.buttonStyle(.borderedProminent).tint(tint)
        }
    }
}
