import SwiftUI

// MARK: - Tab Button Properties

/// Properties for a single tab button in a segmented control.
public struct LemonadeTabButtonProperties: Identifiable, Hashable {
    public let id: String
    /// The label text for the tab. When nil, only the icon is shown.
    public let label: String?
    /// Optional icon to display before the label.
    public let icon: LemonadeIcon?

    private init(id: String? = nil, label: String?, icon: LemonadeIcon?) {
        self.id = id ?? UUID().uuidString
        self.label = label
        self.icon = icon
    }

    /// Creates a tab with a text label.
    public static func label(_ label: String) -> Self {
        Self(label: label, icon: nil)
    }

    /// Creates a tab with a text label and an icon.
    public static func labelAndIcon(_ label: String, icon: LemonadeIcon) -> Self {
        Self(label: label, icon: icon)
    }

    /// Creates a tab with only an icon.
    public static func icon(_ icon: LemonadeIcon) -> Self {
        Self(label: nil, icon: icon)
    }
}

/// Size variants for the segmented control.
public enum LemonadeSegmentedControlSize {
    case small
    case medium
    case large
}

private extension LemonadeSegmentedControlSize {
    var containerHeight: CGFloat {
        switch self {
        case .small: return LemonadeTheme.sizes.size800
        case .medium: return LemonadeTheme.sizes.size1000
        case .large: return LemonadeTheme.sizes.size1200
        }
    }

    var buttonContentGap: CGFloat {
        switch self {
        case .small: return LemonadeTheme.spaces.spacing50
        case .medium, .large: return LemonadeTheme.spaces.spacing100
        }
    }

    var containerPadding: CGFloat {
        switch self {
        case .small: return LemonadeTheme.spaces.spacing50
        case .medium, .large: return LemonadeTheme.spaces.spacing100
        }
    }

    var buttonMinWidth: CGFloat {
        switch self {
        case .small: return LemonadeTheme.sizes.size800
        case .medium, .large: return LemonadeTheme.sizes.size1200
        }
    }

    var buttonMinHeight: CGFloat {
        switch self {
        case .small: return LemonadeTheme.sizes.size700
        case .medium, .large: return LemonadeTheme.sizes.size800
        }
    }

    var buttonHorizontalPadding: CGFloat {
        switch self {
        case .small: return LemonadeTheme.spaces.spacing200
        case .medium, .large: return LemonadeTheme.spaces.spacing300
        }
    }

    var textStyle: LemonadeTextStyle {
        switch self {
        case .small: return LemonadeTypography.shared.bodyXSmallMedium
        case .medium, .large: return LemonadeTypography.shared.bodySmallMedium
        }
    }
}

// MARK: - Segmented Control Component

public extension LemonadeUi {
    /// A horizontal control used to select a single option from a set of two or more segments.
    /// Ideal for toggling between views or filtering content.
    ///
    /// ## Usage
    /// ```swift
    /// LemonadeUi.SegmentedControl(
    ///     properties: [
    ///         .labelAndIcon("Tab 1", icon: .heart),
    ///         .labelAndIcon("Tab 2", icon: .laptop),
    ///         .label("Tab 3"),
    ///     ],
    ///     selectedTab: selectedTabIndex,
    ///     onTabSelected: { tabIndex in /* ... */ }
    /// )
    /// ```
    ///
    /// - Parameters:
    ///   - properties: A list of `LemonadeTabButtonProperties` that represent the tab buttons' information.
    ///   - selectedTab: Int that indicates what is the index of the selected tab.
    ///   - size: The size of the segmented control. Defaults to `.large`.
    ///   - onTabSelected: A callback invoked when a tab is selected.
    /// - Returns: A styled SegmentedControl view
    @ViewBuilder
    static func SegmentedControl(
        properties: [LemonadeTabButtonProperties],
        selectedTab: Int,
        size: LemonadeSegmentedControlSize = .large,
        onTabSelected: @escaping (Int) -> Void
    ) -> some View {
        LemonadeSegmentedControlView(
            properties: properties,
            selectedTab: selectedTab,
            size: size,
            onTabSelected: onTabSelected
        )
    }
}

// MARK: - Internal Segmented Control View

private struct LemonadeSegmentedControlView: View {
    let properties: [LemonadeTabButtonProperties]
    let selectedTab: Int
    let size: LemonadeSegmentedControlSize
    let onTabSelected: (Int) -> Void

    @Namespace private var indicatorNamespace

    private var clampedSelectedTab: Int {
        min(max(selectedTab, 0), properties.count - 1)
    }

