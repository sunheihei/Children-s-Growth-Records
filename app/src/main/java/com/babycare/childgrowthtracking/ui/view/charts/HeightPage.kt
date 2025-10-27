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
import androidx.compose.ui.platform.LocalConfiguration
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
import com.babycare.childgrowthtracking.extensions.cmToFeetDouble
import com.babycare.childgrowthtracking.extensions.cmToInchesDouble
import com.babycare.childgrowthtracking.extensions.rotateWithLayout
import com.babycare.childgrowthtracking.model.Child
import com.babycare.childgrowthtracking.ui.charts.ChartStandardUiState
import com.babycare.childgrowthtracking.ui.charts.standard.ChartsHeightViewModel
import com.babycare.childgrowthtracking.ui.theme.ChildGrowthTrackingTheme
import com.babycare.childgrowthtracking.utils.ChartsUtils
import com.babycare.childgrowthtracking.utils.HEAD_UNIT_CM
import com.babycare.childgrowthtracking.utils.HEIGHT
import com.babycare.childgrowthtracking.utils.HEIGHT_UNIT_CM
import com.babycare.childgrowthtracking.utils.HEIGHT_UNIT_FT_IN
import com.babycare.childgrowthtracking.utils.HEIGHT_UNIT_IN
import com.babycare.childgrowthtracking.utils.ORGANIZATION_CDC
import com.babycare.childgrowthtracking.utils.ORGANIZATION_NHC
import com.babycare.childgrowthtracking.utils.ORGANIZATION_WHO
import javax.annotation.meta.When


// Height 页面
@Composable
fun HeightPage(child: Child, heightMap: Map<Long, String>, heightUnit: String, organize: Int) {
    val chartsHeightViewModel = hiltViewModel<ChartsHeightViewModel>()
    val standardDataUiState by chartsHeightViewModel.standardHeightUiState.collectAsState()
    val standardData by chartsHeightViewModel.standardData.collectAsState()


    LaunchedEffect(heightMap.toList(), child.gender, child.age, organize, heightUnit) {
        val range = ChartsUtils.calculateMonthRange(heightMap, child.age)
        when (organize) {
            ORGANIZATION_WHO -> {
                if (range.minMonth < 60) {
                    chartsHeightViewModel.fetchHeightStandard(
                        gender = child.gender,
                        heightMap = heightMap,
                        age = child.age,
                        organize = organize,
                        heightUnit = heightUnit
                    )
                } else {
                    // 即使跳过标准数据请求，也更新为 Success 以渲染用户数据
                    chartsHeightViewModel.updateToSuccess(emptyList())
                }
            }

            ORGANIZATION_CDC -> {
                if (range.minMonth < 240) {
                    chartsHeightViewModel.fetchHeightStandard(
                        gender = child.gender,
                        heightMap = heightMap,
                        age = child.age,
                        organize = organize,
                        heightUnit = heightUnit
                    )
                } else {
                    chartsHeightViewModel.updateToSuccess(emptyList())
                }
            }

            ORGANIZATION_NHC -> {
                if (range.minMonth < 84) {
                    chartsHeightViewModel.fetchHeightStandard(
                        gender = child.gender,
                        heightMap = heightMap,
                        age = child.age,
                        organize = organize,
                        heightUnit = heightUnit
                    )
                } else {
                    chartsHeightViewModel.updateToSuccess(emptyList())
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
                standardDataUiState,
                chartsHeightViewModel,
                child,
                heightMap,
                organize,
                heightUnit
            )
        }

        is ChartStandardUiState.Success -> {
            Log.d("Charts", "Success state: standardData=$standardData, heightMap=$heightMap")
            if (heightMap.isNotEmpty()) {
                HeightChartsScreen(heightMap, standardData, heightUnit)
            } else {
                Log.d("Charts", "Success but no user data available")
                Text(
                    text = "No height data available",
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun HeightChartsScreen(
    heightMap: Map<Long, String>,
    standardData: List<List<Double>>,
    heightUnit: String
) {
    Log.d("Recomposition", "HeightChartsScreen")
    HeightChartContent(heightMap, standardData, heightUnit)
}

@Composable
fun HeightChartContent(
    heightMap: Map<Long, String>,
    standardData: List<List<Double>>,
    heightUnit: String
) {
    val (xAxisData, data) = remember(heightMap) {
        val xAxisData = mutableListOf<String>()
        val data = mutableListOf<Double>()
        heightMap.entries.sortedBy { it.key }.forEach { entry ->
            val timestamp = entry.key
            val height = entry.value.toDouble()
            xAxisData.add(timestamp.birthdayFormat())
            data.add(height)
        }
        xAxisData to data
    }

// 直接在 Composable 上下文中构造 lineParameters
    val lineParameters = if (standardData.isNotEmpty()) {
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
                label = "${stringResource(R.string.height)} (${
                    if (heightUnit == HEIGHT_UNIT_CM) HEIGHT_UNIT_CM else HEIGHT_UNIT_IN
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
                label = "${stringResource(R.string.height)} (${
                    if (heightUnit == HEIGHT_UNIT_CM) HEIGHT_UNIT_CM else HEIGHT_UNIT_IN
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
    chartsHeightViewModel: ChartsHeightViewModel,
    child: Child,
    heightMap: Map<Long, String>,
    organize: Int,
    heightUnit: String
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
            chartsHeightViewModel.fetchHeightStandard(
                gender = child.gender,
                heightMap = heightMap,
                age = child.age,
                organize = organize,
                heightUnit = heightUnit
            )
        }) {
            Text("Retry")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            chartsHeightViewModel.fetchHeightStandard(
                gender = child.gender,
                heightMap = heightMap,
                age = child.age,
                organize = organize,
                heightUnit = heightUnit
            )
        }) {
            Text("Clear Cache and Retry")
        }
    }
}

@Composable
@Preview
fun HeightPage_Preview() {
    ChildGrowthTrackingTheme{
        HeightPage(child = Child.TempChild, emptyMap(), HEIGHT_UNIT_CM, ORGANIZATION_WHO)
    }
}
