package com.example.zenithportfolio.data.dto

import com.example.zenithportfolio.domain.model.Crypto
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

/**
 * Handles CoinGecko's inconsistent `image` field:
 * - `/coins/markets` returns a plain string URL
 * - `/coins/{id}` returns an object: {"thumb":"...","small":"...","large":"..."}
 */
object ImageUrlSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ImageUrl", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        val jsonDecoder = decoder as JsonDecoder
        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> element.content
            is JsonObject -> {
                (element["large"] ?: element["small"] ?: element["thumb"])
                    ?.jsonPrimitive?.content ?: ""
            }
            else -> ""
        }
    }

    override fun serialize(encoder: Encoder, value: String) {
        encoder.encodeString(value)
    }
}

@Serializable
data class CryptoDto(
    val id: String,
    val name: String,
    val symbol: String,
    @SerialName("current_price")
    val currentPrice: Double = 0.0,
    @SerialName("price_change_percentage_24h")
    val priceChangePercentage24h: Double? = null,
    @Serializable(with = ImageUrlSerializer::class)
    @SerialName("image")
    val imageUrl: String = "",
    @SerialName("market_cap")
    val marketCap: Long = 0L,
    @SerialName("market_cap_rank")
    val marketCapRank: Int? = null
)

fun CryptoDto.toDomain() = Crypto(
    id = id,
    name = name,
    symbol = symbol,
    price = currentPrice,
    changePercent24h = priceChangePercentage24h ?: 0.0,
    imageUrl = imageUrl,
    marketCap = marketCap,
    rank = marketCapRank ?: 0
)