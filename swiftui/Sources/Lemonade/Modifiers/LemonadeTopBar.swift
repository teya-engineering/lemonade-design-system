import SwiftUI
import CoreImage.CIFilterBuiltins

#if os(iOS)

// MARK: - TopBarAction

/// The type of navigation action displayed in the leading slot of a top bar.
/// Mirrors the KMP `TopBarAction` enum.
///
/// - Note: On iOS, `.back` defers to the native `NavigationStack` back button so the
///   system swipe-to-go-back gesture keeps working. The `NavigationAction.onAction`
///   callback is **not** invoked for `.back` — use `.close` (with `@Environment(\.dismiss)`)
///   if you need to run logic when the user dismisses the screen.
public enum TopBarAction {
    case back
    case close
}

// MARK: - NavigationAction

/// Configuration for the navigation action in the leading slot of a top bar.
/// Mirrors the KMP `NavigationAction` data class.
public struct NavigationAction {
    public let action: TopBarAction
    let onAction: () -> Void

    public init(action: TopBarAction, onAction: @escaping () -> Void) {
        self.action = action
        self.onAction = onAction
    }
}

// MARK: - Empty Toolbar Content

/// Empty toolbar content for overloads that don't need toolbar items.
private struct LemonadeEmptyToolbarContent: ToolbarContent {
    var body: some ToolbarContent {
        ToolbarItem(placement: .navigationBarTrailing) {
            EmptyView()
        }
    }
}

// MARK: - Basic TopBar Modifier

/// A modifier that applies a native iOS navigation bar styled with Lemonade tokens.
/// Uses `@ToolbarContentBuilder` so consumers interact with the toolbar the
/// same way as the native `.toolbar` modifier.
private struct BasicTopBarModifier<Toolbar: ToolbarContent, BottomContent: View>: ViewModifier {
    let label: String
    let subheading: String?
    let collapsedLabel: String?
    let navigationAction: NavigationAction?
    let toolbarContent: Toolbar
    let bottomSlot: (() -> BottomContent)?

    func body(content: Content) -> some View {
        content
            .lemonadeNavigationTitle(title: collapsedLabel ?? label, subheading: subheading)
            .navigationBarBackButtonHidden(navigationAction?.action == .close)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    if let navigationAction, navigationAction.action == .close {
                        LemonadeUi.ToolbarIconButton(
                            icon: .times,
                            contentDescription: "Close",
                            action: navigationAction.onAction
                        )
                    }
                }
                toolbarContent
            }
            .lemonadeFallbackPrincipalTitle(label: collapsedLabel ?? label, subheading: subheading)
            .lemonadeTopBarBottomSlot(bottomSlot)
    }
}

// MARK: - Search TopBar Modifier

/// A modifier that applies a native iOS navigation bar with an integrated search field.
/// Uses `.searchable` with `.navigationBarDrawer` placement for native collapse behavior.
private struct SearchTopBarModifier<Toolbar: ToolbarContent, BottomContent: View>: ViewModifier {
    let label: String
    let subheading: String?
    @Binding var searchInput: String
    let searchPrompt: String
    let expandedLabel: String?
    let navigationAction: NavigationAction?
    let toolbarContent: Toolbar
    let bottomSlot: (() -> BottomContent)?

    func body(content: Content) -> some View {
        content
            .lemonadeNavigationTitle(title: expandedLabel ?? label, subheading: subheading)
            .searchable(
                text: $searchInput,
                placement: .navigationBarDrawer(displayMode: .always),
                prompt: searchPrompt
            )
            .navigationBarBackButtonHidden(navigationAction?.action == .close)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    if let navigationAction, navigationAction.action == .close {
                        LemonadeUi.ToolbarIconButton(
                            icon: .times,
                            contentDescription: "Close",
                            action: navigationAction.onAction
                        )
                    }
                }
                toolbarContent
            }
            .lemonadeFallbackPrincipalTitle(label: expandedLabel ?? label, subheading: subheading)
            .lemonadeTopBarBottomSlot(bottomSlot)
    }
}

// MARK: - PreferenceKeys

private struct HeaderHeightKey: PreferenceKey {
    static var defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}

// MARK: - Scroll Offset Observer

/// Invisible UIKit view that finds the nearest `UIScrollView` ancestor
/// and observes its `contentOffset` via KVO. Reports changes to SwiftUI.
private struct ScrollOffsetObserver: UIViewRepresentable {
    let onChange: (CGFloat) -> Void

    func makeUIView(context: Context) -> ScrollOffsetObserverUIView {
        ScrollOffsetObserverUIView(onChange: onChange)
    }

    func updateUIView(_ uiView: ScrollOffsetObserverUIView, context: Context) {}

    class ScrollOffsetObserverUIView: UIView {
        let onChange: (CGFloat) -> Void
        private var observation: NSKeyValueObservation?

