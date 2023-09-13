package com.ivy.home

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import com.ivy.common.androidtest.IvyAndroidTest
import com.ivy.common.androidtest.test_data.saveAccountWithTransactions
import com.ivy.common.androidtest.test_data.transactionWithTime
import com.ivy.data.transaction.TransactionType
import com.ivy.navigation.Navigator
import com.ivy.wallet.ui.RootActivity
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

@HiltAndroidTest
class HomeScreenTest : IvyAndroidTest() {

    @get:Rule
    val composeRule = createAndroidComposeRule<RootActivity>()

    @Inject
    lateinit var navigator: Navigator

    @Test
    fun testSelectingDateRange() = runBlocking<Unit> {
        val date = LocalDate.of(2023, 7, 23)
        setDate(date)

        val transaction1 = transactionWithTime(Instant.parse("2023-07-24T09:00:00Z")).copy(
            title = "Transaction1"
        )
        val transaction2 = transactionWithTime(Instant.parse("2023-08-01T09:00:00Z")).copy(
            title = "Transaction2"
        )
        val transaction3 = transactionWithTime(Instant.parse("2023-08-31T09:00:00Z")).copy(
            title = "Transaction3"
        )
        db.saveAccountWithTransactions(
            transactions = listOf(transaction1, transaction2, transaction3)
        )

        HomeScreenRobot(composeRule)
            .navigateTo(navigator)
            .openDateRangeSheet(timeProvider)
            .selectMonth("August")
            .assertDateIsDisplayed(1, "August")
            .assertDateIsDisplayed(31, "August")
            .apply {
                composeRule.onRoot().printToLog("Root")
            }
            .clickDone()
            .clickUpcoming()
            .assertTransactionDoesNotExist("Transaction1")
            .assertTransactionIsDisplayed("Transaction2")
            .assertTransactionIsDisplayed("Transaction3")
    }

    @Test
    fun testGetOverdueTransaction_turnsIntoNormalTransaction() = runBlocking<Unit> {
        val date = LocalDate.of(2023, 7, 9)
        setDate(date)

        val overdueIncomeTransaction = transactionWithTime(Instant.parse("2023-07-08T09:00:00Z")).copy(
            title = "TransactionOverdue",
            type = TransactionType.Income
        )

        db.saveAccountWithTransactions(
            transactions = listOf(overdueIncomeTransaction)
        )

        HomeScreenRobot(composeRule)
            .navigateTo(navigator)
            .clickOverdue()
            .clickGet()
            .assertTransactionIsDisplayed("TransactionOverdue")
            .assertBalanceIsDisplayed(
                overdueIncomeTransaction.amount,
                overdueIncomeTransaction.currency
            )
    }
}