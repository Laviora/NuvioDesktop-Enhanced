package com.nuvio.app.features.streams

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal data class StreamSourcePinTarget(
    val key: String,
    val label: String,
)

internal data class PinnedFirstStreamGroups(
    val pinnedGroups: List<AddonStreamGroup>,
    val remainingGroups: List<AddonStreamGroup>,
)

internal fun pinnedStreamSourceKey(addonId: String, sourceName: String?): String {
    val normalizedSource = sourceName?.trim()?.takeIf(String::isNotEmpty)
    return if (normalizedSource == null) addonId else "$addonId|$normalizedSource"
}

internal fun streamSourcePinTarget(stream: StreamItem): StreamSourcePinTarget? {
    if (stream.addonId.isBlank() || stream.addonId.startsWith("debrid:") || stream.isDirectDebridStream) {
        return null
    }
    val sourceName = stream.sourceName?.trim()?.takeIf(String::isNotEmpty)
    return StreamSourcePinTarget(
        key = pinnedStreamSourceKey(stream.addonId, sourceName),
        label = sourceName ?: stream.addonName,
    )
}

internal fun splitPinnedSources(
    groups: List<AddonStreamGroup>,
    pinnedSourceIds: List<String>,
): PinnedFirstStreamGroups {
    if (pinnedSourceIds.isEmpty()) return PinnedFirstStreamGroups(emptyList(), groups)

    val pinnedRank = linkedMapOf<String, Int>()
    pinnedSourceIds.forEachIndexed { index, key -> pinnedRank.putIfAbsent(key, index) }
    val pinned = mutableListOf<Pair<Int, AddonStreamGroup>>()
    val remaining = mutableListOf<AddonStreamGroup>()

    groups.forEach { group ->
        val pinnedStreams = if (group.addonId.startsWith("debrid:")) {
            emptyMap()
        } else {
            group.streams
                .filterNot(StreamItem::isDirectDebridStream)
                .groupBy { stream -> pinnedStreamSourceKey(group.addonId, stream.sourceName) }
                .filterKeys(pinnedRank::containsKey)
        }

        if (pinnedStreams.isEmpty()) {
            remaining += group
            return@forEach
        }

        val extracted = pinnedStreams.values.flatten().toSet()
        pinnedStreams.forEach { (key, streams) ->
            pinned += pinnedRank.getValue(key) to group.copy(
                addonName = streams.firstOrNull()?.sourceName?.takeIf(String::isNotBlank) ?: group.addonName,
                streams = streams,
                isLoading = false,
            )
        }

        val leftovers = group.streams.filterNot(extracted::contains)
        if (leftovers.isNotEmpty() || group.isLoading) {
            remaining += group.copy(streams = leftovers)
        }
    }

    return PinnedFirstStreamGroups(
        pinnedGroups = pinned.sortedBy(Pair<Int, AddonStreamGroup>::first).map(Pair<Int, AddonStreamGroup>::second),
        remainingGroups = remaining,
    )
}

object PinnedStreamSourcesRepository {
    private const val SEPARATOR = "\n"

    private val _pinnedSourceIds = MutableStateFlow<List<String>>(emptyList())
    val pinnedSourceIds: StateFlow<List<String>> = _pinnedSourceIds.asStateFlow()
    private var hasLoaded = false

    fun ensureLoaded() {
        if (!hasLoaded) loadFromDisk()
    }

    fun onProfileChanged() = loadFromDisk()

    fun setPinned(sourceKey: String, pinned: Boolean) {
        ensureLoaded()
        val normalized = sourceKey.trim()
        if (normalized.isEmpty()) return
        val current = _pinnedSourceIds.value
        val updated = when {
            pinned && normalized !in current -> current + normalized
            !pinned && normalized in current -> current.filterNot { it == normalized }
            else -> return
        }
        _pinnedSourceIds.value = updated
        StreamBadgeSettingsStorage.savePinnedStreamSources(updated.joinToString(SEPARATOR))
    }

    fun clearLocalState() {
        hasLoaded = false
        _pinnedSourceIds.value = emptyList()
    }

    private fun loadFromDisk() {
        hasLoaded = true
        _pinnedSourceIds.value = StreamBadgeSettingsStorage.loadPinnedStreamSources()
            ?.split(SEPARATOR)
            ?.map(String::trim)
            ?.filter(String::isNotEmpty)
            ?.distinct()
            .orEmpty()
    }
}
