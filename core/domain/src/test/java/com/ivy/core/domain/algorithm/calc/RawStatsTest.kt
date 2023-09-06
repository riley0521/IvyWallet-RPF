package com.ivy.core.domain.algorithm.calc

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.transaction.TransactionType
import org.junit.jupiter.api.Test
import java.time.Instant

internal class RawStatsTest {

    @Test
    fun `Test creating raw stats from transactions`() {
        val fiveSecondsAgo = Instant.now().minusSeconds(5)
        val tenSecondsAgo = Instant.now().minusSeconds(10)
        val fifteenSecondsAgo = Instant.now().minusSeconds(15)

        val stats = rawStats(
            listOf(
                CalcTrn(
                    amount = 5.0,
                    currency = "EUR",
                    type = TransactionType.Income,
                    time = fiveSecondsAgo
                ),
                CalcTrn(
                    amount = 2.0,
                    currency = "USD",
                    type = TransactionType.Expense,
                    time = tenSecondsAgo
                ),
                CalcTrn(
                    amount = 2.0,
                    currency = "USD",
                    type = TransactionType.Expense,
                    time = fifteenSecondsAgo
                )
            )
        )

        assertThat(stats.incomesCount).isEqualTo(1)
        assertThat(stats.incomes).isEqualTo(mapOf("EUR" to 5.0))

        assertThat(stats.expensesCount).isEqualTo(2)
        assertThat(stats.expenses).isEqualTo(mapOf("USD" to 4.0))

        assertThat(stats.newestTrnTime).isEqualTo(fiveSecondsAgo)
    }
}