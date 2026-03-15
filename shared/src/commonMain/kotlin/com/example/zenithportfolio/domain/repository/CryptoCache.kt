package com.example.zenithportfolio.domain.repository

import com.example.zenithportfolio.domain.model.Crypto

interface CryptoCache {
    suspend fun saveCryptos(cryptos: List<Crypto>)
    suspend fun getCachedCryptos(): List<Crypto>
}