        init(onChange: @escaping (CGFloat) -> Void) {
            self.onChange = onChange
            super.init(frame: .zero)
            isUserInteractionEnabled = false
        }

        required init?(coder: NSCoder) { fatalError() }

        override func didMoveToWindow() {
            super.didMoveToWindow()
            guard observation == nil, window != nil else { return }
            // Delay slightly to let SwiftUI finish building the view hierarchy
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) { [weak self] in
                self?.attachToScrollView()
            }
        }

        private func attachToScrollView() {
            guard observation == nil else { return }
            // Walk up ancestors; at each level search that subtree for a UIScrollView.
            // This finds sibling scroll views that aren't direct ancestors.
            var current: UIView? = superview
            while let ancestor = current {
                if let scrollView = findScrollView(in: ancestor) {
                    observation = scrollView.observe(\.contentOffset, options: .new) { [weak self] _, change in
                        guard let offset = change.newValue else { return }
                        self?.onChange(offset.y)
                    }
                    return
                }
                current = ancestor.superview
            }
        }

        /// BFS to find the first UIScrollView in the subtree, skipping self.
        private func findScrollView(in root: UIView) -> UIScrollView? {
            var queue = root.subviews
            var index = 0
            while index < queue.count {
                let view = queue[index]
                index += 1
                if view === self { continue }
                if let scrollView = view as? UIScrollView {
                    return scrollView
                }
                queue.append(contentsOf: view.subviews)
            }
            return nil
        }

        override func removeFromSuperview() {
            observation?.invalidate()
            observation = nil
            super.removeFromSuperview()
        }
    }
}

// MARK: - Variable Blur View

/// Variable blur using the same private `CAFilter` API that Apple uses internally
/// in Music, Photos, and the App Store. Based on nikstar/VariableBlur (MIT license).
private enum VariableBlurDirection {
    case blurredTopClearBottom
    case blurredBottomClearTop
}

private struct VariableBlurView: UIViewRepresentable {
    let maxBlurRadius: CGFloat
    let direction: VariableBlurDirection

    func makeUIView(context: Context) -> VariableBlurUIView {
        VariableBlurUIView(maxBlurRadius: maxBlurRadius, direction: direction)
    }

    func updateUIView(_ uiView: VariableBlurUIView, context: Context) {}
}

private class VariableBlurUIView: UIVisualEffectView {
    private static let ciContext = CIContext()

    init(maxBlurRadius: CGFloat, direction: VariableBlurDirection) {
        // Falls back to standard .regular blur if the private API is unavailable
        super.init(effect: UIBlurEffect(style: .regular))

        let clsName = String("retliFAC".reversed())
        guard let Cls = NSClassFromString(clsName) as? NSObject.Type else { return }
        let selName = String(":epyThtiWretlif".reversed())
        guard let variableBlur = Cls.perform(NSSelectorFromString(selName), with: "variableBlur")
            .takeUnretainedValue() as? NSObject else { return }

        guard let gradientImage = Self.makeGradientImage(direction: direction) else { return }
        variableBlur.setValue(maxBlurRadius, forKey: "inputRadius")
        variableBlur.setValue(gradientImage, forKey: "inputMaskImage")
        variableBlur.setValue(true, forKey: "inputNormalizeEdges")

        let backdropLayer = subviews.first?.layer
        backdropLayer?.filters = [variableBlur]

        for subview in subviews.dropFirst() {
            subview.alpha = 0
        }
    }

    required init?(coder: NSCoder) { fatalError() }

    override func didMoveToWindow() {
        super.didMoveToWindow()
        guard let window, let backdropLayer = subviews.first?.layer else { return }
        backdropLayer.setValue(window.traitCollection.displayScale, forKey: "scale")
    }

    // Intentionally empty — suppresses crash in super on trait changes.
    // Deprecated in iOS 17; the blur filter does not need trait-driven updates.
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {}

    private static func makeGradientImage(
        width: CGFloat = 100,
        height: CGFloat = 100,
        direction: VariableBlurDirection
    ) -> CGImage? {
        let gradientFilter = CIFilter.linearGradient()
        gradientFilter.color0 = CIColor.black
        gradientFilter.color1 = CIColor.clear
        gradientFilter.point0 = CGPoint(x: 0, y: height)
        gradientFilter.point1 = CGPoint(x: 0, y: 0)
        if case .blurredBottomClearTop = direction {
            gradientFilter.point0 = .init(x: 0, y: 0)
            gradientFilter.point1 = .init(x: 0, y: height)
        }
        guard let outputImage = gradientFilter.outputImage else { return nil }
        return ciContext.createCGImage(
            outputImage,
            from: CGRect(x: 0, y: 0, width: width, height: height)
        )
    }
}

// MARK: - Native Search Bar

