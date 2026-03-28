package sk.o2.scratchcard.domain.usecase

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import sk.o2.scratchcard.domain.model.ScratchCard
import sk.o2.scratchcard.domain.model.ScratchCardState
import sk.o2.scratchcard.domain.repository.ScratchCardRepository

@OptIn(ExperimentalCoroutinesApi::class)
class ScratchCardUseCaseTest {

    private lateinit var repository: ScratchCardRepository
    private lateinit var useCase: ScratchCardUseCase
    private lateinit var scratchCardFlow: MutableStateFlow<ScratchCard>

    @Before
    fun setup() {
        repository = mockk()
        scratchCardFlow = MutableStateFlow(ScratchCard())
        every { repository.scratchCard } returns scratchCardFlow
        useCase = ScratchCardUseCase(repository)
    }

    @Test
    fun `scratch returns code after delay`() = runTest {
        val expectedCode = "test-uuid"
        coEvery { repository.scratch() } returns expectedCode

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(expectedCode, result.getOrNull())
    }

    @Test
    fun `scratch fails if card is already scratched`() = runTest {
        scratchCardFlow.value = ScratchCard(
            state = ScratchCardState.SCRATCHED,
            code = "existing"
        )

        val result = useCase()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `scratch fails if card is already activated`() = runTest {
        scratchCardFlow.value = ScratchCard(
            state = ScratchCardState.ACTIVATED,
            code = "existing"
        )

        val result = useCase()

        assertTrue(result.isFailure)
    }

    @Test
    fun `scratch is cancellable during delay`() = runTest {
        coEvery { repository.scratch() } returns "code"

        val job = launch { useCase() }

        advanceTimeBy(1000) // halfway through the 2s delay
        job.cancelAndJoin()

        // Repository.scratch() should never have been called since we cancelled during delay
        io.mockk.coVerify(exactly = 0) { repository.scratch() }
    }

    @Test
    fun `scratch propagates repository exception as failure`() = runTest {
        coEvery { repository.scratch() } throws RuntimeException("Network error")

        val result = useCase()

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
