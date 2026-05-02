package com.whitelabel.platform.data.repository

import com.whitelabel.platform.data.datasource.CatalogLocalDataSource
import com.whitelabel.platform.data.mapper.CatalogItemMapper.toItem
import com.whitelabel.platform.data.mapper.CatalogItemMapper.toItems
import com.whitelabel.platform.data.models.CatalogItem
import com.whitelabel.core.domain.model.GroupMetadata
import com.whitelabel.core.domain.model.Result
import com.whitelabel.core.domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class CatalogRepository(
    private val dataSource: CatalogLocalDataSource,
    private val languageCodeProvider: () -> String
) : ItemRepository<CatalogItem> {

    fun getAllSites(): Flow<Result<List<CatalogItem>>> =
        dataSource.getAllSites()
            .map<List<com.whitelabel.platform.data.local.Museum_item>, Result<List<CatalogItem>>> { Result.Success(it.map { item -> item.toItem() }) }
            .catch { emit(Result.Error(it)) }

    fun getSiteById(id: Long): Flow<Result<CatalogItem?>> =
        dataSource.getSiteById(id)
            .map<List<com.whitelabel.platform.data.local.Museum_item>?, Result<CatalogItem?>> { items ->
                val firstItem = items?.firstOrNull()
                if (firstItem == null) {
                    Result.Error(Exception("Site not found"))
                } else {
                    Result.Success(firstItem.toItem())
                }
            }
            .catch { emit(Result.Error(it)) }

    fun getFavoriteSites(): Flow<Result<List<CatalogItem>>> =
        dataSource.getFavoriteSites()
            .map<List<com.whitelabel.platform.data.local.Museum_item>, Result<List<CatalogItem>>> {
                Result.Success(it.toItems())
            }
            .catch { emit(Result.Error(it)) }

    fun searchSites(query: String): Flow<Result<List<CatalogItem>>> {
        val languageCode = languageCodeProvider()
        return dataSource.searchSites(query, languageCode)
            .map<List<com.whitelabel.platform.data.local.Museum_item>, Result<List<CatalogItem>>> {
                Result.Success(it.toItems())
            }
            .catch { emit(Result.Error(it)) }
    }

    suspend fun getSiteCount(): Result<Long> =
        try {
            Result.Success(dataSource.getCount())
        } catch (e: Exception) {
            Result.Error(e)
        }

    // === ItemRepository<CatalogItem> interface methods ===

    override fun getAllItems(): Flow<Result<List<CatalogItem>>> = getAllSites()

    override fun getItemById(id: Long): Flow<Result<CatalogItem?>> = getSiteById(id)

    override fun getFavoriteItems(): Flow<Result<List<CatalogItem>>> = getFavoriteSites()

    override fun searchItems(query: String, languageCode: String): Flow<Result<List<CatalogItem>>> =
        dataSource.searchSites(query, languageCode)
            .map<List<com.whitelabel.platform.data.local.Museum_item>, Result<List<CatalogItem>>> {
                Result.Success(it.toItems())
            }
            .catch { emit(Result.Error(it)) }

    override suspend fun toggleFavorite(item: CatalogItem): Result<Unit> =
        try {
            dataSource.updateFavorite(item.id, !item.isFavorite)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }

    override suspend fun markAsViewed(itemId: Long): Result<Unit> =
        try {
            dataSource.markAsViewed(itemId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }

    override suspend fun getItemCount(): Result<Long> = getSiteCount()

    override suspend fun getGroupMetadata(groupKeys: List<String>): Result<Map<String, GroupMetadata>> =
        Result.Success(emptyMap())
}
