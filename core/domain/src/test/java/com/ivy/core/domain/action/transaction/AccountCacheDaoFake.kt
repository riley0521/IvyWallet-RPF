package com.ivy.core.domain.action.transaction

import com.ivy.core.persistence.algorithm.accountcache.AccountCacheDao
import com.ivy.core.persistence.algorithm.accountcache.AccountCacheEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.Instant

class AccountCacheDaoFake : AccountCacheDao {

    private val accounts = MutableStateFlow<List<AccountCacheEntity>>(emptyList())

    override fun findAccountCache(accountId: String): Flow<AccountCacheEntity?> {
        return accounts.map { entities ->
            entities.firstOrNull { it.accountId == accountId }
        }
    }

    override suspend fun findTimestampById(accountId: String): Instant? {
        return accounts.value.firstOrNull { it.accountId == accountId }?.timestamp
    }

    override suspend fun save(cache: AccountCacheEntity) {
        accounts.update { it + cache }
    }

    override suspend fun delete(accountId: String) {
        val filteredAccounts = accounts.value.filter { it.accountId != accountId }

        accounts.update { filteredAccounts }
    }

    override suspend fun deleteAll() {
        accounts.update { emptyList() }
    }
}