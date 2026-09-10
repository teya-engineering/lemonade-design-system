import SwiftUI
import Lemonade

struct TagDisplayView: View {
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 32) {
                voicesSection
                withIconsSection
                useCasesSection
                inContextSection
            }
            .padding()
        }
        .navigationTitle("Tag")
        .background(.bg.bgSubtle)
    }

    private var voicesSection: some View {
        sectionView(title: "Voices") {
            VStack(alignment: .leading, spacing: 12) {
                HStack(spacing: 8) {
                    LemonadeUi.Tag(label: "Neutral", voice: .neutral)
                    LemonadeUi.Tag(label: "Critical", voice: .critical)
                    LemonadeUi.Tag(label: "Warning", voice: .warning)
                }

                HStack(spacing: 8) {
                    LemonadeUi.Tag(label: "Info", voice: .info)
                    LemonadeUi.Tag(label: "Positive", voice: .positive)
                    LemonadeUi.Tag(label: "Featured", voice: .featured)
                }

                LemonadeUi.Tag(label: "Neutral On Color", voice: .neutralOnColor)
                    .padding(.all, 8)
                    .background(LemonadeTheme.colors.background.bgAlwaysDark)
                    .clipShape(RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius150))
            }
        }
    }

    private var withIconsSection: some View {
        sectionView(title: "With Icons") {
            VStack(alignment: .leading, spacing: 12) {
                LemonadeUi.Tag(label: "Neutral", icon: .heart, voice: .neutral)
                LemonadeUi.Tag(label: "Error", icon: .circleX, voice: .critical)
                LemonadeUi.Tag(label: "Warning", icon: .triangleAlert, voice: .warning)
                LemonadeUi.Tag(label: "Info", icon: .circleInfo, voice: .info)
                LemonadeUi.Tag(label: "Success", icon: .circleCheck, voice: .positive)
                LemonadeUi.Tag(label: "Featured", icon: .sparkles, voice: .featured)
                LemonadeUi.Tag(label: "Neutral On Color", icon: .heart, voice: .neutralOnColor)
                    .padding(.all, 8)
                    .background(LemonadeTheme.colors.background.bgAlwaysDark)
                    .clipShape(RoundedRectangle(cornerRadius: LemonadeTheme.radius.radius150))
            }
        }
    }

    private var useCasesSection: some View {
        sectionView(title: "Use Cases") {
            VStack(alignment: .leading, spacing: 16) {
                statusTagRows
                categoryTags
            }
        }
    }

    private var statusTagRows: some View {
        Group {
            HStack(spacing: 8) {
                Text("Order Status:")
                LemonadeUi.Tag(label: "Shipped", icon: .check, voice: .positive)
            }

            HStack(spacing: 8) {
                Text("Payment:")
                LemonadeUi.Tag(label: "Pending", voice: .warning)
            }

            HStack(spacing: 8) {
                Text("Account:")
                LemonadeUi.Tag(label: "Verified", icon: .circleCheck, voice: .info)
            }
        }
    }

    private var categoryTags: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Categories:")
                .font(.subheadline)

            HStack(spacing: 8) {
                LemonadeUi.Tag(label: "Electronics", voice: .neutral)
                LemonadeUi.Tag(label: "Sale", voice: .critical)
                LemonadeUi.Tag(label: "New", voice: .positive)
            }
        }
    }

    private var inContextSection: some View {
        sectionView(title: "In Context") {
            VStack(spacing: 16) {
                productCardExample
            }
        }
    }

    private var productCardExample: some View {
        HStack(alignment: .top, spacing: 12) {
            RoundedRectangle(cornerRadius: .radius.radius400)
                .fill(.bg.bgNeutralSubtle)
                .frame(width: 60, height: 60)

            VStack(alignment: .leading, spacing: 4) {
                HStack {
                    Text("Product Name")
                        .font(.headline)
                    LemonadeUi.Tag(label: "New", voice: .positive)
                }

                Text("$99.99")
                    .foregroundStyle(.content.contentSecondary)

                HStack(spacing: 4) {
                    LemonadeUi.Tag(label: "In Stock", voice: .info)
                    LemonadeUi.Tag(label: "Free Shipping", voice: .neutral)
                }
            }

            Spacer()
        }
        .padding()
        .background(.bg.bgDefault)
        .clipShape(.rect(cornerRadius: .radius.radius600))
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
        TagDisplayView()
    }
}
