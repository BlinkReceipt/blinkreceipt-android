package com.blinkreceipt.ocr.ui.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microblink.BlinkReceiptSdk
import com.microblink.core.BlinkReceiptCoreSdk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val _isLoading: StateFlow<Boolean> = savedStateHandle.getStateFlow("isLoading", false)
    val uiState: StateFlow<HomeUiState> =
        combine(
            _isLoading,
            flowOf(Unit)
        ) { isLoading, _ ->
            HomeUiState(
                isLoading = isLoading,
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = HomeUiState(),
            )

    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    fun handleAction(action: HomeAction) {
        viewModelScope.launch {
            when (action) {
                is HomeAction.StartCustomScan -> {
                    _events.emit(HomeEvent.OnStartCustomScan)
                }
                is HomeAction.StartOobCameraScan -> {
                    _events.emit(HomeEvent.OnStartOobCameraScan)
                }
                is HomeAction.DisplayVersion -> {
                    _events.emit(
                        HomeEvent.OnDisplayVersion(
                            BlinkReceiptSdk.versionName(BlinkReceiptCoreSdk.applicationContext())
                        )
                    )
                }
            }
        }
    }

}