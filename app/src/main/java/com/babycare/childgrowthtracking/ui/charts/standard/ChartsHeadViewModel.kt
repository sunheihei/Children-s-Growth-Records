package com.babycare.childgrowthtracking.ui.charts.standard

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babycare.childgrowthtracking.extensions.cmToInchesDouble
import com.babycare.childgrowthtracking.model.GrowthStandard
import com.babycare.childgrowthtracking.model.Organize
import com.babycare.childgrowthtracking.ui.charts.ChartStandardUiState
import com.babycare.childgrowthtracking.ui.charts.ChartsRepository
import com.babycare.childgrowthtracking.utils.HEAD
import com.babycare.childgrowthtracking.utils.HEAD_UNIT_CM
import com.babycare.childgrowthtracking.utils.HEAD_UNIT_IN
import com.babycare.childgrowthtracking.utils.HEIGHT
import com.babycare.childgrowthtracking.utils.WEIGHT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartsHeadViewModel @Inject constructor(
    private val chartsRepository: ChartsRepository
) : ViewModel(), DefaultLifecycleObserver {

    private val _standardHeadUiState =
        MutableStateFlow<ChartStandardUiState>(ChartStandardUiState.Loading)
    val standardHeadUiState: StateFlow<ChartStandardUiState> = _standardHeadUiState.asStateFlow()

    private val _standardData = MutableStateFlow<List<List<Double>>>(emptyList())
    val standardData: StateFlow<List<List<Double>>> = _standardData.asStateFlow()

    private var lastHeadMap: Map<Long, String>? = null
    private var lastParams: FetchParams? = null

    data class FetchParams(val gender: Int, val age: Long, val organize: Int, val unit: String)

    fun fetchHeadStandard(
        gender: Int,
        headMap: Map<Long, String>,
        age: Long,
        organize: Int,
        headUnit: String = HEAD_UNIT_CM
    ) {
        val currentParams = FetchParams(gender, age, organize, headUnit)
        if (lastHeadMap?.toList() == headMap.toList() && lastParams == currentParams) {
            Log.d("Charts", "Skipping fetch: same headMap and params")
            return
        }

        lastHeadMap = headMap.toMap()
        lastParams = currentParams

        viewModelScope.launch {
            try {
                _standardHeadUiState.value = ChartStandardUiState.Loading
                val data = chartsRepository.getStandardValuesForRecords(
                    gender, HEAD, headMap, age, organize
                )
                Log.d("Charts", "Repository returned data: $data")
                val processedData = processStandardData(data, headUnit)
                _standardData.value = processedData
                _standardHeadUiState.value = ChartStandardUiState.Success(data)
                Log.d("Charts", "Updated state: uiState=Success, standardData=$processedData")
            } catch (e: Exception) {
                _standardHeadUiState.value =
                    ChartStandardUiState.Error("Failed to fetch standard data: ${e.message}")
                _standardData.value = emptyList()
                Log.e("Charts", "Error fetching data: ${e.message}", e)
            }
        }
    }

    fun updateToSuccess(data: List<GrowthStandard>) {
        _standardHeadUiState.value = ChartStandardUiState.Success(data)
        _standardData.value = processStandardData(data, HEAD_UNIT_CM)
        Log.d("Charts", "Forced Success state: data=$data, standardData=${_standardData.value}")
    }

    fun clearCache() {
        lastHeadMap = null
        lastParams = null
        _standardHeadUiState.value = ChartStandardUiState.Loading
        _standardData.value = emptyList()
        Log.d("Charts", "Cache cleared")
    }

    private fun processStandardData(
        standardValues: List<GrowthStandard>,
        headUnit: String
    ): List<List<Double>> {
        Log.d(
            "Charts",
            "Processing standardValues: size=${standardValues.size}, headUnit=$headUnit"
        )
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
            if (headUnit == HEAD_UNIT_CM) {
                p5.add(standard.p5.toDoubleOrNull() ?: 0.0)
                p25.add(standard.p25.toDoubleOrNull() ?: 0.0)
                p50.add(standard.p50.toDoubleOrNull() ?: 0.0)
                p75.add(standard.p75.toDoubleOrNull() ?: 0.0)
                p95.add(standard.p95.toDoubleOrNull() ?: 0.0)
            } else if (headUnit == HEAD_UNIT_IN) {
                p5.add((standard.p5.toDoubleOrNull() ?: 0.0).cmToInchesDouble())
                p25.add((standard.p25.toDoubleOrNull() ?: 0.0).cmToInchesDouble())
                p50.add((standard.p50.toDoubleOrNull() ?: 0.0).cmToInchesDouble())
                p75.add((standard.p75.toDoubleOrNull() ?: 0.0).cmToInchesDouble())
                p95.add((standard.p95.toDoubleOrNull() ?: 0.0).cmToInchesDouble())
            }
        }

        val result = listOf(p5, p25, p50, p75, p95)
        Log.d("Charts", "Processed data: $result")
        return result
    }
}