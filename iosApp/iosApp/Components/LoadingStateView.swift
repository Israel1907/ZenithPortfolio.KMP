import SwiftUI

struct LoadingStateView: View {
    let message: String
    @Environment(\.appColors) var colors

    var body: some View {
        VStack {
            ProgressView()
                .tint(AppColors.accent)
            Text(message)
                .foregroundColor(colors.onSurfaceVariant)
                .font(.subheadline)
                .padding(.top, 16)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}
