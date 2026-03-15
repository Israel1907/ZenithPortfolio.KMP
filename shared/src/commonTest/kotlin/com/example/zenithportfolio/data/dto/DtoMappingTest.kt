package com.example.zenithportfolio.data.dto

import com.example.zenithportfolio.fakes.TestFixtures
import kotlin.test.Test
import kotlin.test.assertEquals

class DtoMappingTest {

    @Test
    fun toDomainMapsAllFieldsCorrectly() {
        val dto = CryptoDto(
            id = "bitcoin",
            name = "Bitcoin",
            symbol = "btc",
            currentPrice = 50000.0,
            priceChangePercentage24h = 2.5,
            imageUrl = "https://img.png",
            marketCap = 1000000000L,
            marketCapRank = 1
        )

        val crypto = dto.toDomain()

        assertEquals("bitcoin", crypto.id)
        assertEquals("Bitcoin", crypto.name)
        assertEquals("btc", crypto.symbol)
        assertEquals(50000.0, crypto.price)
        assertEquals(2.5, crypto.changePercent24h)
        assertEquals("https://img.png", crypto.imageUrl)
        assertEquals(1000000000L, crypto.marketCap)
        assertEquals(1, crypto.rank)
    }

    @Test
    fun toDomainNullPriceChangeDefaultsToZero() {
        val dto = TestFixtures.cryptoDto(priceChangePercentage24h = null)

        val crypto = dto.toDomain()

        assertEquals(0.0, crypto.changePercent24h)
    }

    @Test
    fun toDomainNullMarketCapRankDefaultsToZero() {
        val dto = TestFixtures.cryptoDto(marketCapRank = null)

        val crypto = dto.toDomain()

        assertEquals(0, crypto.rank)
    }

    @Test
    fun toDomainBothNullableFieldsAreNull() {
        val dto = TestFixtures.cryptoDto(
            priceChangePercentage24h = null,
            marketCapRank = null
        )

        val crypto = dto.toDomain()

        assertEquals(0.0, crypto.changePercent24h)
        assertEquals(0, crypto.rank)
    }
}
