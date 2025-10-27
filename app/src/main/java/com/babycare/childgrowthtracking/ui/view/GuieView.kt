package com.babycare.childgrowthtracking.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babycare.childgrowthtracking.datastore.DataStoreViewModel
import com.babycare.childgrowthtracking.model.Organize
import com.babycare.childgrowthtracking.ui.theme.ChildGrowthTrackingTheme
import com.babycare.childgrowthtracking.utils.ORGANIZATION_CDC
import com.babycare.childgrowthtracking.utils.ORGANIZATION_NHC
import com.babycare.childgrowthtracking.utils.ORGANIZATION_WHO
import com.babycare.childgrowthtracking.utils.OrganizeData

@Composable
fun GuideView(enterNext: () -> Unit) {
    val dataStoreViewModel = hiltViewModel<DataStoreViewModel>()

    val organizeCode by dataStoreViewModel.organizationCode.collectAsState(initial = ORGANIZATION_WHO)
    var currentOrganize: Int by remember(organizeCode) { mutableIntStateOf(organizeCode) }

    GuideScreen(currentOrganize, chooseOrganize = { selectOrganize ->
        currentOrganize = selectOrganize
    }, startAndEnter = {
        dataStoreViewModel.saveOrganizationCode(currentOrganize)
        enterNext()
    })
}

@Composable
fun GuideScreen(
    currentOrganize: Int,
    chooseOrganize: (selectOrganize: Int) -> Unit,
    startAndEnter: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                "Please select a growth standard",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Choose the most appropriate reference standard to track your child's growth",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(OrganizeData) { index, organize ->
                    OrganizeItemForGuide(
                        organize,
                        index,
                        currentOrganize,
                        chooseOrganize = { newOrganize: Int ->
                            if (newOrganize != currentOrganize) {
                                chooseOrganize(newOrganize)
                            }
                        })
                }
            }
            Spacer(modifier = Modifier.height(120.dp))
            Button(
                onClick = {
                    startAndEnter()
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)

            ) {
                Text(
                    text = "Start",
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun OrganizeItemForGuide(
    organize: Organize,
    index: Int,
    currentOrganize: Int,
    chooseOrganize: (index: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(12.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceContainer)
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
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
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
        AnimatedVisibility(visible = currentOrganize == index) {
            Text(
                organize.subscription,
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

    }
}


@Composable
@Preview
fun Item_Preview() {
    ChildGrowthTrackingTheme{
        OrganizeItemForGuide(OrganizeData[0], 0, 0, chooseOrganize = {})
    }
}

@Composable
@Preview
fun GuideView_Preview() {
    ChildGrowthTrackingTheme{
        GuideScreen(0, chooseOrganize = {}, startAndEnter = {})
    }
}