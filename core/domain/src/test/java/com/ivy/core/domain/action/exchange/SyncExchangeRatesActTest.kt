package com.ivy.core.domain.action.exchange

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SyncExchangeRatesActTest {

    private lateinit var syncExchangeRatesAct: SyncExchangeRatesAct
    private lateinit var exchangeProviderFake: RemoteExchangeProviderFake
    private lateinit var exchangeRateDaoFake: ExchangeRateDaoFake

    @BeforeEach
    fun setup() {
        exchangeProviderFake = RemoteExchangeProviderFake()
        exchangeRateDaoFake = ExchangeRateDaoFake()
        syncExchangeRatesAct = SyncExchangeRatesAct(
            exchangeProvider = exchangeProviderFake,
            exchangeRateDao = exchangeRateDaoFake
        )
    }

    @Test
    fun `Test sync exchange rate, negative values ignored`() = runBlocking {
        syncExchangeRatesAct("USD")

        val usdRates = exchangeRateDaoFake
            .findAllByBaseCurrency("USD")
            .first { it.isNotEmpty() }

        val cadRate = usdRates.firstOrNull { it.currency == "CAD" }

        assertThat(cadRate).isNull()
    }

    @Test
    fun `Test sync exchange rates, valid values are saved`() = runBlocking<Unit> {
        syncExchangeRatesAct("USD")

        val usdRates = exchangeRateDaoFake
            .findAllByBaseCurrency("USD")
            .first { it.isNotEmpty() }

        val eurRate = usdRates.firstOrNull { it.currency == "EUR" }
        val audRate = usdRates.firstOrNull { it.currency == "AUD" }

        assertThat(eurRate).isNotNull()
        assertThat(audRate).isNotNull()
    }
}