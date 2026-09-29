package com.nuvio.app.features.details.components

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DetailActionLayoutTest {
    @Test
    fun `preferred icon size and spacing are kept when the row has room`() {
        val layout = fitIconActionRow(
            count = 6,
            availableWidth = 520.dp,
            preferredSize = 56.dp,
            preferredSpacing = 20.dp,
        )

        assertEquals(56.dp, layout.size)
        assertEquals(20.dp, layout.spacing)
        assertTrue(layout.fits)
    }

    @Test
    fun `spacing tightens before icon buttons shrink`() {
        val layout = fitIconActionRow(
            count = 6,
            availableWidth = 360.dp,
            preferredSize = 52.dp,
            preferredSpacing = 16.dp,
        )

        assertEquals(52.dp, layout.size)
        assertEquals(9.6.dp, layout.spacing)
        assertTrue(layout.fits)
    }

    @Test
    fun `icon buttons shrink no smaller than the desktop touch target`() {
        val layout = fitIconActionRow(
            count = 6,
            availableWidth = 320.dp,
            preferredSize = 52.dp,
            preferredSpacing = 16.dp,
        )

        assertEquals((280f / 6f).dp, layout.size)
        assertEquals(8.dp, layout.spacing)
        assertTrue(layout.fits)
    }

    @Test
    fun `row reports overflow when minimum icon buttons cannot fit`() {
        val layout = fitIconActionRow(
            count = 6,
            availableWidth = 280.dp,
            preferredSize = 52.dp,
            preferredSpacing = 16.dp,
        )

        assertEquals(44.dp, layout.size)
        assertEquals(8.dp, layout.spacing)
        assertFalse(layout.fits)
    }
}
