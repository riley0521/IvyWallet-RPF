package com.ivy.core.domain.action.exchange

import com.ivy.core.persistence.dao.exchange.ExchangeRateOverrideDao
import com.ivy.core.persistence.entity.exchange.ExchangeRateOverrideEntity
import com.ivy.data.DELETING
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class ExchangeRateOverrideDaoFake : ExchangeRateOverrideDao {

    private val exchangeRates = MutableStateFlow<List<ExchangeRateOverrideEntity>>(emptyList())

    override suspend fun save(values: List<ExchangeRateOverrideEntity>) {
        exchangeRates.update { values }
    }

    override suspend fun findAllBlocking(): List<ExchangeRateOverrideEntity> {
        return exchangeRates.value.filter {
            it.sync.code != DELETING
        }
    }

    override fun findAllByBaseCurrency(baseCurrency: String): Flow<List<ExchangeRateOverrideEntity>> {
        return exchangeRates.map { entities ->
            entities.filter {
                it.baseCurrency.uppercase() == baseCurrency.uppercase()
            }
        }
    }

    override suspend fun deleteByBaseCurrencyAndCurrency(baseCurrency: String, currency: String) {
        exchangeRates.update { state ->
            state.filter {
                it.baseCurrency.uppercase() != baseCurrency.uppercase()
                        && it.currency.uppercase() != currency.uppercase()
            }
        }
    }
}