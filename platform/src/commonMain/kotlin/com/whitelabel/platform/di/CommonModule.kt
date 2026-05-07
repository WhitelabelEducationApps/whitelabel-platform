package com.whitelabel.platform.di

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import com.whitelabel.core.domain.language.LanguageProvider
import com.whitelabel.core.domain.repository.ItemRepository
import com.whitelabel.core.domain.usecase.GetItemDetailUseCase
import com.whitelabel.core.domain.usecase.GetItemsUseCase
import com.whitelabel.core.domain.usecase.SearchItemsUseCase
import com.whitelabel.core.domain.usecase.ToggleFavoriteUseCase
import com.whitelabel.core.presentation.home.ItemGrouper
import com.whitelabel.platform.data.CatalogLanguageProvider
import com.whitelabel.platform.data.datasource.CatalogLocalDataSource
import com.whitelabel.platform.data.models.CatalogItem
import com.whitelabel.platform.data.repository.CatalogRepository
import com.whitelabel.platform.presentation.HeritageItemGrouper
import com.whitelabel.platform.utils.ImagePreloader
import com.whitelabel.platform.utils.LOG
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

@OptIn(ExperimentalCoilApi::class)
val commonModule = module {
    single<CoroutineDispatcher> {
        LOG("DI - Creating CoroutineDispatcher (SINGLETON)", tag = "DI")
        Dispatchers.Default
    }

    single {
        LOG("DI - Creating CatalogLocalDataSource (SINGLETON)", tag = "DI")
        CatalogLocalDataSource(get(), get())
    }

    single<ItemRepository<CatalogItem>> {
        LOG("DI - Creating CatalogRepository (SINGLETON)", tag = "DI")
        CatalogRepository(get()) { com.whitelabel.platform.utils.LocalizationManager.getCurrentLanguageCode() }
    }

    single<LanguageProvider> { CatalogLanguageProvider() }

    factory {
        LOG("DI - Creating NEW GetItemsUseCase", tag = "DI")
        GetItemsUseCase(get<ItemRepository<CatalogItem>>())
    }
    factory {
        LOG("DI - Creating NEW SearchItemsUseCase", tag = "DI")
        SearchItemsUseCase(get<ItemRepository<CatalogItem>>(), get())
    }
    factory {
        LOG("DI - Creating NEW ToggleFavoriteUseCase", tag = "DI")
        ToggleFavoriteUseCase(get<ItemRepository<CatalogItem>>())
    }
    factory {
        LOG("DI - Creating NEW GetItemDetailUseCase", tag = "DI")
        GetItemDetailUseCase(get<ItemRepository<CatalogItem>>())
    }

    single<ItemGrouper<CatalogItem>> { HeritageItemGrouper() }

    single { (context: PlatformContext) ->
        LOG("DI - Creating ImagePreloader (SINGLETON)", tag = "DI")
        ImagePreloader(context, ImageLoader(context))
    }
}
