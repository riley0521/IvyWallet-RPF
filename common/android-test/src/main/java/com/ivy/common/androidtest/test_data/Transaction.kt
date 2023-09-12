package com.ivy.common.androidtest.test_data

import com.ivy.core.persistence.dao.trn.SaveTrnData
import com.ivy.core.persistence.entity.trn.TransactionEntity
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.data.SyncState
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnState
import java.time.Instant
import java.util.UUID

fun transactionEntity(
    amount: Double,
    accountId: String
): TransactionEntity {
    return TransactionEntity(
        id = UUID.randomUUID().toString(),
        accountId = accountId,
        type = TransactionType.Expense,
        amount = amount,
        currency = "EUR",
        time = Instant.now(),
        timeType = TrnTimeType.Due,
        title = "Test transaction",
        description = "Test description",
        categoryId = null,
        state = TrnState.Default,
        purpose = TrnPurpose.Fee,
        sync = SyncState.Syncing,
        lastUpdated = Instant.now()
    )
}

fun transactionWithTime(
    time: Instant,
    transaction: TransactionEntity = transactionEntity(amount = 50.0, accountId = "test-account")
): TransactionEntity {
    return transaction.copy(time = time, lastUpdated = time)
}

fun saveTrnData(
    entity: TransactionEntity = transactionEntity(amount = 50.0, accountId = "test-account")
): SaveTrnData {
    return SaveTrnData(
        entity = entity,
        tags = emptyList(),
        attachments = emptyList(),
        metadata = emptyList()
    )
}