/// Wraps `UISearchBar` so we get the exact native iOS search appearance
/// (capsule field, magnifying glass, cancel button) while placing it
/// anywhere in our view hierarchy — not locked to the navigation bar.
private struct NativeSearchBar: UIViewRepresentable {
    @Binding var text: String
    let placeholder: String
    let onFocusChange: (Bool) -> Void

    func makeCoordinator() -> Coordinator {
        Coordinator(text: $text, onFocusChange: onFocusChange)
    }

    func makeUIView(context: Context) -> UISearchBar {
        let searchBar = UISearchBar()
        searchBar.delegate = context.coordinator
        searchBar.placeholder = placeholder
        searchBar.searchBarStyle = .minimal
        searchBar.autocapitalizationType = .none
        searchBar.autocorrectionType = .no
        searchBar.backgroundImage = UIImage()
        searchBar.searchTextField.backgroundColor = UIColor.systemGray6
        return searchBar
    }

    func updateUIView(_ uiView: UISearchBar, context: Context) {
        if uiView.text != text {
            uiView.text = text
        }
    }

    class Coordinator: NSObject, UISearchBarDelegate {
        @Binding var text: String
        let onFocusChange: (Bool) -> Void

        init(text: Binding<String>, onFocusChange: @escaping (Bool) -> Void) {
            _text = text
            self.onFocusChange = onFocusChange
        }

        func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
            text = searchText
        }

        func searchBarTextDidBeginEditing(_ searchBar: UISearchBar) {
            searchBar.setShowsCancelButton(true, animated: true)
            // Remove custom background — lets UIKit render default glass-like style
            UIView.animate(withDuration: 0.2) {
                searchBar.searchTextField.backgroundColor = nil
            }
            onFocusChange(true)
        }

        func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
            searchBar.setShowsCancelButton(false, animated: true)
            // Restore gray background to match native .searchable unfocused
            UIView.animate(withDuration: 0.2) {
                searchBar.searchTextField.backgroundColor = UIColor.systemGray6
            }
            onFocusChange(false)
        }

        func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
            searchBar.text = ""
            text = ""
            searchBar.resignFirstResponder()
        }

        func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
            searchBar.resignFirstResponder()
        }
    }
}

// MARK: - Hide Navigation Bar + Preserve Swipe Back

private extension View {
    /// Hides the navigation bar and re-enables the swipe-back gesture.
    @ViewBuilder
    func lemonadeHideNavigationBar() -> some View {
        if #available(iOS 16.0, *) {
            self.toolbar(.hidden, for: .navigationBar)
                .onAppear { Self.enableSwipeBack() }
        } else {
            self.navigationBarHidden(true)
                .onAppear { Self.enableSwipeBack() }
        }
    }

    /// Finds the UINavigationController from the window hierarchy and
    /// re-enables the interactive pop gesture recognizer.
    static func enableSwipeBack() {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let window = windowScene.windows.first,
              let nav = findNavigationController(in: window.rootViewController) else { return }
        nav.interactivePopGestureRecognizer?.isEnabled = true
        nav.interactivePopGestureRecognizer?.delegate = nil
    }

    private static func findNavigationController(in vc: UIViewController?) -> UINavigationController? {
        guard let vc else { return nil }
        if let nav = vc as? UINavigationController { return nav }
        for child in vc.children {
            if let found = findNavigationController(in: child) { return found }
        }
        if let presented = vc.presentedViewController {
            return findNavigationController(in: presented)
        }
        return nil
    }
}

// MARK: - Compact Large TopBar Header

/// Custom header row matching KMP `CompactLargeTopBarHeading` — title left, trailing slot right,
/// on the same line. No navigation bar involvement.
private struct CompactLargeHeader<TrailingContent: View>: View {
    let label: String
    let subheading: String?
    let trailingSlot: (() -> TrailingContent)?

