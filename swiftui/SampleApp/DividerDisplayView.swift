import SwiftUI
import Lemonade

struct DividerDisplayView: View {
    var body: some View {
        ScrollView(.vertical) {
            LazyVStack(alignment: .leading, spacing: .space.spacing600) {
                horizontalDividerSection
                horizontalDividerWithLabelSection
                verticalDividerSection
                inContextSection
            }
            .padding(.space.spacing400)
        }
        .background(.bg.bgSubtle)
        .navigationTitle("Divider")
    }

    private var horizontalDividerSection: some View {
        dividerSection(title: "Horizontal Divider") {
            VStack(alignment: .leading, spacing: .space.spacing400) {
                VStack(alignment: .leading, spacing: .space.spacing200) {
                    LemonadeUi.Text(
                        "Default",
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: .content.contentSecondary
                    )
                    LemonadeUi.HorizontalDivider()
                }

                VStack(alignment: .leading, spacing: .space.spacing200) {
                    LemonadeUi.Text(
                        "Dashed",
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: .content.contentSecondary
                    )
                    LemonadeUi.HorizontalDivider(variant: .dashed)
                }
            }
        }
    }

    private var horizontalDividerWithLabelSection: some View {
        dividerSection(title: "Horizontal Divider with Label") {
            VStack(alignment: .leading, spacing: .space.spacing400) {
                VStack(alignment: .leading, spacing: .space.spacing200) {
                    LemonadeUi.Text(
                        "Default with Label",
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: .content.contentSecondary
                    )
                    LemonadeUi.HorizontalDivider(label: "OR")
                }

                VStack(alignment: .leading, spacing: .space.spacing200) {
                    LemonadeUi.Text(
                        "Dashed with Label",
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: .content.contentSecondary
                    )
                    LemonadeUi.HorizontalDivider(label: "OR", variant: .dashed)
                }

                VStack(alignment: .leading, spacing: .space.spacing200) {
                    LemonadeUi.Text(
                        "Long Label",
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: .content.contentSecondary
                    )
                    LemonadeUi.HorizontalDivider(label: "Are you already at a PayPoint?")
                }

                VStack(alignment: .leading, spacing: .space.spacing200) {
                    LemonadeUi.Text(
                        "Label with preposition",
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: .content.contentSecondary
                    )
                    LemonadeUi.HorizontalDivider(label: "Or use a saved address")
                }

                VStack(alignment: .leading, spacing: .space.spacing200) {
                    LemonadeUi.Text(
                        "Narrow Container",
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: .content.contentSecondary
                    )
                    LemonadeUi.HorizontalDivider(label: "Are you already at a PayPoint?")
                        .frame(width: 200)
                }

                VStack(alignment: .leading, spacing: .space.spacing200) {
                    LemonadeUi.Text(
                        "Label with preposition — narrow container",
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: .content.contentSecondary
                    )
                    LemonadeUi.HorizontalDivider(label: "Or use a saved address")
                        .frame(width: 200)
                }
            }
        }
    }

    private var verticalDividerSection: some View {
        dividerSection(title: "Vertical Divider") {
            HStack(spacing: .space.spacing600) {
                VStack(spacing: .space.spacing200) {
                    LemonadeUi.Text(
                        "Default",
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: .content.contentSecondary
                    )
                    LemonadeUi.VerticalDivider()
                        .frame(height: 48)
                }

                VStack(spacing: .space.spacing200) {
                    LemonadeUi.Text(
                        "Dashed",
                        textStyle: LemonadeTypography.shared.bodySmallRegular,
                        color: .content.contentSecondary
                    )
                    LemonadeUi.VerticalDivider(variant: .dashed)
                        .frame(height: 48)
                }
            }
        }
    }

    private var inContextSection: some View {
        dividerSection(title: "In Context") {
            VStack(spacing: .space.spacing600) {
                contentSeparationExample
                loginSeparatorExample
                statsRowExample
            }
        }
    }

    private var contentSeparationExample: some View {
        VStack(alignment: .leading, spacing: .space.spacing300) {
            LemonadeUi.Text(
                "Section 1",
                textStyle: LemonadeTypography.shared.bodyMediumMedium
            )
            LemonadeUi.Text(
                "Some content for the first section",
                textStyle: LemonadeTypography.shared.bodySmallRegular,
                color: .content.contentSecondary
            )
            LemonadeUi.HorizontalDivider()
            LemonadeUi.Text(
                "Section 2",
                textStyle: LemonadeTypography.shared.bodyMediumMedium
            )
            LemonadeUi.Text(
                "Some content for the second section",
                textStyle: LemonadeTypography.shared.bodySmallRegular,
                color: .content.contentSecondary
            )
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.space.spacing400)
        .background(.bg.bgElevated)
        .clipShape(RoundedRectangle(cornerRadius: .radius.radius300))
    }

    private var loginSeparatorExample: some View {
        VStack(spacing: .space.spacing400) {
            LemonadeUi.Button(
                label: "Continue with Email",
                onClick: {}
            )
            .frame(maxWidth: .infinity)

            LemonadeUi.HorizontalDivider(label: "OR")

            LemonadeUi.Button(
                label: "Continue with Google",
                onClick: {}
            )
            .frame(maxWidth: .infinity)
        }
        .padding(.space.spacing400)
        .background(.bg.bgElevated)
        .clipShape(RoundedRectangle(cornerRadius: .radius.radius300))
    }

    private var statsRowExample: some View {
        HStack {
            Spacer()
            VStack {
                LemonadeUi.Text(
                    "125",
                    textStyle: LemonadeTypography.shared.headingSmall
                )
                LemonadeUi.Text(
                    "Posts",
                    textStyle: LemonadeTypography.shared.bodySmallRegular,
                    color: .content.contentSecondary
                )
            }
            Spacer()
            LemonadeUi.VerticalDivider()
                .frame(height: 40)
            Spacer()
            VStack {
                LemonadeUi.Text(
                    "1.2K",
                    textStyle: LemonadeTypography.shared.headingSmall
                )
                LemonadeUi.Text(
                    "Followers",
                    textStyle: LemonadeTypography.shared.bodySmallRegular,
                    color: .content.contentSecondary
                )
            }
            Spacer()
            LemonadeUi.VerticalDivider()
                .frame(height: 40)
            Spacer()
            VStack {
                LemonadeUi.Text(
                    "348",
                    textStyle: LemonadeTypography.shared.headingSmall
                )
                LemonadeUi.Text(
                    "Following",
                    textStyle: LemonadeTypography.shared.bodySmallRegular,
                    color: .content.contentSecondary
                )
            }
            Spacer()
        }
        .padding(.space.spacing400)
        .background(.bg.bgElevated)
        .clipShape(RoundedRectangle(cornerRadius: .radius.radius300))
    }

    private func dividerSection<Content: View>(
        title: String,
        @ViewBuilder content: () -> Content
    ) -> some View {
        VStack(alignment: .leading, spacing: .space.spacing300) {
            LemonadeUi.Text(
                title,
                textStyle: LemonadeTypography.shared.headingXSmall,
                color: .content.contentSecondary
            )
            content()
        }
    }
}

#Preview {
    NavigationStack {
        DividerDisplayView()
    }
}
