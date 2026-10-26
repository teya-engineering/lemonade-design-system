import SwiftUI
import Lemonade

struct TextAreaDisplayView: View {
    private static let maxLength = 1000

    @State private var basicText = ""
    @State private var counterText = ""
    @State private var errorText = "Not enough detail"

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                sectionView(title: "Basic") {
                    LemonadeUi.TextArea(
                        input: $basicText,
                        placeholderText: "Enter text..."
                    )
                }

                sectionView(title: "With Label and Counter") {
                    LemonadeUi.TextArea(
                        input: $counterText,
                        label: "What happened",
                        optionalIndicator: "Optional",
                        supportText: "Anything you add helps us look into it.",
                        placeholderText: "For example: I didn't make this payment and I've never heard of Acme Suppliers Ltd.",
                        errorMessage: "Shorten this to \(Self.maxLength) characters",
                        maxLength: Self.maxLength,
                        error: counterText.utf16.count > Self.maxLength
                    )
                }

                sectionView(title: "With Error") {
                    LemonadeUi.TextArea(
                        input: $errorText,
                        label: "What happened",
                        errorMessage: "Tell us a bit more",
                        maxLength: Self.maxLength,
                        error: true
                    )
                }

                sectionView(title: "Disabled") {
                    LemonadeUi.TextArea(
                        input: .constant("This field is disabled"),
                        label: "What happened",
                        supportText: "Support text",
                        maxLength: Self.maxLength,
                        enabled: false
                    )
                }
            }
            .padding()
        }
        .navigationTitle("TextArea")
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
        TextAreaDisplayView()
    }
}
