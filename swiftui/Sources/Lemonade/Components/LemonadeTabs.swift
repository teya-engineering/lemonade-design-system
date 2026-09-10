import SwiftUI
#if canImport(UIKit)
import UIKit
#endif

// MARK: - Tab Item Model

public struct LemonadeTabItem {
    public let label: String
    public let icon: LemonadeIcon?
    public let isDisabled: Bool

    public init(
        label: String,
        icon: LemonadeIcon? = nil,
        isDisabled: Bool = false
    ) {
        self.label = label
        self.icon = icon
        self.isDisabled = isDisabled
    }
}

// MARK: - Tab Items Size

public enum TabItemsSize {
    case hug
    case stretch
}

// MARK: - Tabs Component

public extension LemonadeUi {
    /// A horizontal tab bar with selection indicator.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.Tabs(
    ///     tabs: [
    ///         LemonadeTabItem(label: "Overview"),
    ///         LemonadeTabItem(label: "Details"),
    ///         LemonadeTabItem(label: "Reviews")
    ///     ],
    ///     selectedIndex: 0,
    ///     onTabSelected: { index in }
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - tabs: List of tab items to display
    ///   - selectedIndex: Index of the currently selected tab
    ///   - onTabSelected: Callback invoked with the tab index when tapped
    ///   - itemsSize: `.hug` for content-sized scrollable tabs, `.stretch` for equal-width tabs
    ///   - showDivider: Flag to show a divider below the tabs. Defaults to true.
    ///     Pass `false` when the host already draws its own separator.
    /// - Returns: A styled tabs view
    @ViewBuilder
    static func Tabs(
        tabs: [LemonadeTabItem],
        selectedIndex: Int,
        onTabSelected: @escaping (Int) -> Void,
        itemsSize: TabItemsSize = .hug,
        showDivider: Bool = true
    ) -> some View {
        LemonadeTabsView(
            tabs: tabs,
            selectedIndex: selectedIndex,
            onTabSelected: onTabSelected,
            itemsSize: itemsSize,
            showDivider: showDivider
        )
    }
}

// MARK: - Internal Tabs View

private struct LemonadeTabsView: View {
    let tabs: [LemonadeTabItem]
    let selectedIndex: Int
    let onTabSelected: (Int) -> Void
    let itemsSize: TabItemsSize
    let showDivider: Bool

    @Namespace private var tabNamespace
    @State private var contentWidth: CGFloat = 0
    @State private var containerWidth: CGFloat = 0
    @State private var scrollOffset: CGFloat = 0
    @State private var didInitialScroll = false
    @State private var contentWrapperWidths: [Int: CGFloat] = [:]

    // The fade applies only when the strip is scrollable and there is still content past the
    // trailing edge. `scrollOffset` is observed live from the underlying UIScrollView via
    // `ScrollViewOffsetObserver`, since SwiftUI's ScrollView doesn't expose offset directly on the
    // iOS 15 deployment target.
    private var showTrailingFade: Bool {
        let scrollThreshold: CGFloat = 1
        guard contentWidth > containerWidth, containerWidth > 0 else { return false }
        return scrollOffset + containerWidth < contentWidth - scrollThreshold
    }

