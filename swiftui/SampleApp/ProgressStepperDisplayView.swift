import SwiftUI
import Lemonade

private struct ProgressStepperSample {
    let title: String
    let items: [LemonadeProgressStepperItem]
    let completedSteps: Int
    var isFailed = false

    static let orderSteps = [("Ordered", "12 Jun"), ("Dispatched", "14 Jun"), ("Delivered", "18 Jun")]

    static let all: [ProgressStepperSample] =
        (0...3).map { completedSteps in
            ProgressStepperSample(
                title: "With titles — \(completedSteps)/3",
                items: orderSteps.enumerated().map { index, step in
                    LemonadeProgressStepperItem(label: step.0, subheading: index < completedSteps ? step.1 : nil)
                },
                completedSteps: completedSteps
            )
        }
        + (0...3).map { completedSteps in
            ProgressStepperSample(
                title: "Bars only — \(completedSteps)/3",
                items: Array(repeating: LemonadeProgressStepperItem(), count: 3),
                completedSteps: completedSteps
            )
        }
        + [
            ProgressStepperSample(
                title: "Failed — with titles",
                items: [
                    LemonadeProgressStepperItem(label: "Ordered", subheading: "12 Jun"),
                    LemonadeProgressStepperItem(label: "Delivery failed", subheading: "14 Jun"),
                    LemonadeProgressStepperItem(label: "Delivered")
                ],
                completedSteps: 1,
                isFailed: true
            ),
            ProgressStepperSample(
                title: "Failed — bars only",
                items: Array(repeating: LemonadeProgressStepperItem(), count: 3),
                completedSteps: 2,
                isFailed: true
            ),
            ProgressStepperSample(
                title: "Four steps, wrapping titles",
                items: [
                    LemonadeProgressStepperItem(label: "Application sent"),
                    LemonadeProgressStepperItem(label: "Identity checked"),
                    LemonadeProgressStepperItem(label: "Under review"),
                    LemonadeProgressStepperItem(label: "Account ready")
                ],
                completedSteps: 2
            )
        ]
}

struct ProgressStepperDisplayView: View {
    var body: some View {
        ScrollView(.vertical) {
            LazyVStack(spacing: .space.spacing400) {
                ForEach(ProgressStepperSample.all, id: \.title) { sample in
                    LemonadeUi.Card(
                        contentPadding: .medium,
                        header: CardHeaderConfig(title: sample.title)
                    ) {
                        LemonadeUi.ProgressStepper(
                            items: sample.items,
                            completedSteps: sample.completedSteps,
                            isFailed: sample.isFailed
                        )
                    }
                }
            }
            .padding(.space.spacing400)
        }
        .background(LemonadeTheme.colors.background.bgSubtle)
        .navigationTitle("ProgressStepper")
        .navigationBarTitleDisplayMode(.inline)
    }
}
