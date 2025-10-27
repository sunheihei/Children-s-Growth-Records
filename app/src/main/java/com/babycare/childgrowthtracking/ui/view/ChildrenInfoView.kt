package com.babycare.childgrowthtracking.ui.view

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babycare.childgrowthtracking.R
import com.babycare.childgrowthtracking.extensions.toast
import com.babycare.childgrowthtracking.extensions.birthdayFormat
import com.babycare.childgrowthtracking.model.Child
import com.babycare.childgrowthtracking.ui.children.ChildUiState
import com.babycare.childgrowthtracking.ui.children.ChildrenViewModel
import com.babycare.childgrowthtracking.ui.common.CommonDatePickerDialog
import com.babycare.childgrowthtracking.ui.theme.ChildGrowthTrackingTheme
import com.babycare.childgrowthtracking.utils.BitmapUtils
import com.babycare.childgrowthtracking.utils.GENDER_BOY
import com.babycare.childgrowthtracking.utils.GENDER_GIRL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.util.Date


@SuppressLint("SimpleDateFormat", "ContextCastToActivity")
@Composable
fun ChildrenInfoView(
    childId: Int = -1,
    childrenViewModel: ChildrenViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    checkClick: () -> Unit
) {
    val context = (LocalContext.current as ComponentActivity)
    context.lifecycle.addObserver(childrenViewModel)
    val uiState by childrenViewModel.uiStateChild.collectAsState()

    // 编辑模式标志
    var isEdit by remember { mutableStateOf(childId != -1) }

    // 头像选择器
    var avatarBase64String by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        avatarBase64String = BitmapUtils.uriToCompressedBase64(context, uri)!!
                    } catch (e: Exception) {
                        Log.e("PhotoPicker", "Error processing image: ${e.message}")
                    }
                }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    // 性别选择
    val options = listOf(stringResource(R.string.boy), stringResource(R.string.girl))
    var selectedIndex by remember { mutableIntStateOf(0) }

    // 日期选择
    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(Date().time) }
    var dateFieldValue by remember { mutableStateOf(TextFieldValue(selectedDate.birthdayFormat())) }

    // 输入字段
    var nameFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var noteFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    // 加载数据（编辑模式）
    LaunchedEffect(childId) {
        if (childId != -1) {
            isEdit = true
            childrenViewModel.getChildById(childId)
        }
    }

    // 同步数据到输入字段（仅在编辑模式下）
    LaunchedEffect(uiState) {
        if (isEdit && uiState is ChildUiState.Success) {
            val child = (uiState as ChildUiState.Success).child
            avatarBase64String = child.avatar
            selectedIndex = child.gender
            nameFieldValue = TextFieldValue(text = child.name)
            selectedDate = child.age
            dateFieldValue = TextFieldValue(text = child.age.birthdayFormat())
            noteFieldValue = TextFieldValue(text = child.note)
        }
    }

    ChildrenInfoScreen(
        uiState = uiState,
        isEdit = isEdit,
        pickMedia = pickMedia,
        avatarBase64String = avatarBase64String,
        options = options,
        optionSelectedIndex = selectedIndex,
        optionOnClick = { optionIndex -> selectedIndex = optionIndex },
        dateFieldValue = dateFieldValue,
        dateOnValueChange = { dateFieldValue = it },
        showDialog = showDialog,
        showDateDialogOnClick = { showDialog = true },
        selectedDate = selectedDate,
        selectDialogConform = { millis ->
            selectedDate = millis
            dateFieldValue = TextFieldValue(millis.birthdayFormat())
            showDialog = false
        },
        selectDialogCancel = { showDialog = false },
        nameFieldValue = nameFieldValue,
        nameFieldValueOnChange = { nameFieldValue = it },
        noteFieldValue = noteFieldValue,
        noteFieldValueOnChange = { noteFieldValue = it },
        navigateBack = navigateBack,
        saveChild = {
            val avatar = avatarBase64String.ifEmpty {
                BitmapUtils.drawableToCompressedBase64(context, R.drawable.logo)!!
            }
            val name = nameFieldValue.text
            val gender = if (selectedIndex == 0) GENDER_BOY else GENDER_GIRL
            val age = selectedDate
            val note = noteFieldValue.text

            if (name.isNotEmpty()) {
                childrenViewModel.saveOrUpdateChild(
                    id = childId,
                    name = nameFieldValue.text,
                    gender = gender,
                    avatar = avatar,
                    age = age,
                    note = note,
                    onSuccess = {
                        if (isEdit) {
                            context.getString(R.string.edit_children_info_success).toast(context)
                        } else {
                            context.getString(R.string.added_children_success).toast(context)
                        }
                        checkClick.invoke()
                    },
                    onError = {}
                )
            } else {
                context.getString(R.string.please_enter_the_child_s_name).toast(context)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildrenInfoScreen(
    uiState: ChildUiState,
    isEdit: Boolean,
    pickMedia: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>?,
    avatarBase64String: String,
    options: List<String>,
    optionSelectedIndex: Int,
    optionOnClick: (index: Int) -> Unit,
    dateFieldValue: TextFieldValue,
    dateOnValueChange: (value: TextFieldValue) -> Unit,
    showDialog: Boolean,
    showDateDialogOnClick: () -> Unit,
    selectedDate: Long,
    selectDialogConform: (millis: Long) -> Unit,
    selectDialogCancel: () -> Unit,
    nameFieldValue: TextFieldValue,
    nameFieldValueOnChange: (value: TextFieldValue) -> Unit,
    noteFieldValue: TextFieldValue,
    noteFieldValueOnChange: (value: TextFieldValue) -> Unit,
    navigateBack: () -> Unit,
    saveChild: () -> Unit
) {
    if (!isEdit) {
        // 添加模式：直接显示编辑界面
        ChildrenInfoEditForm(
            pickMedia = pickMedia,
            avatarBase64String = avatarBase64String,
            options = options,
            optionSelectedIndex = optionSelectedIndex,
            optionOnClick = optionOnClick,
            dateFieldValue = dateFieldValue,
            dateOnValueChange = dateOnValueChange,
            showDialog = showDialog,
            showDateDialogOnClick = showDateDialogOnClick,
            selectedDate = selectedDate,
            selectDialogConform = selectDialogConform,
            selectDialogCancel = selectDialogCancel,
            nameFieldValue = nameFieldValue,
            nameFieldValueOnChange = nameFieldValueOnChange,
            noteFieldValue = noteFieldValue,
            noteFieldValueOnChange = noteFieldValueOnChange,
            isEdit = isEdit,
            navigateBack = navigateBack,
            saveChild = saveChild
        )
    } else {
        // 编辑模式：根据 uiState 显示不同 UI
        when (uiState) {
            is ChildUiState.Loading -> {

            }

            is ChildUiState.Error -> {

            }

            is ChildUiState.Success -> {
                ChildrenInfoEditForm(
                    pickMedia = pickMedia,
                    avatarBase64String = avatarBase64String,
                    options = options,
                    optionSelectedIndex = optionSelectedIndex,
                    optionOnClick = optionOnClick,
                    dateFieldValue = dateFieldValue,
                    dateOnValueChange = dateOnValueChange,
                    showDialog = showDialog,
                    showDateDialogOnClick = showDateDialogOnClick,
                    selectedDate = selectedDate,
                    selectDialogConform = selectDialogConform,
                    selectDialogCancel = selectDialogCancel,
                    nameFieldValue = nameFieldValue,
                    nameFieldValueOnChange = nameFieldValueOnChange,
                    noteFieldValue = noteFieldValue,
                    noteFieldValueOnChange = noteFieldValueOnChange,
                    isEdit = isEdit,
                    navigateBack = navigateBack,
                    saveChild = saveChild
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChildrenInfoEditForm(
    pickMedia: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>?,
    avatarBase64String: String,
    options: List<String>,
    optionSelectedIndex: Int,
    optionOnClick: (index: Int) -> Unit,
    dateFieldValue: TextFieldValue,
    dateOnValueChange: (value: TextFieldValue) -> Unit,
    showDialog: Boolean,
    showDateDialogOnClick: () -> Unit,
    selectedDate: Long,
    selectDialogConform: (millis: Long) -> Unit,
    selectDialogCancel: () -> Unit,
    nameFieldValue: TextFieldValue,
    nameFieldValueOnChange: (value: TextFieldValue) -> Unit,
    noteFieldValue: TextFieldValue,
    noteFieldValueOnChange: (value: TextFieldValue) -> Unit,
    isEdit: Boolean,
    navigateBack: () -> Unit,
    saveChild: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        if (isEdit) stringResource(R.string.edit) else stringResource(R.string.add_children),
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
                    IconButton(onClick = saveChild) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Confirm"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        val avatarBitmap by remember(avatarBase64String) {
            mutableStateOf(BitmapUtils.base64ToBitmap(avatarBase64String))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.weight(0.3f))
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(MaterialShapes.Cookie7Sided.toShape())
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .clickable {
                        pickMedia?.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (avatarBase64String.isEmpty()) {
                    Icon(
                        imageVector = Icons.Default.CameraEnhance,
                        contentDescription = "Select Image",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Image(
                        bitmap = avatarBitmap!!.asImageBitmap(),
                        contentDescription = "Selected Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }
            SingleChoiceSegmentedButtonRow {
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        onClick = { optionOnClick.invoke(index) },
                        selected = index == optionSelectedIndex,
                        label = { Text(label) }
                    )
                }
            }
            OutlinedTextField(
                value = dateFieldValue,
                onValueChange = dateOnValueChange,
                label = {
                    Text(
                        stringResource(R.string.birthday),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.CalendarMonth,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { showDateDialogOnClick() },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            if (showDialog) {
                CommonDatePickerDialog(
                    selectedDate,
                    confirmButton = selectDialogConform,
                    cancelButtonClick = selectDialogCancel,
                    onDismissRequest = { selectDialogCancel() }
                )
            }
            OutlinedTextField(
                value = nameFieldValue,
                onValueChange = nameFieldValueOnChange,
                label = {
                    Text(
                        stringResource(R.string.name),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.ChildCare,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = noteFieldValue,
                onValueChange = noteFieldValueOnChange,
                label = {
                    Text(
                        stringResource(R.string.note),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}


@Composable
@Preview
fun AddChildrenView_Preview() {
    ChildGrowthTrackingTheme {
        ChildrenInfoScreen(
            uiState = ChildUiState.Loading, // 添加模式不依赖此状态
            isEdit = false,
            pickMedia = null, // 预览中不需要实际的图片选择器
            avatarBase64String = "",
            options = listOf("Boy", "Girl"), // 模拟 stringResource
            optionSelectedIndex = 0,
            optionOnClick = {},
            dateFieldValue = TextFieldValue("2025-04-11"),
            dateOnValueChange = {},
            showDialog = false,
            showDateDialogOnClick = {},
            selectedDate = Date().time,
            selectDialogConform = {},
            selectDialogCancel = {},
            nameFieldValue = TextFieldValue(""),
            nameFieldValueOnChange = {},
            noteFieldValue = TextFieldValue(""),
            noteFieldValueOnChange = {},
            navigateBack = {},
            saveChild = {}
        )
    }
}
