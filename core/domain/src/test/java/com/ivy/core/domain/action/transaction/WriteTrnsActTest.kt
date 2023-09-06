package com.ivy.core.domain.action.transaction

import android.graphics.Color
import arrow.core.firstOrNone
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.ivy.core.domain.algorithm.accountcache.InvalidateAccCacheAct
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.Value
import com.ivy.data.account.Account
import com.ivy.data.account.AccountState
import com.ivy.data.attachment.Attachment
import com.ivy.data.attachment.AttachmentSource
import com.ivy.data.attachment.AttachmentType
import com.ivy.data.category.Category
import com.ivy.data.category.CategoryState
import com.ivy.data.category.CategoryType
import com.ivy.data.tag.Tag
import com.ivy.data.tag.TagState
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnMetadata
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnState
import com.ivy.data.transaction.TrnTime
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

        val newTag = Tag(
            id = tagId.toString(),
            color = Color.BLUE,
            name = "Sample Tag",
            orderNum = 0.0,
            state = TagState.Default,
            sync = Sync(
                state = SyncState.Synced,
                lastUpdated = timeProviderFake.timeNow()
            )
        )

        val newAttachment = Attachment(
            id = attachmentId.toString(),
            associatedId = "",
            uri = "",
            source = AttachmentSource.Local,
            filename = null,
            type = AttachmentType.Image,
            sync = Sync(
                state = SyncState.Synced,
                lastUpdated = timeProviderFake.timeNow()
            )
        )

        val newTransaction = Transaction(
            id = transactionId,
            account = Account(
                id = UUID.randomUUID(),
                name = "Sample Account",
                currency = "USD",
                color = Color.BLUE,
                icon = "account",
                excluded = false,
                folderId = null,
                orderNum = 0.0,
                state = AccountState.Default,
                sync = Sync(
                    state = SyncState.Synced,
                    lastUpdated = timeProviderFake.timeNow()
                )
            ),
            type = TransactionType.Income,
            value = Value(
                amount = 100.0,
                currency = "USD"
            ),
            category = Category(
                id = UUID.randomUUID(),
                name = "Sample Category",
                type = CategoryType.Both,
                parentCategoryId = null,
                color = Color.BLUE,
                icon = "category",
                orderNum = 0.0,
                state = CategoryState.Default,
                sync = Sync(
                    state = SyncState.Synced,
                    lastUpdated = timeProviderFake.timeNow()
                )
            ),
            time = TrnTime.Actual(actual = timeProviderFake.timeNow()),
            title = "Sample Transaction",
            description = null,
            state = TrnState.Default,
            purpose = TrnPurpose.Fee,
            tags = listOf(newTag),
            attachments = listOf(newAttachment),
            metadata = TrnMetadata(
                recurringRuleId = null,
                loanId = null,
                loanRecordId = null
            ),
            sync = Sync(
                state = SyncState.Synced,
                lastUpdated = timeProviderFake.timeNow()
            )
        )

        writeTrnsAct(WriteTrnsAct.Input.CreateNew(trn = newTransaction))

        val createdTransaction = transactionDaoFake.transactions.firstOrNull { it.id == transactionId.toString() }

        val createdTag = transactionDaoFake.tags.firstOrNull { it.tagId == tagId.toString() }
        val createdAttachment = transactionDaoFake.attachments.firstOrNull { it.id == attachmentId.toString() }

        assertThat(createdTransaction).isNotNull()
        assertThat(createdTransaction?.type).isEqualTo(TransactionType.Income)

        assertThat(createdTag).isNotNull()
        assertThat(createdAttachment).isNotNull()
    }
}