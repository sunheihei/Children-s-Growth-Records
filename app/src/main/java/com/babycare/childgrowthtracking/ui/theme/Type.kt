package com.babycare.childgrowthtracking.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.babycare.childgrowthtracking.R


private val Montserrat = FontFamily(
    Font(R.font.montserrat_medium),
    Font(R.font.montserrat_semibold), Font(R.font.montserrat_regular)
)

private val robotomono = FontFamily(
    Font(R.font.robotomono_variablefont_wght)
)

//
//@Suppress("DEPRECATION")
val defaultTextStyle = TextStyle(
    fontFamily = robotomono,
    platformStyle = PlatformTextStyle(
        includeFontPadding = false
    ),
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None
    )
)


val AppTypography = Typography(
//    displayLarge = defaultTextStyle.copy(
//        fontSize = 57.sp, lineHeight = 64.sp
//    ),
//    displayMedium = defaultTextStyle.copy(
//        fontSize = 45.sp, lineHeight = 52.sp
//    ),
//    displaySmall = defaultTextStyle.copy(
//        fontSize = 36.sp, lineHeight = 44.sp
//    ),
//    headlineLarge = defaultTextStyle.copy(
//        fontSize = 32.sp, lineHeight = 40.sp
//    ),
//    headlineMedium = defaultTextStyle.copy(
//        fontSize = 28.sp, lineHeight = 36.sp
//    ),
//    headlineSmall = defaultTextStyle.copy(
//        fontSize = 24.sp, lineHeight = 32.sp
//    ),
//    titleLarge = defaultTextStyle.copy(
//        fontSize = 22.sp, lineHeight = 28.sp,
//    ),
//    titleMedium = defaultTextStyle.copy(
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        lineBreak = LineBreak.Heading
//    ),
//    titleSmall = defaultTextStyle.copy(
//        fontSize = 14.sp,
//        lineHeight = 20.sp, lineBreak = LineBreak.Heading
//    ),
//    labelLarge = defaultTextStyle.copy(
//        fontSize = 14.sp, lineHeight = 20.sp, lineBreak = LineBreak.Heading
//    ),
//    labelMedium = defaultTextStyle.copy(
//        fontSize = 12.sp, lineHeight = 16.sp,
//    ),
//    labelSmall = defaultTextStyle.copy(
//        fontSize = 11.sp, lineHeight = 16.sp
//    ),
//    bodyLarge = defaultTextStyle.copy(
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        lineBreak = LineBreak.Paragraph
//    ),
//    bodyMedium = defaultTextStyle.copy(
//        fontSize = 14.sp,
//        lineHeight = 20.sp,
//        lineBreak = LineBreak.Paragraph
//    ),
//    bodySmall = defaultTextStyle.copy(
//        fontSize = 12.sp,
//        lineHeight = 16.sp,
//        lineBreak = LineBreak.Paragraph
//    ),
)
