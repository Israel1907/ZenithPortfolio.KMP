import SwiftUI

struct EmptyStateView: View {
    let icon: String
    let title: String
    var subtitle: String? = nil
    var actionLabel: String? = nil
    var onAction: (() -> Void)? = nil
    @Environment(\.appColors) var colors

    var body: some View {
        VStack(spacing: 12) {
            Text(icon)
                .font(.system(size: 48))
            Text(title)
                .fontWeight(.bold)
                .foregroundColor(colors.onSurface)
            if let subtitle = subtitle {
                Text(subtitle)
                    .foregroundColor(colors.onSurfaceVariant)
                    .font(.subheadline)
                    .multilineTextAlignment(.center)
            }
            if let actionLabel = actionLabel, let onAction = onAction {
                Button(actionLabel) {
                    onAction()
                }
                .foregroundColor(AppColors.accent)
                .padding(.top, 8)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}
