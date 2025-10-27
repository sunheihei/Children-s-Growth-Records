package com.babycare.childgrowthtracking.ui.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.navigation.compose.hiltViewModel
import com.babycare.childgrowthtracking.AppContext
import com.babycare.childgrowthtracking.R
import com.babycare.childgrowthtracking.datastore.DataStoreViewModel
import com.babycare.childgrowthtracking.datastore.UnitsUiState
import com.babycare.childgrowthtracking.model.Child
import com.babycare.childgrowthtracking.model.GrowthRecord
import com.babycare.childgrowthtracking.model.Organize
import com.babycare.childgrowthtracking.ui.charts.ChartUiState
import com.babycare.childgrowthtracking.ui.charts.ChartsViewModel
import com.babycare.childgrowthtracking.ui.common.ErrorView
import com.babycare.childgrowthtracking.ui.common.LoadingView
import com.babycare.childgrowthtracking.ui.theme.ChildGrowthTrackingTheme
import com.babycare.childgrowthtracking.ui.view.charts.HeadPage
import com.babycare.childgrowthtracking.ui.view.charts.HeightPage
import com.babycare.childgrowthtracking.ui.view.charts.WeightPage
import com.babycare.childgrowthtracking.utils.HEAD_UNIT_CM
import com.babycare.childgrowthtracking.utils.HEIGHT_UNIT_CM
import com.babycare.childgrowthtracking.utils.ORGANIZATION_WHO
import com.babycare.childgrowthtracking.utils.OrganizeData
import com.babycare.childgrowthtracking.utils.WEIGHT_UNIT_KG
import kotlinx.coroutines.launch


@SuppressLint("ContextCastToActivity")
@Composable
fun GrowthDataCharts(
    childId: Int = -1,
    navigateBack: () -> Unit,
    chartsViewModel: ChartsViewModel = hiltViewModel(),
    dataStoreViewModel: DataStoreViewModel = hiltViewModel(),
) {
    val chartUiState by chartsViewModel.chartUiState.collectAsState()
    val units by dataStoreViewModel.unitsUiState.collectAsState()
    val organize by dataStoreViewModel.organizationCode.collectAsState(initial = ORGANIZATION_WHO)

    LaunchedEffect(childId) {
        Log.d("GrowthDataCharts", "childId $childId")
        if (childId != -1) {
            chartsViewModel.fetchChildAndGrowthRecords(childId)
        }
    }

    GrowthDataChartsScreen(
        uiState = chartUiState,
        unitsUiState = units,
        organize = organize,
        navigateBack = navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthDataChartsScreen(
    uiState: ChartUiState,
    unitsUiState: UnitsUiState,
    organize: Int,
    navigateBack: () -> Unit
) {
    val tabTitles = listOf(
        getString(AppContext.getContext(), R.string.height),
        getString(AppContext.getContext(), R.string.weight),
        getString(AppContext.getContext(), R.string.head)
    )
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val coroutineScope = rememberCoroutineScope()
    var showChartsHelpBottomSheet by remember { mutableStateOf(false) }

    // 根据 uiState 获取 child 和 growthRecords
    val (child, growthRecords) = when (uiState) {
        is ChartUiState.Success -> Pair(uiState.child, uiState.growthRecords)
        else -> Pair(Child.TempChild, emptyList())
    }

    // 分割 growthRecords 为 heightMap, weightMap, headMap
    val (heightMap, weightMap, headMap) = splitGrowthRecords(
        growthRecords,
        unitsUiState
    )

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        stringResource(R.string.charts),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showChartsHelpBottomSheet = true
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.charts_help),
                            contentDescription = "charts help",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box {
            Column(modifier = Modifier.padding(innerPadding)) {
                // TabRow 组件
                TabRow(selectedTabIndex = pagerState.currentPage) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(title) }
                        )
                    }
                }

                GrowthDataChartsContent(
                    uiState = uiState,
                    child = child,
                    heightMap = heightMap,
                    weightMap = weightMap,
                    headMap = headMap,
                    unitsUiState = unitsUiState,
                    organize = organize,
                    pagerState = pagerState
                )
            }

            if (showChartsHelpBottomSheet) {
                ChartsBottomSheet(
                    organize,
                    onDismissRequest = {
                        showChartsHelpBottomSheet = false
                    }
                )
            }
        }
    }
}

