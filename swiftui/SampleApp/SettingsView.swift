import SwiftUI
import Lemonade

struct SettingsView: View {
    @EnvironmentObject private var styleHandler: LemonadeStyleHandler
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: .space.spacing400) {
                    LemonadeUi.Card(
                        contentPadding: .none,
                        header: CardHeaderConfig(title: "Appearance", headingStyle: .overline)
                    ) {
                        let styles = LemonadeStyle.allCases
                        ForEach(Array(styles.enumerated()), id: \.element) { index, style in
                            LemonadeUi.SelectListItem(
                                label: style.label,
                                type: .single,
                                checked: styleHandler.currentStyle == style,
                                onItemClicked: { styleHandler.currentStyle = style },
                                showDivider: index < styles.count - 1
                            )
                        }
                    }
                }
                .padding(.space.spacing400)
            }
            .background(.bg.bgSubtle)
            .navigationTitle("Settings")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    LemonadeUi.ToolbarIconButton(
                        icon: .times,
                        contentDescription: "Close",
                        action: { dismiss() }
                    )
                }
            }
        }
        // The sheet may be hosted in its own window; make sure it picks up the
        // selected appearance as soon as it appears. Subsequent changes are
        // handled by the handler's `didSet`.
        .onAppear { styleHandler.applyToWindows() }
    }
}

#Preview {
    SettingsView()
        .environmentObject(LemonadeStyleHandler())
}
