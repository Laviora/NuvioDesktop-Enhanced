package com.nuvio.app.features.details

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EpisodeRatingLabelsTest {
    @Test
    fun `keeps IMDb and TMDB labels separate`() {
        val labels = resolveEpisodeRatingLabels(
            imdbRating = 9.1,
            tmdbRating = 8.7,
            addonRating = 7.2,
            visibility = EpisodeRatingsVisibility.SHOW_ALL,
            isWatched = false,
        )

        assertEquals("9.1", labels.imdb)
        assertEquals("8.7", labels.tmdb)
        assertNull(labels.addon)
    }

    @Test
    fun `uses a neutral label for an addon rating with no known source`() {
        val labels = resolveEpisodeRatingLabels(
            imdbRating = null,
            tmdbRating = null,
            addonRating = 7.25,
            visibility = EpisodeRatingsVisibility.SHOW_ALL,
            isWatched = false,
        )

        assertNull(labels.imdb)
        assertNull(labels.tmdb)
        assertEquals("7.3", labels.addon)
    }

    @Test
    fun `hides every rating source when unwatched ratings are disabled`() {
        val labels = resolveEpisodeRatingLabels(
            imdbRating = 9.1,
            tmdbRating = 8.7,
            addonRating = 7.2,
            visibility = EpisodeRatingsVisibility.HIDE_UNWATCHED_EPISODES,
            isWatched = false,
        )

        assertNull(labels.imdb)
        assertNull(labels.tmdb)
        assertNull(labels.addon)
    }
}
