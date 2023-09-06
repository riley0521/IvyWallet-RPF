package com.ivy.core.domain.action.transaction

import android.graphics.Color
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
import java.time.LocalDateTime
import java.util.UUID

fun account(): Account {
    return Account(
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
            lastUpdated = LocalDateTime.now()
        )
    )
}

fun tag(): Tag {
    return Tag(
        id = UUID.randomUUID().toString(),
        color = Color.BLUE,
        name = "Sample Tag",
        orderNum = 0.0,
        state = TagState.Default,
        sync = Sync(
            state = SyncState.Synced,
            lastUpdated = LocalDateTime.now()
        )
    )
}

fun attachment(associatedId: String): Attachment {
    return Attachment(
        id = UUID.randomUUID().toString(),
        associatedId = associatedId,
        uri = "",
        source = AttachmentSource.Local,
        filename = null,
        type = AttachmentType.Image,
        sync = Sync(
            state = SyncState.Synced,
            lastUpdated = LocalDateTime.now()
        )
    )
}

fun category(): Category {
    return Category(
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
            lastUpdated = LocalDateTime.now()
        )
    )
}

fun metadata(): TrnMetadata {
    return TrnMetadata(
        recurringRuleId = null,
        loanId = null,
        loanRecordId = null
    )
}

fun transaction(
    id: UUID,
    account: Account,
    value: Value,
    category: Category,
    tags: List<Tag> = emptyList(),
    attachments: List<Attachment> = emptyList()
): Transaction {
    return Transaction(
        id = id,
        account = account,
        type = TransactionType.Income,
        value = value,
        category = category,
        time = TrnTime.Actual(LocalDateTime.now()),
        title = "Sample Transaction",
        description = null,
        state = TrnState.Default,
        purpose = TrnPurpose.Fee,
        tags = tags,
        attachments = attachments,
        metadata = TrnMetadata(
            recurringRuleId = null,
            loanId = null,
            loanRecordId = null
        ),
        sync = Sync(
            state = SyncState.Synced,
            lastUpdated = LocalDateTime.now()
        )
    )
}