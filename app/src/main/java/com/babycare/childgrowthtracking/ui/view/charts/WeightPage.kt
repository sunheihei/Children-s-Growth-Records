package com.babycare.childgrowthtracking.ui.view.charts

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import com.babycare.childgrowthtracking.R
import com.babycare.childgrowthtracking.extensions.birthdayFormat
import com.babycare.childgrowthtracking.extensions.kgToLbDouble
import com.babycare.childgrowthtracking.extensions.rotateWithLayout
import com.babycare.childgrowthtracking.model.Child
import com.babycare.childgrowthtracking.model.WeightForAgeBoys
import com.babycare.childgrowthtracking.ui.charts.ChartStandardUiState
import com.babycare.childgrowthtracking.ui.charts.standard.ChartsHeightViewModel
import com.babycare.childgrowthtracking.ui.charts.standard.ChartsWeightViewModel
import com.babycare.childgrowthtracking.ui.theme.ChildGrowthTrackingTheme
import com.babycare.childgrowthtracking.utils.ChartsUtils
import com.babycare.childgrowthtracking.utils.HEIGHT
import com.babycare.childgrowthtracking.utils.ORGANIZATION_CDC
import com.babycare.childgrowthtracking.utils.ORGANIZATION_NHC
import com.babycare.childgrowthtracking.utils.ORGANIZATION_WHO
import com.babycare.childgrowthtracking.utils.WEIGHT
import com.babycare.childgrowthtracking.utils.WEIGHT_UNIT_KG
import com.babycare.childgrowthtracking.utils.WEIGHT_UNIT_LB

