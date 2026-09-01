import Lemonade
import SwiftUI

private struct SampleAccount: Identifiable {
    let id: String
    let name: String
    let email: String
    let initials: String
}

private let sampleAccounts = [
    SampleAccount(id: "1", name: "Kathryn Murphy", email: "kathryn.murphy@mail.com", initials: "KM"),
    SampleAccount(id: "2", name: "Marvin McKinney", email: "marvin.mckinney@mail.com", initials: "MM"),
    SampleAccount(id: "3", name: "Jenny Wilson", email: "jenny.wilson@mail.com", initials: "JW")
]

struct SwipeActionRowDisplayView: View {
    @State private var openId: AnyHashable?
    @State private var removed: Set<String> = []

    var body: some View {
        ScrollView {
            VStack(spacing: .space.spacing600) {
                LemonadeUi.Card(
                    header: CardHeaderConfig(
                        title: "One open row at a time",
                        subtitle: "Drag a row left. Dragging across it fires the first action."
                    )
                ) {
                    let visible = sampleAccounts.filter { !removed.contains($0.id) }
                    ForEach(Array(visible.enumerated()), id: \.element.id) { index, account in
                        LemonadeUi.SwipeActionRow(
                            id: account.id,
                            openId: $openId,
                            actions: [
                                SwipeAction(
                                    icon: .trash,
                                    contentDescription: "Remove \(account.name)",
                                    onClick: { removed.insert(account.id) }
                                )
                            ],
                            showDivider: index != visible.count - 1
                        ) {
                            LemonadeUi.ActionListItem(
                                label: account.name,
                                supportText: account.email,
                                showNavigationIndicator: true,
                                // The container draws the divider: an item's own would travel with it.
                                showDivider: false,
                                onItemClicked: { },
                                leadingSlot: {
                                    LemonadeUi.SymbolContainer(
                                        text: account.initials,
                                        voice: .neutral,
                                        size: .medium,
                                        shape: .circle
                                    )
                                }
                            )
                        }
                    }
                }

                LemonadeUi.Card(
                    header: CardHeaderConfig(
                        title: "Two actions, allowsFullSwipe: false",
                        subtitle: "Travel past the reveal rubber-bands, and nothing commits."
                    )
                ) {
                    LemonadeUi.SwipeActionRow(
                        actions: [
                            SwipeAction(icon: .trash, contentDescription: "Delete", onClick: { }),
                            SwipeAction(
                                icon: .pencilLine,
                                contentDescription: "Edit",
                                onClick: { },
                                variant: .neutral
                            )
                        ],
                        allowsFullSwipe: false
                    ) {
                        LemonadeUi.ActionListItem(
                            label: "Two actions",
                            supportText: "Outermost action first",
                            // The SwipeActionRow draws the divider: one drawn here would travel with the row.
                            showDivider: false,
                            onItemClicked: { }
                        )
                    }
                }
            }
            .padding(.space.spacing400)
        }
        .navigationTitle("SwipeActionRow")
    }
}
