package com.blinkreceipt.ocr.ui.feature.results

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blinkreceipt.ocr.ui.MainDestinations
import com.blinkreceipt.ocr.ui.feature.home.data.ProductItemsFileService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val productItemsFileService: ProductItemsFileService,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    val uiState: StateFlow<ResultsUiState> = combine(
        savedStateHandle.getStateFlow(KEY_BLINKRECEIPT_ID, "")
            .filter { it.isNotEmpty() }
            .distinctUntilChanged()
            .map { blinkreceiptId ->
                productItemsFileService
                    .get(blinkreceiptId)
                    .distinct()
            },
        flowOf(Unit),
    ) { productItems, _ ->
        ResultsUiState(
            productItems = productItems,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ResultsUiState(),
    )

    private val _events = MutableSharedFlow<ResultsEvent>()
    val events: SharedFlow<ResultsEvent> = _events.asSharedFlow()

    fun handleAction(action: ResultsAction) {
        viewModelScope.launch {
            when(action) {
                is ResultsAction.Dismiss -> {
                    // Delete the Results file
                    savedStateHandle.get<String>(KEY_BLINKRECEIPT_ID)?.let { blinkreceiptId ->
                        productItemsFileService.delete(blinkreceiptId)
                    }

                    // Then, dismiss
                    _events.emit(ResultsEvent.OnDismiss)
                }
            }
        }
    }

    companion object {
        private const val KEY_BLINKRECEIPT_ID = MainDestinations.Results.KEY_BLINKRECEIPT_ID
    }
}