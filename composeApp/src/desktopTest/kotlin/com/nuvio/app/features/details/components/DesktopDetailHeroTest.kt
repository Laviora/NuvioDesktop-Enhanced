package com.nuvio.app.features.details.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.nuvio.app.features.details.MetaDetails
import kotlin.test.Test
import kotlin.test.assertTrue
import org.junit.Rule

class DesktopDetailHeroTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun randomEpisodeActionIsVisibleAndInvokesPlayback() {
        var randomEpisodeRequested = false

        compose.setContent {
            DesktopDetailHero(
                meta = MetaDetails(id = "series", type = "series", name = "Series"),
                showOverallRatings = false,
                isMdbListActive = false,
                playButtonLabel = "Play",
                iconActionRow = true,
                isSaved = false,
                isWatched = false,
                onHeightChanged = {},
                heroTrailerSourceUrl = null,
                heroTrailerReady = false,
                heroTrailerMuted = true,
                onHeroTrailerMuteToggle = {},
                onPlayClick = {},
                onPlayFromStartClick = null,
                onRandomEpisodeClick = { randomEpisodeRequested = true },
                onPlayLongClick = null,
                onWatchedClick = {},
                onSaveClick = {},
                onSaveLongClick = null,
            )
        }

        compose.onNodeWithContentDescription("Play random episode")
            .assertIsDisplayed()
            .performClick()

        assertTrue(randomEpisodeRequested)
    }

    @Test
    fun randomEpisodeActionIsAvailableFromTheOverflowLayout() {
        var randomEpisodeRequested = false

        compose.setContent {
            DesktopDetailHero(
                meta = MetaDetails(id = "series", type = "series", name = "Series"),
                showOverallRatings = false,
                isMdbListActive = false,
                playButtonLabel = "Play",
                iconActionRow = false,
                isSaved = false,
                isWatched = false,
                onHeightChanged = {},
                heroTrailerSourceUrl = null,
                heroTrailerReady = false,
                heroTrailerMuted = true,
                onHeroTrailerMuteToggle = {},
                onPlayClick = {},
                onPlayFromStartClick = null,
                onRandomEpisodeClick = { randomEpisodeRequested = true },
                onPlayLongClick = null,
                onWatchedClick = {},
                onSaveClick = {},
                onSaveLongClick = null,
            )
        }

        compose.onNodeWithContentDescription("More actions").performClick()
        compose.onNodeWithContentDescription("Play random episode")
            .assertIsDisplayed()
            .performClick()

        assertTrue(randomEpisodeRequested)
    }
}
