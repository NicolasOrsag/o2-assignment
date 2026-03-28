package sk.o2.scratchcard.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import sk.o2.scratchcard.domain.model.ScratchCard
import sk.o2.scratchcard.domain.model.ScratchCardState
import sk.o2.scratchcard.domain.repository.ScratchCardRepository

@OptIn(ExperimentalCoroutinesApi::class)
class ActivateCardUseCaseTest {

    private lateinit var repository: ScratchCardRepository
    private lateinit var useCase: ActivateCardUseCase
    private lateinit var scratchCardFlow: MutableStateFlow<ScratchCard>

    @Before
    fun setup() {
        repository = mockk()
        scratchCardFlow = MutableStateFlow(
            ScratchCard(state = ScratchCardState.SCRATCHED, code = "test-code")
        )
        every { repository.scratchCard } returns scratchCardFlow
        useCase = ActivateCardUseCase(repository)
    }

    @Test
    fun `activate succeeds when API returns success`() = runTest {
        coEvery { repository.activate("test-code") } returns Result.success(Unit)

        val result = useCase()

        assertTrue(result.isSuccess)
        coVerify { repository.activate("test-code") }
    }

    @Test
    fun `activate fails when card is unscratched`() = runTest {
        scratchCardFlow.value = ScratchCard(state = ScratchCardState.UNSCRATCHED)

        val result = useCase()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `activate fails when card is already activated`() = runTest {
        scratchCardFlow.value = ScratchCard(
            state = ScratchCardState.ACTIVATED,
            code = "test-code"
        )

        val result = useCase()

        assertTrue(result.isFailure)
    }

    @Test
    fun `activate is not cancelled when coroutine is cancelled`() = runTest {
        coEvery { repository.activate("test-code") } returns Result.success(Unit)

        val job = launch { useCase() }
        // Let the coroutine start and enter the NonCancellable block
        advanceUntilIdle()
        job.cancel()
        advanceUntilIdle()

        // The activate call should still have been made due to NonCancellable
        coVerify { repository.activate("test-code") }
    }

    @Test
    fun `activate propagates API failure`() = runTest {
        coEvery { repository.activate("test-code") } returns
            Result.failure(RuntimeException("API error"))

        val result = useCase()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message == "API error")
    }
}
