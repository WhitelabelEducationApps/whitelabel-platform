package com.whitelabel.platform.di

import com.whitelabel.core.domain.repository.ItemRepository
import com.whitelabel.core.domain.usecase.GetItemDetailUseCase
import com.whitelabel.core.presentation.detail.ItemDetailViewModel
import com.whitelabel.core.presentation.home.HomeViewModel
import com.whitelabel.core.presentation.home.ItemGrouper
import com.whitelabel.core.presentation.language.LanguageSelectionViewModel
import com.whitelabel.platform.data.models.CatalogItem
import com.whitelabel.platform.utils.LOG
import com.whitelabel.platform.utils.LocationFilterPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import org.koin.dsl.module

val viewModelModule = module {
    factory {
        LOG("DI - Creating NEW CoroutineScope", tag = "DI")
        CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }

    single {
        LOG("DI - Creating HomeViewModel (SINGLETON)", tag = "DI")
        val locationFilter = combine(
            LocationFilterPreferences.useLocationFilter,
            LocationFilterPreferences.currentUserZones
        ) { use, zones ->
            if (!use || zones.isEmpty()) { _: CatalogItem -> true }
            else { item: CatalogItem ->
                item.countries.isEmpty() || item.countries.any { it in zones }
            }
        }
        HomeViewModel(
            getItemsUseCase = get(),
            searchItemsUseCase = get(),
            toggleFavoriteUseCase = get(),
            repository = get<ItemRepository<CatalogItem>>(),
            itemGrouper = get<ItemGrouper<CatalogItem>>(),
            languageProvider = get(),
            coroutineScope = get(),
            itemFilter = locationFilter
        )
    }

    factory { params ->
        val siteId = params.get<Long>()
        LOG("DI - Creating NEW ItemDetailViewModel for siteId=$siteId", tag = "DI")
        ItemDetailViewModel<CatalogItem>(
            itemId = siteId,
            getItemDetailUseCase = get<GetItemDetailUseCase<CatalogItem>>(),
            toggleFavoriteUseCase = get(),
            repository = get<ItemRepository<CatalogItem>>(),
            wallpaperService = get(),
            languageProvider = get(),
            coroutineScope = get()
        )
    }

    factory {
        LanguageSelectionViewModel(
            languageProvider = get()
        )
    }
}
