package com.babycare.childgrowthtracking.ui.charts

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babycare.childgrowthtracking.model.Child
import com.babycare.childgrowthtracking.model.GrowthRecord
import com.babycare.childgrowthtracking.model.GrowthStandard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

import javax.inject.Inject

sealed interface ChartUiState {
    object Loading : ChartUiState
    data class Success(
        val child: Child, val growthRecords: List<GrowthRecord>
    ) : ChartUiState

    data class Error(val message: String) : ChartUiState
}

sealed interface ChartStandardUiState {
    object Loading : ChartStandardUiState
    data class Success(
        val standardValue: List<GrowthStandard>
    ) : ChartStandardUiState

    data class Error(val message: String) : ChartStandardUiState
}


@HiltViewModel
class ChartsViewModel @Inject constructor(private val chartsRepository: ChartsRepository) :
    ViewModel(), DefaultLifecycleObserver {

    private val _chartUiState = MutableStateFlow<ChartUiState>(value = ChartUiState.Loading)
    val chartUiState: StateFlow<ChartUiState> = _chartUiState.asStateFlow()


    fun fetchChildAndGrowthRecords(childId: Int) {
        viewModelScope.launch {
            chartsRepository.getChildAndGrowthRecords(childId).onStart {
                _chartUiState.value = ChartUiState.Loading
            }.collectLatest { state ->
                _chartUiState.value = state
            }
        }
    }


}