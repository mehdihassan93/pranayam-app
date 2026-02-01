package com.pranayam.app.viewmodel

import app.cash.turbine.test
import com.pranayam.app.data.model.Profile
import com.pranayam.app.di.UserSessionManager
import com.pranayam.app.repository.PranayamRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private val repository: PranayamRepository = mockk(relaxed = true)
    private val sessionManager: UserSessionManager = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private val testProfiles = listOf(
        Profile(
            id = "1",
            name = "Test User",
            age = 25,
            photos = listOf("photo.jpg"),
            profession = "Engineer",
            distance = 5,
            isVerified = true,
            hasVideo = false,
            prompts = emptyList()
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { sessionManager.getUserId() } returns "test-user-id"
        coEvery { repository.getDiscoveryProfiles(any(), any(), any()) } returns flowOf(Result.success(testProfiles))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty profiles`() = runTest {
        viewModel = HomeViewModel(repository, sessionManager)
        assertTrue(viewModel.profiles.value.isEmpty())
    }

    @Test
    fun `initial current index is zero`() = runTest {
        viewModel = HomeViewModel(repository, sessionManager)
        assertEquals(0, viewModel.currentIndex.value)
    }

    @Test
    fun `loadProfiles updates profiles state`() = runTest {
        viewModel = HomeViewModel(repository, sessionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.profiles.test {
            val profiles = awaitItem()
            assertEquals(1, profiles.size)
            assertEquals("Test User", profiles[0].name)
        }
    }

    @Test
    fun `like increments current index`() = runTest {
        coEvery { repository.swipeProfile(any(), any(), any()) } returns Result.success(
            com.pranayam.app.data.model.LikeResponse(false, null)
        )

        viewModel = HomeViewModel(repository, sessionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        val initialIndex = viewModel.currentIndex.value
        viewModel.like()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(initialIndex + 1, viewModel.currentIndex.value)
    }

    @Test
    fun `pass increments current index`() = runTest {
        coEvery { repository.swipeProfile(any(), any(), any()) } returns Result.success(
            com.pranayam.app.data.model.LikeResponse(false, null)
        )

        viewModel = HomeViewModel(repository, sessionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        val initialIndex = viewModel.currentIndex.value
        viewModel.pass()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(initialIndex + 1, viewModel.currentIndex.value)
    }

    @Test
    fun `undo decrements current index when greater than zero`() = runTest {
        coEvery { repository.swipeProfile(any(), any(), any()) } returns Result.success(
            com.pranayam.app.data.model.LikeResponse(false, null)
        )

        viewModel = HomeViewModel(repository, sessionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        // First move forward by liking
        viewModel.like()
        testDispatcher.scheduler.advanceUntilIdle()

        // Index should be 1 after like
        assertEquals(1, viewModel.currentIndex.value)

        // Undo should go back to 0
        viewModel.undo()
        assertEquals(0, viewModel.currentIndex.value)
    }

    @Test
    fun `undo does nothing when index is zero`() = runTest {
        viewModel = HomeViewModel(repository, sessionManager)

        viewModel.undo()

        assertEquals(0, viewModel.currentIndex.value)
    }

    @Test
    fun `error state is set on repository failure`() = runTest {
        coEvery { repository.getDiscoveryProfiles(any(), any(), any()) } returns flowOf(
            Result.failure(Exception("Network error"))
        )

        viewModel = HomeViewModel(repository, sessionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.error.value)
        assertTrue(viewModel.error.value!!.contains("Network"))
    }
}
