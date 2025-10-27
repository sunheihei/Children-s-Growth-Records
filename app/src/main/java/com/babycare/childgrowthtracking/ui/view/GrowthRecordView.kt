package com.babycare.childgrowthtracking.ui.view

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Button
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babycare.childgrowthtracking.R
import com.babycare.childgrowthtracking.datastore.DataStoreViewModel
import com.babycare.childgrowthtracking.datastore.UnitsUiState
import com.babycare.childgrowthtracking.extensions.birthdayFormat
import com.babycare.childgrowthtracking.model.GrowthRecord
import com.babycare.childgrowthtracking.ui.common.CommonDatePickerDialog
import com.babycare.childgrowthtracking.ui.growthrecord.GrowthRecordUiState
import com.babycare.childgrowthtracking.ui.growthrecord.GrowthRecordViewModel
import com.babycare.childgrowthtracking.ui.theme.ChildGrowthTrackingTheme
import com.babycare.childgrowthtracking.utils.HEAD_UNIT_CM
import com.babycare.childgrowthtracking.utils.HEAD_UNIT_IN
import com.babycare.childgrowthtracking.utils.HEIGHT_UNIT_CM
import com.babycare.childgrowthtracking.utils.HEIGHT_UNIT_FT_IN
import com.babycare.childgrowthtracking.utils.WEIGHT_UNIT_KG
import com.babycare.childgrowthtracking.utils.WEIGHT_UNIT_LB
import java.util.Date

