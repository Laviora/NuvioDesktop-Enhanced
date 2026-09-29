package com.nuvio.app.features.streams

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PinnedStreamSourcesTest {

    @Test
    fun `pinning one provider lifts only that provider out of its addon group`() {
        val groups = listOf(
            group("addon:usenet", "Usenet Streamer", null),
            group("plugin-repo:d3adly", "D3adlyRocket", "NetMirror", "SomethingElse"),
        )

        val result = splitPinnedSources(
            groups = groups,
            pinnedSourceIds = listOf("plugin-repo:d3adly|NetMirror"),
        )

        assertEquals(listOf("NetMirror"), result.pinnedGroups.map { it.addonName })
        assertEquals(listOf("Usenet Streamer", "D3adlyRocket"), result.remainingGroups.map { it.addonName })
        assertEquals(listOf("SomethingElse"), result.remainingGroups.last().streams.map { it.sourceName })
    }

    @Test
    fun `pinning an addon without named providers lifts the whole addon`() {
        val groups = listOf(
            group("addon:usenet", "Usenet Streamer", null),
            group("addon:torbox", "TorBox", null),
        )

        val result = splitPinnedSources(groups, listOf("addon:usenet"))

        assertEquals(listOf("Usenet Streamer"), result.pinnedGroups.map { it.addonName })
        assertEquals(listOf("TorBox"), result.remainingGroups.map { it.addonName })
    }

    @Test
    fun `pinned providers are returned in pin order`() {
        val groups = listOf(
            group("plugin-repo:d3adly", "D3adlyRocket", "NetMirror", "Zoechip"),
        )

        val result = splitPinnedSources(
            groups,
            listOf("plugin-repo:d3adly|Zoechip", "plugin-repo:d3adly|NetMirror"),
        )

        assertEquals(listOf("Zoechip", "NetMirror"), result.pinnedGroups.map { it.addonName })
        assertTrue(result.remainingGroups.isEmpty())
    }

    @Test
    fun `loading addon remains after all current streams are pinned`() {
        val groups = listOf(
            group("plugin-repo:d3adly", "D3adlyRocket", "NetMirror").copy(isLoading = true),
        )

        val result = splitPinnedSources(groups, listOf("plugin-repo:d3adly|NetMirror"))

        assertEquals(listOf("NetMirror"), result.pinnedGroups.map { it.addonName })
        assertEquals(listOf("D3adlyRocket"), result.remainingGroups.map { it.addonName })
        assertTrue(result.remainingGroups.single().streams.isEmpty())
    }

    @Test
    fun `no pins leave stream groups unchanged`() {
        val groups = listOf(group("addon:usenet", "Usenet Streamer", null))

        val result = splitPinnedSources(groups, emptyList())

        assertTrue(result.pinnedGroups.isEmpty())
        assertEquals(groups, result.remainingGroups)
    }

    @Test
    fun `direct debrid streams cannot become pin targets`() {
        val stream = stream(
            addonId = "debrid:real-debrid",
            addonName = "Real-Debrid",
            sourceName = null,
        )

        assertNull(streamSourcePinTarget(stream))
        assertEquals(
            listOf("Real-Debrid"),
            splitPinnedSources(
                groups = listOf(AddonStreamGroup("Real-Debrid", "debrid:real-debrid", listOf(stream))),
                pinnedSourceIds = listOf("debrid:real-debrid"),
            ).remainingGroups.map { it.addonName },
        )
    }

    @Test
    fun `pin identity includes provider name and falls back to addon id`() {
        assertEquals("addon:usenet", pinnedStreamSourceKey("addon:usenet", null))
        assertEquals("addon:usenet", pinnedStreamSourceKey("addon:usenet", "  "))
        assertEquals(
            "plugin-repo:d3adly|NetMirror",
            pinnedStreamSourceKey("plugin-repo:d3adly", "NetMirror"),
        )
    }

    private fun group(addonId: String, addonName: String, vararg sources: String?) = AddonStreamGroup(
        addonName = addonName,
        addonId = addonId,
        streams = sources.map { source -> stream(addonId, addonName, source) },
    )

    private fun stream(addonId: String, addonName: String, sourceName: String?): StreamItem = StreamItem(
        name = sourceName ?: addonName,
        url = "https://example.com/${sourceName ?: addonName}.mkv",
        sourceName = sourceName,
        addonName = addonName,
        addonId = addonId,
    )
}
