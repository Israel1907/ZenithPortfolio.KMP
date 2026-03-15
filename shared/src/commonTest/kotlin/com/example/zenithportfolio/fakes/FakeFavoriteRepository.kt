package com.example.zenithportfolio.fakes

import com.example.zenithportfolio.domain.repository.FavoriteRepository

class FakeFavoriteRepository : FavoriteRepository {
    val favorites = mutableSetOf<String>()

    override suspend fun getAllFavorites(): Set<String> = favorites.toSet()

    override suspend fun addFavorite(cryptoId: String) {
        favorites.add(cryptoId)
    }

    override suspend fun removeFavorite(cryptoId: String) {
        favorites.remove(cryptoId)
    }
}
