package com.nuvio.app.features.catalog

import com.nuvio.app.features.details.MetaDetails
import com.nuvio.app.features.details.MoreLikeThisPage
import com.nuvio.app.features.details.MoreLikeThisSource

internal fun MoreLikeThisPage.toCatalogPage(requestedPage: Int): CatalogPage =
    CatalogPage(
        items = items,
        rawItemCount = items.size,
        nextSkip = if (hasMore && items.isNotEmpty()) requestedPage + 1 else null,
    )

internal fun MetaDetails.canOpenMoreLikeThisCatalog(): Boolean =
    moreLikeThisHasMore && when (moreLikeThisSource) {
        MoreLikeThisSource.TMDB,
        MoreLikeThisSource.TRAKT,
        -> true

        MoreLikeThisSource.SIMKL,
        null,
        -> false
    }
