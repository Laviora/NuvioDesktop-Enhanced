package com.nuvio.app.features.catalog

import com.nuvio.app.features.details.MetaDetails
import com.nuvio.app.features.details.MoreLikeThisPage
import com.nuvio.app.features.details.MoreLikeThisSource
import com.nuvio.app.features.home.MetaPreview
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MoreLikeThisCatalogTest {
    @Test
    fun `paged recommendations advance to the next API page`() {
        val page = MoreLikeThisPage(
            items = listOf(preview("tmdb:1"), preview("tmdb:2")),
            hasMore = true,
        ).toCatalogPage(requestedPage = 3)

        assertEquals(2, page.rawItemCount)
        assertEquals(4, page.nextSkip)
    }

    @Test
    fun `final recommendations page stops pagination`() {
        val page = MoreLikeThisPage(
            items = listOf(preview("tmdb:1")),
            hasMore = false,
        ).toCatalogPage(requestedPage = 2)

        assertNull(page.nextSkip)
    }

    @Test
    fun `view all is available only for paged recommendation sources`() {
        assertTrue(meta(MoreLikeThisSource.TMDB, hasMore = true).canOpenMoreLikeThisCatalog())
        assertTrue(meta(MoreLikeThisSource.TRAKT, hasMore = true).canOpenMoreLikeThisCatalog())
        assertFalse(meta(MoreLikeThisSource.SIMKL, hasMore = true).canOpenMoreLikeThisCatalog())
        assertFalse(meta(MoreLikeThisSource.TMDB, hasMore = false).canOpenMoreLikeThisCatalog())
    }

    @Test
    fun `more like this target keeps content type and supports pagination`() {
        val target = CatalogTarget.MoreLikeThis(
            itemId = "tt0111161",
            itemType = "movie",
            source = MoreLikeThisSource.TMDB,
        )

        assertEquals("movie", target.contentType)
        assertTrue(target.supportsPagination)
    }

    private fun meta(source: MoreLikeThisSource, hasMore: Boolean): MetaDetails =
        MetaDetails(
            id = "tt0111161",
            type = "movie",
            name = "The Shawshank Redemption",
            moreLikeThis = listOf(preview("tmdb:278")),
            moreLikeThisSource = source,
            moreLikeThisHasMore = hasMore,
        )

    private fun preview(id: String): MetaPreview = MetaPreview(
        id = id,
        type = "movie",
        name = id,
    )
}
