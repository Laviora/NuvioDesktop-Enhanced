package com.nuvio.app.features.details

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MetaScreenSettingsPayloadCodecTest {
    @Test
    fun `legacy payloads enable the icon action row by default`() {
        val decoded = MetaScreenSettingsPayloadCodec.decode(
            """{"blur_unwatched_episodes":true}""",
        )

        assertTrue(decoded!!.iconActionRow)
    }

    @Test
    fun `disabled icon action row survives a settings payload round trip`() {
        val encoded = MetaScreenSettingsPayloadCodec.encode(
            StoredMetaScreenSettingsPayload(iconActionRow = false),
        )

        assertFalse(MetaScreenSettingsPayloadCodec.decode(encoded)!!.iconActionRow)
    }
}
