package com.ivy.exchangeRates

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import com.ivy.MainCoroutineExtension
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.persistence.algorithm.calc.Rate
import com.ivy.exchangeRates.data.RateUi
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineExtension::class)
internal class RatesStateFlowTest {

    private lateinit var ratesStateFlow: RatesStateFlow

    private lateinit var baseCurrencyFlow: BaseCurrencyFlow
    private lateinit var ratesDao: RatesDaoFake

    @BeforeEach
    fun setup() {

        baseCurrencyFlow = mockk()
        every { baseCurrencyFlow.invoke() } returns flowOf("", "EUR")

        ratesDao = RatesDaoFake()

        ratesStateFlow = RatesStateFlow(baseCurrencyFlow, ratesDao)
    }

    @Test
    fun `Test rates state flow emissions`() = runTest {

        ratesStateFlow().test {
            val em1 = awaitItem()

            assertThat(em1).isEqualTo(
                RatesState(
                    baseCurrency = "",
                    manual = emptyList(),
                    automatic = emptyList()
                )
            )

            val em2 = awaitItem()

            assertThat(em2.baseCurrency).isEqualTo("EUR")
            assertThat(em2.manual).hasSize(1)
            assertThat(em2.automatic).hasSize(2)

            val automaticRatesMap = em2.automatic.associate { it.to to it.rate }
            assertThat(automaticRatesMap["USD"]).isEqualTo(1.2)
            assertThat(automaticRatesMap["AUD"]).isEqualTo(1.9)

            val manualRatesMap = em2.manual.associate { it.to to it.rate }
            assertThat(manualRatesMap["CAD"]).isEqualTo(2.0)

            ratesDao.ratesToReturn.value += Rate(62.0, "PHP")

            val em3 = awaitItem()
            val addedRate = RateUi("EUR", "PHP", 62.0)
            assertThat(em3.automatic).contains(addedRate)
            assertThat(em3.manual).doesNotContain(addedRate)
        }
    }
}