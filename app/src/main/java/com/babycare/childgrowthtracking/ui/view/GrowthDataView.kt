package com.babycare.childgrowthtracking.ui.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Addchart
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.babycare.childgrowthtracking.R
import com.babycare.childgrowthtracking.datastore.DataStoreViewModel
import com.babycare.childgrowthtracking.datastore.UnitsUiState
import com.babycare.childgrowthtracking.extensions.birthdayFormat
import com.babycare.childgrowthtracking.extensions.toast
import com.babycare.childgrowthtracking.model.Child
import com.babycare.childgrowthtracking.model.GrowthRecord
import com.babycare.childgrowthtracking.ui.children.ChildUiState
import com.babycare.childgrowthtracking.ui.children.ChildrenViewModel
import com.babycare.childgrowthtracking.ui.common.LoadingView
import com.babycare.childgrowthtracking.ui.growthrecord.CombinedUiState
import com.babycare.childgrowthtracking.ui.growthrecord.GrowthRecordListUiState
import com.babycare.childgrowthtracking.ui.growthrecord.GrowthRecordViewModel
import com.babycare.childgrowthtracking.ui.theme.ChildGrowthTrackingTheme
import com.babycare.childgrowthtracking.utils.BitmapUtils
import com.babycare.childgrowthtracking.utils.CommonUtils
import com.babycare.childgrowthtracking.utils.HEAD_UNIT_CM
import com.babycare.childgrowthtracking.utils.HEIGHT_UNIT_CM
import com.babycare.childgrowthtracking.utils.WEIGHT_UNIT_KG
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.Date