@SuppressLint("DefaultLocale")
@Composable
fun GrowthRecordView(
    id: Int = -1,
    childId: Int = -1,
    navigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    growthRecordViewModel: GrowthRecordViewModel = hiltViewModel(),
    dataStoreViewModel: DataStoreViewModel = hiltViewModel()
) {
    val growthRecordUiState by growthRecordViewModel.uiStateGrowthRecord.collectAsState()
    val units by dataStoreViewModel.unitsUiState.collectAsState()

    var editMode by remember { mutableStateOf(id != -1) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(Date().time) }
    var dateFieldValue by remember { mutableStateOf(TextFieldValue(selectedDate.birthdayFormat())) }
    var showHeightUnitMenu by remember { mutableStateOf(false) }
    var showWeightUnitMenu by remember { mutableStateOf(false) }
    var showHeadUnitMenu by remember { mutableStateOf(false) }

    var heightCmInPut by remember { mutableStateOf("") }
    var heightFTInPut by remember { mutableStateOf("") }
    var heightINInPut by remember { mutableStateOf("") }
    var weightKgInPut by remember { mutableStateOf("") }
    var weightLbInPut by remember { mutableStateOf("") }
    var headCmInput by remember { mutableStateOf("") }
    var headInInput by remember { mutableStateOf("") }
    var noteFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    // 加载数据（编辑模式）
    LaunchedEffect(id) {
        if (id != -1) {
            growthRecordViewModel.getGrowRecord(id)
        }
    }

    // 同步数据到输入字段（仅在编辑模式下）
    LaunchedEffect(growthRecordUiState) {
        if (editMode && growthRecordUiState is GrowthRecordUiState.Success) {
            val record = (growthRecordUiState as GrowthRecordUiState.Success).growthRecord
            selectedDate = record.date
            dateFieldValue = TextFieldValue(text = record.date.birthdayFormat())
            heightCmInPut = record.heightCm
            heightFTInPut = record.heightFt
            heightINInPut = record.heightIn
            weightKgInPut = record.weightKg
            weightLbInPut = record.weightLb
            headCmInput = record.headCircleCm
            headInInput = record.headCircleIn
            noteFieldValue = TextFieldValue(text = record.note)
        }
    }

    GrowthRecordScreen(
        growthRecordUiState = growthRecordUiState,
        editMode = editMode,
        childId = childId,
        unitsUiState = units,
        showDatePicker = showDatePicker,
        selectedDate = selectedDate,
        dateFieldValue = dateFieldValue,
        showHeightUnitMenu = showHeightUnitMenu,
        showWeightUnitMenu = showWeightUnitMenu,
        showHeadUnitMenu = showHeadUnitMenu,
        heightCmInPut = heightCmInPut,
        heightFTInPut = heightFTInPut,
        heightINInPut = heightINInPut,
        weightKgInPut = weightKgInPut,
        weightLbInPut = weightLbInPut,
        headCmInput = headCmInput,
        headInInput = headInInput,
        noteFieldValue = noteFieldValue,
        navigateBack = navigateBack,
        updateShowDatePicker = { showDatePicker = it },
        updateSelectedDate = { selectedDate = it },
        updateDateFieldValue = { dateFieldValue = it },
        updateShowHeightUnitMenu = { showHeightUnitMenu = it },
        updateShowWeightUnitMenu = { showWeightUnitMenu = it },
        updateShowHeadUnitMenu = { showHeadUnitMenu = it },
        updateHeightCmInPut = { heightCmInPut = it },
        updateHeightFTInPut = { heightFTInPut = it },
        updateHeightINInPut = { heightINInPut = it },
        updateWeightKgInPut = { weightKgInPut = it },
        updateWeightLbInPut = { weightLbInPut = it },
        updateHeadCmInput = { headCmInput = it },
        updateHeadInInput = { headInInput = it },
        updateNoteFieldValue = { noteFieldValue = it },
        onSaveSuccess = onSaveSuccess,
        deleteGrowthRecord = { record ->
            growthRecordViewModel.deleteGrowthRecord(record)
            onSaveSuccess()
        },
        saveHeightUnit = { unit ->
            dataStoreViewModel.saveHeightUnit(unit)
        },
        saveWeightUnit = { unit ->
            dataStoreViewModel.saveWeightUnit(unit)
        },
        saveHeadUnit = { unit ->
            dataStoreViewModel.saveHeadUnit(unit)
        },
        saveGrowthRecord = { record ->
            if (editMode) {
                growthRecordViewModel.updateGrowthRecord(record)
            } else {
                growthRecordViewModel.addGrowthRecord(record)
            }
            onSaveSuccess()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthRecordScreen(
    growthRecordUiState: GrowthRecordUiState,
    editMode: Boolean,
    childId: Int,
    unitsUiState: UnitsUiState,
    showDatePicker: Boolean,
    selectedDate: Long,
    dateFieldValue: TextFieldValue,
    showHeightUnitMenu: Boolean,
    showWeightUnitMenu: Boolean,
    showHeadUnitMenu: Boolean,
    heightCmInPut: String,
    heightFTInPut: String,
    heightINInPut: String,
    weightKgInPut: String,
    weightLbInPut: String,
    headCmInput: String,
    headInInput: String,
    noteFieldValue: TextFieldValue,
    navigateBack: () -> Unit,
    updateShowDatePicker: (Boolean) -> Unit,
    updateSelectedDate: (Long) -> Unit,
    updateDateFieldValue: (TextFieldValue) -> Unit,
    updateShowHeightUnitMenu: (Boolean) -> Unit,
    updateShowWeightUnitMenu: (Boolean) -> Unit,
    updateShowHeadUnitMenu: (Boolean) -> Unit,
    updateHeightCmInPut: (String) -> Unit,
    updateHeightFTInPut: (String) -> Unit,
    updateHeightINInPut: (String) -> Unit,
    updateWeightKgInPut: (String) -> Unit,
    updateWeightLbInPut: (String) -> Unit,
    updateHeadCmInput: (String) -> Unit,
    updateHeadInInput: (String) -> Unit,
    updateNoteFieldValue: (TextFieldValue) -> Unit,
    onSaveSuccess: () -> Unit,
    deleteGrowthRecord: (GrowthRecord) -> Unit,
    saveHeightUnit: (String) -> Unit,
    saveWeightUnit: (String) -> Unit,
    saveHeadUnit: (String) -> Unit,
    saveGrowthRecord: (GrowthRecord) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        if (editMode) {
                            stringResource(R.string.update_growth_record)
                        } else {
                            stringResource(R.string.add_growth_data)
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
                    if (editMode) {
                        IconButton(onClick = {
                            if (growthRecordUiState is GrowthRecordUiState.Success) {
                                val record =
                                    (growthRecordUiState as GrowthRecordUiState.Success).growthRecord
                                deleteGrowthRecord(record)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.DeleteSweep,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "Delete Record"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        GrowthRecordContent(
            growthRecordUiState = growthRecordUiState,
            editMode = editMode,
            childId = childId,
            unitsUiState = unitsUiState,
            showDatePicker = showDatePicker,
            selectedDate = selectedDate,
            dateFieldValue = dateFieldValue,
            showHeightUnitMenu = showHeightUnitMenu,
            showWeightUnitMenu = showWeightUnitMenu,
            showHeadUnitMenu = showHeadUnitMenu,
            heightCmInPut = heightCmInPut,
            heightFTInPut = heightFTInPut,
            heightINInPut = heightINInPut,
            weightKgInPut = weightKgInPut,
            weightLbInPut = weightLbInPut,
            headCmInput = headCmInput,
            headInInput = headInInput,
            noteFieldValue = noteFieldValue,
            updateShowDatePicker = updateShowDatePicker,
            updateSelectedDate = updateSelectedDate,
            updateDateFieldValue = updateDateFieldValue,
            updateShowHeightUnitMenu = updateShowHeightUnitMenu,
            updateShowWeightUnitMenu = updateShowWeightUnitMenu,
            updateShowHeadUnitMenu = updateShowHeadUnitMenu,
            updateHeightCmInPut = updateHeightCmInPut,
            updateHeightFTInPut = updateHeightFTInPut,
            updateHeightINInPut = updateHeightINInPut,
            updateWeightKgInPut = updateWeightKgInPut,
            updateWeightLbInPut = updateWeightLbInPut,
            updateHeadCmInput = updateHeadCmInput,
            updateHeadInInput = updateHeadInInput,
            updateNoteFieldValue = updateNoteFieldValue,
            saveHeightUnit = saveHeightUnit,
            saveWeightUnit = saveWeightUnit,
            saveHeadUnit = saveHeadUnit,
            saveGrowthRecord = saveGrowthRecord,
            modifier = Modifier.padding(padding)
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun GrowthRecordContent(
    growthRecordUiState: GrowthRecordUiState,
    editMode: Boolean,
    childId: Int,
    unitsUiState: UnitsUiState,
    showDatePicker: Boolean,
    selectedDate: Long,
    dateFieldValue: TextFieldValue,
    showHeightUnitMenu: Boolean,
    showWeightUnitMenu: Boolean,
    showHeadUnitMenu: Boolean,
    heightCmInPut: String,
    heightFTInPut: String,
    heightINInPut: String,
    weightKgInPut: String,
    weightLbInPut: String,
    headCmInput: String,
    headInInput: String,
    noteFieldValue: TextFieldValue,
    updateShowDatePicker: (Boolean) -> Unit,
    updateSelectedDate: (Long) -> Unit,
    updateDateFieldValue: (TextFieldValue) -> Unit,
    updateShowHeightUnitMenu: (Boolean) -> Unit,
    updateShowWeightUnitMenu: (Boolean) -> Unit,
    updateShowHeadUnitMenu: (Boolean) -> Unit,
    updateHeightCmInPut: (String) -> Unit,
    updateHeightFTInPut: (String) -> Unit,
    updateHeightINInPut: (String) -> Unit,
    updateWeightKgInPut: (String) -> Unit,
    updateWeightLbInPut: (String) -> Unit,
    updateHeadCmInput: (String) -> Unit,
    updateHeadInInput: (String) -> Unit,
    updateNoteFieldValue: (TextFieldValue) -> Unit,
    saveHeightUnit: (String) -> Unit,
    saveWeightUnit: (String) -> Unit,
    saveHeadUnit: (String) -> Unit,
    saveGrowthRecord: (GrowthRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    // 提前计算 childId
    val resolvedChildId = if (editMode && growthRecordUiState is GrowthRecordUiState.Success) {
        (growthRecordUiState as GrowthRecordUiState.Success).growthRecord.childId
    } else {
        childId
    }

    // 获取原始的 GrowthRecord（编辑模式下使用）
    val originalRecord = if (editMode && growthRecordUiState is GrowthRecordUiState.Success) {
        (growthRecordUiState as GrowthRecordUiState.Success).growthRecord
    } else {
        null
    }

    if (!editMode) {
        // 添加模式：直接显示空白编辑界面
        GrowthRecordEditForm(
            childId = resolvedChildId,
            originalRecord = originalRecord,
            unitsUiState = unitsUiState,
            showDatePicker = showDatePicker,
            selectedDate = selectedDate,
            dateFieldValue = dateFieldValue,
            showHeightUnitMenu = showHeightUnitMenu,
            showWeightUnitMenu = showWeightUnitMenu,
            showHeadUnitMenu = showHeadUnitMenu,
            heightCmInPut = heightCmInPut,
            heightFTInPut = heightFTInPut,
            heightINInPut = heightINInPut,
            weightKgInPut = weightKgInPut,
            weightLbInPut = weightLbInPut,
            headCmInput = headCmInput,
            headInInput = headInInput,
            noteFieldValue = noteFieldValue,
            updateShowDatePicker = updateShowDatePicker,
            updateSelectedDate = updateSelectedDate,
            updateDateFieldValue = updateDateFieldValue,
            updateShowHeightUnitMenu = updateShowHeightUnitMenu,
            updateShowWeightUnitMenu = updateShowWeightUnitMenu,
            updateShowHeadUnitMenu = updateShowHeadUnitMenu,
            updateHeightCmInPut = updateHeightCmInPut,
            updateHeightFTInPut = updateHeightFTInPut,
            updateHeightINInPut = updateHeightINInPut,
            updateWeightKgInPut = updateWeightKgInPut,
            updateWeightLbInPut = updateWeightLbInPut,
            updateHeadCmInput = updateHeadCmInput,
            updateHeadInInput = updateHeadInInput,
            updateNoteFieldValue = updateNoteFieldValue,
            saveHeightUnit = saveHeightUnit,
            saveWeightUnit = saveWeightUnit,
            saveHeadUnit = saveHeadUnit,
            saveGrowthRecord = saveGrowthRecord,
            modifier = modifier,
            editMode = editMode
        )
    } else {
        // 编辑模式：根据 growthRecordUiState 显示不同 UI
        when (growthRecordUiState) {
            is GrowthRecordUiState.Loading -> {

            }

            is GrowthRecordUiState.Error -> {

            }

            is GrowthRecordUiState.Success -> {
                GrowthRecordEditForm(
                    childId = resolvedChildId,
                    originalRecord = originalRecord,
                    unitsUiState = unitsUiState,
                    showDatePicker = showDatePicker,
                    selectedDate = selectedDate,
                    dateFieldValue = dateFieldValue,
                    showHeightUnitMenu = showHeightUnitMenu,
                    showWeightUnitMenu = showWeightUnitMenu,
                    showHeadUnitMenu = showHeadUnitMenu,
                    heightCmInPut = heightCmInPut,
                    heightFTInPut = heightFTInPut,
                    heightINInPut = heightINInPut,
                    weightKgInPut = weightKgInPut,
                    weightLbInPut = weightLbInPut,
                    headCmInput = headCmInput,
                    headInInput = headInInput,
                    noteFieldValue = noteFieldValue,
                    updateShowDatePicker = updateShowDatePicker,
                    updateSelectedDate = updateSelectedDate,
                    updateDateFieldValue = updateDateFieldValue,
                    updateShowHeightUnitMenu = updateShowHeightUnitMenu,
                    updateShowWeightUnitMenu = updateShowWeightUnitMenu,
                    updateShowHeadUnitMenu = updateShowHeadUnitMenu,
                    updateHeightCmInPut = updateHeightCmInPut,
                    updateHeightFTInPut = updateHeightFTInPut,
                    updateHeightINInPut = updateHeightINInPut,
                    updateWeightKgInPut = updateWeightKgInPut,
                    updateWeightLbInPut = updateWeightLbInPut,
                    updateHeadCmInput = updateHeadCmInput,
                    updateHeadInInput = updateHeadInInput,
                    updateNoteFieldValue = updateNoteFieldValue,
                    saveHeightUnit = saveHeightUnit,
                    saveWeightUnit = saveWeightUnit,
                    saveHeadUnit = saveHeadUnit,
                    saveGrowthRecord = saveGrowthRecord,
                    modifier = modifier,
                    editMode = editMode
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun GrowthRecordEditForm(
    childId: Int,
    originalRecord: GrowthRecord?, // 添加原始记录参数
    unitsUiState: UnitsUiState,
    showDatePicker: Boolean,
    selectedDate: Long,
    dateFieldValue: TextFieldValue,
    showHeightUnitMenu: Boolean,
    showWeightUnitMenu: Boolean,
    showHeadUnitMenu: Boolean,
    heightCmInPut: String,
    heightFTInPut: String,
    heightINInPut: String,
    weightKgInPut: String,
    weightLbInPut: String,
    headCmInput: String,
    headInInput: String,
    noteFieldValue: TextFieldValue,
    updateShowDatePicker: (Boolean) -> Unit,
    updateSelectedDate: (Long) -> Unit,
    updateDateFieldValue: (TextFieldValue) -> Unit,
    updateShowHeightUnitMenu: (Boolean) -> Unit,
    updateShowWeightUnitMenu: (Boolean) -> Unit,
    updateShowHeadUnitMenu: (Boolean) -> Unit,
    updateHeightCmInPut: (String) -> Unit,
    updateHeightFTInPut: (String) -> Unit,
    updateHeightINInPut: (String) -> Unit,
    updateWeightKgInPut: (String) -> Unit,
    updateWeightLbInPut: (String) -> Unit,
    updateHeadCmInput: (String) -> Unit,
    updateHeadInInput: (String) -> Unit,
    updateNoteFieldValue: (TextFieldValue) -> Unit,
    saveHeightUnit: (String) -> Unit,
    saveWeightUnit: (String) -> Unit,
    saveHeadUnit: (String) -> Unit,
    saveGrowthRecord: (GrowthRecord) -> Unit,
    modifier: Modifier = Modifier,
    editMode: Boolean
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        OutlinedTextField(
            value = dateFieldValue,
            onValueChange = { updateDateFieldValue(it) },
            label = {
                Text(
                    stringResource(R.string.date),
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
                .clickable { updateShowDatePicker(true) },
            enabled = false, // 禁用直接输入，只允许通过日期选择器修改
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        // 日期选择对话框
        if (showDatePicker) {
            CommonDatePickerDialog(
                selectedDate,
                confirmButton = { millis ->
                    updateSelectedDate(millis)
                    updateDateFieldValue(TextFieldValue(millis.birthdayFormat()))
                    updateShowDatePicker(false)
                },
                cancelButtonClick = { updateShowDatePicker(false) },
                onDismissRequest = { updateShowDatePicker(false) }
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (unitsUiState.heightUnit) {
                HEIGHT_UNIT_CM -> {
                    OutlinedTextField(
                        value = heightCmInPut,
                        onValueChange = { updateHeightCmInPut(it) },
                        label = {
                            Text(
                                stringResource(R.string.height) + "(${unitsUiState.heightUnit})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f)
                            .padding(horizontal = 16.dp)
                    )
                }

                HEIGHT_UNIT_FT_IN -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f)
                    ) {
                        OutlinedTextField(
                            value = heightFTInPut,
                            onValueChange = { updateHeightFTInPut(it) },
                            label = {
                                Text(
                                    "ft", fontWeight = FontWeight.Bold, fontSize = 14.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                            ),
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(start = 16.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedTextField(
                            value = heightINInPut,
                            onValueChange = { updateHeightINInPut(it) },
                            label = {
                                Text(
                                    "in", fontWeight = FontWeight.Bold, fontSize = 14.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                            ),
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(end = 16.dp)
                        )
                    }
                }
            }
            UnitSelection(
                currentUnit = unitsUiState.heightUnit,
                isMenuExpanded = showHeightUnitMenu,
                onMenuToggle = updateShowHeightUnitMenu,
                onUnitSelected = { unit ->
                    saveHeightUnit(unit)
                    updateShowHeightUnitMenu(false)
                },
                options = listOf(HEIGHT_UNIT_CM, HEIGHT_UNIT_FT_IN),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (unitsUiState.weightUnit) {
                WEIGHT_UNIT_KG -> {
                    OutlinedTextField(
                        value = weightKgInPut,
                        onValueChange = { updateWeightKgInPut(it) },
                        label = {
                            Text(
                                stringResource(R.string.weight) + "(${unitsUiState.weightUnit})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f)
                            .padding(horizontal = 16.dp)
                    )
                }

                WEIGHT_UNIT_LB -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f)
                    ) {
                        OutlinedTextField(
                            value = weightLbInPut,
                            onValueChange = { updateWeightLbInPut(it) },
                            label = {
                                Text(
                                    stringResource(R.string.weight) + "(${unitsUiState.weightUnit})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                            ),
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }
            UnitSelection(
                currentUnit = unitsUiState.weightUnit,
                isMenuExpanded = showWeightUnitMenu,
                onMenuToggle = updateShowWeightUnitMenu,
                onUnitSelected = { unit ->
                    saveWeightUnit(unit)
                    updateShowWeightUnitMenu(false)
                },
                options = listOf(WEIGHT_UNIT_KG, WEIGHT_UNIT_LB),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (unitsUiState.headUnit) {
                HEAD_UNIT_CM -> {
                    OutlinedTextField(
                        value = headCmInput,
                        onValueChange = { updateHeadCmInput(it) },
                        label = {
                            Text(
                                stringResource(R.string.head_circumference) + "(${unitsUiState.headUnit})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f)
                            .padding(horizontal = 16.dp)
                    )
                }

                HEAD_UNIT_IN -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f)
                    ) {
                        OutlinedTextField(
                            value = headInInput,
                            onValueChange = { updateHeadInInput(it) },
                            label = {
                                Text(
                                    stringResource(R.string.head) + "(${unitsUiState.headUnit})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                            ),
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }
            UnitSelection(
                currentUnit = unitsUiState.headUnit,
                isMenuExpanded = showHeadUnitMenu,
                onMenuToggle = updateShowHeadUnitMenu,
                onUnitSelected = { unit ->
                    saveHeadUnit(unit)
                    updateShowHeadUnitMenu(false)
                },
                options = listOf(HEAD_UNIT_CM, HEAD_UNIT_IN),
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = noteFieldValue,
            onValueChange = { updateNoteFieldValue(it) },
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
                .padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                if (editMode && originalRecord != null) {
                    // 编辑模式：更新原始记录
                    val updatedRecord = originalRecord.copy(
                        date = selectedDate,
                        heightCm = heightCmInPut,
                        heightFt = heightFTInPut,
                        heightIn = heightINInPut,
                        weightKg = weightKgInPut,
                        weightLb = weightLbInPut,
                        headCircleCm = headCmInput,
                        headCircleIn = headInInput,
                        note = noteFieldValue.text
                    )
                    saveGrowthRecord(updatedRecord)
                } else {
                    // 添加模式：创建新记录
                    val newRecord = GrowthRecord(
                        childId = childId,
                        date = selectedDate,
                        heightCm = heightCmInPut,
                        heightFt = heightFTInPut,
                        heightIn = heightINInPut,
                        weightKg = weightKgInPut,
                        weightLb = weightLbInPut,
                        headCircleCm = headCmInput,
                        headCircleIn = headInInput,
                        note = noteFieldValue.text
                    )
                    saveGrowthRecord(newRecord)
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RectangleShape)
        ) {
            Text(
                text = if (editMode) {
                    stringResource(R.string.update_record)
                } else {
                    stringResource(R.string.add_record)
                },
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun UnitSelection(
    currentUnit: String,
    isMenuExpanded: Boolean,
    onMenuToggle: (Boolean) -> Unit,
    onUnitSelected: (String) -> Unit,
    options: List<String>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(6.dp)
            .clickable { onMenuToggle(true) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = currentUnit, fontSize = 18.sp)
            Icon(Icons.Filled.ArrowDropDown, contentDescription = "")
        }
        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { onMenuToggle(false) }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = 16.sp) },
                    onClick = { onUnitSelected(option) }
                )
            }
        }
    }
}

@Composable
@Preview
fun GrowthRecordView_PreView() {
    ChildGrowthTrackingTheme {
        GrowthRecordScreen(
            growthRecordUiState = GrowthRecordUiState.Loading, // 添加模式不依赖此状态
            editMode = false,
            childId = 1,
            unitsUiState = UnitsUiState(),
            showDatePicker = false,
            selectedDate = Date().time,
            dateFieldValue = TextFieldValue(Date().time.birthdayFormat()),
            showHeightUnitMenu = false,
            showWeightUnitMenu = false,
            showHeadUnitMenu = false,
            heightCmInPut = "",
            heightFTInPut = "",
            heightINInPut = "",
            weightKgInPut = "",
            weightLbInPut = "",
            headCmInput = "",
            headInInput = "",
            noteFieldValue = TextFieldValue(""),
            navigateBack = {},
            updateShowDatePicker = {},
            updateSelectedDate = {},
            updateDateFieldValue = {},
            updateShowHeightUnitMenu = {},
            updateShowWeightUnitMenu = {},
            updateShowHeadUnitMenu = {},
            updateHeightCmInPut = {},
            updateHeightFTInPut = {},
            updateHeightINInPut = {},
            updateWeightKgInPut = {},
            updateWeightLbInPut = {},
            updateHeadCmInput = {},
            updateHeadInInput = {},
            updateNoteFieldValue = {},
            onSaveSuccess = {},
            deleteGrowthRecord = {},
            saveHeightUnit = {},
            saveWeightUnit = {},
            saveHeadUnit = {},
            saveGrowthRecord = {}
        )

    }
}
