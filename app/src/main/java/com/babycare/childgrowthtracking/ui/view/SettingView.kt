package com.babycare.childgrowthtracking.ui.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babycare.childgrowthtracking.R
import com.babycare.childgrowthtracking.datastore.DataStoreViewModel
import com.babycare.childgrowthtracking.datastore.UnitsUiState
import com.babycare.childgrowthtracking.model.Organize
import com.babycare.childgrowthtracking.ui.theme.ChildGrowthTrackingTheme
import com.babycare.childgrowthtracking.utils.CommonUtils
import com.babycare.childgrowthtracking.utils.HEAD_UNIT_CM
import com.babycare.childgrowthtracking.utils.HEAD_UNIT_IN
import com.babycare.childgrowthtracking.utils.HEIGHT_UNIT_CM
import com.babycare.childgrowthtracking.utils.HEIGHT_UNIT_FT_IN
import com.babycare.childgrowthtracking.utils.ORGANIZATION_CDC
import com.babycare.childgrowthtracking.utils.ORGANIZATION_NHC
import com.babycare.childgrowthtracking.utils.ORGANIZATION_WHO
import com.babycare.childgrowthtracking.utils.OrganizeData
import com.babycare.childgrowthtracking.utils.WEIGHT_UNIT_KG
import com.babycare.childgrowthtracking.utils.WEIGHT_UNIT_LB
import kotlinx.coroutines.launch


