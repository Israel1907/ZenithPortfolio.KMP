package com.example.zenithportfolio.fakes

import com.example.zenithportfolio.domain.model.Crypto
import com.example.zenithportfolio.domain.repository.CryptoCache

class FakeCryptoCache : CryptoCache {
    val stored = mutableListOf<Crypto>()

    override suspend fun saveCryptos(cryptos: List<Crypto>) {
        stored.clear()
        stored.addAll(cryptos)
    }

    override suspend fun getCachedCryptos(): List<Crypto> = stored.toList()
}
