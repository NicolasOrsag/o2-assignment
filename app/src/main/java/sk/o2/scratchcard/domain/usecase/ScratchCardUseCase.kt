package sk.o2.scratchcard.domain.usecase

import kotlinx.coroutines.delay
import sk.o2.scratchcard.domain.model.ScratchCardState
import sk.o2.scratchcard.domain.repository.ScratchCardRepository
import javax.inject.Inject

class ScratchCardUseCase @Inject constructor(
    private val repository: ScratchCardRepository
) {
    suspend operator fun invoke(): Result<String> {
        val currentState = repository.scratchCard.value.state
        if (currentState != ScratchCardState.UNSCRATCHED) {
            return Result.failure(IllegalStateException("Card is already scratched"))
        }
        delay(2000)
        return try {
            val code = repository.scratch()
            Result.success(code)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
