package com.ivy.core.domain.action.transaction

import androidx.sqlite.db.SupportSQLiteQuery
import com.ivy.core.persistence.dao.trn.AccountIdAndTrnTime
import com.ivy.core.persistence.dao.trn.TransactionDao
import com.ivy.core.persistence.entity.attachment.AttachmentEntity
import com.ivy.core.persistence.entity.trn.TransactionEntity
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.core.persistence.entity.trn.TrnTagEntity
import com.ivy.data.DELETING
import com.ivy.data.SyncState

class TransactionDaoFake : TransactionDao() {

    val transactions = mutableListOf<TransactionEntity>()
    val transactionMetaDataList = mutableListOf<TrnMetadataEntity>()
    val attachments = mutableListOf<AttachmentEntity>()
    val tags = mutableListOf<TrnTagEntity>()

    override suspend fun saveTrnEntity(entity: TransactionEntity) {
        transactions.add(entity)
    }

    override suspend fun updateTrnTagsSyncByTrnId(trnId: String, sync: SyncState) {
        tags.map {
            return@map if (it.trnId == trnId) {
                it.copy(
                    sync = sync
                )
            } else it
        }
    }

    override suspend fun saveTags(entity: List<TrnTagEntity>) {
        tags.addAll(entity)
    }

    override suspend fun updateAttachmentsSyncByAssociatedId(
        associatedId: String,
        sync: SyncState
    ) {
        attachments.map {
            return@map if (it.associatedId == associatedId) {
                it.copy(
                    sync = sync
                )
            } else it
        }
    }

    override suspend fun saveAttachments(entity: List<AttachmentEntity>) {
        attachments.addAll(entity)
    }

    override suspend fun updateMetadataSyncByTrnId(trnId: String, sync: SyncState) {
        transactionMetaDataList.map {
            return@map if (it.trnId == trnId) {
                it.copy(
                    sync = sync
                )
            } else it
        }
    }

    override suspend fun saveMetadata(entity: List<TrnMetadataEntity>) {
        transactionMetaDataList.addAll(entity)
    }

    override suspend fun findAllBlocking(): List<TransactionEntity> {
        return transactions.filter { it.sync.code != DELETING }
    }

    override suspend fun findBySQL(query: SupportSQLiteQuery): List<TransactionEntity> {
        // We will not execute any raw SQL here in fake.

        return emptyList()
    }

    override suspend fun findAccountIdAndTimeById(trnId: String): AccountIdAndTrnTime? {
        val transaction = transactions.firstOrNull { it.id == trnId } ?: return null

        return AccountIdAndTrnTime(
            accountId = transaction.accountId,
            time = transaction.time,
            timeType = transaction.timeType
        )
    }

    override suspend fun updateTrnEntitySyncById(trnId: String, sync: SyncState) {
        transactions.map {
            return@map if (it.id == trnId) {
                it.copy(
                    sync = sync
                )
            } else it
        }
    }
}