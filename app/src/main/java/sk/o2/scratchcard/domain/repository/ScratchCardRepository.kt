package sk.o2.scratchcard.domain.repository

import kotlinx.coroutines.flow.StateFlow
import sk.o2.scratchcard.domain.model.ScratchCard

interface ScratchCardRepository {
    val scratchCard: StateFlow<ScratchCard>
    suspend fun scratch(): String
    suspend fun activate(code: String): Result<Unit>
}
