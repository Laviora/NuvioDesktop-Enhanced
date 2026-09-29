package com.nuvio.app.features.details

import kotlin.math.absoluteValue
import kotlin.math.roundToInt

internal data class EpisodeRatingLabels(
    val imdb: String? = null,
    val tmdb: String? = null,
    val addon: String? = null,
)

internal fun resolveEpisodeRatingLabels(
    imdbRating: Double?,
    tmdbRating: Double?,
    addonRating: Double?,
    visibility: EpisodeRatingsVisibility,
    isWatched: Boolean,
): EpisodeRatingLabels {
    if (!visibility.showRating(isWatched)) return EpisodeRatingLabels()

    val imdb = imdbRating.validEpisodeRatingLabel()
    val tmdb = tmdbRating.validEpisodeRatingLabel()
    return EpisodeRatingLabels(
        imdb = imdb,
        tmdb = tmdb,
        addon = addonRating.validEpisodeRatingLabel().takeIf { imdb == null && tmdb == null },
    )
}

private fun Double?.validEpisodeRatingLabel(): String? =
    this?.takeIf { it > 0.0 }?.let(::formatEpisodeRating)

internal fun formatEpisodeRating(rating: Double): String {
    val roundedTenths = (rating * 10.0).roundToInt()
    val whole = roundedTenths / 10
    val tenth = (roundedTenths % 10).absoluteValue
    return "$whole.$tenth"
}
