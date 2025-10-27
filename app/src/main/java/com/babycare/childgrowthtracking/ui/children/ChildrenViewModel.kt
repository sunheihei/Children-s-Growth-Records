package com.babycare.childgrowthtracking.ui.children

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Update
import com.babycare.childgrowthtracking.model.Child
import com.babycare.childgrowthtracking.model.ChildWithLatestRecord
import com.babycare.childgrowthtracking.ui.growthrecord.GrowthRecordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


sealed interface ChildrenUiState {
    data class Success(val childrenWithLatestRecord: List<ChildWithLatestRecord>) : ChildrenUiState
    data class Error(val message: String) : ChildrenUiState
    object Loading : ChildrenUiState
}

sealed interface ChildUiState {
    data class Success(val child: Child) : ChildUiState
    data class Error(val message: String) : ChildUiState
    object Loading : ChildUiState
}


@HiltViewModel
class ChildrenViewModel @Inject constructor(private val childrenRepository: ChildrenRepository) :
    ViewModel(), DefaultLifecycleObserver {

    // 获取用户信息的方法
    val uiStateChildren = childrenRepository.getChildrenWithLatestRecord()
        .map<List<ChildWithLatestRecord>, ChildrenUiState> {
            ChildrenUiState.Success(it) }
        .catch { emit(ChildrenUiState.Error("Failed to fetch children: ${it.message}")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ChildrenUiState.Loading
        )


    private val _selectedChildId = MutableStateFlow<Int?>(null)

    val uiStateChild = _selectedChildId
        .filterNotNull()
        .flatMapLatest { id ->
            childrenRepository.getChildById(id)
                .map<Child, ChildUiState> { ChildUiState.Success(it) }
                .catch { emit(ChildUiState.Error("Child not found: ${it.message}")) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ChildUiState.Loading
        )

    fun getChildById(id: Int) {
        _selectedChildId.value = id
    }


    fun addChildren(child: Child) {
        viewModelScope.launch {
            childrenRepository.addChildren(child)
        }
    }


    fun deleteChildren(child: Child) {
        viewModelScope.launch {
            childrenRepository.deleteChildren(child)
        }
    }

    fun updateChildren(child: Child) {
        viewModelScope.launch {
            childrenRepository.updateChildren(child)
        }
    }


    fun saveOrUpdateChild(
        id: Int?, // 若为 null 或 -1 表示新增
        name: String,
        gender: Int,
        avatar: String,
        age: Long,
        note: String,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                val child = Child(
                    id = if (id == null || id == -1) 0 else id,
                    name = name.trim(),
                    gender = gender,
                    avatar = avatar,
                    age = age,
                    note = note.trim()
                )
                if (id == null || id == -1) {
                    childrenRepository.addChildren(child)
                } else {
                    childrenRepository.updateChildren(child)
                }
                onSuccess?.invoke()
            } catch (e: Exception) {
                onError?.invoke("保存失败：${e.message}")
            }
        }
    }

}