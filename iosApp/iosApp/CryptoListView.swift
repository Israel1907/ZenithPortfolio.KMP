//
//  CryptoListView.swift
//  
//
//  Created by Joe Israel Armijos on 18/1/26.
//

import SwiftUI
import Shared

struct CryptoListView: View {
    @StateObject private var viewModel = CryptoViewModelWrapper()
    @State private var selectedCrypto: Crypto? = nil
    @Environment(\.appColors) var colors

    var body: some View {
        NavigationStack {
            ZStack {
                colors.background.ignoresSafeArea()

                if viewModel.isLoading && viewModel.allCryptos.isEmpty {
                    LoadingStateView(message: "Cargando criptomonedas...")
                } else if !viewModel.isLoading, let error = viewModel.error, viewModel.allCryptos.isEmpty {
                    ErrorStateView(message: error, onRetry: { viewModel.loadCryptos() })
                } else if !viewModel.isLoading && viewModel.allCryptos.isEmpty && viewModel.error == nil {
                    EmptyStateView(
                        icon: "\u{1F4ED}",
                        title: "Sin criptomonedas",
                        subtitle: "No hay datos disponibles en este momento",
                        actionLabel: "Reintentar",
                        onAction: { viewModel.loadCryptos() }
                    )
                } else {
                    VStack(spacing: 0) {
                        // Search bar
                        HStack {
                            Text("\u{1F50D}")
                            TextField("Buscar crypto...", text: $viewModel.searchQuery)
                                .foregroundColor(colors.onSurface)
                                .onChange(of: viewModel.searchQuery) { newValue in
                                    viewModel.search(query: newValue)
                                }
                        }
                        .padding()
                        .background(colors.surface)
                        .cornerRadius(12)
                        .padding()

                        if viewModel.cryptos.isEmpty && !viewModel.searchQuery.isEmpty {
                            EmptyStateView(
                                icon: "\u{1F50D}",
                                title: "Sin resultados",
                                subtitle: "No se encontraron criptomonedas para '\(viewModel.searchQuery)'",
                                actionLabel: "Limpiar b\u{00FA}squeda",
                                onAction: {
                                    viewModel.searchQuery = ""
                                    viewModel.search(query: "")
                                }
                            )
                        } else {
                            List(viewModel.cryptos, id: \.id) { crypto in
                                CryptoRow(
                                    crypto: crypto,
                                    isFavorite: viewModel.favorites.contains(crypto.id),
                                    onFavoriteToggle: {
                                        viewModel.toggleFavorite(cryptoId: crypto.id)
                                    },
                                    colors: colors
                                )
                                .listRowBackground(colors.background)
                                .listRowSeparator(.hidden)
                                .listRowInsets(EdgeInsets(top: 4, leading: 0, bottom: 4, trailing: 0))
                                .onTapGesture {
                                    selectedCrypto = crypto
                                }
                            }
                            .listStyle(.plain)
                            .refreshable {
                                viewModel.refresh()
                            }
                        }
                    }
                }
            }
            .navigationDestination(item: $selectedCrypto) { crypto in
                CryptoDetailView(crypto: crypto)
            }
            .overlay(alignment: .bottom) {
                if viewModel.showCacheWarning {
                    Text("Datos del cache - CoinGecko limitado")
                        .foregroundColor(.white)
                        .padding()
                        .background(Color.orange)
                        .cornerRadius(8)
                        .padding(.bottom, 50)
                        .onAppear {
                            DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                                viewModel.showCacheWarning = false
                            }
                        }
                }
            }
        }
    }
}

struct CryptoRow: View {
    let crypto: Crypto
    let isFavorite: Bool
    let onFavoriteToggle: () -> Void
    let colors: AppColors

    var body: some View {
        HStack {
            AsyncImage(url: URL(string: crypto.imageUrl)) { image in
                image.resizable()
            } placeholder: {
                Circle().fill(Color.gray)
            }
            .frame(width: 48, height: 48)
            .clipShape(Circle())

            VStack(alignment: .leading) {
                Text(crypto.name)
                    .foregroundColor(colors.onSurface)
                    .fontWeight(.bold)
                Text(crypto.symbol.uppercased())
                    .foregroundColor(colors.onSurfaceVariant)
                    .font(.caption)
            }

            Spacer()

            VStack(alignment: .trailing) {
                Text("$\(formatPrice(crypto.price))")
                    .foregroundColor(colors.onSurface)
                    .fontWeight(.bold)
                Text("\(crypto.changePercent24h >= 0 ? "+" : "")\(String(format: "%.2f", crypto.changePercent24h))%")
                    .foregroundColor(crypto.changePercent24h >= 0 ? AppColors.positive : AppColors.negative)
                    .font(.caption)
                Button(action: onFavoriteToggle) {
                    Text(isFavorite ? "⭐" : "☆")
                        .font(.title2)
                }
            }


        }
        .padding()
        .background(colors.surface)
        .cornerRadius(16)
        .padding(.horizontal, 6)
    }

    func formatPrice(_ price: Double) -> String {
        if price >= 1 {
            return String(format: "%.2f", price)
        } else {
            return String(format: "%.6f", price)
        }
    }
}

// Helper para colores hex
extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 6:
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(.sRGB, red: Double(r) / 255, green: Double(g) / 255, blue: Double(b) / 255, opacity: Double(a) / 255)
    }
}