    var body: some View {
        HStack(alignment: .top, spacing: LemonadeSpacing.spacing200.value) {
            VStack(alignment: .leading, spacing: 0) {
                LemonadeUi.Text(
                    label,
                    textStyle: LemonadeTypography.shared.headingLarge,
                    color: LemonadeTheme.colors.content.contentPrimary
                )
                .lineLimit(2)

                if let subheading {
                    LemonadeUi.Text(
                        subheading,
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: LemonadeTheme.colors.content.contentSecondary
                    )
                    .lineLimit(1)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            if let trailingSlot {
                HStack(spacing: LemonadeSpacing.spacing200.value) {
                    trailingSlot()
                }
            }
        }
        .padding(.horizontal, LemonadeSpacing.spacing400.value)
        .padding(.bottom, LemonadeSpacing.spacing200.value)
    }
}

// MARK: - Compact Large Blur Layout

/// Shared layout for compact-large variants: ZStack with scrollable content,
/// progressive blur layer, and a measured header slot.
///
private struct CompactLargeBlurLayout<HeaderContent: View>: View {
    let scrollableContent: AnyView
    let onScrollOffsetChange: ((CGFloat) -> Void)?
    @ViewBuilder let headerContent: () -> HeaderContent

    private let fadeExtension: CGFloat = 64
    /// Tracks the maximum header height ever seen, used for the safeAreaInset
    /// spacer so it doesn't shrink when the search bar collapses (avoids feedback loop).
    @State private var maxHeaderHeight: CGFloat = 76
    @Environment(\.colorScheme) private var colorScheme

    init(
        scrollableContent: AnyView,
        onScrollOffsetChange: ((CGFloat) -> Void)? = nil,
        @ViewBuilder headerContent: @escaping () -> HeaderContent
    ) {
        self.scrollableContent = scrollableContent
        self.onScrollOffsetChange = onScrollOffsetChange
        self.headerContent = headerContent
    }

    var body: some View {
        ZStack(alignment: .top) {
            scrollableContent
                .overlay(alignment: .top) {
                    if let onScrollOffsetChange {
                        ScrollOffsetObserver(onChange: onScrollOffsetChange)
                            .frame(width: 0, height: 0)
                    }
                }
                .safeAreaInset(edge: .top, spacing: 0) {
                    Color.clear.frame(height: maxHeaderHeight)
                }

            let totalHeight = maxHeaderHeight + fadeExtension
            let headerRatio = maxHeaderHeight / totalHeight
            VariableBlurView(maxBlurRadius: 5, direction: .blurredTopClearBottom)
                .overlay {
                    LinearGradient(stops: [
                        .init(color: fadeTint.opacity(0.7), location: 0),
                        .init(color: fadeTint.opacity(0.7), location: headerRatio),
                        .init(color: fadeTint.opacity(0), location: 1),
                    ], startPoint: .top, endPoint: .bottom)
                }
                .frame(height: totalHeight)
                .ignoresSafeArea(edges: .top)
                .allowsHitTesting(false)

            headerContent()
                .frame(maxWidth: .infinity, alignment: .leading)
                .overlay(
                    GeometryReader { geo in
                        Color.clear.preference(key: HeaderHeightKey.self, value: geo.size.height)
                    }
                )
        }
        .onPreferenceChange(HeaderHeightKey.self) { newHeight in
            if newHeight > maxHeaderHeight {
                maxHeaderHeight = newHeight
            }
        }
        .lemonadeHideNavigationBar()
    }

    private var fadeTint: Color {
        colorScheme == .dark ? .black : .white
    }
}

// MARK: - Compact Large TopBar Modifier (Native iOS 26+)

@available(iOS 16.0, *)
private struct NativeCompactLargeTopBarModifier<Toolbar: ToolbarContent>: ViewModifier {
    let label: String
    let subheading: String?
    let toolbarContent: Toolbar

    @State private var showTitle = true
    @State private var lastScrollOffset: CGFloat = 0

    func body(content: Content) -> some View {
        content
            .coordinateSpace(name: "compactLargeNativeScroll")
            .onPreferenceChange(NativeScrollOffsetKey.self) { offset in
                guard abs(offset - lastScrollOffset) > 2 else { return }
                lastScrollOffset = offset
                let newValue = offset > -10
                guard newValue != showTitle else { return }
                withAnimation(.easeInOut(duration: 0.2)) { showTitle = newValue }
            }
            .overlay(alignment: .top) {
                GeometryReader { geo in
                    Color.clear.preference(
                        key: NativeScrollOffsetKey.self,
                        value: geo.frame(in: .named("compactLargeNativeScroll")).minY
                    )
                }
                .frame(height: 0)
            }
            .navigationBarBackButtonHidden(true)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    LemonadeCompactLargeTitle(label: label, subheading: subheading)
                        .blur(radius: showTitle ? 0 : 4)
                        .opacity(showTitle ? 1 : 0)
                }
                .lemonadeHiddenSharedBackground()

                toolbarContent
            }
    }
}

@available(iOS 16.0, *)
private struct NativeCompactLargeSearchTopBarModifier<Toolbar: ToolbarContent>: ViewModifier {
    let label: String
    let subheading: String?
    @Binding var searchInput: String
    let searchPrompt: String
    let toolbarContent: Toolbar

    @State private var showTitle = true
    @State private var lastScrollOffset: CGFloat = 0

    func body(content: Content) -> some View {
        content
            .coordinateSpace(name: "compactLargeSearchNativeScroll")
            .onPreferenceChange(NativeScrollOffsetKey.self) { offset in
                guard abs(offset - lastScrollOffset) > 2 else { return }
                lastScrollOffset = offset
                let newValue = offset > -10
                guard newValue != showTitle else { return }
                withAnimation(.easeInOut(duration: 0.2)) { showTitle = newValue }
            }
            .overlay(alignment: .top) {
                GeometryReader { geo in
                    Color.clear.preference(
                        key: NativeScrollOffsetKey.self,
                        value: geo.frame(in: .named("compactLargeSearchNativeScroll")).minY
                    )
                }
                .frame(height: 0)
            }
            .navigationBarBackButtonHidden(true)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    LemonadeCompactLargeTitle(label: label, subheading: subheading)
                        .blur(radius: showTitle ? 0 : 4)
                        .opacity(showTitle ? 1 : 0)
                }
                .lemonadeHiddenSharedBackground()

