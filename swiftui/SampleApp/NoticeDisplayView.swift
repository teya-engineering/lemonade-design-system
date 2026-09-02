import SwiftUI
import Lemonade

struct NoticeDisplayView: View {
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                // All Voices
                sectionView(title: "Voices") {
                    VStack(spacing: 12) {
                        LemonadeUi.Notice(content: "This is an informational notice.", voice: .info, actionLabel: "Action", onActionClick: {})
                        LemonadeUi.Notice(content: "Operation completed successfully.", voice: .positive, actionLabel: "Action", onActionClick: {})
                        LemonadeUi.Notice(content: "Please review before proceeding.", voice: .warning, actionLabel: "Action", onActionClick: {})
                        LemonadeUi.Notice(content: "An error occurred. Please try again.", voice: .critical, actionLabel: "Action", onActionClick: {})
                        LemonadeUi.Notice(content: "No new updates available.", voice: .neutral, actionLabel: "Action", onActionClick: {})
                    }
                }

                // With Title
                sectionView(title: "With Title") {
                    VStack(spacing: 12) {
                        LemonadeUi.Notice(content: "Your account settings have been updated.", voice: .info, title: "Information")
                        LemonadeUi.Notice(content: "Payment of $42.00 was processed.", voice: .positive, title: "Success")
                        LemonadeUi.Notice(content: "Your subscription expires in 3 days.", voice: .warning, title: "Warning")
                        LemonadeUi.Notice(content: "Unable to connect to the server.", voice: .critical, title: "Error")
                        LemonadeUi.Notice(content: "You have no pending notifications.", voice: .neutral, title: "Note")
                    }
                }

                // With Action
                sectionView(title: "With Action") {
                    VStack(spacing: 12) {
                        LemonadeUi.Notice(
                            content: "A new version is available.",
                            voice: .info,
                            actionLabel: "Update now",
                            onActionClick: {}
                        )
                        LemonadeUi.Notice(
                            content: "Please update your billing information to avoid service interruption.",
                            voice: .warning,
                            title: "Action required",
                            actionLabel: "Update billing",
                            onActionClick: {}
                        )
                        LemonadeUi.Notice(
                            content: "We couldn't process your last payment.",
                            voice: .critical,
                            title: "Payment failed",
                            actionLabel: "Retry payment",
                            onActionClick: {}
                        )
                    }
                }

                // Without Icon
                sectionView(title: "Without Icon") {
                    VStack(spacing: 12) {
                        LemonadeUi.Notice(
                            content: "A simple notice without an icon.",
                            voice: .info,
                            showIcon: false
                        )
                        LemonadeUi.Notice(
                            content: "This notice has a title but no icon.",
                            voice: .positive,
                            title: "Custom content",
                            showIcon: false,
                            actionLabel: "Learn more",
                            onActionClick: {}
                        )
                    }
                }
            }
            .padding()
        }
        .navigationTitle("Notice")
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
        NoticeDisplayView()
    }
}
