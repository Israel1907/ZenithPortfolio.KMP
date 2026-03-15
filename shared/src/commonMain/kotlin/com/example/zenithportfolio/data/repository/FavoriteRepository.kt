package com.example.zenithportfolio.data.repository

import com.example.zenithportfolio.db.FavoriteQueries
import com.example.zenithportfolio.domain.repository.FavoriteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class FavoriteRepositoryImpl(
    private val queries: FavoriteQueries,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : FavoriteRepository {

    override suspend fun getAllFavorites(): Set<String> = withContext(dispatcher) {
        queries.selectAll().executeAsList().toSet()
    }

    override suspend fun addFavorite(cryptoId: String) = withContext(dispatcher) {
        queries.insert(cryptoId)
    }

    override suspend fun removeFavorite(cryptoId: String) = withContext(dispatcher) {
        queries.delete(cryptoId)
    }
}
