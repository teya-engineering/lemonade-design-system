import SwiftUI
import Lemonade

struct IconButtonDisplayView: View {
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                // Primary Solid
                sectionView(title: "Primary Solid") {
                    sizesRow(icon: .heart, variant: .primary, type: .solid)
                }

                // Secondary Solid
                sectionView(title: "Secondary Solid") {
                    sizesRow(icon: .heart, variant: .secondary, type: .solid)
                }

                // Neutral Subtle (default)
                sectionView(title: "Neutral Subtle") {
                    sizesRow(icon: .heart, variant: .neutral, type: .subtle)
                }

                // Neutral Ghost
                sectionView(title: "Neutral Ghost") {
                    sizesRow(icon: .circleX, variant: .neutral, type: .ghost)
                }

                // Critical Subtle
                sectionView(title: "Critical Subtle") {
                    sizesRow(icon: .trash, variant: .critical, type: .subtle)
                }

                // Critical Solid
                sectionView(title: "Critical Solid") {
                    sizesRow(icon: .trash, variant: .critical, type: .solid)
                }

                // Loading
                sectionView(title: "Loading") {
                    HStack(spacing: 12) {
                        LemonadeUi.IconButton(
                            icon: .heart,
                            contentDescription: "Loading",
                            onClick: {},
                            variant: .primary,
                            type: .solid,
                            loading: true
                        )
                        LemonadeUi.IconButton(
                            icon: .heart,
                            contentDescription: "Loading",
                            onClick: {},
                            variant: .neutral,
                            type: .subtle,
                            loading: true
                        )
                        LemonadeUi.IconButton(
                            icon: .heart,
                            contentDescription: "Loading",
                            onClick: {},
                            variant: .critical,
                            type: .solid,
                            loading: true
                        )
                    }
                }

                // Circular Shape
                sectionView(title: "Circular") {
                    HStack(spacing: 12) {
                        LemonadeUi.IconButton(
                            icon: .heart,
                            contentDescription: "Circular",
                            onClick: {},
                            variant: .primary,
                            type: .solid,
                            shape: .circular
                        )
                        LemonadeUi.IconButton(
                            icon: .heart,
                            contentDescription: "Circular",
                            onClick: {},
                            variant: .neutral,
                            type: .subtle,
                            shape: .circular
                        )
                        LemonadeUi.IconButton(
                            icon: .heart,
                            contentDescription: "Circular",
                            onClick: {},
                            variant: .critical,
                            type: .solid,
                            shape: .circular
                        )
                    }
                }

                // Disabled
                sectionView(title: "Disabled") {
                    HStack(spacing: 12) {
                        LemonadeUi.IconButton(
                            icon: .heart,
                            contentDescription: "Disabled",
                            onClick: {},
                            enabled: false,
                            variant: .primary,
                            type: .solid
                        )
                        LemonadeUi.IconButton(
                            icon: .heart,
                            contentDescription: "Disabled",
                            onClick: {},
                            enabled: false,
                            variant: .neutral,
                            type: .subtle
                        )
                        LemonadeUi.IconButton(
                            icon: .heart,
                            contentDescription: "Disabled",
                            onClick: {},
                            enabled: false,
                            variant: .neutral,
                            type: .ghost
                        )
                    }
                }

                // Various Icons
                sectionView(title: "Various Icons") {
                    HStack(spacing: 12) {
                        LemonadeUi.IconButton(
                            icon: .bell,
                            contentDescription: "Notifications",
                            onClick: {}
                        )
                        LemonadeUi.IconButton(
                            icon: .gear,
                            contentDescription: "Settings",
                            onClick: {}
                        )
                        LemonadeUi.IconButton(
                            icon: .trash,
                            contentDescription: "Delete",
                            onClick: {},
                            variant: .critical,
                            type: .subtle
                        )
                        LemonadeUi.IconButton(
                            icon: .star,
                            contentDescription: "Bookmark",
                            onClick: {}
                        )
                    }
                }
            }
            .padding()
        }
        .navigationTitle("IconButton")
    }

    private func sizesRow(
        icon: LemonadeIcon,
        variant: LemonadeButtonVariant,
        type: LemonadeButtonType
    ) -> some View {
        VStack(spacing: 16) {
            HStack(spacing: 12) {
                LemonadeUi.IconButton(
                    icon: icon,
                    contentDescription: "Small",
                    onClick: {},
                    variant: variant,
                    type: type,
                    size: .small
                )
                LemonadeUi.IconButton(
                    icon: icon,
                    contentDescription: "Medium",
                    onClick: {},
                    variant: variant,
                    type: type,
                    size: .medium
                )
                LemonadeUi.IconButton(
                    icon: icon,
                    contentDescription: "Large",
                    onClick: {},
                    variant: variant,
                    type: type,
                    size: .large
                )
            }

            HStack(spacing: 12) {
                LemonadeUi.IconButton(
                    icon: icon,
                    contentDescription: "Disabled",
                    onClick: {},
                    enabled: false,
                    variant: variant,
                    type: type,
                    size: .medium
                )
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

#Preview {
    NavigationStack {
        IconButtonDisplayView()
    }
}
