package com.ivy.core.domain.action.transaction

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.ivy.core.domain.algorithm.accountcache.InvalidateAccCacheAct
import com.ivy.data.Value
import com.ivy.data.transaction.TransactionType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

internal class WriteTrnsActTest {

    private lateinit var transactionDaoFake: TransactionDaoFake
    private lateinit var trnsSignal: TrnsSignal
    private lateinit var timeProviderFake: TimeProviderFake
    private lateinit var accountCacheDaoFake: AccountCacheDaoFake

    // SUT
    private lateinit var writeTrnsAct: WriteTrnsAct

    @BeforeEach
    fun setup() {
        transactionDaoFake = TransactionDaoFake()
        trnsSignal = TrnsSignal()
        timeProviderFake = TimeProviderFake()
        accountCacheDaoFake = AccountCacheDaoFake()
        writeTrnsAct = WriteTrnsAct(
            transactionDao = transactionDaoFake,
            trnsSignal = trnsSignal,
            timeProvider = timeProviderFake,
            invalidateAccCacheAct = InvalidateAccCacheAct(
                accountCacheDao = accountCacheDaoFake,
                timeProvider = timeProviderFake
            ),
            accountCacheDao = accountCacheDaoFake
        )
    }

    @Test
    fun `Test create new income transaction, validate saved data`() = runBlocking<Unit> {

        val transactionId = UUID.randomUUID()
        val tagId = UUID.randomUUID()
        val attachmentId = UUID.randomUUID()

        val newTag = tag().copy(
            id = tagId.toString()
        )

        val newAttachment = attachment(transactionId.toString()).copy(
            id = attachmentId.toString()
        )

        val newAccount = account()

        val newTransaction = transaction(
            id = transactionId,
            account = newAccount,
            value = Value(100.0, "USD"),
            category = category(),
            tags = listOf(newTag),
            attachments = listOf(newAttachment)
        )

        writeTrnsAct(WriteTrnsAct.Input.CreateNew(trn = newTransaction))

        val createdTransaction =
            transactionDaoFake.transactions.firstOrNull { it.id == transactionId.toString() }

        val createdTag = transactionDaoFake.tags.firstOrNull { it.tagId == tagId.toString() }
        val createdAttachment =
            transactionDaoFake.attachments.firstOrNull { it.id == attachmentId.toString() }

        assertThat(createdTransaction).isNotNull()
        assertThat(createdTransaction?.type).isEqualTo(TransactionType.Income)

        assertThat(createdTag).isNotNull()
        assertThat(createdAttachment).isNotNull()
    }
}