import SwiftUI
import Lemonade

struct ShadowsDisplayView: View {
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: .space.spacing1200) {
                ForEach(LemonadeShadow.allCases, id: \.self) { shadow in
                    VStack(alignment: .leading, spacing: .space.spacing100) {
                        Text(shadow.displayName)
                            .font(.caption)
                            .foregroundStyle(.content.contentSecondary)

                        RoundedRectangle(cornerRadius: .radius.radius600)
                            .fill(.bg.bgDefault)
                            .frame(height: 100)
                            .lemonadeShadow(shadow)
                    }
                }
            }
            // The vertical padding has to clear the largest shadow's offset and blur, or
            // the ScrollView clips the bottom shadow of the last swatch.
            .padding(.horizontal, .space.spacing400)
            .padding(.vertical, .space.spacing800)
        }
        .background(.bg.bgSubtle)
        .navigationTitle("Shadows")
    }
}

#Preview {
    NavigationStack {
        ShadowsDisplayView()
    }
}
