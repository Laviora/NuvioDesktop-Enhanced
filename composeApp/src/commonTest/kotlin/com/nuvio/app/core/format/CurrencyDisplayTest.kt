package com.nuvio.app.core.format

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CurrencyDisplayTest {
    @Test
    fun `missing and non-positive amounts are hidden`() {
        assertNull(formatUsdAmountForDisplay(null))
        assertNull(formatUsdAmountForDisplay(0L))
        assertNull(formatUsdAmountForDisplay(-1L))
    }

    @Test
    fun `positive amounts use dollar prefix and comma grouping`() {
        assertEquals("$1", formatUsdAmountForDisplay(1L))
        assertEquals("$999", formatUsdAmountForDisplay(999L))
        assertEquals("$1,000", formatUsdAmountForDisplay(1_000L))
        assertEquals("$1,000,000", formatUsdAmountForDisplay(1_000_000L))
        assertEquals("$9,223,372,036,854,775,807", formatUsdAmountForDisplay(Long.MAX_VALUE))
    }
}
