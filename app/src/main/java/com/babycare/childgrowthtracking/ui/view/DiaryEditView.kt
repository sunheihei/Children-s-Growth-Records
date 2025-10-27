package com.babycare.childgrowthtracking.ui.view

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.babycare.childgrowthtracking.R
import com.babycare.childgrowthtracking.extensions.birthdayFormat
import com.babycare.childgrowthtracking.model.Child
import com.babycare.childgrowthtracking.model.DiaryPhoto
import com.babycare.childgrowthtracking.model.DiaryWithPhotos
import com.babycare.childgrowthtracking.model.GrowthDiary
import com.babycare.childgrowthtracking.ui.children.ChildUiState
import com.babycare.childgrowthtracking.ui.children.ChildrenViewModel
import com.babycare.childgrowthtracking.ui.common.CommonAlertDialog
import com.babycare.childgrowthtracking.ui.common.CommonDatePickerDialog
import com.babycare.childgrowthtracking.ui.common.ErrorView
import com.babycare.childgrowthtracking.ui.common.LoadingDialog
import com.babycare.childgrowthtracking.ui.common.LoadingView
import com.babycare.childgrowthtracking.ui.growthdiary.CombinedDiaryUiState
import com.babycare.childgrowthtracking.ui.growthdiary.GrowthDiaryUiState
import com.babycare.childgrowthtracking.ui.growthdiary.GrowthDiaryViewModel
import com.babycare.childgrowthtracking.ui.theme.ChildGrowthTrackingTheme
import com.babycare.childgrowthtracking.utils.DIARYMODE_ADD
import com.babycare.childgrowthtracking.utils.DIARYMODE_EDIT
import com.babycare.childgrowthtracking.utils.DIARYMODE_VIEW
import java.util.Date
import kotlin.math.log