@Composable
fun Setting(
    navigateBack: () -> Unit,
    navigateToPrivacy: (ps: Int) -> Unit,
    dataStoreViewModel: DataStoreViewModel = hiltViewModel()
) {


    val units by dataStoreViewModel.unitsUiState.collectAsState()
    val organizeCode by dataStoreViewModel.organizationCode.collectAsState(initial = ORGANIZATION_WHO)
    val organize: Organize by remember(organizeCode) { mutableStateOf(OrganizeData[organizeCode]) }

    SettingScreen(
        units,
        organizeCode,
        organize,
        updateOrganize = { newOrganize ->
            dataStoreViewModel.saveOrganizationCode(newOrganize)
        },
        lengthMenuClick = { lengthUnit: String ->
            if (lengthUnit == "cm") {
                dataStoreViewModel.saveHeightUnit(HEIGHT_UNIT_CM)
                dataStoreViewModel.saveHeadUnit(HEAD_UNIT_CM)
            } else {
                dataStoreViewModel.saveHeightUnit(HEIGHT_UNIT_FT_IN)
                dataStoreViewModel.saveHeadUnit(HEAD_UNIT_IN)
            }
        },
        weightMenuClick = { weightUnit: String ->
            dataStoreViewModel.saveWeightUnit(weightUnit)
        },
        navigateToPrivacy = navigateToPrivacy,
        navigateBack = navigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    unitsUiState: UnitsUiState,
    currentOrganize: Int,
    organize: Organize,
    updateOrganize: (newOrganize: Int) -> Unit,
    lengthMenuClick: (lengthUnit: String) -> Unit,
    weightMenuClick: (weightUnit: String) -> Unit,
    navigateToPrivacy: (ps: Int) -> Unit,
    navigateBack: () -> Unit
) {
    var showLengthUnitMenu by remember { mutableStateOf(false) }
    var showWeightUnitMenu by remember { mutableStateOf(false) }
    var showOrganizeBottomSheet by remember { mutableStateOf(false) }

    var versionName by remember { mutableStateOf("1.0.0") }

    LaunchedEffect(Unit) { versionName = CommonUtils.getAppVersionName() }


    Scaffold(topBar = {
        TopAppBar(
            colors = topAppBarColors(
                titleContentColor = MaterialTheme.colorScheme.primary,
            ), title = {
                Text(
                    stringResource(R.string.setting),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
            }, navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Back", tint = MaterialTheme.colorScheme.primary
                    )
                }
            })
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
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
                        .clickable {
                            showOrganizeBottomSheet = true
                        }
                        .padding(12.dp)


                ) {
                    Icon(
                        Icons.Default.LocalLibrary,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                    )
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
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }
                    Image(
                        painter = painterResource(organize.logo),
                        contentDescription = stringResource(R.string.organization_logo),
                        Modifier.size(68.dp)
                    )
                }
                Text(
                    organize.subscription,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(start = 12.dp)
                )

                Spacer(Modifier.height(24.dp))
                Text(
                    "UNIT",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.icon_unit_height),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription =
                                stringResource(R.string.height_unit)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Length Unit", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.weight(1f))
                        Box(modifier = Modifier.clickable {
                            showLengthUnitMenu = true
                        }) {
                            Row {
                                Text(
                                    unitsUiState.heightUnit,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Icon(
                                    painter = painterResource(R.drawable.unfold_more),
                                    contentDescription = stringResource(R.string.change_height_unit),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                            DropdownMenu(
                                expanded = showLengthUnitMenu,
                                onDismissRequest = { showLengthUnitMenu = false }) {
                                DropdownMenuItem(text = {
                                    Text(
                                        HEIGHT_UNIT_CM, fontSize = 16.sp
                                    )
                                }, onClick = {
                                    lengthMenuClick(HEIGHT_UNIT_CM)
                                    showLengthUnitMenu = false
                                })
                                DropdownMenuItem(text = {
                                    Text(
                                        HEIGHT_UNIT_FT_IN, fontSize = 16.sp
                                    )
                                }, onClick = {
                                    lengthMenuClick(HEIGHT_UNIT_FT_IN)
                                    showLengthUnitMenu = false
                                })
                            }

                        }


                    }
                    Divider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 32.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.icon_unit_weight),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription =
                                stringResource(R.string.weight_unit)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Weight Unit", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.weight(1f))

                        Box(modifier = Modifier.clickable {
                            showWeightUnitMenu = true
                        }) {
                            Row {
                                Text(
                                    unitsUiState.weightUnit,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Icon(
                                    painter = painterResource(R.drawable.unfold_more),
                                    contentDescription = stringResource(R.string.change_weight_unit),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }

                            DropdownMenu(
                                expanded = showWeightUnitMenu,
                                onDismissRequest = { showWeightUnitMenu = false }) {
                                DropdownMenuItem(text = {
                                    Text(
                                        WEIGHT_UNIT_KG, fontSize = 16.sp
                                    )
                                }, onClick = {
                                    weightMenuClick(WEIGHT_UNIT_KG)
                                    showWeightUnitMenu = false
                                })
                                DropdownMenuItem(text = {
                                    Text(
                                        WEIGHT_UNIT_LB, fontSize = 16.sp
                                    )
                                }, onClick = {
                                    weightMenuClick(WEIGHT_UNIT_LB)
                                    showWeightUnitMenu = false
                                })
                            }

                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "POLICY",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            navigateToPrivacy(0)
                        }) {
                        Icon(
                            painter = painterResource(R.drawable.icon_privacy_policy),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription =
                                "Privacy Policy icon"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.privacy_policy),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            painter = painterResource(R.drawable.arrow_right),
                            contentDescription = "change height unit",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                    Divider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 32.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            navigateToPrivacy(1)
                        }) {
                        Icon(
                            painter = painterResource(R.drawable.icon_terms_of_use),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription =
                                "Terms of Use icon"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.terms_of_use),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(R.drawable.arrow_right),
                            contentDescription = "change weight unit",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }


                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Version $versionName")
                }
            }


            if (showOrganizeBottomSheet) {
                OrganizeBottomSheet(
                    currentOrganize,
                    onDismissRequest = {
                        showOrganizeBottomSheet = false
                    }, updateOrganize = { newOrganize ->
                        updateOrganize(newOrganize)
                    })
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizeBottomSheet(
    currentOrganize: Int,
    onDismissRequest: () -> Unit,
    updateOrganize: (newOrganize: Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = sheetState
    ) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(OrganizeData) { index, organize ->
                OrganizeItem(
                    organize,
                    index,
                    currentOrganize,
                    chooseOrganize = { newOrganize: Int ->
                        if (newOrganize != currentOrganize) {

                            updateOrganize(newOrganize)
                        }
                        scope.launch { sheetState.hide() }
                            .invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    onDismissRequest()
                                }
                            }
                    })
            }
        }
    }
}

@Composable
fun OrganizeItem(
    organize: Organize,
    index: Int,
    currentOrganize: Int,
    chooseOrganize: (index: Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(12.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(12.dp)
            .clickable {
                when (index) {
                    0 -> {
                        chooseOrganize(ORGANIZATION_WHO)
                    }

                    1 -> {
                        chooseOrganize(ORGANIZATION_CDC)
                    }

                    2 -> {
                        chooseOrganize(ORGANIZATION_NHC)
                    }
                }

            }
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
        if (currentOrganize == index) {
            Icon(
                Icons.Rounded.CheckCircle,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
@Preview
fun OrganizeItem_Preview() {
    ChildGrowthTrackingTheme {
        OrganizeItem(OrganizeData[0], 0, 0, chooseOrganize = {})
    }
}


@Composable
@Preview(showBackground = true)
fun Setting_Preview() {
    ChildGrowthTrackingTheme {
        SettingScreen(
            UnitsUiState(),
            0,
            OrganizeData[0],
            updateOrganize = {},
            lengthMenuClick = {},
            weightMenuClick = {},
            navigateToPrivacy = {},
            navigateBack = {})
    }
}
