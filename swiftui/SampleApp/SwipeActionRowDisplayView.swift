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
    @State private var fired = 0
    @State private var pinned = false
    @State private var read = false
    @State private var bothEdgesRead = false
    @State private var deleted = 0
    /// The caller decides which row is open, including before anyone has touched one.
    @State private var startsOpenId: AnyHashable? = "unread"
    /// The row a swipe has asked to remove, held until the reader confirms it.
    @State private var pendingRemoval: SampleAccount?

    var body: some View {
        ScrollView {
            // Everything on the screen is one group: opening a row closes the last one, and a tap
            // anywhere closes whichever is open.
            LemonadeUi.SwipeActionGroup {
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
                                trailingActions: [
                                    LemonadeSwipeAction(
                                        icon: .trash,
                                        contentDescription: "Remove \(account.name)",
                                        onClick: { pendingRemoval = account },
                                        // The row is what the confirmation is about, so it stays
                                        // open behind it.
                                        keepsRowOpen: true
                                    )
                                ],
                                showDivider: index != visible.count - 1
                            ) {
                                LemonadeUi.ActionListItem(
                                    label: account.name,
                                    supportText: account.email,
                                    showNavigationIndicator: true,
                                    // Content inside a SwipeActionRow never draws its own divider:
                                    // it would travel with the row as the row opens.
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
                            title: "Opened by the caller",
                            subtitle: "This row starts open, and closes on a tap away like any other."
                        )
                    ) {
                        LemonadeUi.SwipeActionRow(
                            id: "unread",
                            openId: $startsOpenId,
                            trailingActions: [
                                LemonadeSwipeAction(
                                    icon: .envelope,
                                    contentDescription: "Mark unread",
                                    onClick: { },
                                    variant: .neutral
                                )
                            ],
                            allowsFullSwipe: false
                        ) {
                            LemonadeUi.ActionListItem(
                                label: "Already open",
                                supportText: startsOpenId == nil ? "Closed" : "Open",
                                showDivider: false,
                                onItemClicked: { }
                            )
                        }
                    }

                    LemonadeUi.Card(
                        header: CardHeaderConfig(
                            title: "Two actions, allowsFullSwipe: false",
                            subtitle: "Travel past the reveal is capped, and nothing commits."
                        )
                    ) {
                        LemonadeUi.SwipeActionRow(
                            trailingActions: [
                                LemonadeSwipeAction(icon: .trash, contentDescription: "Delete", onClick: { }),
                                LemonadeSwipeAction(
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
                                showDivider: false,
                                onItemClicked: { }
                            )
                        }
                    }

                    LemonadeUi.Card(
                        header: CardHeaderConfig(
                            title: "Two actions, allowsFullSwipe: true",
                            subtitle: "Dragging across the row fires the first action, which takes over the second."
                        )
                    ) {
                        LemonadeUi.SwipeActionRow(
                            trailingActions: [
                                LemonadeSwipeAction(
                                    icon: .trash,
                                    contentDescription: "Delete",
                                    onClick: { fired += 1 }
                                ),
                                LemonadeSwipeAction(
                                    icon: .pencilLine,
                                    contentDescription: "Edit",
                                    onClick: { },
                                    variant: .neutral
                                )
                            ]
                        ) {
                            LemonadeUi.ActionListItem(
                                label: "Two actions",
                                // Counted rather than removed, so the swipe can be tried again.
                                supportText: fired == 0 ? "Drag across to fire Delete" : "Delete fired \(fired)×",
                                showDivider: false,
                                onItemClicked: { }
                            )
                        }
                    }
                    LemonadeUi.Card(
                        header: CardHeaderConfig(
                            title: "Leading actions",
                            subtitle: "Drag right. Nothing sits on the trailing edge, so dragging left does nothing."
                        )
                    ) {
                        LemonadeUi.SwipeActionRow(
                            leadingActions: [
                                LemonadeSwipeAction(
                                    icon: .check,
                                    contentDescription: "Mark as read",
                                    onClick: { read.toggle() },
                                    variant: .primary
                                )
                            ]
                        ) {
                            LemonadeUi.ActionListItem(
                                label: "Leading only",
                                supportText: read ? "Read" : "Unread",
                                showDivider: false,
                                onItemClicked: { }
                            )
                        }
                    }

                    LemonadeUi.Card(
                        header: CardHeaderConfig(
                            title: "Both edges",
                            subtitle: "Each edge has its own actions. One drag reveals one edge, and can fire it."
                        )
                    ) {
                        LemonadeUi.SwipeActionRow(
                            leadingActions: [
                                LemonadeSwipeAction(
                                    icon: .check,
                                    contentDescription: "Mark as read",
                                    onClick: { bothEdgesRead.toggle() },
                                    variant: .primary
                                ),
                                LemonadeSwipeAction(
                                    icon: .star,
                                    contentDescription: "Star",
                                    onClick: { },
                                    variant: .neutral
                                )
                            ],
                            trailingActions: [
                                LemonadeSwipeAction(
                                    icon: .trash,
                                    contentDescription: "Delete",
                                    onClick: { deleted += 1 }
                                )
                            ]
                        ) {
                            LemonadeUi.ActionListItem(
                                label: "Both edges",
                                // Counted rather than removed, so either swipe can be tried again.
                                supportText: deleted > 0
                                    ? "Delete fired \(deleted)×"
                                    : (bothEdgesRead ? "Read" : "Unread"),
                                showDivider: false,
                                onItemClicked: { }
                            )
                        }
                    }

                    LemonadeUi.Card(
                        header: CardHeaderConfig(
                            title: "Any icon, any variant",
                            subtitle: "An action is an icon, a description and a closure. Nothing here removes the row."
                        )
                    ) {
                        LemonadeUi.SwipeActionRow(
                            trailingActions: [
                                LemonadeSwipeAction(
                                    icon: .pin,
                                    contentDescription: "Pin",
                                    onClick: { pinned.toggle() },
                                    variant: .primary
                                ),
                                LemonadeSwipeAction(
                                    icon: .envelope,
                                    contentDescription: "Mark unread",
                                    onClick: { },
                                    variant: .neutral
                                )
                            ],
                            allowsFullSwipe: false
                        ) {
                            LemonadeUi.ActionListItem(
                                label: "Pin or mark unread",
                                supportText: pinned ? "Pinned" : "Not pinned",
                                showDivider: false,
                                onItemClicked: { }
                            )
                        }
                    }

                    LemonadeUi.Card(
                        header: CardHeaderConfig(
                            title: "Three actions",
                            subtitle: "Each arrives as the row clears it, outermost first."
                        )
                    ) {
                        LemonadeUi.SwipeActionRow(
                            trailingActions: [
                                LemonadeSwipeAction(icon: .trash, contentDescription: "Delete", onClick: { }),
                                LemonadeSwipeAction(
                                    icon: .pin,
                                    contentDescription: "Pin",
                                    onClick: { },
                                    variant: .primary
                                ),
                                LemonadeSwipeAction(
                                    icon: .envelope,
                                    contentDescription: "Mark unread",
                                    onClick: { },
                                    variant: .neutral
                                )
                            ],
                            allowsFullSwipe: false
                        ) {
                            LemonadeUi.ActionListItem(
                                label: "Three actions",
                                supportText: "The row opens far enough for all of them",
                                showDivider: false,
                                onItemClicked: { }
                            )
                        }
                    }

                    LemonadeUi.Card(
                        header: CardHeaderConfig(
                            title: "Wrapping any row",
                            subtitle: "The content is whatever you pass, not only an ActionListItem."
                        )
                    ) {
                        LemonadeUi.SwipeActionRow(
                            trailingActions: [
                                LemonadeSwipeAction(icon: .trash, contentDescription: "Delete", onClick: { })
                            ],
                            allowsFullSwipe: false
                        ) {
                            LemonadeUi.ContentListItem(label: "Balance", value: "£1,204.00")
                        }
                    }

                    LemonadeUi.Card(
                        header: CardHeaderConfig(
                            title: "enabled: false",
                            subtitle: "The drag is left to whatever is scrolling underneath."
                        )
                    ) {
                        LemonadeUi.SwipeActionRow(
                            trailingActions: [
                                LemonadeSwipeAction(icon: .trash, contentDescription: "Delete", onClick: { })
                            ],
                            enabled: false
                        ) {
                            LemonadeUi.ActionListItem(
                                label: "Disabled",
                                supportText: "This row does not open",
                                showDivider: false,
                                onItemClicked: { }
                            )
                        }
                    }
                }
                .padding(.space.spacing400)
            }
        }
        .background(.bg.bgSubtle)
        .navigationTitle("SwipeActionRow")
        // A destructive action fired by a gesture is the one most easily fired by accident,
        // so the swipe asks for confirmation instead of removing the row itself.
        .confirmationDialog(
            "This will be deleted and you will not be able to recover it.",
            isPresented: Binding(
                get: { pendingRemoval != nil },
                // The row cannot see the confirmation dismissed, so closing it is the caller's job.
                set: { presented in
                    if !presented {
                        pendingRemoval = nil
                        openId = nil
                    }
                }
            ),
            titleVisibility: .visible
        ) {
            Button("Delete", role: .destructive) {
                if let account = pendingRemoval { removed.insert(account.id) }
                pendingRemoval = nil
            }
            // Plain rather than `.cancel`: iOS leaves a cancel-role button out of this
            // presentation, where tapping away is the way out.
            Button("Cancel") { pendingRemoval = nil }
        }
    }
}
