package com.whitelabel.platform.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LocationFilterPreferences {
    private val _useLocationFilter = MutableStateFlow(false)
    val useLocationFilter: StateFlow<Boolean> = _useLocationFilter.asStateFlow()

    private val _currentUserZones = MutableStateFlow<List<String>>(emptyList())
    val currentUserZones: StateFlow<List<String>> = _currentUserZones.asStateFlow()

    private var persistence: ILocationFilterPersistence? = null

    fun initPersistence(p: ILocationFilterPersistence) {
        persistence = p
        _useLocationFilter.value = p.getUseLocationFilter()
    }

    fun setUseLocationFilter(value: Boolean) {
        _useLocationFilter.value = value
        persistence?.saveUseLocationFilter(value)
    }

    fun setCurrentUserZones(zones: List<String>) {
        _currentUserZones.value = zones
    }
}

interface ILocationFilterPersistence {
    fun getUseLocationFilter(): Boolean
    fun saveUseLocationFilter(value: Boolean)
}