    var body: some View {
        VStack(spacing: 0) {
            switch itemsSize {
            case .hug:
                ScrollViewReader { proxy in
                    ScrollView(.horizontal, showsIndicators: false) {
                        tabRow
                            .background(
                                GeometryReader { geo in
                                    Color.clear
                                        .onAppear { contentWidth = geo.size.width }
                                        .onChange(of: geo.size.width) { contentWidth = $0 }
                                }
                            )
                            #if canImport(UIKit)
                            .background(
                                ScrollViewOffsetObserver(offset: $scrollOffset)
                                    .frame(width: 0, height: 0)
                            )
                            #endif
                    }
                    .background(
                        GeometryReader { geo in
                            Color.clear
                                .onAppear { containerWidth = geo.size.width }
                                .onChange(of: geo.size.width) { containerWidth = $0 }
                        }
                    )
                    .mask(alignment: .leading) {
                        HStack(spacing: 0) {
                            Rectangle().fill(Color.black)
                            LinearGradient(
                                colors: [.black, .clear],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                            .frame(width: showTrailingFade ? max(containerWidth * 0.15, 0) : 0)
                        }
                        .animation(.easeInOut(duration: 0.2), value: showTrailingFade)
                    }
                    .onChange(of: selectedIndex) { newIndex in
                        // Any intentional selection change supersedes the
                        // initial-scroll path so widths arriving late can't
                        // cancel the animated scroll with a non-animated one.
                        didInitialScroll = true
                        withAnimation(.easeInOut(duration: 0.25)) {
                            proxy.scrollTo(newIndex, anchor: .center)
                        }
                    }
                    .onChange(of: contentWidth) { _ in
                        performInitialScrollIfNeeded(proxy: proxy)
                    }
                    .onChange(of: containerWidth) { _ in
                        performInitialScrollIfNeeded(proxy: proxy)
                    }
                }
            case .stretch:
                tabRow
            }

            if showDivider {
                LemonadeUi.HorizontalDivider()
            }
        }
    }

    private func performInitialScrollIfNeeded(proxy: ScrollViewProxy) {
        guard !didInitialScroll, contentWidth > 0, containerWidth > 0 else { return }
        didInitialScroll = true
        proxy.scrollTo(selectedIndex, anchor: .center)
    }

    private var tabRow: some View {
        HStack(spacing: 0) {
            ForEach(Array(tabs.enumerated()), id: \.offset) { index, tab in
                VStack(spacing: 0) {
                    TabItemView(
                        tab: tab,
                        isSelected: index == selectedIndex,
                        stretch: itemsSize == .stretch,
                        onClick: {
                            withAnimation(.easeInOut(duration: 0.25)) {
                                onTabSelected(index)
                            }
                        },
                        onContentWidth: { width in
                            contentWrapperWidths[index] = width
                        }
                    )

                    if index == selectedIndex {
                        // Fill the top-rounded shape directly instead of clipping a Rectangle:
                        // clipShape masks the bar into an offscreen pass on every indicator
                        // animation frame; filling the shape is identical and mask-free.
                        TopRoundedRectangle(radius: 2)
                            .fill(LemonadeTheme.colors.background.bgBrandHigh)
                            .frame(
                                width: contentWrapperWidths[selectedIndex],
                                height: LemonadeTheme.borderWidth.base.border75
                            )
                            .matchedGeometryEffect(id: "indicator", in: tabNamespace)
                            // TODO: Add .glassEffect(.regular.interactive()) when building with Xcode 26+ SDK
                    } else {
                        Color.clear
                            .frame(height: LemonadeTheme.borderWidth.base.border75)
                    }
                }
                .id(index)
            }
        }
    }
}

// MARK: - Top Rounded Rectangle Shape

private struct TopRoundedRectangle: Shape {
    let radius: CGFloat

    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: rect.minX, y: rect.maxY))
        path.addLine(to: CGPoint(x: rect.minX, y: rect.minY + radius))
        path.addQuadCurve(
            to: CGPoint(x: rect.minX + radius, y: rect.minY),
            control: CGPoint(x: rect.minX, y: rect.minY)
        )
        path.addLine(to: CGPoint(x: rect.maxX - radius, y: rect.minY))
        path.addQuadCurve(
            to: CGPoint(x: rect.maxX, y: rect.minY + radius),
            control: CGPoint(x: rect.maxX, y: rect.minY)
        )
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.maxY))
        path.closeSubpath()
        return path
    }
}

// MARK: - Tab Item View

private struct TabItemView: View {
    let tab: LemonadeTabItem
    let isSelected: Bool
    let stretch: Bool
    let onClick: () -> Void
    var onContentWidth: ((CGFloat) -> Void)? = nil

    @State private var isPressed = false
    @State private var isHovering = false

    private var contentColor: Color {
        isSelected
            ? LemonadeTheme.colors.content.contentBrandHigh
            : LemonadeTheme.colors.content.contentSecondary
    }

    private var iconColor: Color {
        isSelected
            ? LemonadeTheme.colors.content.contentBrand
            : LemonadeTheme.colors.content.contentPrimary
    }

    private var textStyle: LemonadeTextStyle {
        isSelected
            ? LemonadeTypography.shared.bodySmallSemiBold
            : LemonadeTypography.shared.bodySmallMedium
    }

    private var wrapperBackground: Color {
        (isHovering || isPressed) && !tab.isDisabled
            ? LemonadeTheme.colors.interaction.bgSubtleInteractive
            : .clear
    }

    var body: some View {
        SwiftUI.Button(action: onClick) {
            HStack(spacing: LemonadeTheme.spaces.spacing100) {
                if let icon = tab.icon {
                    LemonadeUi.Icon(
                        icon: icon,
                        contentDescription: nil,
                        size: .small,
                        tint: iconColor
                    )
                }
                ZStack {
                    LemonadeUi.Text(
                        tab.label,
                        textStyle: LemonadeTypography.shared.bodySmallSemiBold,
                        color: .clear,
                        overflow: .tail,
                        maxLines: 1
                    )
                    LemonadeUi.Text(
                        tab.label,
                        textStyle: textStyle,
                        color: contentColor,
                        overflow: .tail,
                        maxLines: 1
                    )
                }
            }
            .padding(.horizontal, LemonadeTheme.spaces.spacing200)
            .padding(.vertical, LemonadeTheme.spaces.spacing100)
            .background(
                RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius150)
                    .fill(wrapperBackground)
            )
            .background(
                GeometryReader { geo in
                    Color.clear.onAppear {
                        onContentWidth?(geo.size.width)
                    }
                    .onChange(of: geo.size.width) { newWidth in
                        onContentWidth?(newWidth)
                    }
                }
            )
            .padding(.horizontal, LemonadeTheme.spaces.spacing200)
            .frame(minHeight: LemonadeTheme.sizes.size1100)
            .frame(maxWidth: stretch ? .infinity : nil)
        }
        .buttonStyle(TabPressButtonStyle(isPressed: $isPressed))
        .disabled(tab.isDisabled)
        .opacity(tab.isDisabled ? LemonadeTheme.opacity.state.opacityDisabled : 1.0)
        .onHover { hovering in isHovering = hovering }
        .animation(.easeInOut(duration: 0.15), value: isHovering)
        .accessibilityAddTraits(isSelected ? .isSelected : [])
    }
}

