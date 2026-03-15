package com.example.zenithportfolio.presentation.crypto

import com.example.zenithportfolio.data.repository.CryptoResult
import com.example.zenithportfolio.fakes.FakeCryptoRepository
import com.example.zenithportfolio.fakes.FakeFavoriteRepository
import com.example.zenithportfolio.fakes.TestFixtures
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CryptoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeRepo = FakeCryptoRepository()
    private val fakeFavRepo = FakeFavoriteRepository()

    private fun createViewModel() = CryptoViewModel(
        repository = fakeRepo,
        favoriteRepository = fakeFavRepo,
        dispatcher = testDispatcher
    )

    // --- 3.2: Init / Favorites tests ---

    @Test
    fun initLoadsFavoritesFromRepository() = runTest(testDispatcher) {
        fakeFavRepo.favorites.addAll(setOf("bitcoin", "ethereum"))

        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals(setOf("bitcoin", "ethereum"), vm.state.value.favorites)
    }

    @Test
    fun initWithEmptyFavorites() = runTest(testDispatcher) {
        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals(emptySet(), vm.state.value.favorites)
    }

    // --- 3.3: LoadCryptos success ---

    @Test
    fun loadCryptosSuccessPopulatesState() = runTest(testDispatcher) {
        val cryptos = listOf(TestFixtures.bitcoin, TestFixtures.ethereum)
        fakeRepo.getCryptosResult = Result.success(CryptoResult(cryptos, fromCache = false))

        val vm = createViewModel()
        advanceUntilIdle()

        vm.onIntent(CryptoIntent.LoadCryptos)
        advanceUntilIdle()

        assertFalse(vm.state.value.isLoading)
        assertEquals(cryptos, vm.state.value.cryptos)
        assertEquals(cryptos, vm.state.value.filteredCryptos)
        assertFalse(vm.state.value.fromCache)
        assertNull(vm.state.value.error)
    }

    // --- 3.4: LoadCryptos failure ---

    @Test
    fun loadCryptosFailureSetsError() = runTest(testDispatcher) {
        fakeRepo.getCryptosResult = Result.failure(RuntimeException("Network error"))

        val vm = createViewModel()
        advanceUntilIdle()

        vm.onIntent(CryptoIntent.LoadCryptos)
        advanceUntilIdle()

        assertFalse(vm.state.value.isLoading)
        assertEquals("Network error", vm.state.value.error)
        assertTrue(vm.state.value.cryptos.isEmpty())
    }

    // --- 3.5: LoadCryptos effect tests (4 scenarios) ---

    @Test
    fun loadCryptosFromCacheEmitsToast() = runTest(testDispatcher) {
        fakeRepo.getCryptosResult = Result.success(
            CryptoResult(listOf(TestFixtures.bitcoin), fromCache = true)
        )

        val vm = createViewModel()
        advanceUntilIdle()

        val effects = mutableListOf<CryptoEffect>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects.add(it) }
        }

        vm.onIntent(CryptoIntent.LoadCryptos)
        advanceUntilIdle()

        assertTrue(effects.any { it is CryptoEffect.ShowToast && it.message == "Datos del cache - CoinGecko limitado" })

        job.cancel()
    }

    @Test
    fun loadCryptosFromApiDoesNotEmitToast() = runTest(testDispatcher) {
        fakeRepo.getCryptosResult = Result.success(
            CryptoResult(listOf(TestFixtures.bitcoin), fromCache = false)
        )

        val vm = createViewModel()
        advanceUntilIdle()

        val effects = mutableListOf<CryptoEffect>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects.add(it) }
        }

        vm.onIntent(CryptoIntent.LoadCryptos)
        advanceUntilIdle()

        assertTrue(effects.none { it is CryptoEffect.ShowToast })

        job.cancel()
    }

    @Test
    fun loadCryptosFailureEmitsShowError() = runTest(testDispatcher) {
        fakeRepo.getCryptosResult = Result.failure(RuntimeException("Network error"))

        val vm = createViewModel()
        advanceUntilIdle()

        val effects = mutableListOf<CryptoEffect>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects.add(it) }
        }

        vm.onIntent(CryptoIntent.LoadCryptos)
        advanceUntilIdle()

        assertTrue(effects.any { it is CryptoEffect.ShowError && it.message == "Network error" })

        job.cancel()
    }

    @Test
    fun loadCryptosFailureWithNullMessageUsesDefault() = runTest(testDispatcher) {
        fakeRepo.getCryptosResult = Result.failure(RuntimeException())

        val vm = createViewModel()
        advanceUntilIdle()

        val effects = mutableListOf<CryptoEffect>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects.add(it) }
        }

        vm.onIntent(CryptoIntent.LoadCryptos)
        advanceUntilIdle()

        assertTrue(effects.any { it is CryptoEffect.ShowError && it.message == "Error desconocido" })

        job.cancel()
    }

    // --- 3.6: Refresh tests (3 scenarios) ---

    @Test
    fun refreshSuccessUpdatesState() = runTest(testDispatcher) {
        val cryptos = listOf(TestFixtures.bitcoin, TestFixtures.ethereum)
        fakeRepo.getCryptosResult = Result.success(CryptoResult(cryptos, fromCache = false))

        val vm = createViewModel()
        advanceUntilIdle()

        vm.onIntent(CryptoIntent.Refresh)
        advanceUntilIdle()

        assertFalse(vm.state.value.isRefreshing)
        assertEquals(cryptos, vm.state.value.cryptos)
        assertEquals(cryptos, vm.state.value.filteredCryptos)
    }

    @Test
    fun refreshSuccessPreservesActiveSearchFilter() = runTest(testDispatcher) {
        val cryptos = listOf(TestFixtures.bitcoin, TestFixtures.ethereum)
        fakeRepo.getCryptosResult = Result.success(CryptoResult(cryptos, fromCache = false))

        val vm = createViewModel()
        advanceUntilIdle()

        // Set up search query first
        vm.onIntent(CryptoIntent.SearchCrypto("bit"))

        // Now refresh
        vm.onIntent(CryptoIntent.Refresh)
        advanceUntilIdle()

        assertEquals("bit", vm.state.value.searchQuery)
        assertTrue(vm.state.value.filteredCryptos.all {
            it.name.contains("bit", ignoreCase = true) || it.symbol.contains("bit", ignoreCase = true)
        })
        assertEquals(1, vm.state.value.filteredCryptos.size)
    }

    @Test
    fun refreshFailureEmitsErrorEffect() = runTest(testDispatcher) {
        fakeRepo.getCryptosResult = Result.failure(RuntimeException("Timeout"))

        val vm = createViewModel()
        advanceUntilIdle()

        val effects = mutableListOf<CryptoEffect>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects.add(it) }
        }

        vm.onIntent(CryptoIntent.Refresh)
        advanceUntilIdle()

        assertFalse(vm.state.value.isRefreshing)
        assertTrue(effects.any { it is CryptoEffect.ShowError && it.message == "Timeout" })

        job.cancel()
    }

    // --- 3.7: SearchCrypto tests (5 scenarios) ---

    @Test
    fun searchFiltersByName() = runTest(testDispatcher) {
        val cryptos = listOf(TestFixtures.bitcoin, TestFixtures.ethereum)
        fakeRepo.getCryptosResult = Result.success(CryptoResult(cryptos, fromCache = false))

        val vm = createViewModel()
        advanceUntilIdle()

        vm.onIntent(CryptoIntent.LoadCryptos)
        advanceUntilIdle()

        vm.onIntent(CryptoIntent.SearchCrypto("Bitcoin"))

        assertEquals(1, vm.state.value.filteredCryptos.size)
        assertEquals("bitcoin", vm.state.value.filteredCryptos[0].id)
        assertEquals("Bitcoin", vm.state.value.searchQuery)
    }

    @Test
    fun searchFiltersBySymbol() = runTest(testDispatcher) {
        val cryptos = listOf(TestFixtures.bitcoin, TestFixtures.ethereum)
        fakeRepo.getCryptosResult = Result.success(CryptoResult(cryptos, fromCache = false))

        val vm = createViewModel()
        advanceUntilIdle()

        vm.onIntent(CryptoIntent.LoadCryptos)
        advanceUntilIdle()

        vm.onIntent(CryptoIntent.SearchCrypto("eth"))

        assertEquals(1, vm.state.value.filteredCryptos.size)
        assertEquals("ethereum", vm.state.value.filteredCryptos[0].id)
    }

    @Test
    fun searchIsCaseInsensitive() = runTest(testDispatcher) {
        val cryptos = listOf(TestFixtures.bitcoin)
        fakeRepo.getCryptosResult = Result.success(CryptoResult(cryptos, fromCache = false))

        val vm = createViewModel()
        advanceUntilIdle()

        vm.onIntent(CryptoIntent.LoadCryptos)
        advanceUntilIdle()

        vm.onIntent(CryptoIntent.SearchCrypto("bitcoin"))

        assertEquals(1, vm.state.value.filteredCryptos.size)
        assertEquals("bitcoin", vm.state.value.filteredCryptos[0].id)
    }

    @Test
    fun blankSearchQueryReturnsAllCryptos() = runTest(testDispatcher) {
        val cryptos = listOf(TestFixtures.bitcoin, TestFixtures.ethereum)
        fakeRepo.getCryptosResult = Result.success(CryptoResult(cryptos, fromCache = false))

        val vm = createViewModel()
        advanceUntilIdle()

        vm.onIntent(CryptoIntent.LoadCryptos)
        advanceUntilIdle()

        vm.onIntent(CryptoIntent.SearchCrypto("Bitcoin"))
        assertEquals(1, vm.state.value.filteredCryptos.size)

        vm.onIntent(CryptoIntent.SearchCrypto(""))
        assertEquals(cryptos, vm.state.value.filteredCryptos)
    }

    @Test
    fun searchWithNoMatchesReturnsEmptyList() = runTest(testDispatcher) {
        val cryptos = listOf(TestFixtures.bitcoin, TestFixtures.ethereum)
        fakeRepo.getCryptosResult = Result.success(CryptoResult(cryptos, fromCache = false))

        val vm = createViewModel()
        advanceUntilIdle()

        vm.onIntent(CryptoIntent.LoadCryptos)
        advanceUntilIdle()

        vm.onIntent(CryptoIntent.SearchCrypto("dogecoin"))
        assertTrue(vm.state.value.filteredCryptos.isEmpty())
    }

    // --- 3.8: ToggleFavorite tests (2 scenarios) ---

    @Test
    fun toggleFavoriteAddsNewFavorite() = runTest(testDispatcher) {
        val vm = createViewModel()
        advanceUntilIdle()

        val effects = mutableListOf<CryptoEffect>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects.add(it) }
        }

        vm.onIntent(CryptoIntent.ToggleFavorite("bitcoin"))
        advanceUntilIdle()

        assertTrue(vm.state.value.favorites.contains("bitcoin"))
        assertTrue(fakeFavRepo.favorites.contains("bitcoin"))
        assertTrue(effects.any { it is CryptoEffect.ShowToast && it.message == "Favorito actualizado" })

        job.cancel()
    }

    @Test
    fun toggleFavoriteRemovesExistingFavorite() = runTest(testDispatcher) {
        fakeFavRepo.favorites.add("bitcoin")

        val vm = createViewModel()
        advanceUntilIdle()

        val effects = mutableListOf<CryptoEffect>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects.add(it) }
        }

        vm.onIntent(CryptoIntent.ToggleFavorite("bitcoin"))
        advanceUntilIdle()

        assertFalse(vm.state.value.favorites.contains("bitcoin"))
        assertFalse(fakeFavRepo.favorites.contains("bitcoin"))
        assertTrue(effects.any { it is CryptoEffect.ShowToast && it.message == "Favorito actualizado" })

        job.cancel()
    }

    // --- 3.9: SelectCrypto test ---

    @Test
    fun selectCryptoEmitsNavigateToDetailEffect() = runTest(testDispatcher) {
        val vm = createViewModel()
        advanceUntilIdle()

        val effects = mutableListOf<CryptoEffect>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects.add(it) }
        }

        vm.onIntent(CryptoIntent.SelectCrypto("bitcoin"))
        advanceUntilIdle()

        assertEquals(1, effects.size)
        assertTrue(effects[0] is CryptoEffect.NavigateToDetail)
        assertEquals("bitcoin", (effects[0] as CryptoEffect.NavigateToDetail).cryptoId)

        job.cancel()
    }
}
