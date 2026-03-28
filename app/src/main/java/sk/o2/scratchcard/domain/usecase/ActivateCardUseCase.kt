package sk.o2.scratchcard.domain.usecase

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import sk.o2.scratchcard.domain.model.ScratchCardState
import sk.o2.scratchcard.domain.repository.ScratchCardRepository
import javax.inject.Inject

class ActivateCardUseCase @Inject constructor(
    private val repository: ScratchCardRepository
) {
    suspend operator fun invoke(): Result<Unit> = withContext(NonCancellable) {
        val card = repository.scratchCard.value
        if (card.state != ScratchCardState.SCRATCHED) {
            return@withContext Result.failure(
                IllegalStateException("Card must be in scratched state to activate")
            )
        }
        val code = card.code
            ?: return@withContext Result.failure(IllegalStateException("No code available"))
        repository.activate(code)
    }
}
