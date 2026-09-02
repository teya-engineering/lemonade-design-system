import SwiftUI
import Lemonade

struct PinCodeDisplayView: View {
    private let samplePin = "159999"

    @State private var numericPin = ""
    @State private var numericError = false
    @State private var alphaPin = ""
    @State private var noAutofillPin = ""

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                sectionView(title: "Numeric") {
                    VStack(spacing: 16) {
                        LemonadeUi.PinCode(
                            value: Binding(
                                get: { numericPin },
                                set: { numericPin = $0; numericError = false }
                            ),
                            error: numericError,
                            onComplete: { numericError = $0 != samplePin }
                        )
                        Text("Entered: \(numericPin.count) digit(s)")
                            .font(.caption)
                            .foregroundStyle(.content.contentSecondary)
                    }
                }

                sectionView(title: "Alphanumeric (system keyboard)") {
                    LemonadeUi.PinCode(value: $alphaPin, variant: .alphanumeric)
                }

                sectionView(title: "Autofill disabled") {
                    VStack(spacing: 16) {
                        LemonadeUi.PinCode(value: $noAutofillPin, oneTimeCodeAutofill: false)
                        Text("No OTC suggestion above the keyboard")
                            .font(.caption)
                            .foregroundStyle(.content.contentSecondary)
                    }
                }

                sectionView(title: "Submitting") {
                    LemonadeUi.PinCode(value: .constant(samplePin), submitting: true)
                }
            }
            .padding()
        }
        .navigationTitle("PinCode")
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
        PinCodeDisplayView()
    }
}
