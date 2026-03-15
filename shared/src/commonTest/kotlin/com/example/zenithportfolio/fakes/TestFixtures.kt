package com.example.zenithportfolio.fakes

import com.example.zenithportfolio.data.dto.CryptoDto
import com.example.zenithportfolio.data.repository.CryptoResult
import com.example.zenithportfolio.domain.model.Crypto

object TestFixtures {

    fun crypto(
        id: String = "bitcoin",
        name: String = "Bitcoin",
        symbol: String = "BTC",
        price: Double = 50000.0,
        changePercent24h: Double = 2.5,
        imageUrl: String = "https://example.com/btc.png",
        marketCap: Long = 1_000_000_000L,
        rank: Int = 1
    ) = Crypto(id, name, symbol, price, changePercent24h, imageUrl, marketCap, rank)

    fun cryptoDto(
        id: String = "bitcoin",
        name: String = "Bitcoin",
        symbol: String = "btc",
        currentPrice: Double = 50000.0,
        priceChangePercentage24h: Double? = 2.5,
        imageUrl: String = "https://example.com/btc.png",
        marketCap: Long = 1_000_000_000L,
        marketCapRank: Int? = 1
    ) = CryptoDto(id, name, symbol, currentPrice, priceChangePercentage24h, imageUrl, marketCap, marketCapRank)

    fun cryptoResult(
        cryptos: List<Crypto> = listOf(crypto()),
        fromCache: Boolean = false
    ) = CryptoResult(cryptos, fromCache)

    val bitcoin = crypto()
    val ethereum = crypto(
        id = "ethereum",
        name = "Ethereum",
        symbol = "ETH",
        price = 3000.0,
        changePercent24h = 1.5,
        imageUrl = "https://example.com/eth.png",
        marketCap = 500_000_000L,
        rank = 2
    )

    val bitcoinDto = cryptoDto()
    val ethereumDto = cryptoDto(
        id = "ethereum",
        name = "Ethereum",
        symbol = "eth",
        currentPrice = 3000.0,
        priceChangePercentage24h = 1.5,
        imageUrl = "https://example.com/eth.png",
        marketCap = 500_000_000L,
        marketCapRank = 2
    )
}
