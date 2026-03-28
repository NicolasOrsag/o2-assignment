package sk.o2.scratchcard.ui.scratch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sk.o2.scratchcard.domain.model.ScratchCardState
import sk.o2.scratchcard.domain.repository.ScratchCardRepository
import sk.o2.scratchcard.domain.usecase.ScratchCardUseCase
import javax.inject.Inject

sealed interface ScratchUiState {
    data object Idle : ScratchUiState
    data object Loading : ScratchUiState
    data class Success(val code: String) : ScratchUiState
    data class Error(val message: String) : ScratchUiState
}

@HiltViewModel
class ScratchViewModel @Inject constructor(
    private val scratchCardUseCase: ScratchCardUseCase,
    repository: ScratchCardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScratchUiState>(ScratchUiState.Idle)
    val uiState: StateFlow<ScratchUiState> = _uiState.asStateFlow()

    val scratchCard = repository.scratchCard

    fun scratch() {
        if (_uiState.value is ScratchUiState.Loading) return

        // This coroutine is tied to viewModelScope, so it gets cancelled
        // when the ViewModel is cleared (user navigates back).
        viewModelScope.launch {
            _uiState.value = ScratchUiState.Loading
            scratchCardUseCase()
                .onSuccess { code ->
                    _uiState.value = ScratchUiState.Success(code)
                }
                .onFailure { error ->
                    _uiState.value = ScratchUiState.Error(
                        error.message ?: "Unknown error"
                    )
                }
        }
    }
}
