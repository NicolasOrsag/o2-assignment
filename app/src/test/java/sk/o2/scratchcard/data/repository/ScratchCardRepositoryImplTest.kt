package sk.o2.scratchcard.data.repository

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import sk.o2.scratchcard.data.api.O2ApiService
import sk.o2.scratchcard.data.api.VersionResponse
import sk.o2.scratchcard.domain.model.ScratchCardState

class ScratchCardRepositoryImplTest {

    private lateinit var apiService: O2ApiService
    private lateinit var repository: ScratchCardRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        repository = ScratchCardRepositoryImpl(apiService)
    }

    @Test
    fun `initial state is unscratched with no code`() {
        val card = repository.scratchCard.value

        assertEquals(ScratchCardState.UNSCRATCHED, card.state)
        assertEquals(null, card.code)
    }

    @Test
    fun `scratch generates UUID and updates state`() = runTest {
        val code = repository.scratch()

        assertNotNull(code)
        assertTrue(code.isNotEmpty())

        val card = repository.scratchCard.value
        assertEquals(ScratchCardState.SCRATCHED, card.state)
        assertEquals(code, card.code)
    }

    @Test
    fun `activate updates state when API returns version above threshold`() = runTest {
        repository.scratch() // first scratch the card
        val code = repository.scratchCard.value.code!!

        coEvery { apiService.getVersion(code) } returns VersionResponse("287028")

        val result = repository.activate(code)

        assertTrue(result.isSuccess)
        assertEquals(ScratchCardState.ACTIVATED, repository.scratchCard.value.state)
    }

    @Test
    fun `activate fails when API returns version at threshold`() = runTest {
        repository.scratch()
        val code = repository.scratchCard.value.code!!

        coEvery { apiService.getVersion(code) } returns VersionResponse("277028")

        val result = repository.activate(code)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ActivationException)
        assertEquals(ScratchCardState.SCRATCHED, repository.scratchCard.value.state)
    }

    @Test
    fun `activate fails when API returns version below threshold`() = runTest {
        repository.scratch()
        val code = repository.scratchCard.value.code!!

        coEvery { apiService.getVersion(code) } returns VersionResponse("100000")

        val result = repository.activate(code)

        assertTrue(result.isFailure)
        assertEquals(ScratchCardState.SCRATCHED, repository.scratchCard.value.state)
    }

    @Test
    fun `activate fails when API returns non-numeric version`() = runTest {
        repository.scratch()
        val code = repository.scratchCard.value.code!!

        coEvery { apiService.getVersion(code) } returns VersionResponse("invalid")

        val result = repository.activate(code)

        assertTrue(result.isFailure)
        assertEquals(ScratchCardState.SCRATCHED, repository.scratchCard.value.state)
    }

    @Test
    fun `activate wraps network exception in failure`() = runTest {
        repository.scratch()
        val code = repository.scratchCard.value.code!!

        coEvery { apiService.getVersion(code) } throws RuntimeException("Network error")

        val result = repository.activate(code)

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