@Composable
fun GrowthDataChartsContent(
    uiState: ChartUiState,
    child: Child,
    heightMap: Map<Long, String>,
    weightMap: Map<Long, String>,
    headMap: Map<Long, String>,
    unitsUiState: UnitsUiState,
    organize: Int,
    pagerState: PagerState
) {
    when (uiState) {
        is ChartUiState.Loading -> {
            LoadingView()
        }

        is ChartUiState.Error -> {
            ErrorScreen()
        }

        is ChartUiState.Success -> {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> if (heightMap.isNotEmpty()) {
                        HeightPage(
                            child = child,
                            heightMap = retainSevenValues(heightMap),
                            heightUnit = unitsUiState.heightUnit,
                            organize = organize
                        )
                    } else {
                        ErrorScreen()
                    }

                    1 -> if (weightMap.isNotEmpty()) {
                        WeightPage(
                            child = child,
                            weightMap = retainSevenValues(weightMap),
                            weightUnit = unitsUiState.weightUnit,
                            organize = organize
                        )
                    } else {
                        ErrorScreen()
                    }

                    2 -> if (headMap.isNotEmpty()) {
                        HeadPage(
                            child = child,
                            headMap = retainSevenValues(headMap),
                            headUnit = unitsUiState.headUnit,
                            organize = organize
                        )
                    } else {
                        ErrorScreen()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartsBottomSheet(
    currentOrganize: Int,
    onDismissRequest: () -> Unit
) {
    val organize = OrganizeData[currentOrganize]
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = sheetState
    ) {
        SheetScreen(organize)
    }
}

@Composable
private fun SheetScreen(organize: Organize) {
    Column(modifier = Modifier.padding(12.dp)) {
        Text(
            stringResource(R.string.child_growth_standards),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(12.dp)
        ) {
            Image(
                painter = painterResource(organize.logo),
                contentDescription = "",
                Modifier.size(68.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    organize.abbreviationName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, maxLines = 1
                )
                Text(
                    organize.fullName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(12.dp)
        ) {
            Text(
                stringResource(R.string.tips),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.growth_standard_tip),
                style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center,

                )
        }
    }
}


@Composable
fun ErrorScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Insufficient data, please add more records",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

fun splitGrowthRecords(
    growthRecords: List<GrowthRecord>,
    unitsUiState: UnitsUiState
): Triple<Map<Long, String>, Map<Long, String>, Map<Long, String>> {
    val heightMap = mutableMapOf<Long, String>()
    val weightMap = mutableMapOf<Long, String>()
    val headMap = mutableMapOf<Long, String>()

    growthRecords.forEach { record ->
        // 身高：使用 heightCm
        if (unitsUiState.heightUnit == HEIGHT_UNIT_CM) {
            if (record.heightCm.isNotEmpty()) {
                heightMap[record.date] = record.heightCm
            }
        } else {
            if (record.heightFt.isNotEmpty() || record.heightIn.isNotEmpty()) {
                val Ft = record.heightFt.toDoubleOrNull() ?: 0.0
                val In = record.heightIn.toDoubleOrNull() ?: 0.0
                heightMap[record.date] = convertToInches(Ft, In).toString()
            }
        }

        // 体重：使用 weightKg
        if (unitsUiState.weightUnit == WEIGHT_UNIT_KG) {
            if (record.weightKg.isNotEmpty()) {
                weightMap[record.date] = record.weightKg
            }
        } else {
            if (record.weightLb.isNotEmpty()) {
                weightMap[record.date] = record.weightLb
            }
        }

        // 头围：使用 headCircleCm
        if (unitsUiState.headUnit == HEAD_UNIT_CM) {
            if (record.headCircleCm.isNotEmpty()) {
                headMap[record.date] = record.headCircleCm
            }
        } else {
            if (record.headCircleIn.isNotEmpty()) {
                headMap[record.date] = record.headCircleIn
            }
        }

    }

    return Triple(heightMap, weightMap, headMap)
}

fun retainSevenValues(heightMap: Map<Long, String>): MutableMap<Long, String> {
    // 如果小于或等于7个值，返回原map的副本
    if (heightMap.size <= 7) return heightMap.toMutableMap()

    // 获取所有日期键并排序
    val sortedKeys = heightMap.keys.sorted()
    val totalSize = sortedKeys.size
    val step = (totalSize - 1) / 6.0 // 计算步长，确保分成6段以保留7个点

    // 选择7个尽量均匀分布的键
    val keysToKeep = mutableListOf<Long>()
    for (i in 0..6) {
        val index = (i * step).toInt()
        keysToKeep.add(sortedKeys[index])
    }

    // 创建新map，仅保留选中的键值对
    val resultMap = mutableMapOf<Long, String>()
    keysToKeep.forEach { key ->
        heightMap[key]?.let { value ->
            resultMap[key] = value
        }
    }

    return resultMap
}


fun convertToInches(feet: Double, inches: Double): Double {
    // 1 英尺 = 12 英寸
    val feetToInches = feet * 12.0
    // 总英寸 = 英尺转换的英寸 + 额外的英寸
    return feetToInches + inches
}


@Composable
@Preview
fun SheetScreen_Preview() {
    ChildGrowthTrackingTheme {
        SheetScreen(OrganizeData[0])
    }

}


@Composable
@Preview
fun GrowthDataChartsScreen_Preview() {
    ChildGrowthTrackingTheme {
        GrowthDataChartsScreen(
            uiState = ChartUiState.Loading,
            unitsUiState = UnitsUiState(),
            organize = 0,
            navigateBack = {})
    }
}