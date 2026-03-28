package sk.o2.scratchcard.ui.activation

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import sk.o2.scratchcard.domain.model.ScratchCard
import sk.o2.scratchcard.domain.model.ScratchCardState
import sk.o2.scratchcard.domain.repository.ScratchCardRepository
import sk.o2.scratchcard.domain.usecase.ActivateCardUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class ActivationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var activateCardUseCase: ActivateCardUseCase
    private lateinit var repository: ScratchCardRepository
    private lateinit var scratchCardFlow: MutableStateFlow<ScratchCard>
    private lateinit var viewModel: ActivationViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        activateCardUseCase = mockk()
        repository = mockk()
        scratchCardFlow = MutableStateFlow(
            ScratchCard(state = ScratchCardState.SCRATCHED, code = "test-code")
        )
        every { repository.scratchCard } returns scratchCardFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        viewModel = ActivationViewModel(activateCardUseCase, repository)

        assertEquals(ActivationUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `activate updates state to Success on success`() = runTest {
        coEvery { activateCardUseCase() } returns Result.success(Unit)
        viewModel = ActivationViewModel(activateCardUseCase, repository)

        viewModel.activate()
        advanceUntilIdle()

        assertEquals(ActivationUiState.Success, viewModel.uiState.value)
    }

    @Test
    fun `activate updates state to Error on failure`() = runTest {
        coEvery { activateCardUseCase() } returns
            Result.failure(RuntimeException("Activation failed"))
        viewModel = ActivationViewModel(activateCardUseCase, repository)

        viewModel.activate()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ActivationUiState.Error)
        assertEquals("Activation failed", (state as ActivationUiState.Error).message)
    }

    @Test
    fun `activate sets loading state immediately`() = runTest {
        coEvery { activateCardUseCase() } returns Result.success(Unit)
        viewModel = ActivationViewModel(activateCardUseCase, repository)

        viewModel.activate()

        assertEquals(ActivationUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `dismissError resets to Idle`() = runTest {
        coEvery { activateCardUseCase() } returns
            Result.failure(RuntimeException("Error"))
        viewModel = ActivationViewModel(activateCardUseCase, repository)

        viewModel.activate()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is ActivationUiState.Error)

        viewModel.dismissError()

        assertEquals(ActivationUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `duplicate activate calls are ignored while loading`() = runTest {
        coEvery { activateCardUseCase() } returns Result.success(Unit)
        viewModel = ActivationViewModel(activateCardUseCase, repository)

        viewModel.activate()
        viewModel.activate() // should be ignored

        advanceUntilIdle()

        io.mockk.coVerify(exactly = 1) { activateCardUseCase() }
    }
}
