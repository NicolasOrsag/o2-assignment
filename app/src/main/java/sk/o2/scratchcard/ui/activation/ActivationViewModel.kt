package sk.o2.scratchcard.ui.activation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sk.o2.scratchcard.domain.model.ScratchCardState
import sk.o2.scratchcard.domain.repository.ScratchCardRepository
import sk.o2.scratchcard.domain.usecase.ActivateCardUseCase
import javax.inject.Inject

sealed interface ActivationUiState {
    data object Idle : ActivationUiState
    data object Loading : ActivationUiState
    data object Success : ActivationUiState
    data class Error(val message: String) : ActivationUiState
}

@HiltViewModel
class ActivationViewModel @Inject constructor(
    private val activateCardUseCase: ActivateCardUseCase,
    repository: ScratchCardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ActivationUiState>(ActivationUiState.Idle)
    val uiState: StateFlow<ActivationUiState> = _uiState.asStateFlow()

    val scratchCard = repository.scratchCard

    fun activate() {
        if (_uiState.value is ActivationUiState.Loading) return

        // The ActivateCardUseCase uses NonCancellable internally,
        // so the API call will complete even if the ViewModel is cleared.
        viewModelScope.launch {
            _uiState.value = ActivationUiState.Loading
            activateCardUseCase()
                .onSuccess {
                    _uiState.value = ActivationUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = ActivationUiState.Error(
                        error.message ?: "Activation failed"
                    )
                }
        }
    }

    fun dismissError() {
        _uiState.value = ActivationUiState.Idle
    }
}
