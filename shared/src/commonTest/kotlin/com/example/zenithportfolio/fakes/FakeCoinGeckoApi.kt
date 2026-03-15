package com.example.zenithportfolio.fakes

import com.example.zenithportfolio.data.api.CoinGeckoApi
import com.example.zenithportfolio.data.dto.CryptoDto
import io.ktor.client.HttpClient

class FakeCoinGeckoApi(client: HttpClient) : CoinGeckoApi(client) {
    var marketsResponse: List<CryptoDto> = emptyList()
    var coinByIdResponse: CryptoDto? = null
    var shouldThrow: Exception? = null

    override suspend fun getMarkets(currency: String, limit: Int): List<CryptoDto> {
        shouldThrow?.let { throw it }
        return marketsResponse
    }

    override suspend fun getCoinById(id: String): CryptoDto {
        shouldThrow?.let { throw it }
        return coinByIdResponse ?: throw Exception("not configured")
    }
}
