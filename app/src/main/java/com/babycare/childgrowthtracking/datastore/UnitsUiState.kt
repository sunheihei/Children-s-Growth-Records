package com.babycare.childgrowthtracking.datastore

import com.babycare.childgrowthtracking.utils.HEAD_UNIT_CM
import com.babycare.childgrowthtracking.utils.HEIGHT_UNIT_CM
import com.babycare.childgrowthtracking.utils.WEIGHT_UNIT_KG

data class UnitsUiState(
    val heightUnit: String = HEIGHT_UNIT_CM,
    val weightUnit: String = WEIGHT_UNIT_KG,
    val headUnit: String = HEAD_UNIT_CM
)
