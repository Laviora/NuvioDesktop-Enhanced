package com.nuvio.app.features.details

import com.nuvio.app.features.watched.releasedPlayableEpisodes
import kotlin.random.Random

internal fun MetaDetails.supportsRandomEpisodeAction(): Boolean =
    type.equals("series", ignoreCase = true) || videos.any { video ->
        video.season != null || video.episode != null
    }

internal fun MetaDetails.randomReleasedPlayableEpisode(
    todayIsoDate: String,
    chooseIndex: (Int) -> Int = { candidateCount -> Random.nextInt(candidateCount) },
): MetaVideo? {
    val candidates = releasedPlayableEpisodes(todayIsoDate)
    if (candidates.isEmpty()) return null
    return candidates[chooseIndex(candidates.size)]
}
