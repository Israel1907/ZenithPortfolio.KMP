package com.example.zenithportfolio.domain.repository

interface FavoriteRepository {
    suspend fun getAllFavorites(): Set<String>
    suspend fun addFavorite(cryptoId: String)
    suspend fun removeFavorite(cryptoId: String)
}