    var body: some View {
        #if canImport(UIKit)
        if #available(iOS 26.0, *) {
            EqualWidthHStack {
                segments(drawSelectionIndicator: false)
            }
            .hidden()
            .background {
                GeometryReader { proxy in
                    LemonadeNativeSegmentedControl(
                        properties: properties,
                        size: size,
                        width: proxy.size.width,
                        selectedIndex: clampedSelectedTab,
                        onSelectionChanged: onTabSelected
                    )
                }
            }
            .frame(
                minWidth: size.buttonMinWidth,
                minHeight: size.buttonMinHeight
            )
            .frame(height: size.containerHeight)
        } else {
            fallbackControl
        }
        #else
        fallbackControl
        #endif
    }

    private var fallbackControl: some View {
        HStack(spacing: 0) {
            segments(drawSelectionIndicator: true)
        }
        .animation(.easeInOut(duration: 0.2), value: clampedSelectedTab)
        .padding(size.containerPadding)
        .frame(
            minWidth: size.buttonMinWidth,
            minHeight: size.buttonMinHeight
        )
        .frame(height: size.containerHeight)
        .background(
            RoundedRectangle(cornerRadius: LemonadeTheme.radius.radiusFull)
                .fill(LemonadeTheme.colors.background.bgElevated)
        )
    }

    @ViewBuilder
    private func segments(drawSelectionIndicator: Bool) -> some View {
        ForEach(properties.indices, id: \.self) { index in
            let property = properties[index]
            let isSelected = index == clampedSelectedTab
            let tintColor = isSelected
                ? LemonadeTheme.colors.content.contentPrimary
                : LemonadeTheme.colors.content.contentSecondary

            Button {
                onTabSelected(index)
            } label: {
                SegmentContent(property: property, size: size, tint: tintColor)
                    .padding(.horizontal, size.buttonHorizontalPadding)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .contentShape(Rectangle())
            }
            .buttonStyle(SegmentPressStyle())
            .frame(maxWidth: .infinity)
            .accessibilityLabel(property.label ?? property.icon?.rawValue ?? "")
            .accessibilityAddTraits(isSelected ? .isSelected : [])
            .background {
                if drawSelectionIndicator && isSelected {
                    RoundedRectangle(cornerRadius: LemonadeTheme.radius.radiusFull)
                        .fill(LemonadeTheme.colors.background.bgDefault)
                        .lemonadeShadow(.xsmall)
                        .matchedGeometryEffect(id: "indicator", in: indicatorNamespace)
                }
            }
        }
    }
}

private struct SegmentContent: View {
    let property: LemonadeTabButtonProperties
    let size: LemonadeSegmentedControlSize
    let tint: Color

    var body: some View {
        HStack(spacing: size.buttonContentGap) {
            if let icon = property.icon {
                LemonadeUi.Icon(
                    icon: icon,
                    contentDescription: property.label,
                    size: .small,
                    tint: tint
                )
            }

            if let label = property.label {
                LemonadeUi.Text(
                    label,
                    textStyle: size.textStyle,
                    textAlign: .center,
                    color: tint,
                    maxLines: 1
                )
            }
        }
    }
}

private struct SegmentPressStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .opacity(configuration.isPressed ? 0.5 : 1.0)
            .animation(.easeInOut(duration: 0.1), value: configuration.isPressed)
    }
}

// MARK: - Native UISegmentedControl (provides Liquid Glass on iOS 26)

#if canImport(UIKit)
import UIKit

@available(iOS 26.0, *)
private struct LemonadeNativeSegmentedControl: UIViewRepresentable {
    let properties: [LemonadeTabButtonProperties]
    let size: LemonadeSegmentedControlSize
    let width: CGFloat
    let selectedIndex: Int
    let onSelectionChanged: (Int) -> Void

    private var contentWidth: CGFloat {
        guard !properties.isEmpty else { return 0 }
        return max(0, (width / CGFloat(properties.count) - size.buttonHorizontalPadding * 2).rounded(.down))
    }

    func makeCoordinator() -> Coordinator {
        Coordinator(onSelectionChanged: onSelectionChanged)
    }

    func makeUIView(context: Context) -> UIView {
        let container = UIView()
        container.backgroundColor = .clear

        let control = UISegmentedControl()
        control.backgroundColor = .clear
        control.setContentCompressionResistancePriority(.defaultLow, for: .vertical)
        control.addTarget(
            context.coordinator,
            action: #selector(Coordinator.segmentChanged(_:)),
            for: .valueChanged
        )

        control.translatesAutoresizingMaskIntoConstraints = false
        container.addSubview(control)
        pinControlToEdges(control, in: container)

        context.coordinator.control = control
        return container
    }

    func updateUIView(_ uiView: UIView, context: Context) {
        guard let control = context.coordinator.control else { return }
        context.coordinator.onSelectionChanged = onSelectionChanged

        syncTint(of: control)
        syncSegments(of: control, context: context)
        syncSelectedIndex(of: control)
    }

