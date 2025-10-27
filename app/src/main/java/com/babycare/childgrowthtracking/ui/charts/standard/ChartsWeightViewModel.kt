package com.babycare.childgrowthtracking.ui.charts.standard

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babycare.childgrowthtracking.extensions.kgToLbDouble
import com.babycare.childgrowthtracking.model.GrowthStandard
import com.babycare.childgrowthtracking.ui.charts.ChartStandardUiState
import com.babycare.childgrowthtracking.ui.charts.ChartsRepository
import com.babycare.childgrowthtracking.utils.HEIGHT
import com.babycare.childgrowthtracking.utils.WEIGHT
import com.babycare.childgrowthtracking.utils.WEIGHT_UNIT_KG
import com.babycare.childgrowthtracking.utils.WEIGHT_UNIT_LB
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartsWeightViewModel @Inject constructor(
    private val chartsRepository: ChartsRepository
) : ViewModel(), DefaultLifecycleObserver {

    private val _standardWeightUiState = MutableStateFlow<ChartStandardUiState>(ChartStandardUiState.Loading)
    val standardWeightUiState: StateFlow<ChartStandardUiState> = _standardWeightUiState.asStateFlow()

    private val _standardData = MutableStateFlow<List<List<Double>>>(emptyList())
    val standardData: StateFlow<List<List<Double>>> = _standardData.asStateFlow()

    private var lastWeightMap: Map<Long, String>? = null
    private var lastParams: FetchParams? = null

    data class FetchParams(val gender: Int, val age: Long, val organize: Int, val unit: String)

    fun fetchWeightStandard(
        gender: Int,
        weightMap: Map<Long, String>,
        age: Long,
        organize: Int,
        weightUnit: String = WEIGHT_UNIT_KG
    ) {
        val currentParams = FetchParams(gender, age, organize, weightUnit)
        if (lastWeightMap?.toList() == weightMap.toList() && lastParams == currentParams) {
            Log.d("Charts", "Skipping fetch: same weightMap and params")
            return
        }

        Log.d("Charts", "Fetching data: gender=$gender, weightMap=$weightMap, age=$age, organize=$organize, weightUnit=$weightUnit")
        lastWeightMap = weightMap.toMap()
        lastParams = currentParams

        viewModelScope.launch {
            try {
                _standardWeightUiState.value = ChartStandardUiState.Loading
                val data = chartsRepository.getStandardValuesForRecords(
                    gender, WEIGHT, weightMap, age, organize
                )
                Log.d("Charts", "Repository returned data: $data")
                val processedData = processStandardData(data, weightUnit)
                _standardData.value = processedData
                _standardWeightUiState.value = ChartStandardUiState.Success(data)
                Log.d("Charts", "Updated state: uiState=Success, standardData=$processedData")
            } catch (e: Exception) {
                _standardWeightUiState.value = ChartStandardUiState.Error("Failed to fetch standard data: ${e.message}")
                _standardData.value = emptyList()
                Log.e("Charts", "Error fetching data: ${e.message}", e)
            }
        }
    }

    fun updateToSuccess(data: List<GrowthStandard>) {
        _standardWeightUiState.value = ChartStandardUiState.Success(data)
        _standardData.value = processStandardData(data, WEIGHT_UNIT_KG)
        Log.d("Charts", "Forced Success state: data=$data, standardData=${_standardData.value}")
    }

    fun clearCache() {
        lastWeightMap = null
        lastParams = null
        _standardWeightUiState.value = ChartStandardUiState.Loading
        _standardData.value = emptyList()
        Log.d("Charts", "Cache cleared")
    }

    private fun processStandardData(standardValues: List<GrowthStandard>, weightUnit: String): List<List<Double>> {
        Log.d("Charts", "Processing standardValues: size=${standardValues.size}, weightUnit=$weightUnit")
        if (standardValues.isEmpty()) {
            Log.d("Charts", "Empty standardValues, returning emptyList")
            return emptyList()
        }

        val p5 = mutableListOf<Double>()
        val p25 = mutableListOf<Double>()
        val p50 = mutableListOf<Double>()
        val p75 = mutableListOf<Double>()
        val p95 = mutableListOf<Double>()

        standardValues.forEach { standard ->
            if (weightUnit == WEIGHT_UNIT_KG) {
                p5.add(standard.p5.toDoubleOrNull() ?: 0.0)
                p25.add(standard.p25.toDoubleOrNull() ?: 0.0)
                p50.add(standard.p50.toDoubleOrNull() ?: 0.0)
                p75.add(standard.p75.toDoubleOrNull() ?: 0.0)
                p95.add(standard.p95.toDoubleOrNull() ?: 0.0)
            } else if (weightUnit == WEIGHT_UNIT_LB) {
                p5.add((standard.p5.toDoubleOrNull() ?: 0.0).kgToLbDouble())
                p25.add((standard.p25.toDoubleOrNull() ?: 0.0).kgToLbDouble())
                p50.add((standard.p50.toDoubleOrNull() ?: 0.0).kgToLbDouble())
                p75.add((standard.p75.toDoubleOrNull() ?: 0.0).kgToLbDouble())
                p95.add((standard.p95.toDoubleOrNull() ?: 0.0).kgToLbDouble())
            }
        }

        val result = listOf(p5, p25, p50, p75, p95)
        Log.d("Charts", "Processed data: $result")
        return result
    }
}