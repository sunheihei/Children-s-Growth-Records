package com.babycare.childgrowthtracking.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.babycare.childgrowthtracking.R
import com.babycare.childgrowthtracking.datastore.UnitsUiState
import com.babycare.childgrowthtracking.extensions.birthdayFormat
import com.babycare.childgrowthtracking.model.ChildWithLatestRecord
import com.babycare.childgrowthtracking.ui.common.CommonAlertDialog
import com.babycare.childgrowthtracking.ui.theme.ChildGrowthTrackingTheme
import com.babycare.childgrowthtracking.utils.BitmapUtils
import com.babycare.childgrowthtracking.utils.CommonUtils
import com.babycare.childgrowthtracking.utils.HEAD_UNIT_CM
import com.babycare.childgrowthtracking.utils.HEIGHT_UNIT_CM
import com.babycare.childgrowthtracking.utils.WEIGHT_UNIT_KG
import java.util.Date

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChildrenItemCard(
    childWithRecord: ChildWithLatestRecord,
    unitsUiState: UnitsUiState,
    onEditInfoClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onGrowthDataClick: () -> Unit,
    onGrowthDiaryClick: () -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current

    // 使用 remember 缓存 ImageRequest，仅在 avatar 变化时更新
    val imageRequest = remember(childWithRecord.child.avatar) {
        ImageRequest.Builder(context)
            .data(BitmapUtils.base64ToBitmap(childWithRecord.child.avatar)) // Base64 转 Bitmap
            .crossfade(true) // 淡入淡出动画
            .build()
    }

    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    ElevatedCard(
        modifier = modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)

        ) {
            // 头像和用户名部分
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                // 模拟头像
                AsyncImage(
                    model = imageRequest,
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
                        text = childWithRecord.child.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = CommonUtils.calculateAgeDifference(
                            childWithRecord.child.age, Date().time
                        ),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "",
                        modifier = Modifier
                            .height(56.dp)
                            .clickable {
                                showMenu = true
                            })
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        // 修改信息选项
                        DropdownMenuItem(text = {
                            Text(
                                stringResource(R.string.edit),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }, onClick = {
                            // 在这里处理修改信息的逻辑
                            onEditInfoClick()
                            showMenu = false
                        }, leadingIcon = {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        })

                        // 删除信息选项
                        DropdownMenuItem(text = {
                            Text(
                                stringResource(R.string.delete),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }, onClick = {
                            showDeleteDialog = true
                            showMenu = false
                        }, leadingIcon = {
                            Icon(
                                Icons.Filled.DeleteSweep,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        })
                    }
                    if (showDeleteDialog) {
                        CommonAlertDialog(
                            dialogTitle = stringResource(R.string.delete_information),
                            dialogText = stringResource(R.string.are_you_sure_you_want_to_delete_the_s_data).format(
                                childWithRecord.child.name
                            ),
                            confirm = stringResource(R.string.ok),
                            dismiss = stringResource(R.string.cancel),
                            onConfirmation = {
                                onDeleteClick()
                                showDeleteDialog = false
                            },
                            onDismissRequest = { showDeleteDialog = false })
                    }
                }

            }
            if (childWithRecord.growthRecord != null) {
                val latestRecord = childWithRecord.growthRecord
                // 日期信息
                Text(
                    text = stringResource(R.string.record_date).format(latestRecord.date.birthdayFormat()),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 数据统计部分（身高、体重、头围）
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onGrowthDataClick()
                        }) {
                    StatItem(
                        stringResource(R.string.height),
                        if (unitsUiState.heightUnit.equals(HEIGHT_UNIT_CM)) {
                            "${latestRecord.heightCm}(cm)"
                        } else {
                            "${latestRecord.heightFt}(ft) ${latestRecord.heightFt}(in)"
                        }
                    )
                    StatItem(
                        stringResource(R.string.weight),
                        if (unitsUiState.weightUnit.equals(WEIGHT_UNIT_KG)) {
                            "${latestRecord.weightKg}(kg)"
                        } else {
                            "${latestRecord.weightLb}(lb)"
                        }
                    )
                    StatItem(
                        stringResource(R.string.head_circumference),
                        if (unitsUiState.headUnit.equals(HEAD_UNIT_CM)) {
                            "${latestRecord.headCircleCm}(cm)"
                        } else {
                            "${latestRecord.headCircleIn}(in)"
                        }
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Icon(Icons.Filled.EditNote, contentDescription = "No Record", tint = Color.Gray)
                    Text(stringResource(R.string.no_growth_record), color = Color.Gray)
                }

            }
            Spacer(modifier = Modifier.height(8.dp))

            // 按钮部分
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                ElevatedButton(
                    onClick = onGrowthDataClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.growth_data),
                            contentDescription = stringResource(R.string.growth_data),
                            Modifier.padding(horizontal = 2.dp)
                        )
                        Text(
                            stringResource(R.string.growth_data),
                            maxLines = 1,
                        )
                    }

                }
                Spacer(modifier = Modifier.width(8.dp))
                ElevatedButton(
                    onClick = onGrowthDiaryClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.growth_diary),
                            contentDescription = stringResource(R.string.growth_diary),
                            Modifier.padding(horizontal = 2.dp)
                        )
                        Text(
                            stringResource(R.string.growth_diary),
                            maxLines = 1,
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column() {
        Text(
            text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold
        )
    }
}


@Composable
@Preview
fun ChildrenCard_PreView() {
    ChildGrowthTrackingTheme {
        ChildrenItemCard(
            childWithRecord = ChildWithLatestRecord.TempChildWithRecord1,
            unitsUiState = UnitsUiState(),
            onEditInfoClick = {},
            onDeleteClick = {},
            onGrowthDataClick = {},
            onGrowthDiaryClick = {}, modifier = Modifier
        )
    }
}