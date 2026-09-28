package com.nuvio.app.features.trakt

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TraktRelatedPaginationTest {
    @Test
    fun `related request uses the requested page and fixed page size`() {
        val url = buildTraktRelatedUrl(
            endpoint = "movies",
            pathId = "tt0111161",
            page = 3,
        )

        assertTrue(url.contains("page=3"))
        assertTrue(url.contains("limit=20"))
        assertFalse(url.contains("page=1"))
    }

    @Test
    fun `pagination headers report whether another related page exists`() {
        assertTrue(
            traktRelatedHasMore(
                requestedPage = 2,
                headers = mapOf("X-Pagination-Page-Count" to "4"),
            ),
        )
        assertFalse(
            traktRelatedHasMore(
                requestedPage = 4,
                headers = mapOf("x-pagination-page-count" to "4"),
            ),
        )
        assertFalse(traktRelatedHasMore(requestedPage = 1, headers = emptyMap()))
    }
}
