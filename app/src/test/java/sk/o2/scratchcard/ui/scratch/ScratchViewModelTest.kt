package sk.o2.scratchcard.ui.scratch

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
import sk.o2.scratchcard.domain.usecase.ScratchCardUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class ScratchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var scratchCardUseCase: ScratchCardUseCase
    private lateinit var repository: ScratchCardRepository
    private lateinit var scratchCardFlow: MutableStateFlow<ScratchCard>
    private lateinit var viewModel: ScratchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        scratchCardUseCase = mockk()
        repository = mockk()
        scratchCardFlow = MutableStateFlow(ScratchCard())
        every { repository.scratchCard } returns scratchCardFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        viewModel = ScratchViewModel(scratchCardUseCase, repository)

        assertEquals(ScratchUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `scratch updates state to Success on success`() = runTest {
        coEvery { scratchCardUseCase() } returns Result.success("test-code")
        viewModel = ScratchViewModel(scratchCardUseCase, repository)

        viewModel.scratch()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ScratchUiState.Success)
        assertEquals("test-code", (state as ScratchUiState.Success).code)
    }

    @Test
    fun `scratch updates state to Error on failure`() = runTest {
        coEvery { scratchCardUseCase() } returns
            Result.failure(IllegalStateException("Already scratched"))
        viewModel = ScratchViewModel(scratchCardUseCase, repository)

        viewModel.scratch()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ScratchUiState.Error)
        assertEquals("Already scratched", (state as ScratchUiState.Error).message)
    }

    @Test
    fun `scratch sets loading state immediately`() = runTest {
        coEvery { scratchCardUseCase() } returns Result.success("code")
        viewModel = ScratchViewModel(scratchCardUseCase, repository)

        viewModel.scratch()

        // Before advancing, should be loading
        assertEquals(ScratchUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `duplicate scratch calls are ignored while loading`() = runTest {
        coEvery { scratchCardUseCase() } returns Result.success("code")
        viewModel = ScratchViewModel(scratchCardUseCase, repository)

        viewModel.scratch()
        viewModel.scratch() // should be ignored

        advanceUntilIdle()

        io.mockk.coVerify(exactly = 1) { scratchCardUseCase() }
    }
}
