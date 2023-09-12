package com.ivy.common.androidtest.test_data

import android.graphics.Color
import com.ivy.core.persistence.IvyWalletCoreDb
import com.ivy.core.persistence.entity.account.AccountEntity
import com.ivy.core.persistence.entity.trn.TransactionEntity
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.account.Account
import com.ivy.data.account.AccountState
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

suspend fun IvyWalletCoreDb.saveAccountWithTransactions(
    accountEntity: AccountEntity = accountEntity(),
    transactions: List<TransactionEntity> = listOf(transactionEntity(amount = 50.0, accountId = "test-account"))
) {

    accountDao().save(listOf(accountEntity))

    val transactionsWithAccount = transactions.map {
        it.copy(
            accountId = accountEntity.id
        )
    }
    transactionsWithAccount.forEach {
        trnDao().save(saveTrnData(it))
    }
}

fun accountEntity(): AccountEntity {
    return AccountEntity(
        id = UUID.randomUUID().toString(),
        name = "Test account",
        currency = "EUR",
        color = Color.BLUE,
        icon = null,
        folderId = null,
        orderNum = 1.0,
        excluded = false,
        state = AccountState.Default,
        sync = SyncState.Syncing,
        lastUpdated = Instant.now()
    )
}