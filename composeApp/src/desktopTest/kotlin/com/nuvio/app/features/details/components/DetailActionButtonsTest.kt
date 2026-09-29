package com.nuvio.app.features.details.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import kotlin.test.Test
import org.junit.Rule

class DetailActionButtonsTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun iconActionRowShowsSecondaryActionsWithoutOpeningTheOverflowMenu() {
        compose.setContent {
            DetailActionButtons(
                playLabel = "Play",
                actionsMenuLabel = "More actions",
                iconActionRow = true,
                iconActions = listOf(
                    DetailSecondaryAction(
                        label = "Mark watched",
                        icon = Icons.Default.CheckCircleOutline,
                    ),
                    DetailSecondaryAction(
                        label = "Add to library",
                        icon = Icons.Default.Add,
                    ),
                ),
            )
        }

        compose.onNodeWithText("Play").assertIsDisplayed()
        compose.onNodeWithContentDescription("Mark watched").assertIsDisplayed()
        compose.onNodeWithContentDescription("Add to library").assertIsDisplayed()
        compose.onNodeWithContentDescription("More actions").assertDoesNotExist()
    }
}
