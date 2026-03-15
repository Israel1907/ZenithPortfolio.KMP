import SwiftUI

struct ErrorStateView: View {
    let message: String
    let onRetry: () -> Void
    @Environment(\.appColors) var colors

    var body: some View {
        VStack(spacing: 16) {
            Text("\u{26A0}\u{FE0F}")
                .font(.system(size: 48))
            Text(message)
                .foregroundColor(colors.onSurfaceVariant)
                .font(.subheadline)
                .multilineTextAlignment(.center)
            Button("Reintentar") {
                onRetry()
            }
            .foregroundColor(AppColors.accent)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}
