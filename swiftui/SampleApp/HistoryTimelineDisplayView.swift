import SwiftUI
import Lemonade

struct HistoryTimelineDisplayView: View {
    @State private var variant: LemonadeHistoryTimelineVariant = .steps

    var body: some View {
        ScrollView(.vertical) {
            LazyVStack(spacing: .space.spacing400) {
                variantPicker
                ForEach(StepsSample.all, id: \.title) { sample in
                    stepsCard(sample)
                }
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

    private var variantPicker: some View {
        LemonadeUi.SegmentedControl(
            properties: [.label("Events"), .label("Steps")],
            selectedTab: variant == .events ? 0 : 1,
            onTabSelected: { variant = $0 == 0 ? .events : .steps }
        )
    }

    private func stepsCard(_ sample: StepsSample) -> some View {
        LemonadeUi.Card(
            contentPadding: .medium,
            header: CardHeaderConfig(title: sample.title)
        ) {
            LemonadeUi.HistoryTimeline(
                items: sample.items,
                currentIndex: sample.currentIndex,
                variant: variant
            )
        }
    }
}

#Preview {
    NavigationStack {
        HistoryTimelineDisplayView()
    }
}

private struct StepsSample {
    let title: String
    let currentIndex: Int?
    let items: [LemonadeHistoryTimelineItem]

    static let all: [StepsSample] = [
        StepsSample(
            title: "Steps — first step",
            currentIndex: 0,
            items: steps(completed: 0, currentSubheading: "An activation email was sent to sofia@teya.com")
        ),
        StepsSample(
            title: "Steps — second step",
            currentIndex: 1,
            items: steps(completed: 1, currentSubheading: "Sofia needs to download the Teya app and log in.")
        ),
        StepsSample(
            title: "Steps — last step",
            currentIndex: 2,
            items: steps(completed: 2, currentSubheading: "The card was delivered. Sofia can now activate it.")
        ),
        StepsSample(title: "Steps — all steps completed", currentIndex: 3, items: steps(completed: 3)),
        StepsSample(
            title: "Steps — critical current step",
            currentIndex: 1,
            items: steps(
                completed: 1,
                currentSubheading: "We couldn't verify Sofia's phone number.",
                currentVoice: .critical
            )
        ),
        StepsSample(
            title: "Steps — current step with an action",
            currentIndex: 1,
            items: [
                LemonadeHistoryTimelineItem(label: "Invitation accepted", voice: .positive),
                LemonadeHistoryTimelineItem(
                    label: "Logs into the Teya App",
                    description: "Remind Sofia to log in so the card can be activated."
                ) {
                    LemonadeUi.Button(label: "Resend invite", onClick: {}, variant: .secondary)
                },
                LemonadeHistoryTimelineItem(label: "Card activation")
            ]
        ),
        StepsSample(title: "Steps — nothing reached", currentIndex: nil, items: steps(completed: 0))
    ]

    private static func steps(
        completed: Int,
        currentSubheading: String? = nil,
        currentVoice: LemonadeHistoryItemVoice = .neutral
    ) -> [LemonadeHistoryTimelineItem] {
        ["Invitation accepted", "Logs into the Teya App", "Card activation"].enumerated().map { index, label in
            if index < completed {
                return LemonadeHistoryTimelineItem(label: label, voice: .positive)
            } else if index == completed {
                return LemonadeHistoryTimelineItem(label: label, subheading: currentSubheading, voice: currentVoice)
            } else {
                return LemonadeHistoryTimelineItem(label: label)
            }
        }
    }
}
