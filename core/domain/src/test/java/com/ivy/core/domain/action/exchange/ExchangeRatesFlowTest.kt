package com.ivy.core.domain.action.exchange

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import com.ivy.MainCoroutineExtension
import com.ivy.TestDispatchers
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
internal class ExchangeRatesFlowTest {

    private lateinit var exchangeRatesFlow: ExchangeRatesFlow

    private lateinit var baseCurrencyFlow: BaseCurrencyFlow
    private lateinit var exchangeRateDao: ExchangeRateDaoFake
    private lateinit var exchangeRateOverrideDao: ExchangeRateOverrideDaoFake

    companion object {
        @JvmField
        @RegisterExtension
        val mainCoroutineExtension = MainCoroutineExtension()
    }

    @BeforeEach
    fun setup() {
        baseCurrencyFlow = mockk()
        every { baseCurrencyFlow.invoke() } returns flowOf("", "EUR")

        exchangeRateDao = ExchangeRateDaoFake()
        exchangeRateOverrideDao = ExchangeRateOverrideDaoFake()

        exchangeRatesFlow = ExchangeRatesFlow(
            baseCurrencyFlow = baseCurrencyFlow,
            exchangeRateDao = exchangeRateDao,
            exchangeRateOverrideDao = exchangeRateOverrideDao,
            dispatcherProvider = TestDispatchers(
                testDispatcher = mainCoroutineExtension.testDispatcher
            )
        )
    }

    @Test
    fun `Test exchange rates flow emissions`() = runTest {
        val exchangeRates = listOf(
            exchangeRateEntity("USD", 1.2),
            exchangeRateEntity("CAD", 1.5),
            exchangeRateEntity("AUD", 1.9)
        )

        val exchangeRateOverrides = listOf(
            exchangeRateOverrideEntity("CAD", 2.2),
        )

        exchangeRatesFlow().test {
            awaitItem() // Ignore initial emission

            exchangeRateDao.save(exchangeRates)
            exchangeRateOverrideDao.save(exchangeRateOverrides)

            val rates1 = awaitItem()

            assertThat(rates1.rates).hasSize(3)
            assertThat(rates1.rates["USD"]).isEqualTo(1.2)
            assertThat(rates1.rates["CAD"]).isEqualTo(2.2) // Override rate
            assertThat(rates1.rates["AUD"]).isEqualTo(1.9)

            exchangeRateOverrideDao.save(emptyList())

            val rates2 = awaitItem()

            assertThat(rates2.rates["CAD"]).isEqualTo(1.5) // Original rate
        }
    }
}