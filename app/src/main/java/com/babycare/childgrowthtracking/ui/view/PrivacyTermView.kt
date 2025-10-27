package com.babycare.childgrowthtracking.ui.view

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import com.babycare.childgrowthtracking.R
import com.babycare.childgrowthtracking.utils.Privacy_Policy
import com.babycare.childgrowthtracking.utils.Term_Service


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyTermService(ps: Int, navigateBack: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(
            colors = topAppBarColors(
                titleContentColor = MaterialTheme.colorScheme.primary,
            ), title = {
                Text(
                    if (ps == 0) {
                        stringResource(R.string.privacy_policy)
                    } else {
                        stringResource(R.string.terms_of_use)
                    },
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
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            WebViewScreen(
                if (ps == 0) {
                    Privacy_Policy
                } else {
                    Term_Service
                }
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url: String = "https://www.example.com") {
    // 使用 AndroidView 嵌入 WebView
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                // 设置 WebViewClient 以处理页面导航
                webViewClient = WebViewClient()
                // 启用 JavaScript（根据需求启用，注意安全问题）
                settings.javaScriptEnabled = true
                // 加载指定的 URL
                loadUrl(url)
            }
        },
        update = { webView ->
            // 更新时重新加载 URL（如果需要）
            webView.loadUrl(url)
        }
    )
}
