package com.nuvio.app.features.streams

internal data class StreamSearchResult(
    val uiState: StreamsUiState,
    val isEmptyBecauseOfSearch: Boolean,
)

internal fun filterStreamsForSearch(
    uiState: StreamsUiState,
    query: String,
    enabled: Boolean,
): StreamSearchResult {
    val terms = if (enabled) {
        query.trim().lowercase().split(Regex("\\s+")).filter(String::isNotEmpty)
    } else {
        emptyList()
    }
    if (terms.isEmpty()) return StreamSearchResult(uiState, isEmptyBecauseOfSearch = false)

    val filtered = uiState.copy(
        groups = uiState.groups.map { group ->
            group.copy(streams = group.streams.filter { stream -> stream.matchesSearchTerms(terms) })
        },
    )
    return StreamSearchResult(
        uiState = filtered,
        isEmptyBecauseOfSearch = uiState.allStreams.isNotEmpty() && filtered.allStreams.isEmpty(),
    )
}

private fun StreamItem.matchesSearchTerms(terms: List<String>): Boolean {
    val searchableText = buildString {
        name?.let { append(it).append(' ') }
        title?.let { append(it).append(' ') }
        description?.let { append(it).append(' ') }
        sourceName?.let { append(it).append(' ') }
        append(addonName).append(' ')
        behaviorHints.filename?.let { append(it).append(' ') }
        badges.forEach { badge -> append(badge.name).append(' ') }
    }.lowercase()
    return terms.all(searchableText::contains)
}
