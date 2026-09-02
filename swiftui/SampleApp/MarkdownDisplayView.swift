import SwiftUI
import Lemonade

struct MarkdownDisplayView: View {
    @State private var input = "Hello **semi-bold** and ***bold*** with __underline__ and ___strikethrough___ or ~~italic~~ plus {critical}critical{/critical} and {positive}positive{/positive}"

    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                sectionView(title: "Markdown Input") {
                    TextField("Type markdown here...", text: $input, axis: .vertical)
                        .textFieldStyle(.roundedBorder)
                        .lineLimit(3...6)
                }

                sectionView(title: "Preview") {
                    LemonadeUi.Text(
                        input.toLemonadeMarkdown(),
                        textStyle: LemonadeTypography.shared.bodyMediumRegular
                    )
                }

                sectionView(title: "Syntax Reference") {
                    VStack(alignment: .leading, spacing: 8) {
                        LemonadeUi.Text(
                            "**text** = Semi-Bold",
                            textStyle: LemonadeTypography.shared.bodySmallRegular,
                            color: LemonadeTheme.colors.content.contentTertiary
                        )
                        LemonadeUi.Text(
                            "***text*** = Bold",
                            textStyle: LemonadeTypography.shared.bodySmallRegular,
                            color: LemonadeTheme.colors.content.contentTertiary
                        )
                        LemonadeUi.Text(
                            "__text__ = Underline",
                            textStyle: LemonadeTypography.shared.bodySmallRegular,
                            color: LemonadeTheme.colors.content.contentTertiary
                        )
                        LemonadeUi.Text(
                            "___text___ = Strikethrough",
                            textStyle: LemonadeTypography.shared.bodySmallRegular,
                            color: LemonadeTheme.colors.content.contentTertiary
                        )
                        LemonadeUi.Text(
                            "~~text~~ = Italic",
                            textStyle: LemonadeTypography.shared.bodySmallRegular,
                            color: LemonadeTheme.colors.content.contentTertiary
                        )
                        LemonadeUi.Text(
                            "{color}text{/color} = Color (e.g. critical, positive, info, caution, brand)",
                            textStyle: LemonadeTypography.shared.bodySmallRegular,
                            color: LemonadeTheme.colors.content.contentTertiary
                        )
                    }
                }
            }
            .padding()
        }
        .navigationTitle("Markdown")
    }

    private func sectionView<Content: View>(
        title: String,
        @ViewBuilder content: () -> Content
    ) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            SwiftUI.Text(title)
                .font(.headline)
                .foregroundStyle(LemonadeTheme.colors.content.contentSecondary)
            content()
        }
    }
}

#Preview {
    NavigationStack {
        MarkdownDisplayView()
    }
}
