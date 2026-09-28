package com.nuvio.app.features.catalog

import com.nuvio.app.features.details.MetaDetails
import com.nuvio.app.features.details.MoreLikeThisPage
import com.nuvio.app.features.details.MoreLikeThisSource
import com.nuvio.app.features.tmdb.TmdbMetadataService
import com.nuvio.app.features.tmdb.TmdbSettingsRepository
import com.nuvio.app.features.trakt.TraktRelatedRepository

internal suspend fun fetchMoreLikeThisCatalogPage(
    target: CatalogTarget.MoreLikeThis,
    page: Int,
): CatalogPage {
    val result = when (target.source) {
        MoreLikeThisSource.TRAKT -> TraktRelatedRepository.getRelated(
            itemId = target.itemId,
            itemType = target.itemType,
            page = page,
        )

        MoreLikeThisSource.TMDB -> {
            TmdbSettingsRepository.ensureLoaded()
            TmdbMetadataService.fetchMoreLikeThisPage(
                itemId = target.itemId,
                itemType = target.itemType,
                page = page,
                settings = TmdbSettingsRepository.snapshot(),
            )
        }

        MoreLikeThisSource.SIMKL -> MoreLikeThisPage()
    }
    return result.toCatalogPage(requestedPage = page)
}

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
