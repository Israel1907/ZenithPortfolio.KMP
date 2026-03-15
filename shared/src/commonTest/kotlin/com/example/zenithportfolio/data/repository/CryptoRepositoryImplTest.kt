package com.example.zenithportfolio.data.repository

import com.example.zenithportfolio.fakes.FakeCoinGeckoApi
import com.example.zenithportfolio.fakes.FakeCryptoCache
import com.example.zenithportfolio.fakes.TestFixtures
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CryptoRepositoryImplTest {

    private val dummyClient = HttpClient(MockEngine { respond("", HttpStatusCode.OK) })
    private val fakeApi = FakeCoinGeckoApi(dummyClient)
    private val fakeCache = FakeCryptoCache()

    private fun createRepository() = CryptoRepositoryImpl(
        api = fakeApi,
        cache = fakeCache
    )

    // --- 4.2: getCryptos tests (3 scenarios) ---

    @Test
    fun getCryptosApiSuccessReturnsFreshDataAndCachesIt() = runTest {
        fakeApi.marketsResponse = listOf(TestFixtures.bitcoinDto, TestFixtures.ethereumDto)

        val repo = createRepository()
        val result = repo.getCryptos()

        assertTrue(result.isSuccess)
        val cryptoResult = result.getOrThrow()
        assertFalse(cryptoResult.fromCache)
        assertEquals(2, cryptoResult.cryptos.size)
        assertEquals(2, fakeCache.stored.size)
    }

    @Test
    fun getCryptosApiFailureFallsBackToCache() = runTest {
        fakeApi.shouldThrow = RuntimeException("Rate limited")
        fakeCache.saveCryptos(listOf(TestFixtures.bitcoin))

        val repo = createRepository()
        val result = repo.getCryptos()

        assertTrue(result.isSuccess)
        val cryptoResult = result.getOrThrow()
        assertTrue(cryptoResult.fromCache)
        assertEquals(1, cryptoResult.cryptos.size)
    }

    @Test
    fun getCryptosApiFailureWithEmptyCacheReturnsFailure() = runTest {
        fakeApi.shouldThrow = RuntimeException("Rate limited")

        val repo = createRepository()
        val result = repo.getCryptos()

        assertTrue(result.isFailure)
        assertEquals("Rate limited", result.exceptionOrNull()?.message)
    }

    // --- 4.3: getCryptoById tests (2 scenarios) ---

    @Test
    fun getCryptoByIdSuccessReturnsDomainModel() = runTest {
        fakeApi.coinByIdResponse = TestFixtures.bitcoinDto

        val repo = createRepository()
        val result = repo.getCryptoById("bitcoin")

        assertTrue(result.isSuccess)
        val crypto = result.getOrThrow()
        assertEquals("bitcoin", crypto.id)
        assertEquals("Bitcoin", crypto.name)
    }

    @Test
    fun getCryptoByIdFailureReturnsFailure() = runTest {
        fakeApi.shouldThrow = RuntimeException("Not found")

        val repo = createRepository()
        val result = repo.getCryptoById("nonexistent")

        assertTrue(result.isFailure)
        assertEquals("Not found", result.exceptionOrNull()?.message)
    }
}
