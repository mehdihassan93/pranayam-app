package com.pranayam.app.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.pranayam.app.data.model.Profile
import com.pranayam.app.ui.theme.PranayamTheme
import org.junit.Rule
import org.junit.Test

/**
 * INTEGRATION TEST: HomeScreen (Matchmaking UI)
 * Ensures the swipe interaction and action buttons are responsive and trigger 
 * the correct callbacks. This is vital for a solo dev to ensure core UX doesn't break.
 */
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockProfiles = listOf(
        Profile(
            id = "1",
            name = "Priya",
            age = 23,
            photos = listOf("https://example.com/photo1.jpg"),
            profession = "Architect",
            distance = 5,
            isVerified = true,
            hasVideo = false,
            prompts = emptyList(),
            bio = "Love designing spaces..."
        ),
        Profile(
            id = "2",
            name = "Rahul",
            age = 25,
            photos = listOf("https://example.com/photo2.jpg"),
            profession = "Software Engineer",
            distance = 12,
            isVerified = false,
            hasVideo = false,
            prompts = emptyList(),
            bio = "Tech enthusiast..."
        )
    )

    @Test
    fun homeScreen_displayProfile_correctly() {
        startHomeScreen(0)

        // Verify Priya is displayed
        composeTestRule.onNodeWithText("Priya, 23").assertExists()
        composeTestRule.onNodeWithText("Architect").assertExists()
        composeTestRule.onNodeWithText("5 km away").assertExists()
    }

    @Test
    fun homeScreen_swipeLike_triggersCallback() {
        var likeTriggered = false

        startHomeScreen(currentIndex = 0, onLike = { likeTriggered = true })

        // Find the Like button
        composeTestRule.onNodeWithContentDescription("Like").performClick()

        // Verify the callback was triggered
        assert(likeTriggered)
    }

    @Test
    fun homeScreen_swipePass_triggersCallback() {
        var passTriggered = false

        startHomeScreen(currentIndex = 0, onPass = { passTriggered = true })

        // Find the Pass button (Close icon)
        composeTestRule.onNodeWithContentDescription("Close").performClick()

        // Verify the callback was triggered
        assert(passTriggered)
    }

    private fun startHomeScreen(
        currentIndex: Int,
        onLike: () -> Unit = {},
        onPass: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            PranayamTheme {
                HomeScreen(
                    profiles = mockProfiles,
                    currentIndex = currentIndex,
                    onProfileClick = {},
                    onLike = onLike,
                    onPass = onPass,
                    onSuperLike = {},
                    onMessage = {},
                    onUndo = {}
                )
            }
        }
    }
}