                toolbarContent
            }
            .searchable(
                text: $searchInput,
                placement: .navigationBarDrawer(displayMode: .automatic),
                prompt: searchPrompt
            )
    }
}

private struct NativeScrollOffsetKey: PreferenceKey {
    static var defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}

// MARK: - Version-specific helpers

@available(iOS 16.0, *)
private extension ToolbarContent {
    /// Hides the shared glass background on iOS 26+, no-op on older.
    @ToolbarContentBuilder
    func lemonadeHiddenSharedBackground() -> some ToolbarContent {
        #if compiler(>=6.2)
        if #available(iOS 26, *) {
            self.sharedBackgroundVisibility(.hidden)
        } else {
            self
        }
        #else
        self
        #endif
    }
}

private extension View {
    /// Applies the navigation title and, on iOS 26, the native `.navigationSubtitle`
    /// (preserves the large-title morph). On iOS < 26 a non-nil subheading switches
    /// to inline display mode so `lemonadeFallbackPrincipalTitle` can render a
    /// stacked `title` + `subheading` in the principal toolbar slot — matching the
    /// platform convention (Mail, Messages, Settings) for pre-iOS 26 two-line nav
    /// bars at the cost of losing the large-title morph. The native `.navigationTitle`
    /// is kept (not emptied) so pushed screens still inherit a back button label.
    @ViewBuilder
    func lemonadeNavigationTitle(title: String, subheading: String?) -> some View {
        #if compiler(>=6.2)
        if #available(iOS 26, *), let subheading {
            self
                .navigationTitle(title)
                .navigationSubtitle(subheading)
                .navigationBarTitleDisplayMode(.large)
        } else if #available(iOS 26, *) {
            self
                .navigationTitle(title)
                .navigationBarTitleDisplayMode(.large)
        } else if subheading != nil {
            self
                .navigationTitle(title)
                .navigationBarTitleDisplayMode(.inline)
        } else {
            self
                .navigationTitle(title)
                .navigationBarTitleDisplayMode(.large)
        }
        #else
        if subheading != nil {
            self
                .navigationTitle(title)
                .navigationBarTitleDisplayMode(.inline)
        } else {
            self
                .navigationTitle(title)
                .navigationBarTitleDisplayMode(.large)
        }
        #endif
    }
}

private extension View {
    /// iOS < 26 fallback: attaches a principal toolbar item that renders
    /// `label` + `subheading` stacked vertically (matches the Mail / Messages
    /// pattern used before `.navigationSubtitle` existed). No-op on iOS 26,
    /// where the native API handles the subheading in both expanded and
    /// collapsed states.
    @ViewBuilder
    func lemonadeFallbackPrincipalTitle(label: String, subheading: String?) -> some View {
        #if compiler(>=6.2)
        if #available(iOS 26, *) {
            self
        } else if let subheading {
            self.toolbar {
                ToolbarItem(placement: .principal) {
                    LemonadePrincipalTwoLineTitle(label: label, subheading: subheading)
                }
            }
        } else {
            self
        }
        #else
        if let subheading {
            self.toolbar {
                ToolbarItem(placement: .principal) {
                    LemonadePrincipalTwoLineTitle(label: label, subheading: subheading)
                }
            }
        } else {
            self
        }
        #endif
    }
}

/// Stacked title + subheading used as `.principal` toolbar item on iOS < 26.
/// Constrained to a single line each with tail truncation so the nav bar
/// height stays stable regardless of label length.
private struct LemonadePrincipalTwoLineTitle: View {
    let label: String
    let subheading: String

    var body: some View {
        VStack(spacing: 2) {
            SwiftUI.Text(label)
                .font(.headingXSmall)
                .foregroundStyle(LemonadeTheme.colors.content.contentPrimary)
                .lineLimit(1)
                .truncationMode(.tail)
            SwiftUI.Text(subheading)
                .font(.bodySmallRegular)
                .foregroundStyle(LemonadeTheme.colors.content.contentSecondary)
                .lineLimit(1)
                .truncationMode(.tail)
        }
    }
}

