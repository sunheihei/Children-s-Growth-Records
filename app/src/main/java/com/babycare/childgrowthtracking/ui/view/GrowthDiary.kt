package com.babycare.childgrowthtracking.ui.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.babycare.childgrowthtracking.R
import com.babycare.childgrowthtracking.extensions.birthdayFormat
import com.babycare.childgrowthtracking.model.DiaryPhoto
import com.babycare.childgrowthtracking.model.DiaryWithPhotos
import com.babycare.childgrowthtracking.ui.common.LoadingView
import com.babycare.childgrowthtracking.ui.growthdiary.GrowthDiaryListUiState
import com.babycare.childgrowthtracking.ui.growthdiary.GrowthDiaryViewModel
import com.babycare.childgrowthtracking.ui.theme.ChildGrowthTrackingTheme


@SuppressLint("ContextCastToActivity")
@Composable
fun GrowthDiaryView(
    childId: Int = -1,
    navigateBack: () -> Unit,
    addGrowthDiary: (child: Int) -> Unit,
    diaryItemClick: (diaryId: Int) -> Unit,
    growthDiaryViewModel: GrowthDiaryViewModel = hiltViewModel(),
) {
    val context = (LocalContext.current as ComponentActivity)

    val growthDiaryListUiState by growthDiaryViewModel.uiStateGrowthDiarys.collectAsState()

    LaunchedEffect(childId) {
        if (childId != -1) {
            growthDiaryViewModel.getDiariesWithPhotosByChildId(childId)
        }
    }

    GrowthDiaryScreen(
        uiState = growthDiaryListUiState,
        childId = childId,
        navigateBack = navigateBack,
        addGrowthDiary = addGrowthDiary,
        diaryItemClick = diaryItemClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthDiaryScreen(
    uiState: GrowthDiaryListUiState,
    childId: Int,
    navigateBack: () -> Unit,
    addGrowthDiary: (child: Int) -> Unit,
    diaryItemClick: (diaryId: Int) -> Unit
) {
    val diaries = when (uiState) {
        is GrowthDiaryListUiState.Success -> uiState.growthDiaryList ?: emptyList()
        else -> emptyList()
    }

    val listState = rememberLazyListState()
    var isFaExpand by remember { mutableStateOf(true) }
    var lastScrollOffset by remember { mutableIntStateOf(0) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .collect { offset ->
                if (offset > lastScrollOffset && offset > 0) {
                    isFaExpand = false
                } else if (offset < lastScrollOffset) {
                    isFaExpand = true
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
                        stringResource(R.string.growth_diary),
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
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                expanded = isFaExpand,
                onClick = { addGrowthDiary(childId) },
                icon = { Icon(painter = painterResource(R.drawable.add_diary), "") },
                text = { Text(text = stringResource(R.string.add_growth_diary)) },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        GrowthDiaryContent(
            uiState = uiState,
            listState = listState,
            innerPadding = innerPadding,
            addGrowthDiary = { addGrowthDiary(childId) },
            diaryItemClick = diaryItemClick
        )
    }
}

@Composable
fun GrowthDiaryContent(
    uiState: GrowthDiaryListUiState,
    listState: LazyListState,
    innerPadding: PaddingValues,
    addGrowthDiary: () -> Unit,
    diaryItemClick: (diaryId: Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)
    ) {
        when (uiState) {
            is GrowthDiaryListUiState.Loading -> {
                LoadingView()
            }

            is GrowthDiaryListUiState.Error -> {
                noDiaryView(addGrowthDiary)
            }

            is GrowthDiaryListUiState.Success -> {
                val diaries = uiState.growthDiaryList ?: emptyList()
                if (diaries.isNotEmpty()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(diaries) { index, diaryWithPhotos ->
                            DiaryItem(
                                diaryWithPhotos,
                                diaryItemClick = { diaryItemClick.invoke(diaryWithPhotos.diary.id) }
                            )
                        }
                    }
                } else {
                    noDiaryView(addGrowthDiary)
                }
            }
        }
    }
}

@Composable
fun noDiaryView(addGrowthDiary: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable {
            addGrowthDiary()
        }) {
            Icon(
                painter = painterResource(R.drawable.add_diary),
                contentDescription = "add diary",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(R.string.no_growth_diary),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Spacer(Modifier.weight(1f))
    }
}


@Composable
fun DiaryItem(diaryWithPhotos: DiaryWithPhotos, diaryItemClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    diaryItemClick.invoke()
                }) {
            Text(
                diaryWithPhotos.diary.date.birthdayFormat(),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                diaryWithPhotos.diary.content, fontWeight = FontWeight.Bold, fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            PhotosShower(diaryWithPhotos.photos)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosShower(items: List<DiaryPhoto>) {
    if (items.size == 1) {
        AsyncImage(
            model = items[0].photoPath, // 直接使用 Uri String
            contentDescription = "Selected Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(MaterialTheme.shapes.medium), // 替换 maskClip
            contentScale = ContentScale.Crop
        )
    } else {
        HorizontalMultiBrowseCarousel(
            state = CarouselState(itemCount = { items.size }),
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp),
            preferredItemWidth = 220.dp,
            itemSpacing = 8.dp,
        ) { index ->
            if (index in items.indices) { // 限制 index 在 0 until items.size
                val item = items[index]
                AsyncImage(
                    model = item.photoPath,
                    contentDescription = "Selected Image $index",
                    modifier = Modifier
                        .height(210.dp)
                        .maskClip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            } else {
                Log.w("PhotosShower", "Index $index out of bounds for size ${items.size}")
            }
        }
    }
}


@Composable
@Preview
fun DiaryItem_Preview() {
    ChildGrowthTrackingTheme {
        DiaryItem(DiaryWithPhotos.TempDiaryWithPhotos, diaryItemClick = {})
    }
}


@Composable
@Preview(showBackground = true)
fun GrowthDiaryScreen_Preview() {
    ChildGrowthTrackingTheme {
        GrowthDiaryScreen(
            uiState = GrowthDiaryListUiState.Error("no data"),
            childId = 1,
            navigateBack = {},
            addGrowthDiary = {},
            diaryItemClick = {}
        )

    }
}


