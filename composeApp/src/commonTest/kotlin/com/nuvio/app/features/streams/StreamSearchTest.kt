package com.nuvio.app.features.streams

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StreamSearchTest {
    @Test
    fun `disabled or blank search leaves stream state unchanged`() {
        val state = stateWith(stream(name = "Movie.2026.2160p"))

        assertEquals(state, filterStreamsForSearch(state, query = "2160p", enabled = false).uiState)
        assertEquals(state, filterStreamsForSearch(state, query = "  ", enabled = true).uiState)
        assertFalse(filterStreamsForSearch(state, query = "2160p", enabled = false).isEmptyBecauseOfSearch)
    }

    @Test
    fun `search is case insensitive and requires every term across stream metadata`() {
        val matching = stream(
            name = "Movie release",
            sourceName = "Orion",
            filename = "Movie.2026-GROUP.mkv",
            badges = listOf(StreamBadge(name = "4K"), StreamBadge(name = "HEVC")),
        )
        val nonMatching = stream(
            name = "Movie release",
            sourceName = "Orion",
            filename = "Movie.2026-OTHER.mkv",
            badges = listOf(StreamBadge(name = "1080P")),
        )

        val result = filterStreamsForSearch(
            uiState = stateWith(matching, nonMatching),
            query = " 4k GROUP orion ",
            enabled = true,
        )

        assertEquals(listOf(matching), result.uiState.groups.single().streams)
        assertFalse(result.isEmptyBecauseOfSearch)
    }

    @Test
    fun `no results is evaluated inside the selected provider`() {
        val selectedStream = stream(name = "1080p source", addonId = "addon:selected")
        val otherStream = stream(name = "4K source", addonId = "addon:other")
        val state = StreamsUiState(
            groups = listOf(
                AddonStreamGroup("Selected", "addon:selected", listOf(selectedStream)),
                AddonStreamGroup("Other", "addon:other", listOf(otherStream)),
            ),
            selectedFilter = "addon:selected",
        )

        val result = filterStreamsForSearch(state, query = "4k", enabled = true)

        assertTrue(result.isEmptyBecauseOfSearch)
        assertEquals(emptyList(), result.uiState.filteredGroups.single().streams)
    }

    private fun stateWith(vararg streams: StreamItem): StreamsUiState = StreamsUiState(
        groups = listOf(AddonStreamGroup("Test addon", "addon:test", streams.toList())),
    )

    private fun stream(
        name: String,
        sourceName: String? = null,
        filename: String? = null,
        badges: List<StreamBadge> = emptyList(),
        addonId: String = "addon:test",
    ): StreamItem = StreamItem(
        name = name,
        sourceName = sourceName,
        addonName = "Test addon",
        addonId = addonId,
        behaviorHints = StreamBehaviorHints(filename = filename),
        badges = badges,
    )
}
