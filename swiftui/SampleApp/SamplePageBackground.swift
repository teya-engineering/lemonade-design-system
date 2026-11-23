import SwiftUI
import Lemonade

extension View {
    /// Paints the sample app's page background behind scroll views and lists alike.
    func samplePageBackground() -> some View {
        scrollContentBackground(.hidden)
            .background(.bg.bgSubtle)
    }
}
