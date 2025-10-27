package com.babycare.childgrowthtracking.ui.growthdiary

import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babycare.childgrowthtracking.model.Child
import com.babycare.childgrowthtracking.model.DiaryPhoto
import com.babycare.childgrowthtracking.model.DiaryWithPhotos
import com.babycare.childgrowthtracking.model.GrowthDiary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


sealed interface GrowthDiaryListUiState {
    data class Success(val growthDiaryList: List<DiaryWithPhotos>) : GrowthDiaryListUiState
    data class Error(val message: String) : GrowthDiaryListUiState
    object Loading : GrowthDiaryListUiState
}

sealed interface GrowthDiaryUiState {
    data class Success(val growthDiary: DiaryWithPhotos) : GrowthDiaryUiState
    data class Error(val message: String) : GrowthDiaryUiState
    object Loading : GrowthDiaryUiState
}

// 合并状态的密封类
sealed class CombinedDiaryUiState {
    object Loading : CombinedDiaryUiState()
    object Error : CombinedDiaryUiState()
    data class Success(val child: Child, val diaryWithPhotos: DiaryWithPhotos) :
        CombinedDiaryUiState()
}

@HiltViewModel
class GrowthDiaryViewModel @Inject constructor(
    private val growthRepository: GrowthDiaryRepository
) : ViewModel(), DefaultLifecycleObserver {

    private val _selectedChildId = MutableStateFlow<Int?>(null)

    // 加载ChildId所有日记及其图片
    fun getDiariesWithPhotosByChildId(id: Int) {
        _selectedChildId.value = id
    }

    val uiStateGrowthDiarys = _selectedChildId
        .filterNotNull()
        .flatMapLatest { id ->
            growthRepository.getDiariesWithPhotosByChildId(id)
                .map<List<DiaryWithPhotos>, GrowthDiaryListUiState> {
                    GrowthDiaryListUiState.Success(it)
                }.catch {
                    emit(GrowthDiaryListUiState.Error("Failed to fetch children: ${it.message}"))
                }
        }.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = GrowthDiaryListUiState.Loading
        )


    private val _selectedDiaryId = MutableStateFlow<Int?>(null)

    // 根据 ID 加载单篇日记及其图片
    fun getDiaryWithPhotosById(diaryId: Int) {
        _selectedDiaryId.value = diaryId
    }

    val uiStateGrowthDiary = _selectedDiaryId
        .filterNotNull()
        .flatMapLatest { diaryId ->
            growthRepository.getDiaryWithPhotosById(diaryId)
                .map<DiaryWithPhotos?, GrowthDiaryUiState> { diaryWithPhotos ->
                    if (diaryWithPhotos == null) {
                        GrowthDiaryUiState.Error("Diary not found")
                    } else {
                        GrowthDiaryUiState.Success(diaryWithPhotos)
                    }
                }.catch {
                    emit(GrowthDiaryUiState.Error("Failed to fetch diary: ${it.message}"))
                }

        }.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = GrowthDiaryUiState.Loading
        )


    // 插入新日记及其图片
    fun insertDiaryWithPhotos(
        diary: GrowthDiary,
        photos: List<DiaryPhoto>,
        onSuccess: () -> Unit = {} // 回调用于 UI 刷新
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                growthRepository.insertDiaryWithPhotos(diary, photos)
                onSuccess() // 调用回调通知 UI
            } catch (e: Exception) {
            }
        }
    }

    fun deleteGrowthDiary(growthDiary: GrowthDiary) {
        viewModelScope.launch {
            growthRepository.deleteGrowthDiary(growthDiary)
        }
    }

    // 更新日记及其图片
    fun updateDiaryWithPhotos(
        diary: GrowthDiary,
        photosToAdd: List<DiaryPhoto> = emptyList(),
        photosToDelete: List<DiaryPhoto> = emptyList(),
        onSuccess: () -> Unit = {} // 回调用于 UI 刷新
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                growthRepository.updateDiaryWithPhotos(diary, photosToAdd, photosToDelete)
                onSuccess() // 调用回调通知 UI
            } catch (e: Exception) {

            }
        }
    }


}

