package com.whitelabel.platform.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.whitelabel.core.domain.language.SupportedLanguage
import com.whitelabel.platform.data.local.CatalogDatabase
import com.whitelabel.platform.data.local.DatabaseDriverFactory
import com.whitelabel.platform.data.local.Museum_item
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class CatalogLocalDataSource(
    driverFactory: DatabaseDriverFactory,
    private val dispatcher: CoroutineDispatcher
) {
    private val database = CatalogDatabase(driverFactory.createDriver())
    private val heritageQueries = database.catalogItemQueries
    private val authorQueries = database.authorQueries

    fun getAllSites(): Flow<List<Museum_item>> {
        return heritageQueries.selectAll()
            .asFlow()
            .mapToList(dispatcher)
            .flowOn(dispatcher)
    }

    fun getSiteById(id: Long): Flow<List<Museum_item>?> {
        return heritageQueries.selectById(id)
            .asFlow()
            .mapToList(dispatcher)
            .flowOn(dispatcher)
    }

    fun getFavoriteSites(): Flow<List<Museum_item>> {
        return heritageQueries.selectFavorites()
            .asFlow()
            .mapToList(dispatcher)
            .flowOn(dispatcher)
    }

    fun searchSites(query: String, languageCode: String): Flow<List<Museum_item>> {
        val language = SupportedLanguage.fromCode(languageCode)

        val searchQuery = when (language) {
            SupportedLanguage.ROMANIAN -> heritageQueries.searchByNameRo(query)
            SupportedLanguage.ITALIAN -> heritageQueries.searchByNameIt(query)
            SupportedLanguage.SPANISH -> heritageQueries.searchByNameEs(query)
            SupportedLanguage.GERMAN -> heritageQueries.searchByNameDe(query)
            SupportedLanguage.FRENCH -> heritageQueries.searchByNameFr(query)
            SupportedLanguage.PORTUGUESE -> heritageQueries.searchByNamePt(query)
            SupportedLanguage.RUSSIAN -> heritageQueries.searchByNameRu(query)
            SupportedLanguage.CHINESE -> heritageQueries.searchByNameZh(query)
            SupportedLanguage.JAPANESE -> heritageQueries.searchByNameJa(query)
            else -> heritageQueries.searchByName(query)  // Default to English for unsupported languages
        }

        return searchQuery
            .asFlow()
            .mapToList(dispatcher)
            .flowOn(dispatcher)
    }

    suspend fun updateFavorite(id: Long, isFavorite: Boolean) {
        withContext(dispatcher) {
            database.transaction {
                heritageQueries.updateFavorite(
                    isFavourite = if (isFavorite) "true" else "false",
                    id = id
                )
            }
        }
    }

    suspend fun markAsViewed(id: Long) {
        withContext(dispatcher) {
            heritageQueries.updateViewed("true", id)
        }
    }

    suspend fun getCount(): Long {
        return withContext(dispatcher) {
            heritageQueries.countAll().executeAsOne()
        }
    }

    suspend fun getAllAuthors() = withContext(dispatcher) {
        authorQueries.selectAll().executeAsList()
    }
}
