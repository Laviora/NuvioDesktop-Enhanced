package com.nuvio.app.features.details.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.nuvio.app.features.details.MetaDetails
import org.junit.Rule
import kotlin.test.Test

class DetailAdditionalInfoSectionTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `positive budget and revenue are displayed`() {
        show(MetaDetails("tt1", "movie", "Movie", budget = 1_000L, revenue = 2_500_000L))

        compose.onNodeWithText("Budget").assertIsDisplayed()
        compose.onNodeWithText("$1,000").assertIsDisplayed()
        compose.onNodeWithText("Revenue").assertIsDisplayed()
        compose.onNodeWithText("$2,500,000").assertIsDisplayed()
    }

    @Test
    fun `unknown budget and revenue are omitted`() {
        show(MetaDetails("tt1", "movie", "Movie", status = "Released", budget = 0L, revenue = null))

        compose.onNodeWithText("Budget").assertDoesNotExist()
        compose.onNodeWithText("Revenue").assertDoesNotExist()
    }

    private fun show(meta: MetaDetails) {
        compose.setContent {
            MaterialTheme {
                DetailAdditionalInfoSection(meta = meta, showHeader = false)
            }
        }
    }
}
