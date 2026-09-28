package com.nuvio.app.features.details.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.nuvio.app.features.home.MetaPreview
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertTrue

class DetailPosterRailSectionTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `view all action is displayed and invokes its callback`() {
        var clicked = false
        compose.setContent {
            MaterialTheme {
                DetailPosterRailSection(
                    title = "More Like This",
                    items = listOf(MetaPreview(id = "tmdb:1", type = "movie", name = "Movie")),
                    watchedKeys = emptySet(),
                    onViewAllClick = { clicked = true },
                )
            }
        }

        compose.onNodeWithContentDescription("View All")
            .assertIsDisplayed()
            .performClick()
        assertTrue(clicked)
    }
}
