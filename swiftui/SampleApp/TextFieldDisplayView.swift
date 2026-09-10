import SwiftUI
import Lemonade

struct TextFieldDisplayView: View {
    @State private var basicText = ""
    @State private var labeledText = ""
    @State private var errorText = "Invalid input"
    @State private var supportText = ""
    @State private var leadingText = ""
    @State private var trailingText = ""
    @State private var selectorText = ""
    @State private var selectedPrefix = "+1"
    @State private var passwordText = ""
    @State private var isPasswordVisible = false
    @State private var emailText = ""
    @State private var pinText = ""
    @State private var amountValue = LemonadeTextFieldValue(text: "")
    @State private var urlText = ""
    @State private var usernameText = ""
    @State private var autofillPasswordText = ""
    @State private var isAutofillPasswordVisible = false

    private let prefixOptions = ["+1", "+44", "+351", "+353"]

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                basicSection
                withLabelSection
                withErrorSection
                withSupportTextSection
                withLeadingIconSection
                withTrailingIconSection
                secureSection
                emailKeyboardSection
                numberPadKeyboardSection
                urlKeyboardSection
                decimalKeyboardSection
                withSelectorSection
                autofillUsernameSection
                autofillPasswordSection
                disabledSection
            }
            .padding()
        }
        .navigationTitle("TextField")
    }

    private var basicSection: some View {
        sectionView(title: "Basic") {
            LemonadeUi.TextField(
                input: $basicText,
                placeholderText: "Enter text..."
            )
        }
    }

    private var withLabelSection: some View {
        sectionView(title: "With Label") {
            LemonadeUi.TextField(
                input: $labeledText,
                label: "Email Address",
                placeholderText: "you@example.com"
            )
        }
    }

    private var withErrorSection: some View {
        sectionView(title: "With Error") {
            LemonadeUi.TextField(
                input: $errorText,
                label: "Username",
                placeholderText: "Enter username",
                errorMessage: "Username is already taken",
                error: true
            )
        }
    }

    private var withSupportTextSection: some View {
        sectionView(title: "With Support Text") {
            LemonadeUi.TextField(
                input: $supportText,
                label: "Password",
                supportText: "Must be at least 8 characters",
                placeholderText: "Enter password"
            )
        }
    }

    private var withLeadingIconSection: some View {
        sectionView(title: "With Leading Icon") {
            LemonadeUi.TextField(
                input: $leadingText,
                label: "Search",
                placeholderText: "Search..."
            ) {
                LemonadeUi.Icon(
                    icon: .search,
                    contentDescription: nil,
                    size: .medium,
                    tint: .content.contentSecondary
                )
            } trailingContent: {
                EmptyView()
            }
        }
    }

    private var withTrailingIconSection: some View {
        sectionView(title: "With Trailing Icon") {
            LemonadeUi.TextField(
                input: $trailingText,
                label: "Amount",
                placeholderText: "0.00"
            ) {
                EmptyView()
            } trailingContent: {
                LemonadeUi.Icon(
                    icon: .circleInfo,
                    contentDescription: nil,
                    size: .medium,
                    tint: .content.contentSecondary
                )
            }
        }
    }

    private var secureSection: some View {
        sectionView(title: "Secure (Password)") {
            LemonadeUi.TextField(
                input: $passwordText,
                label: "Password",
                placeholderText: "Enter password"
            ) {
                LemonadeUi.Icon(
                    icon: .padlock,
                    contentDescription: nil,
                    size: .medium,
                    tint: .content.contentSecondary
                )
            } trailingContent: {
                passwordVisibilityToggle($isPasswordVisible)
            }
            .secureTextEntry(!isPasswordVisible)
        }
    }

    private var emailKeyboardSection: some View {
        sectionView(title: "Keyboard Type — Email") {
            LemonadeUi.TextField(
                input: $emailText,
                label: "Email Address",
                supportText: "Shows the email keyboard with @ and .",
                placeholderText: "you@example.com"
            )
            .lemonadeKeyboardType(.emailAddress)
        }
    }

    private var numberPadKeyboardSection: some View {
        sectionView(title: "Keyboard Type — Number Pad") {
            LemonadeUi.TextField(
                input: $pinText,
                label: "PIN",
                supportText: "Digits-only number pad",
                placeholderText: "0000"
            )
            .lemonadeKeyboardType(.numberPad)
        }
    }

    private var urlKeyboardSection: some View {
        sectionView(title: "Keyboard Type — URL") {
            LemonadeUi.TextField(
                input: $urlText,
                label: "Website",
                placeholderText: "https://example.com"
            )
            .lemonadeKeyboardType(.URL)
        }
    }

    private var decimalKeyboardSection: some View {
        sectionView(title: "Keyboard Type — Decimal (parameter)") {
            LemonadeUi.TextField(
                value: $amountValue,
                label: "Amount",
                supportText: "Set via the keyboardType: parameter",
                placeholderText: "0.00",
                keyboardType: .decimalPad
            )
        }
    }

    private var withSelectorSection: some View {
        sectionView(title: "TextField With Selector") {
            LemonadeUi.TextFieldWithSelector(
                input: $selectorText,
                leadingAction: selectNextPrefix,
                leadingContent: {
                    HStack(spacing: LemonadeTheme.spaces.spacing100) {
                        LemonadeUi.Text(
                            selectedPrefix,
                            textStyle: LemonadeTypography.shared.bodyMediumMedium
                        )
                        LemonadeUi.Icon(
                            icon: .chevronDown,
                            contentDescription: nil,
                            size: .small
                        )
                    }
                },
                label: "Phone Number",
                placeholderText: "Enter phone number"
            )
        }
    }

    private var autofillUsernameSection: some View {
        sectionView(title: "AutoFill — Username") {
            LemonadeUi.TextField(
                input: $usernameText,
                label: "Username",
                supportText: "AutoFill, no capitalization, no autocorrect",
                placeholderText: "you@example.com"
            )
            .lemonadeTextContentType(.username)
            .lemonadeKeyboardType(.emailAddress)
            .lemonadeTextInputAutocapitalization(.none)
            .lemonadeAutocorrectionDisabled()
        }
    }

    private var autofillPasswordSection: some View {
        sectionView(title: "AutoFill — Password") {
            LemonadeUi.TextField(
                input: $autofillPasswordText,
                label: "Password",
                placeholderText: "Enter password"
            ) {
                EmptyView()
            } trailingContent: {
                passwordVisibilityToggle($isAutofillPasswordVisible)
            }
            .lemonadeTextContentType(.password)
            .secureTextEntry(!isAutofillPasswordVisible)
        }
    }

    private var disabledSection: some View {
        sectionView(title: "Disabled") {
            LemonadeUi.TextField(
                input: .constant("Disabled content"),
                label: "Disabled Field",
                placeholderText: "Cannot edit",
                enabled: false
            )
        }
    }

    private func selectNextPrefix() {
        let next = (prefixOptions.firstIndex(of: selectedPrefix) ?? 0) + 1
        selectedPrefix = prefixOptions[next % prefixOptions.count]
    }

    private func sectionView<Content: View>(title: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(title)
                .font(.headline)
                .foregroundStyle(.content.contentSecondary)

            content()
        }
    }

    private func passwordVisibilityToggle(_ isVisible: Binding<Bool>) -> some View {
        Button {
            isVisible.wrappedValue.toggle()
        } label: {
            LemonadeUi.Icon(
                icon: isVisible.wrappedValue ? .eyeOpen : .eyeClosed,
                contentDescription: isVisible.wrappedValue ? "Hide password" : "Show password",
                size: .medium,
                tint: .content.contentSecondary
            )
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    NavigationStack {
        TextFieldDisplayView()
    }
}