private extension View {
    /// Renders a bottom slot attached to the nav bar.
    ///
    /// On iOS 26+: uses the native glass toolbar (no divider) and lets the inset ride on top.
    /// On iOS 16-25: hides the nav bar shadow and wraps the slot in a `.bar` material +
    /// bottom divider so it reads as an extension of the nav bar.
    @ViewBuilder
    func lemonadeTopBarBottomSlot<BottomContent: View>(
        _ bottomSlot: (() -> BottomContent)?
    ) -> some View {
        if let bottomSlot {
            #if compiler(>=6.2)
            if #available(iOS 26, *) {
                self.safeAreaInset(edge: .top, spacing: 0) { bottomSlot() }
            } else {
                self.lemonadeTopBarBottomSlotFallback(bottomSlot)
            }
            #else
            self.lemonadeTopBarBottomSlotFallback(bottomSlot)
            #endif
        } else {
            self
        }
    }

    @ViewBuilder
    func lemonadeTopBarBottomSlotFallback<BottomContent: View>(
        _ bottomSlot: @escaping () -> BottomContent
    ) -> some View {
        let inset = VStack(spacing: 0) {
            bottomSlot()
            LemonadeUi.HorizontalDivider()
        }
        .background(.bar)

        if #available(iOS 16, *) {
            self
                .toolbarBackground(.hidden, for: .navigationBar)
                .safeAreaInset(edge: .top, spacing: 0) { inset }
        } else {
            self.safeAreaInset(edge: .top, spacing: 0) { inset }
        }
    }
}

// MARK: - Compact Large Title helper

@available(iOS 16.0, *)
private struct LemonadeCompactLargeTitle: View {
    let label: String
    let subheading: String?

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            SwiftUI.Text(label)
                .font(.headingLarge)
                .fixedSize(horizontal: true, vertical: false)
            if let subheading {
                SwiftUI.Text(subheading)
                    .font(.bodySmallRegular)
                    .fixedSize(horizontal: true, vertical: false)
                    .foregroundStyle(LemonadeTheme.colors.content.contentSecondary)
                    .padding(.bottom, LemonadeSpacing.spacing200.value)
            }
        }
    }
}

// MARK: - Compact Large TopBar Modifier (Legacy)

private struct CompactLargeTopBarModifier<TrailingContent: View, BottomContent: View>: ViewModifier {
    let label: String
    let subheading: String?
    let trailingSlot: (() -> TrailingContent)?
    let bottomSlot: (() -> BottomContent)?

    func body(content: Content) -> some View {
        CompactLargeBlurLayout(scrollableContent: AnyView(content)) {
            VStack(alignment: .leading, spacing: 0) {
                CompactLargeHeader(
                    label: label,
                    subheading: subheading,
                    trailingSlot: trailingSlot
                )
                .padding(.top, LemonadeSpacing.spacing200.value)

                if let bottomSlot {
                    bottomSlot()
                }
            }
        }
    }
}

// MARK: - Compact Large Search TopBar Modifier

/// A modifier that applies a large left-aligned title with optional subheading
/// and an integrated search field. Content scrolls behind the header with blur.
///
/// When the search field gains focus, the title animates out (shrinks vertically)
/// and only the search field remains — matching KMP behavior.
private struct CompactLargeSearchTopBarModifier<TrailingContent: View>: ViewModifier {
    let label: String
    let subheading: String?
    @Binding var searchInput: String
    let searchPrompt: String
    let trailingSlot: (() -> TrailingContent)?

    private let searchBarHeight: CGFloat = 56
    @State private var isSearchFocused: Bool = false
    @State private var scrollOffset: CGFloat = 0
    @State private var initialOffset: CGFloat?

    /// 0 (fully expanded) to 1 (fully collapsed)
    private var collapseProgress: CGFloat {
        guard !isSearchFocused else { return 0 }
        let delta = scrollOffset - (initialOffset ?? 0)
        return min(1, max(0, delta / searchBarHeight))
    }

    func body(content: Content) -> some View {
        CompactLargeBlurLayout(
            scrollableContent: AnyView(content),
            onScrollOffsetChange: { offset in
                if initialOffset == nil {
                    initialOffset = offset
                }
                scrollOffset = offset
            }
        ) {
            VStack(alignment: .leading, spacing: 0) {
                if !isSearchFocused {
                    CompactLargeHeader(
                        label: label,
                        subheading: subheading,
                        trailingSlot: trailingSlot
                    )
                    .padding(.top, LemonadeSpacing.spacing200.value)
                    .transition(.move(edge: .top).combined(with: .opacity))
                }

                NativeSearchBar(
                    text: $searchInput,
                    placeholder: searchPrompt,
                    onFocusChange: { focused in
                        withAnimation(.easeInOut(duration: 0.25)) {
                            isSearchFocused = focused
                        }
                    }
                )
                .padding(.horizontal, LemonadeSpacing.spacing200.value)
                .frame(height: searchBarHeight * (1 - collapseProgress))
                .clipped()
                .opacity(1 - collapseProgress)
            }
        }
    }
}

// MARK: - View Extensions

public extension View {

    // MARK: 1. Basic TopBar

