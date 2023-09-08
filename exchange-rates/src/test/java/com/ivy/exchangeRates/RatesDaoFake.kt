package com.ivy.exchangeRates

import com.ivy.core.persistence.algorithm.calc.Rate
import com.ivy.core.persistence.algorithm.calc.RatesDao
import com.ivy.data.CurrencyCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class RatesDaoFake : RatesDao {

    var ratesToReturn = MutableStateFlow(
        listOf(
            Rate(1.2, "USD"),
            Rate(1.6, "CAD"),
            Rate(1.9, "AUD")
        )
    )

    var rateOverridesToReturn = listOf(
        Rate(2.0, "CAD")
    )

    override fun findAll(baseCurrency: CurrencyCode): Flow<List<Rate>> {
        return ratesToReturn
    }

    override fun findAllOverrides(baseCurrency: CurrencyCode): Flow<List<Rate>> {
        return flowOf(rateOverridesToReturn)
    }
}