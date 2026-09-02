import SwiftUI
import Lemonade

struct SkeletonDisplayView: View {
    /// Every skeleton kicks off a `repeatForever` shimmer from its own `onAppear`, and the
    /// library gives the caller no handle to stop it. Removing the skeletons from the
    /// hierarchy when the screen goes away is the only teardown available here — without
    /// it the fourteen shimmers on this screen keep animating, and keep invalidating,
    /// while the screen is buried in the navigation stack.
    @State private var isOnScreen = false

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing800) {
                if isOnScreen {
                    skeletonSections
                }
            }
            .padding()
        }
        .onAppear { isOnScreen = true }
        .onDisappear { isOnScreen = false }
        .navigationTitle("Skeleton")
    }

    @ViewBuilder
    private var skeletonSections: some View {
        sectionView(title: "Line Skeleton") {
            VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing300) {
                ForEach(skeletonSizes, id: \.label) { item in
                    VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing100) {
                        Text(item.label)
                            .font(.caption)
                            .foregroundStyle(.content.contentSecondary)
                        LemonadeUi.LineSkeleton(size: item.size)
                    }
                }
            }
        }

        sectionView(title: "Circle Skeleton") {
            HStack(spacing: LemonadeTheme.spaces.spacing400) {
                ForEach(skeletonSizes, id: \.label) { item in
                    VStack(spacing: LemonadeTheme.spaces.spacing100) {
                        LemonadeUi.CircleSkeleton(size: item.size)
                        Text(item.label)
                            .font(.caption2)
                            .foregroundStyle(.content.contentSecondary)
                    }
                }
            }
        }

        sectionView(title: "Block Skeleton") {
            LemonadeUi.BlockSkeleton()
        }

        sectionView(title: "Content Placeholder") {
            HStack(alignment: .top, spacing: LemonadeTheme.spaces.spacing300) {
                LemonadeUi.CircleSkeleton(size: .large)
                VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing200) {
                    LemonadeUi.LineSkeleton(size: .small)
                    LemonadeUi.LineSkeleton(size: .xSmall)
                        .frame(maxWidth: 160)
                }
            }
        }
    }

    private var skeletonSizes: [(label: String, size: LemonadeSkeletonSize)] {
        [
            ("XS", .xSmall),
            ("S", .small),
            ("M", .medium),
            ("L", .large),
            ("XL", .xLarge)
        ]
    }

    private func sectionView<Content: View>(title: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing300) {
            Text(title)
                .font(.headline)
                .foregroundStyle(.content.contentSecondary)

            content()
        }
    }
}

#Preview {
    NavigationStack {
        SkeletonDisplayView()
    }
}
