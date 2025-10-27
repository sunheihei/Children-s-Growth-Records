package com.babycare.childgrowthtracking.ui.growthrecord

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babycare.childgrowthtracking.model.Child
import com.babycare.childgrowthtracking.model.GrowthRecord
import com.babycare.childgrowthtracking.ui.children.ChildUiState
import com.babycare.childgrowthtracking.ui.children.ChildrenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface GrowthRecordListUiState {
    data class Success(val growthRecordList: List<GrowthRecord>) : GrowthRecordListUiState
    data class Error(val message: String) : GrowthRecordListUiState
    object Loading : GrowthRecordListUiState
}

sealed interface GrowthRecordUiState {
    data class Success(val growthRecord: GrowthRecord) : GrowthRecordUiState
    data class Error(val message: String) : GrowthRecordUiState
    object Loading : GrowthRecordUiState
}


// 合并状态的密封类
sealed class CombinedUiState {
    object Loading : CombinedUiState()
    object Error : CombinedUiState()
    data class Success(val child: Child, val growthRecords: List<GrowthRecord>) : CombinedUiState()
}


@HiltViewModel
class GrowthRecordViewModel @Inject constructor(
    private val growthRepository: GrowthRepository,
    private val childrenRepository: ChildrenRepository
) :
    ViewModel(), DefaultLifecycleObserver {

    private val _selectedChildId = MutableStateFlow<Int?>(null)


    val uiStateGrowthRecords = _selectedChildId
        .filterNotNull()
        .flatMapLatest { childId ->
            growthRepository.getGrowthRecordListByChildId(childId)
                .map<List<GrowthRecord>, GrowthRecordListUiState> {
                    GrowthRecordListUiState.Success(
                        it
                    )
                }
                .catch { emit(GrowthRecordListUiState.Error("Failed to fetch children: ${it.message}")) }
        }.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = GrowthRecordListUiState.Loading
        )

    private val _selectedGrowthRecordId = MutableStateFlow<Int?>(null)

    val uiStateGrowthRecord = _selectedGrowthRecordId
        .filterNotNull()
        .flatMapLatest { recordId ->
            growthRepository.getGrowthRecordsById(recordId)
                .map<GrowthRecord, GrowthRecordUiState> {
                    GrowthRecordUiState.Success(
                        it
                    )
                }
                .catch { emit(GrowthRecordUiState.Error("Failed to fetch children: ${it.message}")) }
        }.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = GrowthRecordUiState.Loading
        )

    fun getGrowRecord(id: Int) {
        _selectedGrowthRecordId.value = id
    }


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

    // 合并状态的 StateFlow
    val combinedUiState = combine(
        uiStateChild,
        uiStateGrowthRecords
    ) { childState, growthRecordsState ->
        when {
            childState is ChildUiState.Loading || growthRecordsState is GrowthRecordListUiState.Loading -> {
                CombinedUiState.Loading
            }

            childState is ChildUiState.Error || growthRecordsState is GrowthRecordListUiState.Error -> {
                CombinedUiState.Error
            }

            childState is ChildUiState.Success && growthRecordsState is GrowthRecordListUiState.Success -> {
                CombinedUiState.Success(
                    child = childState.child,
                    growthRecords = growthRecordsState.growthRecordList
                )
            }

            else -> CombinedUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CombinedUiState.Loading
    )

    // 建议将这个方法重命名为更通用的名称
    fun loadChildData(childId: Int) {
        _selectedChildId.value = childId
    }


    fun addGrowthRecord(growthRecord: GrowthRecord) {
        viewModelScope.launch {
            growthRepository.addGrowthRecord(growthRecord)
        }
    }


    fun deleteGrowthRecord(growthRecord: GrowthRecord) {
        viewModelScope.launch {
            growthRepository.deleteGrowthRecord(growthRecord)
        }
    }

    fun updateGrowthRecord(growthRecord: GrowthRecord) {
        viewModelScope.launch {
            growthRepository.updateGrowthRecord(growthRecord)
        }
    }


}

