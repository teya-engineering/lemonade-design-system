import SwiftUI
import Lemonade

struct ToastDisplayView: View {
    @EnvironmentObject private var toastManager: LemonadeToastManager
    @State private var textFieldValue: String = ""
    @State private var showOverSheet = false

    var body: some View {
        List {
            Section("Toast Voices") {
                Button("Show Success Toast") {
                    toastManager.show(
                        label: "Changes saved successfully",
                        voice: .success
                    )
                }

                Button("Show Error Toast") {
                    toastManager.show(
                        label: "Something went wrong",
                        voice: .error
                    )
                }

                Button("Show Neutral Toast") {
                    toastManager.show(
                        label: "Your session will expire soon",
                        voice: .neutral
                    )
                }

                Button("Show Long Content Toast") {
                    toastManager.show(
                        label: "Really long label that should wrap onto multiple lines to demonstrate text wrapping in the toast component",
                        voice: .neutral
                    )
                }
            }

            Section("Loading") {
                Button("Show Loading Toast") {
                    toastManager.show(
                        label: "Downloading your document…",
                        voice: .loading
                    )
                }

                Button("Complete Loading (replace)") {
                    toastManager.show(
                        label: "Download complete",
                        voice: .success
                    )
                }

                Button("Dismiss Loading") {
                    toastManager.dismiss()
                }
            }

            Section("With Action") {
                Button("Success Toast with Action") {
                    toastManager.show(
                        label: "Changes saved",
                        voice: .success,
                        actionLabel: "Undo",
                        onAction: { @MainActor in
                            toastManager.show(label: "Change undone", voice: .neutral)
                        }
                    )
                }

                Button("Error Toast with Action") {
                    toastManager.show(
                        label: "Something went wrong",
                        voice: .error,
                        actionLabel: "Retry",
                        onAction: { @MainActor in
                            toastManager.show(label: "Retrying…", voice: .neutral)
                        }
                    )
                }

                Button("Neutral Toast with Action") {
                    toastManager.show(
                        label: "Added to favorites",
                        voice: .neutral,
                        icon: .heart,
                        actionLabel: "View",
                        onAction: { @MainActor in
                            toastManager.show(label: "Opening favorites", voice: .neutral)
                        }
                    )
                }

                Button("Long Label with Action") {
                    toastManager.show(
                        label: "We couldn't sync your latest changes because the connection dropped partway through",
                        voice: .error,
                        actionLabel: "Try again",
                        onAction: { @MainActor in
                            toastManager.show(label: "Retrying…", voice: .neutral)
                        }
                    )
                }
            }

            Section("Custom Icon (Neutral Only)") {
                Button("Toast with Heart Icon") {
                    toastManager.show(
                        label: "Added to favorites",
                        voice: .neutral,
                        icon: .heart
                    )
                }

                Button("Toast with Bell Icon") {
                    toastManager.show(
                        label: "Notifications enabled",
                        voice: .neutral,
                        icon: .bell
                    )
                }

                Button("Toast with Star Icon") {
                    toastManager.show(
                        label: "Item starred",
                        voice: .neutral,
                        icon: .star
                    )
                }
            }

            Section("Durations") {
                Button("Short Duration (3s)") {
                    toastManager.show(
                        label: "This disappears quickly",
                        voice: .neutral,
                        duration: .short
                    )
                }

                Button("Medium Duration (6s)") {
                    toastManager.show(
                        label: "This stays a bit longer",
                        voice: .neutral,
                        duration: .medium
                    )
                }

                Button("Long Duration (9s)") {
                    toastManager.show(
                        label: "This stays for a while",
                        voice: .neutral,
                        duration: .long
                    )
                }
            }

            Section("Behavior") {
                Button("Non-Dismissible Toast") {
                    toastManager.show(
                        label: "You cannot swipe this away",
                        voice: .neutral,
                        dismissible: false
                    )
                }

                Button("Queue Multiple Toasts") {
                    toastManager.show(label: "First toast", voice: .success)
                    toastManager.show(label: "Second toast", voice: .neutral)
                    toastManager.show(label: "Third toast", voice: .error)
                }

                // Run both to compare: queued, all ten play back long after the tapping has
                // stopped; replaced, one pill counts up and settles on "Added item 10".
                Button("Rapid Burst (queue)") {
                    burst(policy: .queue)
                }

                Button("Rapid Burst (replace)") {
                    burst(policy: .replace)
                }
            }

            Section("Padding") {
                Button("Bottom (default)") {
                    toastManager.show(
                        label: "Default bottom position",
                        voice: .neutral
                    )
                }

                Button("Above Bottom Action Button") {
                    toastManager.show(
                        label: "Item added to cart",
                        voice: .success,
                        paddingValues: EdgeInsets(top: 0, leading: 0, bottom: 112, trailing: 0)
                    )
                }
            }

            Section("Over Bottom Sheet") {
                Button("Open bottom sheet") {
                    showOverSheet = true
                }
            }

            Section("Keyboard Handling") {
                VStack(alignment: .leading, spacing: 12) {
                    Text("Tap the text field to open keyboard, then show a toast:")
                        .font(.caption)
                        .foregroundStyle(.secondary)

                    TextField("Type something...", text: $textFieldValue)
                        .textFieldStyle(.roundedBorder)

                    Button("Show Toast Above Keyboard") {
                        toastManager.show(
                            label: "Toast appears above keyboard",
                            voice: .success
                        )
                    }
                    .buttonStyle(.borderedProminent)
                }
                .padding(.vertical, 8)
            }

            Section("Static Previews") {
                VStack(alignment: .leading, spacing: 16) {
                    LemonadeUi.Toast(label: "Success message", voice: .success)
                    LemonadeUi.Toast(label: "Error message", voice: .error)
                    LemonadeUi.Toast(label: "Neutral message", voice: .neutral)
                    LemonadeUi.Toast(label: "With custom icon", voice: .neutral, icon: .sparkles)
                    LemonadeUi.Toast(label: "Downloading your document…", voice: .loading)
                    LemonadeUi.Toast(label: "Success message", voice: .success, actionLabel: "Undo") {}
                    LemonadeUi.Toast(label: "Error message", voice: .error, actionLabel: "Retry") {}
                    LemonadeUi.Toast(label: "Neutral message", voice: .neutral, actionLabel: "View") {}
                    LemonadeUi.Toast(
                        label: "We couldn't sync your latest changes because the connection dropped partway through",
                        voice: .error,
                        actionLabel: "Try again"
                    ) {}
                }
                .padding(.vertical, 8)
            }
        }
        .navigationTitle("Toast")
        .sheet(isPresented: $showOverSheet) {
            // Give the sheet its own toast container so a toast fired from inside it renders on top of
            // the sheet, not behind it. A `.sheet` is a separate presentation layer above the root, so
            // the root's container can't reach over it — each presented layer that shows toasts needs
            // its own container. (This mirrors how the app's navigator wraps every presented screen.)
            OverBottomSheetContent()
                .lemonadeToastContainer()
                .presentationDetents([.medium])
        }
    }

    /// Ten toasts 150ms apart — spaced far enough to land as separate view updates, unlike the
    /// same-tick "Queue Multiple Toasts" button, and the shape a real burst takes when a till
    /// operator taps "add to cart" repeatedly.
    private func burst(policy: LemonadeToastPolicy) {
        Task { @MainActor in
            for item in 1...10 {
                toastManager.show(label: "Added item \(item)", voice: .success, policy: policy)
                try? await Task.sleep(nanoseconds: 150_000_000)
            }
        }
    }
}

private struct OverBottomSheetContent: View {
    @EnvironmentObject private var toastManager: LemonadeToastManager

    var body: some View {
        VStack(spacing: 16) {
            Text("This sheet has its own toast container, so the toast renders on top of it.")
                .multilineTextAlignment(.center)
                .foregroundStyle(.secondary)
            Button("Show toast on top") {
                toastManager.show(
                    label: "This toast is above the bottom sheet",
                    voice: .success
                )
            }
            .buttonStyle(.borderedProminent)
        }
        .padding()
    }
}

#Preview {
    NavigationStack {
        ToastDisplayView()
    }
    .lemonadeToastContainer()
}
