import SwiftUI
import Lemonade

struct HistoryTimelineDisplayView: View {
    var body: some View {
        ScrollView(.vertical) {
            LazyVStack(spacing: .space.spacing400) {
                positiveFirstSection
                howItWorksSection
                criticalCurrentSection
                neutralCurrentSection
            }
            .padding(.horizontal, .space.spacing400)
            .padding(.vertical, .space.spacing400)
        }
        .background(LemonadeTheme.colors.background.bgSubtle)
        .navigationTitle("HistoryTimeline")
        .navigationBarTitleDisplayMode(.inline)
    }

    private var positiveFirstSection: some View {
        LemonadeUi.Card(
            contentPadding: .medium,
            header: CardHeaderConfig(title: "Timeline — positive first")
        ) {
            LemonadeUi.HistoryTimeline(
                items: [
                    LemonadeHistoryTimelineItem(
                        label: "Payment sent",
                        subheading: "10:24",
                        description: "Your transfer has been initiated successfully.",
                        voice: .positive
                    ),
                    LemonadeHistoryTimelineItem(
                        label: "Processing",
                        subheading: "10:23",
                        description: "Bank is reviewing the transaction."
                    ),
                    LemonadeHistoryTimelineItem(
                        label: "Initiated",
                        subheading: "10:22"
                    )
                ],
                currentIndex: 0
            )
        }
    }

    private var howItWorksSection: some View {
        LemonadeUi.Card(
            contentPadding: .medium,
            header: CardHeaderConfig(title: "How it works — with content slot")
        ) {
            LemonadeUi.HistoryTimeline(
                items: [
                    LemonadeHistoryTimelineItem(
                        label: "Find a Visa PayPoint next to you",
                        description: "PayPoint locations collect cash deposits on our behalf.",
                        voice: .positive
                    ) {
                        LemonadeUi.Button(
                            label: "Find a PayPoint",
                            onClick: {},
                            leadingIcon: .mapPin,
                            variant: .neutral,
                            size: .medium
                        )
                    },
                    LemonadeHistoryTimelineItem(
                        label: "Enter the amount and generate a barcode",
                        description: "Show the barcode to the shopkeeper and deposit the funds."
                    ),
                    LemonadeHistoryTimelineItem(
                        label: "Your money will be available in 10 minutes",
                        description: "We'll notify you once the funds are in your account."
                    )
                ],
                currentIndex: 0
            )
        }
    }

    private var criticalCurrentSection: some View {
        LemonadeUi.Card(
            contentPadding: .medium,
            header: CardHeaderConfig(title: "Timeline — critical current")
        ) {
            LemonadeUi.HistoryTimeline(
                items: [
                    LemonadeHistoryTimelineItem(
                        label: "Payment failed",
                        subheading: "Just now",
                        description: "Your card was declined by the issuer.",
                        voice: .critical
                    ) {
                        LemonadeUi.Tag(label: "Declined", voice: .critical)
                    },
                    LemonadeHistoryTimelineItem(
                        label: "Processing",
                        subheading: "09:58"
                    ),
                    LemonadeHistoryTimelineItem(
                        label: "Initiated",
                        subheading: "09:57"
                    )
                ],
                currentIndex: 0
            )
        }
    }

    private var neutralCurrentSection: some View {
        LemonadeUi.Card(
            contentPadding: .medium,
            header: CardHeaderConfig(title: "Timeline — neutral current")
        ) {
            LemonadeUi.HistoryTimeline(
                items: [
                    LemonadeHistoryTimelineItem(
                        label: "Current item",
                        subheading: "Subheading",
                        description: "Timeline row rendered as the current step.",
                        voice: .neutral
                    ),
                    LemonadeHistoryTimelineItem(
                        label: "Past item",
                        subheading: "Subheading",
                        voice: .neutral
                    )
                ],
                currentIndex: 0
            )
        }
    }
}

#Preview {
    NavigationStack {
        HistoryTimelineDisplayView()
    }
}
