import SwiftUI
import Lemonade

struct BadgeDisplayView: View {
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                sizesSection
                withNumbersSection
                labelsSection
                inContextSection
            }
            .padding()
        }
        .navigationTitle("Badge")
    }

    private var sizesSection: some View {
        sectionView(title: "Sizes") {
            HStack(spacing: 16) {
                VStack(spacing: 8) {
                    LemonadeUi.Badge(text: "New", size: .xSmall)
                    Text("XSmall")
                        .font(.caption)
                }

                VStack(spacing: 8) {
                    LemonadeUi.Badge(text: "New", size: .small)
                    Text("Small")
                        .font(.caption)
                }
            }
        }
    }

    private var withNumbersSection: some View {
        sectionView(title: "With Numbers") {
            HStack(spacing: 16) {
                LemonadeUi.Badge(text: "1", size: .xSmall)
                LemonadeUi.Badge(text: "5", size: .small)
                LemonadeUi.Badge(text: "99", size: .small)
                LemonadeUi.Badge(text: "99+", size: .small)
            }
        }
    }

    private var labelsSection: some View {
        sectionView(title: "Labels") {
            HStack(spacing: 16) {
                LemonadeUi.Badge(text: "New", size: .small)
                LemonadeUi.Badge(text: "Hot", size: .small)
                LemonadeUi.Badge(text: "Sale", size: .small)
                LemonadeUi.Badge(text: "Beta", size: .small)
            }
        }
    }

    private var inContextSection: some View {
        sectionView(title: "In Context") {
            VStack(spacing: 24) {
                notificationIconsRow
                menuItemRow
                tabItemsRow
            }
        }
    }

    private var notificationIconsRow: some View {
        HStack(spacing: 32) {
            BadgedIcon(
                icon: .bell,
                contentDescription: "Notifications",
                size: .large,
                badgeText: "3"
            )

            BadgedIcon(
                icon: .envelope,
                contentDescription: "Messages",
                size: .large,
                badgeText: "12"
            )

            BadgedIcon(
                icon: .shoppingBag,
                contentDescription: "Cart",
                size: .large,
                badgeText: "99+",
                overhang: 12
            )
        }
    }

    private var menuItemRow: some View {
        HStack {
            LemonadeUi.Icon(
                icon: .inbox,
                contentDescription: nil,
                size: .medium
            )
            Text("Inbox")
            Spacer()
            LemonadeUi.Badge(text: "5", size: .small)
        }
        .padding()
        .background(.bg.bgSubtle)
        .clipShape(.rect(cornerRadius: 12))
    }

    private var tabItemsRow: some View {
        HStack(spacing: 24) {
            VStack(spacing: 4) {
                // Home routes through BadgedIcon with no badge so it keeps the same padding
                // as its badged neighbours and the labels stay on one baseline.
                BadgedIcon(
                    icon: .home,
                    contentDescription: nil,
                    size: .medium,
                    badgeText: nil
                )
                Text("Home")
                    .font(.caption)
            }

            VStack(spacing: 4) {
                BadgedIcon(
                    icon: .bell,
                    contentDescription: nil,
                    size: .medium,
                    badgeText: "2"
                )
                Text("Alerts")
                    .font(.caption)
            }

            VStack(spacing: 4) {
                BadgedIcon(
                    icon: .user,
                    contentDescription: nil,
                    size: .medium,
                    badgeText: "New",
                    overhang: 12
                )
                Text("Profile")
                    .font(.caption)
            }
        }
    }

    private func sectionView<Content: View>(title: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(title)
                .font(.headline)
                .foregroundStyle(.content.contentSecondary)

            content()
        }
    }
}

// MARK: - Badged Icon

/// An icon with a badge pinned to its top-trailing corner.
///
/// The badge is an overlay so the icon alone decides the layout size, and the overhang the
/// badge introduces is paid back as padding. Offsetting the badge inside a `ZStack` instead
/// draws it outside the measured frame, where wide badge text clips against the trailing edge.
private struct BadgedIcon: View {
    let icon: LemonadeIcon
    let contentDescription: String?
    let size: LemonadeUiIconSize
    let badgeText: String?
    var overhang: CGFloat = 8

    var body: some View {
        LemonadeUi.Icon(
            icon: icon,
            contentDescription: contentDescription,
            size: size
        )
        .overlay(alignment: .topTrailing) {
            if let badgeText {
                LemonadeUi.Badge(text: badgeText, size: .xSmall)
                    .fixedSize()
                    .offset(x: overhang, y: -verticalOverhang)
            }
        }
        .padding(.top, verticalOverhang)
        .padding(.trailing, overhang)
    }

    private var verticalOverhang: CGFloat { 8 }
}

#Preview {
    NavigationStack {
        BadgeDisplayView()
    }
}
