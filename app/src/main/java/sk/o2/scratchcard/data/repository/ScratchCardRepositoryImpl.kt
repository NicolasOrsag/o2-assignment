package sk.o2.scratchcard.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import sk.o2.scratchcard.data.api.O2ApiService
import sk.o2.scratchcard.domain.model.ScratchCard
import sk.o2.scratchcard.domain.model.ScratchCardState
import sk.o2.scratchcard.domain.repository.ScratchCardRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScratchCardRepositoryImpl @Inject constructor(
    private val apiService: O2ApiService
) : ScratchCardRepository {

    private val _scratchCard = MutableStateFlow(ScratchCard())
    override val scratchCard: StateFlow<ScratchCard> = _scratchCard.asStateFlow()

    override suspend fun scratch(): String {
        val code = UUID.randomUUID().toString()
        _scratchCard.update {
            it.copy(state = ScratchCardState.SCRATCHED, code = code)
        }
        return code
    }

    override suspend fun activate(code: String): Result<Unit> {
        return try {
            val response = apiService.getVersion(code)
            val androidVersion = response.android.toLongOrNull()
                ?: return Result.failure(IllegalStateException("Invalid response format"))

            if (androidVersion > 277028) {
                _scratchCard.update { it.copy(state = ScratchCardState.ACTIVATED) }
                Result.success(Unit)
            } else {
                Result.failure(ActivationException("Activation failed: version $androidVersion is not greater than 277028"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class ActivationException(message: String) : Exception(message)