    /// Applies a Lemonade-styled navigation bar with a collapsible large title.
    ///
    /// Uses native iOS `.navigationTitle` and `.toolbar` under the hood,
    /// preserving all native navigation interactions (swipe-to-go-back, title morphing, etc.).
    /// The `toolbar` parameter uses `@ToolbarContentBuilder` — same API as native `.toolbar`.
    ///
    /// ## Usage
    /// ```swift
    /// ScrollView { content }
    ///     .lemonadeTopBar(
    ///         label: "Settings",
    ///         navigationAction: NavigationAction(action: .back, onAction: { dismiss() })
    ///     ) {
    ///         ToolbarItem(placement: .navigationBarTrailing) {
    ///             LemonadeUi.IconButton(icon: .bell, contentDescription: "Notifications", onClick: {})
    ///         }
    ///     }
    /// ```
    func lemonadeTopBar<Toolbar: ToolbarContent, BottomContent: View>(
        label: String,
        subheading: String? = nil,
        collapsedLabel: String? = nil,
        navigationAction: NavigationAction? = nil,
        @ViewBuilder bottomSlot: @escaping () -> BottomContent,
        @ToolbarContentBuilder toolbar: () -> Toolbar
    ) -> some View {
        modifier(BasicTopBarModifier(
            label: label,
            subheading: subheading,
            collapsedLabel: collapsedLabel,
            navigationAction: navigationAction,
            toolbarContent: toolbar(),
            bottomSlot: bottomSlot
        ))
    }

    /// Applies a Lemonade-styled navigation bar with toolbar content (no bottom slot).
    func lemonadeTopBar<Toolbar: ToolbarContent>(
        label: String,
        subheading: String? = nil,
        collapsedLabel: String? = nil,
        navigationAction: NavigationAction? = nil,
        @ToolbarContentBuilder toolbar: () -> Toolbar
    ) -> some View {
        modifier(BasicTopBarModifier(
            label: label,
            subheading: subheading,
            collapsedLabel: collapsedLabel,
            navigationAction: navigationAction,
            toolbarContent: toolbar(),
            bottomSlot: nil as (() -> EmptyView)?
        ))
    }

    /// Applies a Lemonade-styled navigation bar with a collapsible large title (no toolbar or bottom slot).
    func lemonadeTopBar(
        label: String,
        subheading: String? = nil,
        collapsedLabel: String? = nil,
        navigationAction: NavigationAction? = nil
    ) -> some View {
        modifier(BasicTopBarModifier(
            label: label,
            subheading: subheading,
            collapsedLabel: collapsedLabel,
            navigationAction: navigationAction,
            toolbarContent: LemonadeEmptyToolbarContent(),
            bottomSlot: nil as (() -> EmptyView)?
        ))
    }

    // MARK: 2. Search TopBar

    /// Applies a Lemonade-styled navigation bar with an integrated search field.
    ///
    /// Uses native `.searchable` with `.navigationBarDrawer` placement.
    /// The `toolbar` parameter uses `@ToolbarContentBuilder` — same API as native `.toolbar`.
    ///
    /// ## Usage
    /// ```swift
    /// ScrollView { content }
    ///     .lemonadeTopBar(
    ///         label: "Search",
    ///         searchInput: $query,
    ///         navigationAction: NavigationAction(action: .back, onAction: { dismiss() })
    ///     ) {
    ///         ToolbarItem(placement: .navigationBarTrailing) {
    ///             LemonadeUi.IconButton(icon: .filter, contentDescription: "Filter", onClick: {})
    ///         }
    ///     }
    /// ```
    func lemonadeTopBar<Toolbar: ToolbarContent, BottomContent: View>(
        label: String,
        subheading: String? = nil,
        searchInput: Binding<String>,
        searchPrompt: String = "Search...",
        expandedLabel: String? = nil,
        navigationAction: NavigationAction? = nil,
        @ViewBuilder bottomSlot: @escaping () -> BottomContent,
        @ToolbarContentBuilder toolbar: () -> Toolbar
    ) -> some View {
        modifier(SearchTopBarModifier(
            label: label,
            subheading: subheading,
            searchInput: searchInput,
            searchPrompt: searchPrompt,
            expandedLabel: expandedLabel,
            navigationAction: navigationAction,
            toolbarContent: toolbar(),
            bottomSlot: bottomSlot
        ))
    }

    /// Applies a Lemonade-styled navigation bar with search + toolbar (no bottom slot).
    func lemonadeTopBar<Toolbar: ToolbarContent>(
        label: String,
        subheading: String? = nil,
        searchInput: Binding<String>,
        searchPrompt: String = "Search...",
        expandedLabel: String? = nil,
        navigationAction: NavigationAction? = nil,
        @ToolbarContentBuilder toolbar: () -> Toolbar
    ) -> some View {
        modifier(SearchTopBarModifier(
            label: label,
            subheading: subheading,
            searchInput: searchInput,
            searchPrompt: searchPrompt,
            expandedLabel: expandedLabel,
            navigationAction: navigationAction,
            toolbarContent: toolbar(),
            bottomSlot: nil as (() -> EmptyView)?
        ))
    }

