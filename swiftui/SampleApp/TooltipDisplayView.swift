import SwiftUI
import Lemonade

private let feesAnchor = "fees-info"
private let takingsAnchor = "takings"
private let reportsAnchor = "reports"
private let besideLeadingAnchor = "beside-leading"
private let besideTrailingAnchor = "beside-trailing"

struct TooltipDisplayView: View {
    @EnvironmentObject private var tooltips: LemonadeTooltipManager
    @EnvironmentObject private var toasts: LemonadeToastManager

    @State private var closeButtonDismissed = false
    @State private var footerDismissed = false
    @State private var step = 1

    private let totalSteps = 3

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                sectionView(title: "Anchored — on-demand help") {
                    HStack(spacing: 12) {
                        LemonadeUi.Text(
                            "Daily fees",
                            textStyle: LemonadeTypography.shared.bodyMediumRegular
                        )
                        LemonadeUi.IconButton(
                            icon: .circleInfo,
                            contentDescription: "About fees",
                            onClick: {
                                tooltips.show(
                                    anchor: feesAnchor,
                                    content: "Fees are deducted once a day, just after midnight.",
                                    title: "Daily fees"
                                )
                            },
                            type: .ghost,
                            size: .small
                        )
                        .lemonadeTooltipAnchor(feesAnchor)
                    }
                }

                sectionView(title: "Anchored — guided tour") {
                    VStack(alignment: .leading, spacing: 12) {
                        HStack(spacing: 12) {
                            LemonadeUi.Tag(label: "Takings")
                                .lemonadeTooltipAnchor(takingsAnchor)
                            LemonadeUi.Tag(label: "Reports")
                                .lemonadeTooltipAnchor(reportsAnchor)
                        }

                        LemonadeUi.Button(label: "Start tour") {
                            tooltips.startTour(
                                steps: [
                                    LemonadeTooltipStep(
                                        anchor: takingsAnchor,
                                        content: "Everything you sold today, updated as it happens.",
                                        title: "Daily takings"
                                    ),
                                    LemonadeTooltipStep(
                                        anchor: reportsAnchor,
                                        content: "Dig into the numbers over any period you like.",
                                        title: "Reports"
                                    ),
                                    LemonadeTooltipStep(
                                        anchor: feesAnchor,
                                        content: "Tap here whenever you want the fee breakdown.",
                                        title: "Fees"
                                    )
                                ],
                                onFinish: { toasts.show(label: "Tour finished", voice: .success) },
                                onSkip: { toasts.show(label: "Tour skipped") }
                            )
                        }
                    }
                }

                sectionView(title: "Anchored — beside the anchor") {
                    // A side placement needs a whole tooltip's width of room beside its anchor,
                    // which only an edge-aligned control has.
                    HStack {
                        LemonadeUi.IconButton(
                            icon: .circleInfo,
                            contentDescription: "Points left",
                            onClick: {
                                tooltips.show(
                                    anchor: besideLeadingAnchor,
                                    content: "The indicator is on the left edge, so the tooltip sits to the right of the icon.",
                                    title: "Left Center",
                                    indicatorPlacement: .leftCenter
                                )
                            },
                            type: .ghost,
                            size: .small
                        )
                        .lemonadeTooltipAnchor(besideLeadingAnchor)

                        Spacer()

                        LemonadeUi.IconButton(
                            icon: .circleInfo,
                            contentDescription: "Points right",
                            onClick: {
                                tooltips.show(
                                    anchor: besideTrailingAnchor,
                                    content: "The indicator is on the right edge, so the tooltip sits to the left of the icon.",
                                    title: "Right Center",
                                    indicatorPlacement: .rightCenter
                                )
                            },
                            type: .ghost,
                            size: .small
                        )
                        .lemonadeTooltipAnchor(besideTrailingAnchor)
                    }
                    .frame(maxWidth: .infinity)
                }

                sectionView(title: "Indicator Placements") {
                    VStack(spacing: 12) {
                        ForEach(LemonadeTooltipIndicatorPlacement.allCases, id: \.self) { placement in
                            LemonadeUi.Tooltip(
                                content: "Tap here to see everything you sold today.",
                                title: placement.displayName,
                                indicatorPlacement: placement
                            )
                        }
                    }
                }

                sectionView(title: "Content Only") {
                    LemonadeUi.Tooltip(
                        content: "A tooltip with no title, no cover and no footer.",
                        indicatorPlacement: .topCenter
                    )
                }

                sectionView(title: "With Close Button") {
                    if closeButtonDismissed {
                        LemonadeUi.Button(label: "Show again") {
                            closeButtonDismissed = false
                        }
                    } else {
                        LemonadeUi.Tooltip(
                            content: "Close buttons appear only when you pass onClose.",
                            title: "Dismissible",
                            indicatorPlacement: .topRight,
                            onClose: { closeButtonDismissed = true },
                            closeContentDescription: "Close"
                        )
                    }
                }

                sectionView(title: "With Cover") {
                    LemonadeUi.Tooltip(
                        content: "The cover slot takes any view — an illustration, a screenshot, a video.",
                        title: "Cover slot",
                        indicatorPlacement: .bottomCenter,
                        cover: { LemonadeTheme.colors.background.bgBrandSubtle }
                    )
                }

                sectionView(title: "With Footer") {
                    if footerDismissed {
                        LemonadeUi.Button(label: "Show again") {
                            footerDismissed = false
                            step = 1
                        }
                    } else {
                        LemonadeUi.Tooltip(
                            content: "The footer is a scoped slot — only the step counter and actions belong in it.",
                            title: "Step \(step)",
                            indicatorPlacement: .topLeft,
                            onClose: { footerDismissed = true },
                            closeContentDescription: "Close",
                            footer: { footer in
                                footer.stepCounter(currentStep: step, totalSteps: totalSteps)
                                Spacer()
                                footer.action("Skip", variant: .secondary) { footerDismissed = true }
                                footer.action("Next") { step = step < totalSteps ? step + 1 : 1 }
                            }
                        )
                    }
                }

                sectionView(title: "Footer Without Step Counter") {
                    LemonadeUi.Tooltip(
                        content: "A footer can hold actions on their own — the step counter is optional.",
                        title: "Actions only",
                        indicatorPlacement: .bottomRight,
                        footer: { footer in
                            Spacer()
                            footer.action("Got it") {}
                        }
                    )
                }
            }
            .padding()
        }
        // bgDefault rather than the system page background: the tooltip's surface is
        // bgDefaultInverse, so this is the background it is designed to sit against.
        .background(.bg.bgDefault)
        .navigationTitle("Tooltip")
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
        TooltipDisplayView()
    }
}