@SuppressLint("ContextCastToActivity")
@Composable
fun GrowthData(
    childId: Int = -1,
    navigateBack: () -> Unit,
    navigateCharts: (childId: Int) -> Unit,
    addGrowthRecord: (childId: Int) -> Unit,
    editGrowthRecord: (growthRecordId: Int) -> Unit
) {
    val context = (LocalContext.current as ComponentActivity)

    val dataStoreViewModel = hiltViewModel<DataStoreViewModel>()
    val units by dataStoreViewModel.unitsUiState.collectAsState()


    val growthRecordViewModel = hiltViewModel<GrowthRecordViewModel>()
    val growthRecordsCombineUiState by growthRecordViewModel.combinedUiState.collectAsState()


    LaunchedEffect(childId) {
        if (childId != -1) {
            growthRecordViewModel.loadChildData(childId)
        }
    }

    GrowthDataScreen(
        growthRecordsCombineUiState = growthRecordsCombineUiState,
        context = context,
        unitsUiState = units,
        navigateBack = navigateBack,
        navigateCharts = navigateCharts,
        addGrowthRecord = addGrowthRecord,
        editGrowthRecord = editGrowthRecord
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthDataScreen(
    growthRecordsCombineUiState: CombinedUiState,
    context: Context,
    unitsUiState: UnitsUiState,
    navigateBack: () -> Unit,
    navigateCharts: (childId: Int) -> Unit,
    addGrowthRecord: (childId: Int) -> Unit,
    editGrowthRecord: (growthRecordId: Int) -> Unit
) {

    val listState = rememberLazyListState()
    var isFaExpand by remember { mutableStateOf(true) }
    var lastScrollOffset by remember { mutableIntStateOf(0) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect { offset ->
                // 添加防抖逻辑
                val shouldExpand = offset <= lastScrollOffset
                if (isFaExpand != shouldExpand) {
                    isFaExpand = shouldExpand
                }
                lastScrollOffset = offset
            }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        stringResource(R.string.growth_data),
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
                    if (growthRecordsCombineUiState is CombinedUiState.Success) {
                        IconButton(onClick = {
                            navigateCharts.invoke(growthRecordsCombineUiState.child.id)
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.BarChart,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "Navigate to Charts"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (growthRecordsCombineUiState is CombinedUiState.Success) {
                ExtendedFloatingActionButton(
                    expanded = isFaExpand,
                    onClick = { addGrowthRecord.invoke(growthRecordsCombineUiState.child.id) },
                    icon = { Icon(painter = painterResource(R.drawable.add_record), "") },
                    text = { Text(text = stringResource(R.string.add_growth_data)) },
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        GrowthDataContent(
            uiState = growthRecordsCombineUiState,
            context = context,
            listState = listState,
            unitsUiState = unitsUiState,
            addGrowthRecord = {
                if (growthRecordsCombineUiState is CombinedUiState.Success) {
                    addGrowthRecord.invoke(growthRecordsCombineUiState.child.id)
                }
            },
            editGrowthRecord = editGrowthRecord,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun GrowthDataContent(
    uiState: CombinedUiState,
    context: Context,
    listState: LazyListState,
    unitsUiState: UnitsUiState,
    addGrowthRecord: () -> Unit,
    editGrowthRecord: (growthRecordId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is CombinedUiState.Loading -> {
            LoadingView()
        }

        is CombinedUiState.Error -> {
            NoGrowthRecordView(addGrowthRecord = addGrowthRecord)
        }

        is CombinedUiState.Success -> {
            Column(modifier = modifier) {
                HeadSection(
                    child = uiState.child,
                    growthRecordList = uiState.growthRecords,
                    context = context
                )
                GrowthRecordSection(
                    growthRecordList = uiState.growthRecords,
                    listState = listState,
                    unitsUiState = unitsUiState,
                    addGrowthRecord = addGrowthRecord,
                    growthRecordItemClick = editGrowthRecord
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HeadSection(
    child: Child = Child.TempChild, growthRecordList: List<GrowthRecord>, context: Context
) {

    // 同时缓存 Bitmap 和 ImageRequest
    val avatarBitmap = remember(child.avatar) {
        BitmapUtils.base64ToBitmap(child.avatar)
    }


    // 头像和用户名部分
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 模拟头像
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(avatarBitmap) // 使用缓存的 Bitmap
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile Avatar",
                modifier = Modifier
                    .size(74.dp)
                    .clip(MaterialShapes.Cookie7Sided.toShape()),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.logo), // 加载中显示的占位符
                error = painterResource(id = R.drawable.logo) // 加载失败时显示的错误占位符
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = child.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = CommonUtils.calculateAgeDifference(child.age, Date().time),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Filled.IosShare,
                contentDescription = "data share",
                modifier = Modifier.clickable {
                    if (growthRecordList.isNotEmpty()) {
                        CommonUtils.generateAndSharePdf(child.name, context, growthRecordList)
                    } else {
                        context.getString(R.string.no_data_to_share).toast(context)
                    }
                })
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            Text(
                stringResource(R.string.date),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                stringResource(R.string.height),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                stringResource(R.string.weight),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                stringResource(R.string.head),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
        Divider()
    }


}

@Composable
fun GrowthRecordSection(
    growthRecordList: List<GrowthRecord>,
    listState: LazyListState,
    unitsUiState: UnitsUiState,
    addGrowthRecord: () -> Unit,
    growthRecordItemClick: (growthRecordId: Int) -> Unit,
) {
    if (growthRecordList.isNotEmpty()) {
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            itemsIndexed(growthRecordList) { index, record ->
                GrowthItem(
                    index,
                    record,
                    unitsUiState,
                    growthRecordItemClick = { growthRecordId ->
                        growthRecordItemClick.invoke(
                            growthRecordId
                        )
                    }, modifier = Modifier.animateItem()
                )
            }
        }
    } else {
        NoGrowthRecordView(addGrowthRecord = addGrowthRecord)
    }
}

@Composable
fun NoGrowthRecordView(addGrowthRecord: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable {
            addGrowthRecord()
        }) {
            Icon(
                painter = painterResource(R.drawable.add_record),
                contentDescription = "add diary",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(R.string.no_growth_record),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Spacer(Modifier.weight(1f))
    }
}


@Composable
fun GrowthItem(
    index: Int,
    growthData: GrowthRecord,
    unitsUiState: UnitsUiState,
    growthRecordItemClick: (growthRecordId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (index % 2 == 0) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                }
            )
            .clickable {
                growthRecordItemClick(growthData.id)
            }
            .padding(vertical = 8.dp)) {
        Text(
            growthData.date.birthdayFormat(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = if (unitsUiState.heightUnit.equals(HEIGHT_UNIT_CM)) {
                "${growthData.heightCm}(cm)"
            } else {
                "${growthData.heightFt}(ft) ${growthData.heightIn}(in)"
            },
            maxLines = 1,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = if (unitsUiState.weightUnit.equals(WEIGHT_UNIT_KG)) {
                "${growthData.weightKg}(kg)"
            } else {
                "${growthData.weightLb}(lb)"
            },
            maxLines = 1,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = if (unitsUiState.headUnit.equals(HEAD_UNIT_CM)) {
                "${growthData.headCircleCm}(cm)"
            } else {
                "${growthData.headCircleIn}(in)"
            },
            maxLines = 1,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
@Preview
fun GrowthData_Preview() {
    val context = LocalContext.current
    GrowthDataScreen(
        CombinedUiState.Success(Child.TempChild, GrowthRecord.TempGrowthRecordList),
        context,
        unitsUiState = UnitsUiState(),
        navigateBack = {},
        navigateCharts = {},
        addGrowthRecord = {},
        editGrowthRecord = {})

}

@Composable
@Preview
fun HeadSection_Preview() {
    ChildGrowthTrackingTheme {
        HeadSection(child = Child.TempChild, emptyList(), LocalContext.current)
    }
}

@Composable
@Preview
fun GrowthItem_Preview() {
    ChildGrowthTrackingTheme {
        GrowthItem(
            0,
            GrowthRecord.TempGrowthRecord,
            unitsUiState = UnitsUiState(),
            growthRecordItemClick = {})
    }
}

@Composable
@Preview
fun NoGrowthRecordView_Preview() {
    ChildGrowthTrackingTheme {
        NoGrowthRecordView(addGrowthRecord = {})
    }
}
