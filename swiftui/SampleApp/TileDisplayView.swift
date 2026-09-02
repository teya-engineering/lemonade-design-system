import SwiftUI
import Lemonade

struct TileDisplayView: View {
    @EnvironmentObject private var toastManager: LemonadeToastManager

    @State private var isFilledSelected = true
    @State private var isOutlinedSelected = true
    @State private var isHorizontalFilledSelected = true
    @State private var isHorizontalOutlinedSelected = false

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing600) {
                // MARK: - Variants
                // Pure visual gallery: no `onClick`, so the tiles render as swatches with no
                // press affordance rather than buttons that silently do nothing. The
                // "Interactive" section below is where tapping is demonstrated.
                sectionView(title: "Variants") {
                    HStack(spacing: LemonadeTheme.spaces.spacing400) {
                        VStack(spacing: LemonadeTheme.spaces.spacing200) {
                            LemonadeUi.Tile(
                                label: "Filled",
                                icon: .heart,
                                variant: .filled
                            )
                            LemonadeUi.Text(
                                "Filled",
                                textStyle: LemonadeTypography.shared.bodySmallRegular
                            )
                        }

                        VStack(spacing: LemonadeTheme.spaces.spacing200) {
                            LemonadeUi.Tile(
                                label: "Outlined",
                                icon: .star,
                                variant: .outlined
                            )
                            LemonadeUi.Text(
                                "Outlined",
                                textStyle: LemonadeTypography.shared.bodySmallRegular
                            )
                        }
                    }
                }

                // MARK: - Selected
                sectionView(title: "Selected") {
                    HStack(spacing: LemonadeTheme.spaces.spacing400) {
                        LemonadeUi.Tile(
                            label: "Filled",
                            icon: .circleCheck,
                            isSelected: isFilledSelected,
                            onClick: { isFilledSelected.toggle() },
                            variant: .filled
                        )
                        LemonadeUi.Tile(
                            label: "Outlined",
                            icon: .circleCheck,
                            isSelected: isOutlinedSelected,
                            onClick: { isOutlinedSelected.toggle() },
                            variant: .outlined
                        )
                    }
                }

                // MARK: - Support Text
                sectionView(title: "Support Text") {
                    HStack(spacing: LemonadeTheme.spaces.spacing400) {
                        LemonadeUi.Tile(
                            label: "Filled",
                            icon: .heart,
                            supportText: "Long support text example to check how it wraps and looks on smaller screens",
                            variant: .filled
                        )

                        LemonadeUi.Tile(
                            label: "Outlined",
                            icon: .star,
//                            supportText: "Support",
                            supportText: "Long support text example to check how it wraps and looks on smaller screens",
                            variant: .outlined,
                            stretched: true
                        )
                    }
                }

                // MARK: - Top Accessory
                sectionView(title: "Top Accessory") {
                    HStack(spacing: LemonadeTheme.spaces.spacing400) {
                        LemonadeUi.Tile(
                            label: "Accessory",
                            icon: .heart,
                            variant: .filled
                        ) {
                            LemonadeUi.Icon(
                                icon: .circleInfo,
                                contentDescription: nil,
                                size: .small
                            )
                        }
                    }
                }

                // MARK: - Leading Slot
                sectionView(title: "Leading Slot") {
                    HStack(spacing: LemonadeTheme.spaces.spacing400) {
                        LemonadeUi.Tile(
                            label: "Custom",
                            variant: .filled,
                            leadingSlot: {
                                LemonadeUi.Icon(
                                    icon: .shoppingBag,
                                    contentDescription: nil,
                                    size: .medium
                                )
                            }
                        )
                    }
                }

                // MARK: - Features
                sectionView(title: "Interactive") {
                    HStack(spacing: LemonadeTheme.spaces.spacing400) {
                        LemonadeUi.Tile(
                            label: "Tap me",
                            icon: .handCoins,
                            onClick: { showTap("Tap me") },
                            variant: .filled
                        )

                        LemonadeUi.Tile(
                            label: "Click",
                            icon: .fingerPrint,
                            onClick: { showTap("Click") },
                            variant: .outlined
                        )
                    }
                }

                sectionView(title: "Disabled") {
                    HStack(spacing: LemonadeTheme.spaces.spacing400) {
                        LemonadeUi.Tile(
                            label: "Disabled",
                            icon: .padlock,
                            enabled: false,
                            variant: .filled
                        )

                        LemonadeUi.Tile(
                            label: "Disabled",
                            icon: .padlock,
                            enabled: false,
                            variant: .outlined
                        )
                    }
                }

                // MARK: - Layout Behavior
                sectionView(title: "Default Size (min 120pt)") {
                    LemonadeUi.Tile(
                        label: "Default",
                        icon: .heart,
                        variant: .filled
                    )
                }

                sectionView(title: "Stretched (stretched: true)") {
                    LemonadeUi.Tile(
                        label: "Single Stretched",
                        icon: .heart,
                        onClick: { showTap("Single Stretched") },
                        variant: .filled,
                        stretched: true
                    )

                    HStack(spacing: LemonadeTheme.spaces.spacing400) {
                        LemonadeUi.Tile(
                            label: "Transfer",
                            icon: .arrowLeftRight,
                            onClick: { showTap("Transfer") },
                            variant: .filled,
                            stretched: true
                        )

                        LemonadeUi.Tile(
                            label: "Pay",
                            icon: .card,
                            onClick: { showTap("Pay") },
                            variant: .filled,
                            stretched: true
                        )

                        LemonadeUi.Tile(
                            label: "Request",
                            icon: .download,
                            onClick: { showTap("Request") },
                            variant: .filled,
                            stretched: true
                        )
                    }
                }

                sectionView(title: "Tight Container (200pt for 3 tiles)") {
                    HStack(spacing: LemonadeTheme.spaces.spacing200) {
                        LemonadeUi.Tile(
                            label: "One",
                            icon: .heart,
                            variant: .filled
                        )

                        LemonadeUi.Tile(
                            label: "Two",
                            icon: .star,
                            variant: .filled
                        )

                        LemonadeUi.Tile(
                            label: "Three",
                            icon: .check,
                            variant: .filled
                        )
                    }
                    .frame(width: 200)
                }

                // MARK: - Horizontal Orientation
                sectionView(title: "Horizontal / Variants") {
                    VStack(spacing: LemonadeTheme.spaces.spacing300) {
                        LemonadeUi.Tile(
                            label: "Filled",
                            icon: .heart,
                            variant: .filled,
                            orientation: .horizontal
                        )
                        LemonadeUi.Tile(
                            label: "Outlined",
                            icon: .star,
                            variant: .outlined,
                            orientation: .horizontal
                        )
                    }
                }

                sectionView(title: "Horizontal / Selected") {
                    VStack(spacing: LemonadeTheme.spaces.spacing300) {
                        // One flag per tile, matching the vertical "Selected" section above.
                        // Sharing a single flag (with one tile reading it inverted) made
                        // tapping either one flip both, so they could never match.
                        LemonadeUi.Tile(
                            label: "Filled",
                            icon: .circleCheck,
                            isSelected: isHorizontalFilledSelected,
                            onClick: { isHorizontalFilledSelected.toggle() },
                            variant: .filled,
                            orientation: .horizontal
                        )
                        LemonadeUi.Tile(
                            label: "Outlined",
                            icon: .circleCheck,
                            isSelected: isHorizontalOutlinedSelected,
                            onClick: { isHorizontalOutlinedSelected.toggle() },
                            variant: .outlined,
                            orientation: .horizontal
                        )
                    }
                }

                sectionView(title: "Horizontal / Support Text") {
                    VStack(spacing: LemonadeTheme.spaces.spacing300) {
                        LemonadeUi.Tile(
                            label: "Transfer",
                            icon: .arrowLeftRight,
                            supportText: "Send money instantly",
                            variant: .filled,
                            orientation: .horizontal
                        )
                        LemonadeUi.Tile(
                            label: "Pay",
                            icon: .card,
                            supportText: "Pay with card",
                            variant: .outlined,
                            orientation: .horizontal
                        )
                    }
                }

                sectionView(title: "Horizontal / Leading Slot") {
                    LemonadeUi.Tile(
                        label: "Custom slot",
                        variant: .filled,
                        orientation: .horizontal,
                        leadingSlot: {
                            LemonadeUi.Icon(
                                icon: .shoppingBag,
                                contentDescription: nil,
                                size: .medium
                            )
                        }
                    )
                }

                sectionView(title: "Horizontal / Disabled") {
                    VStack(spacing: LemonadeTheme.spaces.spacing300) {
                        LemonadeUi.Tile(
                            label: "Disabled",
                            icon: .padlock,
                            enabled: false,
                            variant: .filled,
                            orientation: .horizontal
                        )
                        LemonadeUi.Tile(
                            label: "Disabled",
                            icon: .padlock,
                            enabled: false,
                            variant: .outlined,
                            orientation: .horizontal
                        )
                    }
                }

                sectionView(title: "Use Case: Menu List") {
                    VStack(spacing: LemonadeTheme.spaces.spacing300) {
                        LemonadeUi.Tile(
                            label: "Transfer",
                            icon: .arrowLeftRight,
                            supportText: "Send money instantly",
                            onClick: { showTap("Transfer") },
                            variant: .filled,
                            orientation: .horizontal
                        )
                        LemonadeUi.Tile(
                            label: "Pay",
                            icon: .card,
                            supportText: "Pay with card",
                            onClick: { showTap("Pay") },
                            variant: .filled,
                            orientation: .horizontal
                        )
                        LemonadeUi.Tile(
                            label: "Top Up",
                            icon: .plus,
                            supportText: "Add funds to your account",
                            onClick: { showTap("Top Up") },
                            variant: .filled,
                            orientation: .horizontal
                        )
                        LemonadeUi.Tile(
                            label: "Statements",
                            icon: .chart,
                            supportText: "View your transactions",
                            onClick: { showTap("Statements") },
                            variant: .filled,
                            orientation: .horizontal
                        )
                    }
                }

                // MARK: - Use Cases
                sectionView(title: "Use Case: Quick Actions") {
                    LazyVGrid(columns: [
                        GridItem(.flexible()),
                        GridItem(.flexible()),
                        GridItem(.flexible())
                    ], spacing: LemonadeTheme.spaces.spacing400) {
                        LemonadeUi.Tile(
                            label: "Transfer",
                            icon: .arrowLeftRight,
                            onClick: { showTap("Transfer") },
                            variant: .filled
                        )

                        LemonadeUi.Tile(
                            label: "Pay",
                            icon: .card,
                            onClick: { showTap("Pay") },
                            variant: .filled
                        )

                        LemonadeUi.Tile(
                            label: "Request",
                            icon: .download,
                            onClick: { showTap("Request") },
                            variant: .filled
                        )

                        LemonadeUi.Tile(
                            label: "Scan",
                            icon: .qrCode,
                            onClick: { showTap("Scan") },
                            variant: .filled
                        )

                        LemonadeUi.Tile(
                            label: "Top Up",
                            icon: .plus,
                            onClick: { showTap("Top Up") },
                            variant: .filled
                        )

                        LemonadeUi.Tile(
                            label: "More",
                            icon: .ellipsisHorizontal,
                            onClick: { showTap("More") },
                            variant: .filled
                        )
                    }
                }

                sectionView(title: "Use Case: Dashboard") {
                    VStack(spacing: LemonadeTheme.spaces.spacing400) {
                        HStack(spacing: LemonadeTheme.spaces.spacing400) {
                            LemonadeUi.Tile(
                                label: "Orders",
                                icon: .shoppingBag,
                                onClick: { showTap("Orders") },
                                variant: .outlined
                            )

                            LemonadeUi.Tile(
                                label: "Inventory",
                                icon: .package,
                                onClick: { showTap("Inventory") },
                                variant: .outlined
                            )
                        }

                        HStack(spacing: LemonadeTheme.spaces.spacing400) {
                            LemonadeUi.Tile(
                                label: "Reports",
                                icon: .chart,
                                onClick: { showTap("Reports") },
                                variant: .outlined
                            )

                            LemonadeUi.Tile(
                                label: "Settings",
                                icon: .gear,
                                onClick: { showTap("Settings") },
                                variant: .outlined
                            )
                        }
                    }
                }
            }
            .padding(LemonadeTheme.spaces.spacing400)
        }
        .navigationTitle("Tile")
    }

    private func sectionView<Content: View>(title: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: LemonadeTheme.spaces.spacing300) {
            LemonadeUi.Text(
                title,
                textStyle: LemonadeTypography.shared.headingXSmall,
                color: .content.contentSecondary
            )

            content()
        }
    }

    // Action tiles have no selection state of their own, so a tap is acknowledged with a
    // toast. `print` was invisible on device, which is what made these read as broken.
    private func showTap(_ label: String) {
        toastManager.show(label: "\(label) tapped", voice: .neutral, duration: .short)
    }
}

#Preview {
    NavigationStack {
        TileDisplayView()
    }
    .lemonadeToastContainer()
}
