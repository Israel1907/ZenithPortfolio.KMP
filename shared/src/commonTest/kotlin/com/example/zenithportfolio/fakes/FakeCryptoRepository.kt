package com.example.zenithportfolio.fakes

import com.example.zenithportfolio.data.repository.CryptoResult
import com.example.zenithportfolio.domain.model.Crypto
import com.example.zenithportfolio.domain.repository.CryptoRepository

class FakeCryptoRepository : CryptoRepository {
    var getCryptosResult: Result<CryptoResult> = Result.success(CryptoResult(emptyList(), false))
    var getCryptoByIdResult: Result<Crypto> = Result.failure(Exception("not configured"))
    var getCryptosCallCount = 0

    override suspend fun getCryptos(): Result<CryptoResult> {
        getCryptosCallCount++
        return getCryptosResult
    }

    override suspend fun getCryptoById(id: String): Result<Crypto> {
        return getCryptoByIdResult
    }
}
