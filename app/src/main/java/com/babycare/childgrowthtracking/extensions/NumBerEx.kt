package com.babycare.childgrowthtracking.extensions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

@SuppressLint("SimpleDateFormat")
fun Long.birthdayFormat(): String {
    return SimpleDateFormat("yyyy-MM-dd").format(
        this
    )
}


/**
 * 将厘米转换为英尺，保留一位小数
 */
fun Double.cmToFeetDouble(): Double {
    val totalInches = this * 0.393701
    val feet = totalInches / 12
    return (feet * 10).roundToInt() / 10.0 // 使用 roundToInt 四舍五入
}

/**
 * 将厘米转换为英寸（剩余英寸），保留一位小数
 */
fun Double.cmToInchesDouble(): Double {
    val totalInches = this * 0.393701
    return (totalInches * 10).toLong().toDouble() / 10
}


/**
 * 将千克 (kg) 转换为磅 (lb)，保留一位小数
 * @return 磅值 (Double)，保留一位小数
 */
fun Double.kgToLbDouble(): Double {
    if (this < 0) return 0.0
    val pounds = this * 2.20462 // 使用 Double 精度
    return (pounds * 10).roundToInt() / 10.0 // 四舍五入到一位小数
}

/**
 * 将磅 (lb) 转换为千克 (kg)，保留一位小数
 * @return 千克值 (Double)，保留一位小数
 */
fun Double.lbToKgDouble(): Double {
    val kilograms = this * 0.453592 // 使用 Double 精度
    return (kilograms * 10).roundToInt() / 10.0 // 四舍五入到一位小数
}

/**
 * 将英寸 (in) 转换为厘米 (cm)，保留一位小数
 * @return 厘米值 (Double)，保留一位小数
 */
fun Double.inToCmDouble(): Double {
    val centimeters = this * 2.54 // 使用 Double 精度
    return (centimeters * 10).roundToInt() / 10.0 // 四舍五入到一位小数
}