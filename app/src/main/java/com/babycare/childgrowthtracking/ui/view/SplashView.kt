package com.babycare.childgrowthtracking.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babycare.childgrowthtracking.R
import com.babycare.childgrowthtracking.datastore.DataStoreViewModel
import com.babycare.childgrowthtracking.ui.theme.ChildGrowthTrackingTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import okhttp3.internal.concurrent.formatDuration

// 倒计时 Flow
fun countDownFlow(totalSeconds: Int): Flow<Int> = flow {
    var remainingSeconds = totalSeconds
    while (remainingSeconds >= 0) {
        emit(remainingSeconds)
        delay(1000L) // 每秒延迟
        remainingSeconds--
    }
}

@Composable
fun SplashView(
    countdownSeconds: Int = 3,
    enterNext: () -> Unit,
    navigateToGuideView: () -> Unit,
    dataStoreViewModel: DataStoreViewModel = hiltViewModel()
) {
    // 使用 remember 保存倒计时状态，避免重复创建 Flow
    var remainingSeconds by remember { mutableIntStateOf(countdownSeconds) }

    // 启动倒计时
    LaunchedEffect(Unit) {
        dataStoreViewModel.isFirstLaunch.collectLatest { firstLaunch ->
            if (firstLaunch) {
                dataStoreViewModel.saveFirstLaunch(false)
                navigateToGuideView()
            } else {
                countDownFlow(countdownSeconds).collect { seconds ->
                    remainingSeconds = seconds
                    if (seconds == 0) {
                        enterNext()
                    }
                }
            }
        }
    }

    SplashScreen(remainingSeconds = remainingSeconds)
}

@Composable
fun SplashScreen(remainingSeconds: Int) {
    var isLogoVisible by remember { mutableStateOf(false) }

    // 触发动画
    LaunchedEffect(Unit) {
        isLogoVisible = true
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = isLogoVisible,
            enter = fadeIn() + slideInVertically(
                animationSpec = tween(
                    durationMillis = 800, // 动画时长 500ms
                    easing = FastOutSlowInEasing
                ),
                initialOffsetY = { 200 }
            )
        ) {
            Box {
                Image(
                    painter = painterResource(R.drawable.splash_logo),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(180.dp)
                )
            }
        }

        Spacer(Modifier.height(120.dp))
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
        )
    }

}

@Composable
@Preview
fun SplashView_Preview() {
    ChildGrowthTrackingTheme {
        SplashScreen(remainingSeconds = 3)
    }
}