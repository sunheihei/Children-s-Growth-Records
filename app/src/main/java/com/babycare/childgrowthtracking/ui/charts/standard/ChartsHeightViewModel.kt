package com.babycare.childgrowthtracking.ui.charts.standard

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babycare.childgrowthtracking.extensions.cmToInchesDouble
import com.babycare.childgrowthtracking.model.GrowthStandard
import com.babycare.childgrowthtracking.ui.charts.ChartStandardUiState
import com.babycare.childgrowthtracking.ui.charts.ChartsRepository
import com.babycare.childgrowthtracking.utils.HEIGHT
import com.babycare.childgrowthtracking.utils.HEIGHT_UNIT_CM
import com.babycare.childgrowthtracking.utils.HEIGHT_UNIT_FT_IN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartsHeightViewModel @Inject constructor(
    private val chartsRepository: ChartsRepository
) : ViewModel(), DefaultLifecycleObserver {

    private val _standardHeightUiState = MutableStateFlow<ChartStandardUiState>(ChartStandardUiState.Loading)
    val standardHeightUiState: StateFlow<ChartStandardUiState> = _standardHeightUiState.asStateFlow()

    private val _standardData = MutableStateFlow<List<List<Double>>>(emptyList())
    val standardData: StateFlow<List<List<Double>>> = _standardData.asStateFlow()

    private var lastHeightMap: Map<Long, String>? = null
    private var lastParams: FetchParams? = null

    data class FetchParams(val gender: Int, val age: Long, val organize: Int)

    fun fetchHeightStandard(
        gender: Int,
        heightMap: Map<Long, String>,
        age: Long,
        organize: Int,
        heightUnit: String = HEIGHT_UNIT_CM
    ) {
        val currentParams = FetchParams(gender, age, organize)
        if (lastHeightMap?.toList() == heightMap.toList() && lastParams == currentParams) {
            Log.d("Charts", "Skipping fetch: same heightMap and params")
            return
        }

        Log.d("Charts", "Fetching data: gender=$gender, heightMap=$heightMap, age=$age, organize=$organize, heightUnit=$heightUnit")
        lastHeightMap = heightMap.toMap()
        lastParams = currentParams

        viewModelScope.launch {
            try {
                _standardHeightUiState.value = ChartStandardUiState.Loading
                val data = chartsRepository.getStandardValuesForRecords(
                    gender, HEIGHT, heightMap, age, organize
                )
                Log.d("Charts", "Repository returned data: $data")
                val processedData = processStandardData(data, heightUnit)
                _standardData.value = processedData
                _standardHeightUiState.value = ChartStandardUiState.Success(data)
                Log.d("Charts", "Updated state: uiState=Success, standardData=$processedData")
            } catch (e: Exception) {
                _standardHeightUiState.value = ChartStandardUiState.Error("Failed to fetch standard data: ${e.message}")
                _standardData.value = emptyList()
                Log.e("Charts", "Error fetching data: ${e.message}", e)
            }
        }
    }

    fun updateToSuccess(data: List<GrowthStandard>) {
        _standardHeightUiState.value = ChartStandardUiState.Success(data)
        _standardData.value = processStandardData(data, HEIGHT_UNIT_CM)
    }

    private fun processStandardData(standardValues: List<GrowthStandard>, heightUnit: String): List<List<Double>> {
        if (standardValues.isEmpty()) {
            return emptyList()
        }

        val p5 = mutableListOf<Double>()
        val p25 = mutableListOf<Double>()
        val p50 = mutableListOf<Double>()
        val p75 = mutableListOf<Double>()
        val p95 = mutableListOf<Double>()

        standardValues.forEach { standard ->
            if (heightUnit == HEIGHT_UNIT_CM) {
                p5.add(standard.p5.toDoubleOrNull() ?: 0.0)
                p25.add(standard.p25.toDoubleOrNull() ?: 0.0)
                p50.add(standard.p50.toDoubleOrNull() ?: 0.0)
                p75.add(standard.p75.toDoubleOrNull() ?: 0.0)
                p95.add(standard.p95.toDoubleOrNull() ?: 0.0)
            } else if (heightUnit == HEIGHT_UNIT_FT_IN) {
                p5.add((standard.p5.toDoubleOrNull() ?: 0.0).cmToInchesDouble())
                p25.add((standard.p25.toDoubleOrNull() ?: 0.0).cmToInchesDouble())
                p50.add((standard.p50.toDoubleOrNull() ?: 0.0).cmToInchesDouble())
                p75.add((standard.p75.toDoubleOrNull() ?: 0.0).cmToInchesDouble())
                p95.add((standard.p95.toDoubleOrNull() ?: 0.0).cmToInchesDouble())
            }
        }

        val result = listOf(p5, p25, p50, p75, p95)
        return result
    }
}