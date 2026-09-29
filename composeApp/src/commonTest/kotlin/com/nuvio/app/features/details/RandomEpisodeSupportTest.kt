package com.nuvio.app.features.details

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RandomEpisodeSupportTest {
    @Test
    fun `random episode action is available for series and episode-bearing metadata only`() {
        assertTrue(MetaDetails(id = "series", type = "series", name = "Series").supportsRandomEpisodeAction())
        assertTrue(
            MetaDetails(
                id = "show",
                type = "tv",
                name = "Show",
                videos = listOf(MetaVideo(id = "s1e1", title = "Episode 1", season = 1, episode = 1)),
            ).supportsRandomEpisodeAction(),
        )
        assertFalse(MetaDetails(id = "movie", type = "movie", name = "Movie").supportsRandomEpisodeAction())
        assertFalse(
            MetaDetails(
                id = "movie-with-video",
                type = "movie",
                name = "Movie",
                videos = listOf(MetaVideo(id = "movie", title = "Movie")),
            ).supportsRandomEpisodeAction(),
        )
    }

    @Test
    fun `random episode selection includes only released playable episodes`() {
        val meta = MetaDetails(
            id = "series",
            type = "series",
            name = "Series",
            videos = listOf(
                MetaVideo(id = "released-1", title = "Released 1", season = 1, episode = 1, released = "2026-09-01"),
                MetaVideo(id = "unavailable", title = "Unavailable", season = 1, episode = 2, released = "2026-09-02", available = false),
                MetaVideo(id = "future", title = "Future", season = 1, episode = 3, released = "2026-10-01"),
                MetaVideo(id = "released-2", title = "Released 2", season = 1, episode = 4, released = "2026-09-03"),
            ),
        )

        val selected = meta.randomReleasedPlayableEpisode(
            todayIsoDate = "2026-09-29",
            chooseIndex = { candidateCount ->
                assertEquals(2, candidateCount)
                1
            },
        )

        assertEquals("released-2", selected?.id)
    }

    @Test
    fun `random episode selection returns null when no released episode is playable`() {
        val meta = MetaDetails(
            id = "series",
            type = "series",
            name = "Series",
            videos = listOf(
                MetaVideo(id = "future", title = "Future", season = 1, episode = 1, released = "2026-10-01"),
            ),
        )

        assertNull(
            meta.randomReleasedPlayableEpisode(
                todayIsoDate = "2026-09-29",
                chooseIndex = { error("No index should be requested for an empty candidate list") },
            ),
        )
    }
}