// MARK: - Tab Press Button Style

private struct TabPressButtonStyle: ButtonStyle {
    @Binding var isPressed: Bool

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .onChange(of: configuration.isPressed) { newValue in
                withAnimation(.easeInOut(duration: 0.15)) {
                    isPressed = newValue
                }
            }
    }
}

// MARK: - Scroll Offset Observer

#if canImport(UIKit)
/// Walks up to the enclosing `UIScrollView` and KVO-observes `contentOffset.x`,
/// publishing it back into a SwiftUI `@State` via the binding. Needed because
/// `.onPreferenceChange` over a named coordinate space doesn't fire on every
/// scroll tick on iOS 18+ in this configuration, and iOS 15 is the deployment
/// target so `.scrollPosition` / `.onScrollGeometryChange` aren't available.
private struct ScrollViewOffsetObserver: UIViewRepresentable {
    @Binding var offset: CGFloat

    func makeUIView(context: Context) -> UIView {
        let view = UIView()
        view.isUserInteractionEnabled = false
        return view
    }

    func updateUIView(_ uiView: UIView, context: Context) {
        DispatchQueue.main.async {
            context.coordinator.attachIfNeeded(from: uiView)
        }
    }

    static func dismantleUIView(_ uiView: UIView, coordinator: Coordinator) {
        coordinator.detach()
    }

    func makeCoordinator() -> Coordinator {
        Coordinator(offset: $offset)
    }

    final class Coordinator {
        @Binding var offset: CGFloat
        private var observation: NSKeyValueObservation?
        private weak var observedScrollView: UIScrollView?

        init(offset: Binding<CGFloat>) {
            self._offset = offset
        }

        func attachIfNeeded(from view: UIView) {
            guard let scrollView = enclosingScrollView(of: view),
                  scrollView !== observedScrollView else { return }
            observation?.invalidate()
            observedScrollView = scrollView
            observation = scrollView.observe(
                \.contentOffset,
                options: [.initial, .new]
            ) { [weak self] sv, _ in
                let value = sv.contentOffset.x
                DispatchQueue.main.async { [weak self] in
                    guard let self = self, self.offset != value else { return }
                    self.offset = value
                }
            }
        }

        func detach() {
            observation?.invalidate()
            observation = nil
        }

        private func enclosingScrollView(of view: UIView) -> UIScrollView? {
            var current: UIView? = view.superview
            while let v = current {
                if let scrollView = v as? UIScrollView { return scrollView }
                current = v.superview
            }
            return nil
        }
    }
}
#endif

// MARK: - Previews

#if DEBUG
struct LemonadeTabs_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: LemonadeTheme.spaces.spacing800) {
            basicTabs
            tabsWithIcons
            scrollableTabs
            stretchedTabsWithoutDivider
        }
        .previewLayout(.sizeThatFits)
    }

    private static var basicTabs: some View {
        LemonadeUi.Tabs(
            tabs: [
                LemonadeTabItem(label: "Overview"),
                LemonadeTabItem(label: "Details"),
                LemonadeTabItem(label: "Reviews")
            ],
            selectedIndex: 1,
            onTabSelected: { _ in }
        )
    }

    private static var tabsWithIcons: some View {
        LemonadeUi.Tabs(
            tabs: [
                LemonadeTabItem(label: "Home", icon: .home),
                LemonadeTabItem(label: "Search", icon: .search),
                LemonadeTabItem(label: "Profile", icon: .user)
            ],
            selectedIndex: 0,
            onTabSelected: { _ in }
        )
    }

    private static var scrollableTabs: some View {
        LemonadeUi.Tabs(
            tabs: [
                LemonadeTabItem(label: "Dashboard"),
                LemonadeTabItem(label: "Analytics"),
                LemonadeTabItem(label: "Reports"),
                LemonadeTabItem(label: "Settings"),
                LemonadeTabItem(label: "Users"),
                LemonadeTabItem(label: "Activity")
            ],
            selectedIndex: 2,
            onTabSelected: { _ in }
        )
    }

    private static var stretchedTabsWithoutDivider: some View {
        LemonadeUi.Tabs(
            tabs: [
                LemonadeTabItem(label: "Tab A"),
                LemonadeTabItem(label: "Tab B"),
                LemonadeTabItem(label: "Tab C")
            ],
            selectedIndex: 0,
            onTabSelected: { _ in },
            itemsSize: .stretch,
            showDivider: false
        )
    }
}
#endif
