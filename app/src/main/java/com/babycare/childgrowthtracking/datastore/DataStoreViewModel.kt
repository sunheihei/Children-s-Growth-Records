package com.babycare.childgrowthtracking.datastore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babycare.childgrowthtracking.utils.HEAD_UNIT_CM
import com.babycare.childgrowthtracking.utils.HEIGHT_UNIT_CM
import com.babycare.childgrowthtracking.utils.WEIGHT_UNIT_KG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataStoreViewModel @Inject constructor(
    private val dataStore: GrowthTrackerDataStore
) : ViewModel() {

    val heightUnit: Flow<String> = dataStore.heightUnit
    val weightUnit: Flow<String> = dataStore.weightUnit
    val headUnit: Flow<String> = dataStore.headUnit
    val organizationCode: Flow<Int> = dataStore.organization
    val isFirstLaunch: Flow<Boolean> = dataStore.isFirstLaunch

    fun saveHeightUnit(heightUnit: String) {
        viewModelScope.launch {
            dataStore.saveHeightUnit(heightUnit)
        }
    }

    fun saveWeightUnit(weightUnit: String) {
        viewModelScope.launch {
            dataStore.saveWeightUnit(weightUnit)
        }
    }

    fun saveHeadUnit(headUnit: String) {
        viewModelScope.launch {
            dataStore.saveHeadUnit(headUnit)
        }
    }

    fun saveOrganizationCode(organizationCode: Int) {
        viewModelScope.launch {
            dataStore.saveOrganize(organizationCode)
        }
    }

    fun saveFirstLaunch(firstLaunch: Boolean) {
        viewModelScope.launch {
            dataStore.saveIsFirstLaunch(firstLaunch)
        }
    }

    fun saveAll(heightUnit: String, weightUnit: String, headUnit: String) {
        viewModelScope.launch {
            dataStore.saveAll(heightUnit, weightUnit, headUnit)
        }
    }

    val unitsUiState: StateFlow<UnitsUiState> = combine(
        heightUnit,
        weightUnit,
        headUnit
    ) { height, weight, head ->
        UnitsUiState(
            heightUnit = height,
            weightUnit = weight,
            headUnit = head
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UnitsUiState()
    )



}