package com.babycare.childgrowthtracking.ui.view

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.DropdownMenu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babycare.childgrowthtracking.R
import com.babycare.childgrowthtracking.datastore.DataStoreViewModel
import com.babycare.childgrowthtracking.datastore.UnitsUiState
import com.babycare.childgrowthtracking.model.Child
import com.babycare.childgrowthtracking.model.ChildWithLatestRecord
import com.babycare.childgrowthtracking.ui.children.ChildrenUiState
import com.babycare.childgrowthtracking.ui.children.ChildrenViewModel
import com.babycare.childgrowthtracking.ui.common.LoadingView
import com.babycare.childgrowthtracking.ui.theme.ChildGrowthTrackingTheme
import com.babycare.childgrowthtracking.utils.HEAD_UNIT_CM
import com.babycare.childgrowthtracking.utils.HEIGHT_UNIT_CM
import com.babycare.childgrowthtracking.utils.WEIGHT_UNIT_KG


@SuppressLint("ContextCastToActivity")
@Composable
fun ChildrenView(
    addChildren: () -> Unit,
    onEditInfoClick: (child: Child) -> Unit,
    onGrowthDataClick: (child: Child) -> Unit,
    onGrowthDiaryClick: (child: Child) -> Unit,
    navigateSetting: () -> Unit,
    childrenViewModel: ChildrenViewModel = hiltViewModel(),
    dataStoreViewModel: DataStoreViewModel = hiltViewModel(),
) {
    val context = (LocalContext.current as ComponentActivity)
    context.lifecycle.addObserver(childrenViewModel)

    val uiState by childrenViewModel.uiStateChildren.collectAsState()
    val units by dataStoreViewModel.unitsUiState.collectAsState()

    ChildrenScreen(
        uiState = uiState,
        unitsUiState = units,
        addChildren = addChildren,
        onEditInfoClick = onEditInfoClick,
        onDeleteClick = { child ->
            childrenViewModel.deleteChildren(child)
        },
        onGrowthDataClick = onGrowthDataClick,
        onGrowthDiaryClick = onGrowthDiaryClick,
        navigateSetting = navigateSetting
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildrenScreen(
    uiState: ChildrenUiState,
    unitsUiState: UnitsUiState,
    addChildren: () -> Unit,
    onEditInfoClick: (child: Child) -> Unit,
    onDeleteClick: (child: Child) -> Unit,
    onGrowthDataClick: (child: Child) -> Unit,
    onGrowthDiaryClick: (child: Child) -> Unit,
    navigateSetting: () -> Unit
) {
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
                        stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    IconButton(onClick = navigateSetting) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Confirm"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                expanded = isFaExpand,
                onClick = addChildren,
                icon = { Icon(Icons.Filled.PersonAdd, "add children") },
                text = { Text(text = stringResource(R.string.add_children)) },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        ChildrenContent(
            uiState = uiState,
            listState = listState,
            unitsUiState = unitsUiState,
            onEditInfoClick = onEditInfoClick,
            onDeleteClick = onDeleteClick,
            onGrowthDataClick = onGrowthDataClick,
            onGrowthDiaryClick = onGrowthDiaryClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun ChildrenContent(
    uiState: ChildrenUiState,
    listState: LazyListState,
    unitsUiState: UnitsUiState,
    onEditInfoClick: (child: Child) -> Unit,
    onDeleteClick: (child: Child) -> Unit,
    onGrowthDataClick: (child: Child) -> Unit,
    onGrowthDiaryClick: (child: Child) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        when (uiState) {
            is ChildrenUiState.Loading -> {
                LoadingView()
            }

            is ChildrenUiState.Error -> {
                NoChildrenView()
            }

            is ChildrenUiState.Success -> {
                val children = uiState.childrenWithLatestRecord
                if (children.isNotEmpty()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            vertical = 10.dp,
                            horizontal = 10.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(children, key = { it.child.id }) { childrenWithLatestRecord ->
                            ChildrenItemCard(
                                childWithRecord = childrenWithLatestRecord,
                                unitsUiState = unitsUiState,
                                onEditInfoClick = {
                                    onEditInfoClick(childrenWithLatestRecord.child)
                                },
                                onDeleteClick = {
                                    onDeleteClick(childrenWithLatestRecord.child)
                                },
                                onGrowthDataClick = {
                                    onGrowthDataClick(childrenWithLatestRecord.child)
                                },
                                onGrowthDiaryClick = {
                                    onGrowthDiaryClick(childrenWithLatestRecord.child)
                                },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                } else {
                    NoChildrenView()
                }
            }
        }
    }
}

@Composable
fun NoChildrenView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {

        Icon(
            painter = painterResource(R.drawable.add_children),
            contentDescription = "add diary",
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(2.dp))

        Text(
            stringResource(R.string.no_children_please_add),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary
        )
    }
}


@Composable
@Preview
fun ChildrenScreen_Preview() {
    ChildGrowthTrackingTheme {
        ChildrenScreen(
            ChildrenUiState.Success(ChildWithLatestRecord.TempChildWithRecordList),
            unitsUiState = UnitsUiState(),
            addChildren = {},
            onEditInfoClick = {},
            onDeleteClick = {},
            onGrowthDataClick = {},
            onGrowthDiaryClick = {}, navigateSetting = {})

    }
}


@Preview
@Composable
private fun NoChildrenView_Preview() {
    ChildGrowthTrackingTheme {
        NoChildrenView()
    }
}