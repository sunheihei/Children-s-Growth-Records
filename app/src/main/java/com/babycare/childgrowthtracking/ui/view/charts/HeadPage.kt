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
import com.babycare.childgrowthtracking.extensions.cmToInchesDouble
import com.babycare.childgrowthtracking.extensions.rotateWithLayout
import com.babycare.childgrowthtracking.model.Child
import com.babycare.childgrowthtracking.ui.charts.ChartStandardUiState
import com.babycare.childgrowthtracking.ui.charts.standard.ChartsHeadViewModel
import com.babycare.childgrowthtracking.ui.theme.ChildGrowthTrackingTheme
import com.babycare.childgrowthtracking.utils.ChartsUtils
import com.babycare.childgrowthtracking.utils.HEAD_UNIT_CM
import com.babycare.childgrowthtracking.utils.HEAD_UNIT_IN
import com.babycare.childgrowthtracking.utils.ORGANIZATION_CDC
import com.babycare.childgrowthtracking.utils.ORGANIZATION_NHC
import com.babycare.childgrowthtracking.utils.ORGANIZATION_WHO

// Head 页面
@Composable
fun HeadPage(child: Child, headMap: Map<Long, String>, headUnit: String, organize: Int) {
    val chartsHeadViewModel = hiltViewModel<ChartsHeadViewModel>()
    val standardDataUiState by chartsHeadViewModel.standardHeadUiState.collectAsState()
    val standardData by chartsHeadViewModel.standardData.collectAsState()

    Log.d("Charts", "HeadPage params: child=${child.id}, headMap=$headMap, headUnit=$headUnit, organize=$organize")

    LaunchedEffect(headMap.toList(), child.gender, child.age, organize, headUnit) {
        Log.d("Charts", "LaunchedEffect triggered: child=${child.id}, headMap=$headMap, gender=${child.gender}, age=${child.age}, organize=$organize, headUnit=$headUnit")
        val range = ChartsUtils.calculateMonthRange(headMap, child.age)
        Log.d("Charts", "Calculated range: minMonth=${range.minMonth}")
        when (organize) {
            ORGANIZATION_WHO -> {
                if (range.minMonth < 60) {
                    chartsHeadViewModel.fetchHeadStandard(
                        gender = child.gender,
                        headMap = headMap,
                        age = child.age,
                        organize = organize,
                        headUnit = headUnit
                    )
                } else {
                    Log.d("Charts", "WHO: minMonth=${range.minMonth} >= 60, skipping fetch")
                    chartsHeadViewModel.updateToSuccess(emptyList())
                }
            }
            ORGANIZATION_CDC -> {
                if (range.minMonth < 36) {
                    chartsHeadViewModel.fetchHeadStandard(
                        gender = child.gender,
                        headMap = headMap,
                        age = child.age,
                        organize = organize,
                        headUnit = headUnit
                    )
                } else {
                    Log.d("Charts", "CDC: minMonth=${range.minMonth} >= 36, skipping fetch")
                    chartsHeadViewModel.updateToSuccess(emptyList())
                }
            }
            ORGANIZATION_NHC -> {
                if (range.minMonth < 36) {
                    chartsHeadViewModel.fetchHeadStandard(
                        gender = child.gender,
                        headMap = headMap,
                        age = child.age,
                        organize = organize,
                        headUnit = headUnit
                    )
                } else {
                    Log.d("Charts", "NHC: minMonth=${range.minMonth} >= 36, skipping fetch")
                    chartsHeadViewModel.updateToSuccess(emptyList())
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
                chartsHeadViewModel = chartsHeadViewModel,
                child = child,
                headMap = headMap,
                organize = organize,
                headUnit = headUnit
            )
        }
        is ChartStandardUiState.Success -> {
            Log.d("Charts", "Success state: standardData=$standardData, headMap=$headMap")
            if (headMap.isNotEmpty()) {
                HeadChartsScreen(headMap, standardData, headUnit)
            } else {
                Log.d("Charts", "Success but no user data available")
                Text(
                    text = "No head circumference data available",
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun HeadChartsScreen(
    headMap: Map<Long, String>,
    standardData: List<List<Double>>,
    headUnit: String
) {
    Log.d("Recomposition", "HeadChartsScreen")
    HeadChartContent(headMap, standardData, headUnit)
}

@Composable
fun HeadChartContent(
    headMap: Map<Long, String>,
    standardData: List<List<Double>>,
    headUnit: String
) {
    val (xAxisData, data) = remember(headMap) {
        val xAxisData = mutableListOf<String>()
        val data = mutableListOf<Double>()
        headMap.entries.sortedBy { it.key }.forEach { entry ->
            val timestamp = entry.key
            val head = entry.value.toDouble()
            xAxisData.add(timestamp.birthdayFormat())
            data.add(head)
        }
        xAxisData to data
    }

    // 直接在 Composable 上下文中构造 lineParameters，检查长度
    val lineParameters = if (standardData.isNotEmpty() && standardData.size == 5 && standardData.all { it.size == xAxisData.size }) {
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
                label = "${stringResource(R.string.head_circumference)} (${
                    if (headUnit == HEAD_UNIT_CM) HEAD_UNIT_CM else HEAD_UNIT_IN
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
                label = "${stringResource(R.string.head_circumference)} (${
                    if (headUnit == HEAD_UNIT_CM) HEAD_UNIT_CM else HEAD_UNIT_IN
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
    chartsHeadViewModel: ChartsHeadViewModel,
    child: Child,
    headMap: Map<Long, String>,
    organize: Int,
    headUnit: String
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
            chartsHeadViewModel.fetchHeadStandard(
                gender = child.gender,
                headMap = headMap,
                age = child.age,
                organize = organize,
                headUnit = headUnit
            )
        }) {
            Text("Retry")
        }
        Spacer(modifier = Modifier.height(6.dp))
        Button(onClick = {
            chartsHeadViewModel.clearCache()
            chartsHeadViewModel.fetchHeadStandard(
                gender = child.gender,
                headMap = headMap,
                age = child.age,
                organize = organize,
                headUnit = headUnit
            )
        }) {
            Text("Clear Cache and Retry")
        }
    }
}

@Composable
@Preview
fun HeadPage_Preview() {
    ChildGrowthTrackingTheme{
        HeadPage(child = Child.TempChild, emptyMap(), HEAD_UNIT_CM, ORGANIZATION_WHO)
    }
}