    /// Applies a Lemonade-styled navigation bar with search (no toolbar or bottom slot).
    func lemonadeTopBar(
        label: String,
        subheading: String? = nil,
        searchInput: Binding<String>,
        searchPrompt: String = "Search...",
        expandedLabel: String? = nil,
        navigationAction: NavigationAction? = nil
    ) -> some View {
        modifier(SearchTopBarModifier(
            label: label,
            subheading: subheading,
            searchInput: searchInput,
            searchPrompt: searchPrompt,
            expandedLabel: expandedLabel,
            navigationAction: navigationAction,
            toolbarContent: LemonadeEmptyToolbarContent(),
            bottomSlot: nil as (() -> EmptyView)?
        ))
    }

    // MARK: 3. Compact Large TopBar

    /// Applies a Lemonade-styled large left-aligned title bar with optional subheading.
    ///
    /// Designed for top-level screens without navigation actions.
    /// When `bottomSlot` is provided, the title scrolls away and the slot becomes sticky.
    ///
    /// Mirrors KMP `LemonadeUi.TopBar(label, subheading, trailingSlot, bottomSlot)`.
    ///
    /// ## Usage
    /// ```swift
    /// ScrollView { content }
    ///     .lemonadeTopBar(
    ///         label: "Home",
    ///         subheading: "Welcome back",
    ///         trailingSlot: {
    ///             LemonadeUi.IconButton(icon: .bell, contentDescription: "Notifications", onClick: {})
    ///         }
    ///     )
    /// ```
    @ViewBuilder
    func lemonadeTopBar<Toolbar: ToolbarContent>(
        label: String,
        subheading: String?,
        @ToolbarContentBuilder toolbar: () -> Toolbar
    ) -> some View {
        if #available(iOS 16, *) {
            modifier(NativeCompactLargeTopBarModifier(
                label: label, subheading: subheading,
                toolbarContent: toolbar()
            ))
        } else {
            modifier(CompactLargeTopBarModifier<EmptyView, EmptyView>(
                label: label, subheading: subheading,
                trailingSlot: nil, bottomSlot: nil
            ))
        }
    }

    /// Applies a Lemonade-styled large title bar with subheading (no toolbar).
    @ViewBuilder
    func lemonadeTopBar(
        label: String,
        subheading: String?
    ) -> some View {
        if #available(iOS 16, *) {
            modifier(NativeCompactLargeTopBarModifier(
                label: label, subheading: subheading,
                toolbarContent: LemonadeEmptyToolbarContent()
            ))
        } else {
            modifier(CompactLargeTopBarModifier<EmptyView, EmptyView>(
                label: label, subheading: subheading,
                trailingSlot: nil, bottomSlot: nil
            ))
        }
    }

    // MARK: 4. Compact Large Search TopBar

    /// Applies a Lemonade-styled large title bar with subheading and search.
    ///
    /// The title stays fixed while the search field collapses on scroll.
    ///
    /// Mirrors KMP `LemonadeUi.TopBar(label, subheading, searchInput, onSearchChanged, trailingSlot)`.
    ///
    /// ## Usage
    /// ```swift
    /// ScrollView { content }
    ///     .lemonadeTopBar(
    ///         label: "Discover",
    ///         subheading: "Find what you need",
    ///         searchInput: $query
    ///     )
    /// ```
    @ViewBuilder
    func lemonadeTopBar<Toolbar: ToolbarContent>(
        label: String,
        subheading: String?,
        searchInput: Binding<String>,
        searchPrompt: String = "Search...",
        @ToolbarContentBuilder toolbar: () -> Toolbar
    ) -> some View {
        if #available(iOS 16, *) {
            modifier(NativeCompactLargeSearchTopBarModifier(
                label: label, subheading: subheading,
                searchInput: searchInput, searchPrompt: searchPrompt,
                toolbarContent: toolbar()
            ))
        } else {
            modifier(CompactLargeSearchTopBarModifier<EmptyView>(
                label: label, subheading: subheading,
                searchInput: searchInput, searchPrompt: searchPrompt,
                trailingSlot: nil
            ))
        }
    }

    /// Applies a Lemonade-styled large title bar with subheading and search (no toolbar).
    @ViewBuilder
    func lemonadeTopBar(
        label: String,
        subheading: String?,
        searchInput: Binding<String>,
        searchPrompt: String = "Search..."
    ) -> some View {
        if #available(iOS 16, *) {
            modifier(NativeCompactLargeSearchTopBarModifier(
                label: label, subheading: subheading,
                searchInput: searchInput, searchPrompt: searchPrompt,
                toolbarContent: LemonadeEmptyToolbarContent()
            ))
        } else {
            modifier(CompactLargeSearchTopBarModifier<EmptyView>(
                label: label, subheading: subheading,
                searchInput: searchInput, searchPrompt: searchPrompt,
                trailingSlot: nil
            ))
        }
    }
}

#endif
