/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.babycare.childgrowthtracking.utils

import com.babycare.childgrowthtracking.R
import com.babycare.childgrowthtracking.model.Organize

/**
 * Constants used throughout the app.
 */
const val DATABASE_NAME = "child-growth-db"


const val NAV_LAUNCH = "launch"
const val NAV_GUIDE = "guide_view"
const val NAV_HOME = "home"
const val NAV_SETTING = "setting"
const val NAV_ADD_CHILDREN = "add_children"
const val NAV_EDIT_CHILDREN = "edit_children"
const val GROWTH_DATA = "growth_data"
const val GROWTH_RECORD = "growth_record"
const val GROWTH_DATA_CHARTS = "growth_data_charts"
const val GROWTH_DIARY = "grow_diary"
const val GROWTH_DIARY_EDIT = "grow_diary_edit"
const val PRIVACY_TERM_SERVICE = "privacy_term_service"

const val HEIGHT = 0
const val WEIGHT = 1
const val HEAD = 2

const val GENDER_BOY = 0
const val GENDER_GIRL = 1

const val HEIGHT_UNIT_CM = "cm"
const val HEIGHT_UNIT_FT_IN = "ft/in"
const val HEIGHT_UNIT_IN = "in"

const val WEIGHT_UNIT_KG = "kg"
const val WEIGHT_UNIT_LB = "lb"

const val HEAD_UNIT_CM = "cm"
const val HEAD_UNIT_IN = "in"

const val DIARYMODE_ADD = 0
const val DIARYMODE_VIEW = 1
const val DIARYMODE_EDIT = 2

const val ORGANIZATION_WHO = 0
const val ORGANIZATION_CDC = 1
const val ORGANIZATION_NHC = 2

const val Privacy_Policy = "https://sites.google.com/view/growthtracker-privacy"
const val Term_Service = "https://sites.google.com/view/growthtrakcer-termservice"


val OrganizeData: List<Organize> = mutableListOf(
    Organize(
        "WHO",
        "World Health Organization",
        R.drawable.who,
        "Height, weight: 0-5 years old \nHead circumference: 0-5 years old"
    ),
    Organize(
        "CDC",
        "Centers for Disease Control and Prevention",
        R.drawable.cdc,
        "Height, weight: 0-20 years old \nHead circumference: 0-3 years old"
    ),
    Organize(
        "NHC",
        "World Health Organization",
        R.drawable.nhc,
        "Height, weight: 0-7 years old \nHead circumference: 0-3 years old"
    )
)