@Composable
fun DiaryEditView(
    id: Int = -1,
    childId: Int = -1,
    navigateBack: () -> Unit,
    onOperateSuccess: () -> Unit,
    growthDiaryViewModel: GrowthDiaryViewModel = hiltViewModel(),
    childrenViewModel: ChildrenViewModel = hiltViewModel()
) {
    val growthDiaryUiState by growthDiaryViewModel.uiStateGrowthDiary.collectAsState()
    val childUiState by childrenViewModel.uiStateChild.collectAsState()

    var statusMode by remember { mutableStateOf(if (id == -1) DIARYMODE_ADD else DIARYMODE_VIEW) }
    var showProgressDialog by remember { mutableStateOf(false) }

    val imageList = remember { mutableStateListOf<DiaryPhoto>() }
    val addImageListForEdit = remember { mutableStateListOf<DiaryPhoto>() }
    val deleteImageListForEdit = remember { mutableStateListOf<DiaryPhoto>() }
    var selectedDate by remember { mutableStateOf(Date().time) }
    var diaryFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    // 加载孩子数据
    LaunchedEffect(childId) {
        if (childId != -1) {
            childrenViewModel.getChildById(childId)
        }
    }

    // 加载日记数据（编辑模式）
    LaunchedEffect(id) {
        if (id != -1) {
            // Edit mode
            growthDiaryViewModel.getDiaryWithPhotosById(id)
        }
    }

    // 当处于 Success 状态时，同步数据到 imageList、selectedDate 和 diaryFieldValue
    LaunchedEffect(growthDiaryUiState) {
        if (growthDiaryUiState is GrowthDiaryUiState.Success) {
            val diaryWithPhotos = (growthDiaryUiState as GrowthDiaryUiState.Success).growthDiary
            imageList.clear()
            imageList.addAll(diaryWithPhotos.photos)
            selectedDate = diaryWithPhotos.diary.date
            diaryFieldValue = TextFieldValue(text = diaryWithPhotos.diary.content)
        }
    }

    val pickMultipleMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(
            if (9 - imageList.size > 1) 9 - imageList.size else 2
        )
    ) { uriList ->
        if (uriList.isNotEmpty()) {
            val tempList = mutableListOf<DiaryPhoto>()
            uriList.forEach { uri ->
                val diaryPhoto = DiaryPhoto(diaryId = id, photoPath = uri.toString())
                tempList.add(diaryPhoto)
            }
            imageList.addAll(tempList)
            if (statusMode == DIARYMODE_EDIT) {
                addImageListForEdit.addAll(tempList)
            }
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    DiaryEditScreen(
        growthDiaryUiState = growthDiaryUiState,
        childUiState = childUiState,
        statusMode = statusMode,
        selectedDate = selectedDate,
        diaryFieldValue = diaryFieldValue,
        showProgressDialog = showProgressDialog,
        deletePhotosEdit = deleteImageListForEdit,
        imageList = imageList,
        navigateBack = navigateBack,
        onAddPhotoClicked = {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        dateSelect = { selectedDate = it },
        diaryTextFieldValueChange = { diaryFieldValue = it },
        deleteDiary = {
            if (growthDiaryUiState is GrowthDiaryUiState.Success) {
                val diary = (growthDiaryUiState as GrowthDiaryUiState.Success).growthDiary.diary
                growthDiaryViewModel.deleteGrowthDiary(diary)
                onOperateSuccess()
            }
        },
        onOperate = {
            if (statusMode == DIARYMODE_EDIT) {
                if (growthDiaryUiState is GrowthDiaryUiState.Success) {
                    val diaryWithPhotos =
                        (growthDiaryUiState as GrowthDiaryUiState.Success).growthDiary
                    diaryWithPhotos.diary.content = diaryFieldValue.text
                    diaryWithPhotos.diary.date = selectedDate
                    growthDiaryViewModel.updateDiaryWithPhotos(
                        diary = diaryWithPhotos.diary,
                        addImageListForEdit,
                        deleteImageListForEdit,
                        onSuccess = {
                            showProgressDialog = false
                            onOperateSuccess()
                        }
                    )
                    showProgressDialog = true
                }
            } else if (statusMode == DIARYMODE_ADD) {
                val diary = GrowthDiary(
                    childId = childId,
                    title = "",
                    content = diaryFieldValue.text,
                    date = selectedDate
                )
                growthDiaryViewModel.insertDiaryWithPhotos(diary, imageList, onSuccess = {
                    showProgressDialog = false
                    onOperateSuccess()
                })
                showProgressDialog = true
            }
        },
        updateStatusMode = { newMode -> statusMode = newMode }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEditScreen(
    growthDiaryUiState: GrowthDiaryUiState,
    childUiState: ChildUiState,
    statusMode: Int,
    selectedDate: Long,
    diaryFieldValue: TextFieldValue,
    showProgressDialog: Boolean,
    deletePhotosEdit: MutableList<DiaryPhoto>,
    imageList: MutableList<DiaryPhoto>,
    navigateBack: () -> Unit,
    onAddPhotoClicked: () -> Unit,
    dateSelect: (millis: Long) -> Unit,
    diaryTextFieldValueChange: (text: TextFieldValue) -> Unit,
    deleteDiary: () -> Unit,
    onOperate: () -> Unit,
    updateStatusMode: (Int) -> Unit
) {
    // 合并 growthDiaryUiState 和 childUiState，确定整体 UI 状态
    val uiState = when {
        // 添加模式：如果 childId 无效（childUiState 不是 Success），使用默认 Child
        statusMode == DIARYMODE_ADD && childUiState !is ChildUiState.Success -> {
            CombinedDiaryUiState.Success(
                child = Child.TempChild, // 使用默认 Child
                diaryWithPhotos = DiaryWithPhotos.TempDiaryWithPhotos
            )
        }
        // 添加模式：如果 childUiState 是 Success
        statusMode == DIARYMODE_ADD && childUiState is ChildUiState.Success -> {
            CombinedDiaryUiState.Success(
                child = (childUiState as ChildUiState.Success).child,
                diaryWithPhotos = DiaryWithPhotos.TempDiaryWithPhotos
            )
        }
        // 正常合并逻辑
        growthDiaryUiState is GrowthDiaryUiState.Loading || childUiState is ChildUiState.Loading -> {
            CombinedDiaryUiState.Loading
        }

        growthDiaryUiState is GrowthDiaryUiState.Error || childUiState is ChildUiState.Error -> {
            CombinedDiaryUiState.Error
        }

        growthDiaryUiState is GrowthDiaryUiState.Success && childUiState is ChildUiState.Success -> {
            CombinedDiaryUiState.Success(
                child = (childUiState as ChildUiState.Success).child,
                diaryWithPhotos = (growthDiaryUiState as GrowthDiaryUiState.Success).growthDiary
            )
        }

        else -> CombinedDiaryUiState.Loading
    }

    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedPhotoIndex by remember { mutableIntStateOf(-1) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        when (statusMode) {
                            DIARYMODE_EDIT -> stringResource(R.string.edit_growth_diary)
                            DIARYMODE_ADD -> stringResource(R.string.add_growth_diary)
                            else -> stringResource(R.string.growth_diary)
                        },
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
                    if (statusMode == DIARYMODE_VIEW || statusMode == DIARYMODE_EDIT) {
                        IconButton(onClick = {
                            showDeleteDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.DeleteSweep,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "Delete Diary"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        DiaryEditContent(
            uiState = uiState,
            statusMode = statusMode,
            selectedDate = selectedDate,
            diaryFieldValue = diaryFieldValue,
            showProgressDialog = showProgressDialog,
            showDialog = showDialog,
            showDeleteDialog = showDeleteDialog,
            selectedPhotoIndex = selectedPhotoIndex,
            deletePhotosEdit = deletePhotosEdit,
            imageList = imageList,
            onAddPhotoClicked = onAddPhotoClicked,
            dateSelect = dateSelect,
            diaryTextFieldValueChange = diaryTextFieldValueChange,
            deleteDiary = deleteDiary,
            onOperate = onOperate,
            updateShowDialog = { showDialog = it },
            updateShowDeleteDialog = { showDeleteDialog = it },
            updateSelectedPhotoIndex = { selectedPhotoIndex = it },
            updateStatusMode = updateStatusMode,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun DiaryEditContent(
    uiState: CombinedDiaryUiState,
    statusMode: Int,
    selectedDate: Long,
    diaryFieldValue: TextFieldValue,
    showProgressDialog: Boolean,
    showDialog: Boolean,
    showDeleteDialog: Boolean,
    selectedPhotoIndex: Int,
    deletePhotosEdit: MutableList<DiaryPhoto>,
    imageList: MutableList<DiaryPhoto>,
    onAddPhotoClicked: () -> Unit,
    dateSelect: (millis: Long) -> Unit,
    diaryTextFieldValueChange: (text: TextFieldValue) -> Unit,
    deleteDiary: () -> Unit,
    onOperate: () -> Unit,
    updateShowDialog: (Boolean) -> Unit,
    updateShowDeleteDialog: (Boolean) -> Unit,
    updateSelectedPhotoIndex: (Int) -> Unit,
    updateStatusMode: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is CombinedDiaryUiState.Loading -> {
            Log.d("CombinedDiaryUiState", "CombinedDiaryUiState.Loading")
            LoadingView()
        }

        is CombinedDiaryUiState.Error -> {
            Log.d("CombinedDiaryUiState", "CombinedDiaryUiState.Error")
            ErrorView()
        }

        is CombinedDiaryUiState.Success -> {
            Log.d("CombinedDiaryUiState", "CombinedDiaryUiState.Success")
            val scrollState = rememberScrollState()
            Box(
                modifier = modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(scrollState)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = selectedDate.birthdayFormat(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .clickable {
                                        if (statusMode == DIARYMODE_ADD || statusMode == DIARYMODE_EDIT) {
                                            updateShowDialog(true)
                                        }
                                    }
                            )
                            if (statusMode == DIARYMODE_ADD || statusMode == DIARYMODE_EDIT) {
                                Icon(
                                    Icons.Rounded.Edit,
                                    "Edit",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            if (showDialog) {
                                CommonDatePickerDialog(
                                    selectedDate,
                                    confirmButton = { millis ->
                                        dateSelect.invoke(millis)
                                        updateShowDialog(false)
                                    },
                                    cancelButtonClick = {
                                        updateShowDialog(false)
                                    },
                                    onDismissRequest = {}
                                )
                            }
                        }

                        OutlinedTextField(
                            value = diaryFieldValue,
                            onValueChange = { text -> diaryTextFieldValueChange.invoke(text) },
                            label = {
                                if (statusMode == DIARYMODE_ADD || statusMode == DIARYMODE_EDIT) {
                                    Text(
                                        stringResource(R.string.write_something),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            },
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif
                            ),
                            readOnly = statusMode == DIARYMODE_VIEW,
                            enabled = (statusMode == DIARYMODE_ADD || statusMode == DIARYMODE_EDIT),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 200.dp)
                                .padding(horizontal = 16.dp),
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .padding(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                        ) {
                            if (imageList.isNotEmpty()) {
                                SquarePhotoGrid(
                                    statusMode,
                                    imageList,
                                    onAddPhotoClicked = onAddPhotoClicked,
                                    onDeletePhotoClick = { photo -> deletePhotosEdit.add(photo) },
                                    onShowPhotoViewer = { index: Int ->
                                        updateSelectedPhotoIndex(
                                            index
                                        )
                                    }
                                )
                            } else {
                                if (statusMode == DIARYMODE_ADD || statusMode == DIARYMODE_EDIT) {
                                    NoDiaryPhotos(onAddPhotoClicked = onAddPhotoClicked)
                                }
                            }
                        }
                        if (showProgressDialog) {
                            LoadingDialog(onDismissRequest = {})
                        }
                        if (showDeleteDialog) {
                            CommonAlertDialog(
                                dialogTitle = "Delete Diary",
                                dialogText = stringResource(R.string.are_you_sure_you_want_to_delete_this_diary),
                                confirm = stringResource(R.string.ok),
                                dismiss = stringResource(R.string.cancel),
                                onConfirmation = {
                                    deleteDiary()
                                    updateShowDeleteDialog(false)
                                },
                                onDismissRequest = { updateShowDeleteDialog(false) }
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        Button(
                            onClick = {
                                when (statusMode) {
                                    DIARYMODE_VIEW -> {
                                        updateStatusMode(DIARYMODE_EDIT)
                                    }

                                    DIARYMODE_EDIT, DIARYMODE_ADD -> {
                                        onOperate()
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .align(Alignment.Center)
                        ) {
                            Text(
                                text = when (statusMode) {
                                    DIARYMODE_VIEW -> stringResource(R.string.edit_diary)
                                    DIARYMODE_EDIT -> stringResource(R.string.update_diary)
                                    else -> stringResource(R.string.add_diary)
                                },
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = selectedPhotoIndex != -1,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    PhotoViewerScreen(
                        imageList,
                        selectedPhotoIndex,
                        onDismiss = { updateSelectedPhotoIndex(-1) }
                    )
                }
            }
        }
    }
}


@Composable
fun SquarePhotoGrid(
    statusMode: Int,
    items: MutableList<DiaryPhoto>,
    onAddPhotoClicked: () -> Unit,
    onDeletePhotoClick: (photo: DiaryPhoto) -> Unit,
    onShowPhotoViewer: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // 使用 derivedStateOf 降低不必要重组
    val displayItems by remember(items) {
        derivedStateOf { items.take(9) }
    }
    val showAddButton by remember(items) {
        derivedStateOf { items.size < 9 }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // 每行 3 个
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 显示已有图片
        itemsIndexed(displayItems) { index, item ->
            Box(modifier = Modifier.clip(RoundedCornerShape(4.dp))) {
                AsyncImage(
                    model = item.photoPath,
                    contentDescription = "Photo ${item.id}",
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable {
                            if (statusMode == DIARYMODE_VIEW) {
                                onShowPhotoViewer(index)
                            }
                        },
                    contentScale = ContentScale.Crop
                )
                if (statusMode == DIARYMODE_EDIT) {
                    Icon(
                        Icons.Rounded.Cancel,
                        contentDescription = "delete photos",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(
                                Alignment.TopEnd
                            )
                            .padding(6.dp)
                            .clickable {
                                onDeletePhotoClick.invoke(item)
                                items.removeAt(index)
                            })
                }
            }
        }

        if ((showAddButton && statusMode == DIARYMODE_EDIT) || statusMode == DIARYMODE_ADD) {
            item {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f) // 正方形
                        .clip(RoundedCornerShape(4.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                        .clickable { onAddPhotoClicked() }, contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add Photo",
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Add Photo",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoDiaryPhotos(onAddPhotoClicked: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .clickable {
                onAddPhotoClicked()
            }) {
        Icon(
            painter = painterResource(R.drawable.add_photo),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Rounded.Add,
                contentDescription = "Add Photos",
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                "Add Photo",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun PhotoViewerScreen(
    items: MutableList<DiaryPhoto>,
    initialIndex: Int, // 初始显示的图片索引
    onDismiss: () -> Unit // 关闭浏览器回调
) {
    // 使用 ViewPager 实现滑动
    val pagerState = rememberPagerState(initialPage = initialIndex) { items.size }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.align(Alignment.Center)) { page ->
            val photo = items[page]
            AsyncImage(
                model = photo.photoPath,
                contentDescription = "Photo ${photo.id}",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onDismiss() },
            )
        }
        Text(
            text = "${pagerState.currentPage + 1}/${items.size}",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(8.dp)
        )
    }
}


@Composable
@Preview
fun DiaryEditScreen_PreView() {
    ChildGrowthTrackingTheme {
        DiaryEditScreen(
            GrowthDiaryUiState.Loading,
            ChildUiState.Loading,
            DIARYMODE_ADD,
            Date().time,
            TextFieldValue("日记查看测试"),
            false,
            mutableListOf(),
            mutableListOf(),
            navigateBack = {},
            onAddPhotoClicked = {},
            dateSelect = {},
            diaryTextFieldValueChange = {},
            deleteDiary = {},
            onOperate = {},
            updateStatusMode = {})
    }


}

