package com.babycare.childgrowthtracking.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout

fun Modifier.rotateWithLayout(degrees: Float) = this
    .layout { measurable, constraints ->
        // 获取父容器的最大宽高约束（ViewPager 的尺寸）
        val parentWidth = constraints.maxWidth
        val parentHeight = constraints.maxHeight

        // 当旋转角度为 90° 或 -90° 时，交换宽高约束
        val rotatedConstraints = if (degrees % 180 == 90f || degrees % 180 == -90f) {
            constraints.copy(
                minWidth = constraints.minHeight,
                maxWidth = parentHeight, // 使用父容器高度作为旋转后的宽度
                minHeight = constraints.minWidth,
                maxHeight = parentWidth  // 使用父容器宽度作为旋转后的高度
            )
        } else {
            constraints
        }

        // 测量子节点
        val placeable = measurable.measure(rotatedConstraints)

        // 设置布局大小为测量后的宽高，但不超过父容器
        layout(placeable.width.coerceAtMost(parentHeight), placeable.height.coerceAtMost(parentWidth)) {
            // 计算偏移量以保持内容居中
            val xOffset = (parentHeight - placeable.width) / 2
            val yOffset = (parentWidth - placeable.height) / 2

            // 放置内容并应用旋转
            placeable.placeWithLayer(
                x = xOffset.coerceAtLeast(0), // 避免负偏移导致内容被裁剪
                y = yOffset.coerceAtLeast(0)
            ) {
                rotationZ = degrees
            }
        }
    }