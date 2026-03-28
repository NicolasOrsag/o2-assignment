package sk.o2.scratchcard.ui.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import sk.o2.scratchcard.domain.model.ScratchCard
import sk.o2.scratchcard.domain.repository.ScratchCardRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    repository: ScratchCardRepository
) : ViewModel() {
    val scratchCard: StateFlow<ScratchCard> = repository.scratchCard
}