// Weight 页面
@Composable
fun WeightPage(child: Child, weightMap: Map<Long, String>, weightUnit: String, organize: Int) {
    val chartsWeightViewModel = hiltViewModel<ChartsWeightViewModel>()
    val standardDataUiState by chartsWeightViewModel.standardWeightUiState.collectAsState()
    val standardData by chartsWeightViewModel.standardData.collectAsState()

    Log.d(
        "Charts",
        "WeightPage params: child=${child.id}, weightMap=$weightMap, weightUnit=$weightUnit, organize=$organize"
    )

    LaunchedEffect(weightMap.toList(), child.gender, child.age, organize, weightUnit) {
        Log.d(
            "Charts",
            "LaunchedEffect triggered: child=${child.id}, weightMap=$weightMap, gender=${child.gender}, age=${child.age}, organize=$organize, weightUnit=$weightUnit"
        )
        val range = ChartsUtils.calculateMonthRange(weightMap, child.age)
        Log.d("Charts", "Calculated range: minMonth=${range.minMonth}")
        when (organize) {
            ORGANIZATION_WHO -> {
                if (range.minMonth < 60) {
                    chartsWeightViewModel.fetchWeightStandard(
                        gender = child.gender,
                        weightMap = weightMap,
                        age = child.age,
                        organize = organize,
                        weightUnit = weightUnit
                    )
                } else {
                    Log.d("Charts", "WHO: minMonth=${range.minMonth} >= 60, skipping fetch")
                    chartsWeightViewModel.updateToSuccess(emptyList())
                }
            }

            ORGANIZATION_CDC -> {
                if (range.minMonth < 240) {
                    chartsWeightViewModel.fetchWeightStandard(
                        gender = child.gender,
                        weightMap = weightMap,
                        age = child.age,
                        organize = organize,
                        weightUnit = weightUnit
                    )
                } else {
                    Log.d("Charts", "CDC: minMonth=${range.minMonth} >= 240, skipping fetch")
                    chartsWeightViewModel.updateToSuccess(emptyList())
                }
            }

            ORGANIZATION_NHC -> {
                if (range.minMonth < 84) {
                    chartsWeightViewModel.fetchWeightStandard(
                        gender = child.gender,
                        weightMap = weightMap,
                        age = child.age,
                        organize = organize,
                        weightUnit = weightUnit
                    )
                } else {
                    Log.d("Charts", "NHC: minMonth=${range.minMonth} >= 84, skipping fetch")
                    chartsWeightViewModel.updateToSuccess(emptyList())
                }
            }
        }
    }

    when (standardDataUiState) {
        is ChartStandardUiState.Loading -> {
            Log.d("Charts", "Showing Loading state")
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }

        is ChartStandardUiState.Error -> {
            ChartsDataError(
                standardDataUiState = standardDataUiState,
                chartsWeightViewModel = chartsWeightViewModel,
                child = child,
                weightMap = weightMap,
                organize = organize,
                weightUnit = weightUnit
            )
        }

        is ChartStandardUiState.Success -> {
            Log.d("Charts", "Success state: standardData=$standardData, weightMap=$weightMap")
            if (weightMap.isNotEmpty()) {
                WeightChartsScreen(weightMap, standardData, weightUnit)
            } else {
                Log.d("Charts", "Success but no user data available")
                Text(
                    text = "No weight data available",
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun WeightChartsScreen(
    weightMap: Map<Long, String>,
    standardData: List<List<Double>>,
    weightUnit: String
) {
    Log.d("Recomposition", "WeightChartsScreen")
    WeightChartContent(weightMap, standardData, weightUnit)
}

@Composable
fun WeightChartContent(
    weightMap: Map<Long, String>,
    standardData: List<List<Double>>,
    weightUnit: String
) {
    val (xAxisData, data) = remember(weightMap) {
        val xAxisData = mutableListOf<String>()
        val data = mutableListOf<Double>()
        weightMap.entries.sortedBy { it.key }.forEach { entry ->
            val timestamp = entry.key
            val weight = entry.value.toDouble()
            xAxisData.add(timestamp.birthdayFormat())
            data.add(weight)
        }
        xAxisData to data
    }

    // 直接在 Composable 上下文中构造 lineParameters，检查长度
    val lineParameters =
        if (standardData.isNotEmpty() && standardData.size == 5 && standardData.all { it.size == xAxisData.size }) {
            listOf(
                LineParameters(
                    label = "P5",
                    data = standardData[0],
                    lineColor = Color.Red.copy(alpha = 0.3f),
                    lineType = LineType.DEFAULT_LINE,
                    lineShadow = false
                ),
                LineParameters(
                    label = "P25",
                    data = standardData[1],
                    lineColor = Color.Green.copy(alpha = 0.3f),
                    lineType = LineType.DEFAULT_LINE,
                    lineShadow = false
                ),
                LineParameters(
                    label = "P50",
                    data = standardData[2],
                    lineColor = Color.Magenta.copy(alpha = 0.3f),
                    lineType = LineType.DEFAULT_LINE,
                    lineShadow = false
                ),
                LineParameters(
                    label = "P75",
                    data = standardData[3],
                    lineColor = Color.Cyan.copy(alpha = 0.3f),
                    lineType = LineType.DEFAULT_LINE,
                    lineShadow = false
                ),
                LineParameters(
                    label = "P95",
                    data = standardData[4],
                    lineColor = Color.Yellow.copy(alpha = 0.3f),
                    lineType = LineType.DEFAULT_LINE,
                    lineShadow = false
                ),
                LineParameters(
                    label = "${stringResource(R.string.weight)} (${
                        if (weightUnit == WEIGHT_UNIT_KG) WEIGHT_UNIT_KG else WEIGHT_UNIT_LB
                    })",
                    data = data,
                    lineColor = MaterialTheme.colorScheme.primary,
                    lineType = LineType.DEFAULT_LINE,
                    lineShadow = false
                )
            )
        } else {
            listOf(
                LineParameters(
                    label = "${stringResource(R.string.weight)} (${
                        if (weightUnit == WEIGHT_UNIT_KG) WEIGHT_UNIT_KG else WEIGHT_UNIT_LB
                    })",
                    data = data,
                    lineColor = MaterialTheme.colorScheme.primary,
                    lineType = LineType.DEFAULT_LINE,
                    lineShadow = false
                )
            )
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .rotateWithLayout(90f)
            .padding(16.dp)
    ) {
        Box(Modifier) {
            LineChart(
                modifier = Modifier.fillMaxSize(),
                linesParameters = lineParameters,
                isGrid = true,
                gridColor = MaterialTheme.colorScheme.secondary,
                xAxisData = xAxisData,
                animateChart = true,
                showGridWithSpacer = true,
                yAxisStyle = TextStyle(fontSize = 14.sp, color = Color.Gray),
                xAxisStyle = TextStyle(
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal
                ),
                showXAxis = true,
                yAxisRange = 5,
                oneLineChart = false,
                gridOrientation = GridOrientation.GRID
            )
        }
    }
}

@Composable
private fun ChartsDataError(
    standardDataUiState: ChartStandardUiState,
    chartsWeightViewModel: ChartsWeightViewModel,
    child: Child,
    weightMap: Map<Long, String>,
    organize: Int,
    weightUnit: String
) {
    val errorMessage = (standardDataUiState as ChartStandardUiState.Error).message
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            chartsWeightViewModel.fetchWeightStandard(
                gender = child.gender,
                weightMap = weightMap,
                age = child.age,
                organize = organize,
                weightUnit = weightUnit
            )
        }) {
            Text("Retry")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            chartsWeightViewModel.clearCache()
            chartsWeightViewModel.fetchWeightStandard(
                gender = child.gender,
                weightMap = weightMap,
                age = child.age,
                organize = organize,
                weightUnit = weightUnit
            )
        }) {
            Text("Clear Cache and Retry")
        }
    }
}

@Composable
@Preview
fun WeightPage_Preview() {
    ChildGrowthTrackingTheme {
        WeightPage(child = Child.TempChild, emptyMap(), WEIGHT_UNIT_KG, ORGANIZATION_WHO)
    }
}