    // Template images take the title foreground colour, so one image per segment serves both states.
    @MainActor
    private func syncTint(of control: UISegmentedControl) {
        control.setTitleTextAttributes(
            [.foregroundColor: UIColor(LemonadeTheme.colors.content.contentSecondary)],
            for: .normal
        )
        control.setTitleTextAttributes(
            [.foregroundColor: UIColor(LemonadeTheme.colors.content.contentPrimary)],
            for: .selected
        )
    }

    // The content lives inside the native segments, not on top of them, so the glass thumb can refract it.
    @MainActor
    private func syncSegments(of control: UISegmentedControl, context: Context) {
        let key = SegmentsKey(
            labels: properties.map(\.label),
            icons: properties.map(\.icon),
            size: size,
            contentWidth: contentWidth,
            displayScale: context.environment.displayScale,
            dynamicTypeSize: context.environment.dynamicTypeSize,
            layoutDirection: context.environment.layoutDirection,
            legibilityWeight: context.environment.legibilityWeight
        )
        guard context.coordinator.segmentsKey != key else { return }
        context.coordinator.segmentsKey = key

        control.removeAllSegments()
        for (index, property) in properties.enumerated() {
            control.insertSegment(with: renderImage(for: property, key: key), at: index, animated: false)
        }
    }

    @MainActor
    private func renderImage(for property: LemonadeTabButtonProperties, key: SegmentsKey) -> UIImage? {
        let renderer = ImageRenderer(
            content: SegmentContent(property: property, size: size, tint: .black)
                .environment(\.dynamicTypeSize, key.dynamicTypeSize)
                .environment(\.layoutDirection, key.layoutDirection)
                .environment(\.legibilityWeight, key.legibilityWeight)
        )
        renderer.scale = key.displayScale
        // Without a proposed width the label renders untruncated, and UIKit squashes an image wider than its segment.
        renderer.proposedSize = ProposedViewSize(width: key.contentWidth, height: nil)
        let image = renderer.uiImage?.withRenderingMode(.alwaysTemplate)
        image?.accessibilityLabel = property.label ?? property.icon?.rawValue
        return image
    }

    @MainActor
    private func pinControlToEdges(_ control: UISegmentedControl, in container: UIView) {
        NSLayoutConstraint.activate([
            control.leadingAnchor.constraint(equalTo: container.leadingAnchor),
            control.trailingAnchor.constraint(equalTo: container.trailingAnchor),
            control.topAnchor.constraint(equalTo: container.topAnchor),
            control.bottomAnchor.constraint(equalTo: container.bottomAnchor),
        ])
    }

    @MainActor
    private func syncSelectedIndex(of control: UISegmentedControl) {
        let clampedIndex = min(selectedIndex, properties.count - 1)
        if control.selectedSegmentIndex != clampedIndex {
            control.selectedSegmentIndex = clampedIndex
        }
    }

    // The labels own the size; as their background, the native control fills whatever they take.
    func sizeThatFits(_ proposal: ProposedViewSize, uiView: UIView, context: Context) -> CGSize? {
        proposal.replacingUnspecifiedDimensions()
    }

    struct SegmentsKey: Equatable {
        let labels: [String?]
        let icons: [LemonadeIcon?]
        let size: LemonadeSegmentedControlSize
        let contentWidth: CGFloat
        let displayScale: CGFloat
        let dynamicTypeSize: DynamicTypeSize
        let layoutDirection: LayoutDirection
        let legibilityWeight: LegibilityWeight?
    }

    class Coordinator: NSObject {
        var onSelectionChanged: (Int) -> Void
        weak var control: UISegmentedControl?
        var segmentsKey: SegmentsKey?

        init(onSelectionChanged: @escaping (Int) -> Void) {
            self.onSelectionChanged = onSelectionChanged
        }

        @MainActor @objc func segmentChanged(_ control: UISegmentedControl) {
            onSelectionChanged(control.selectedSegmentIndex)
        }
    }
}
#endif

// MARK: - Previews

#if DEBUG
struct LemonadeSegmentedControl_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 24) {
            LemonadeUi.SegmentedControl(
                properties: [
                    .label("Tab 1"),
                    .label("Tab 2"),
                    .label("Tab 3"),
                ],
                selectedTab: 1,
                onTabSelected: { _ in }
            )

            LemonadeUi.SegmentedControl(
                properties: [
                    .label("Tab 1"),
                    .label("Tab 2"),
                    .label("Tab 3"),
                ],
                selectedTab: 0,
                size: .medium,
                onTabSelected: { _ in }
            )

            LemonadeUi.SegmentedControl(
                properties: [
                    .label("Tab 1"),
                    .label("Tab 2"),
                ],
                selectedTab: 0,
                size: .small,
                onTabSelected: { _ in }
            )

            LemonadeUi.SegmentedControl(
                properties: [
                    .icon(.heart),
                    .icon(.star),
                    .icon(.gear),
                ],
                selectedTab: 0,
                size: .small,
                onTabSelected: { _ in }
            )
            .fixedSize(horizontal: true, vertical: false)
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
