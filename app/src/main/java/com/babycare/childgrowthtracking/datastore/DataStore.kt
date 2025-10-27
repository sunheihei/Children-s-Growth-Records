package com.babycare.childgrowthtracking.datastore

import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Organization
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.babycare.childgrowthtracking.model.Organize
import com.babycare.childgrowthtracking.utils.HEIGHT_UNIT_CM
import com.babycare.childgrowthtracking.utils.WEIGHT_UNIT_KG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// 在 Context 上定义 DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")


class GrowthTrackerDataStore(private val context: Context) {

    private val HEIGHT_UNIT = stringPreferencesKey("height_unit")
    private val WEIGHT_UNIT = stringPreferencesKey("weight_unit")
    private val HEAD_UNIT = stringPreferencesKey("head_unit")
    private val ORGANIZATION = intPreferencesKey("organization")
    private val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")

    val heightUnit: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[HEIGHT_UNIT] ?: HEIGHT_UNIT_CM
        }
    val weightUnit: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[WEIGHT_UNIT] ?: WEIGHT_UNIT_KG
        }
    val headUnit: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[HEAD_UNIT] ?: HEIGHT_UNIT_CM
        }
    val organization: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[ORGANIZATION] ?: 0
        }
    val isFirstLaunch: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_FIRST_LAUNCH] ?: true
        }

    suspend fun saveHeightUnit(heightUnit: String) {
        context.dataStore.edit { prefrences -> prefrences[HEIGHT_UNIT] = heightUnit }
    }

    suspend fun saveWeightUnit(weightUnit: String) {
        context.dataStore.edit { prefrences -> prefrences[WEIGHT_UNIT] = weightUnit }
    }

    suspend fun saveHeadUnit(headUnit: String) {
        context.dataStore.edit { prefrences -> prefrences[HEAD_UNIT] = headUnit }
    }

    suspend fun saveOrganize(organize: Int) {
        context.dataStore.edit { prefrences -> prefrences[ORGANIZATION] = organize }
    }

    suspend fun saveIsFirstLaunch(firstLaunch: Boolean) {
        context.dataStore.edit { prefrences -> prefrences[IS_FIRST_LAUNCH] = firstLaunch }
    }

    // 一次性保存所有参数（可选）
    suspend fun saveAll(heightUnit: String, weightUnit: String, headUnit: String) {
        context.dataStore.edit { preferences ->
            preferences[HEIGHT_UNIT] = heightUnit
            preferences[WEIGHT_UNIT] = weightUnit
            preferences[HEAD_UNIT] = headUnit
        }
    }
}