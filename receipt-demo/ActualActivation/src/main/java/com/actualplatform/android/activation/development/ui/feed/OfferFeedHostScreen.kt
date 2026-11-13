package com.actualplatform.android.activation.development.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.actualplatform.activation.ActivationApi
import com.actualplatform.activation.ActivationClient
import com.actualplatform.activation.OfferFeedResult
import com.actualplatform.activation.OffersWall
import com.actualplatform.activation.OffersWallDestination
import com.actualplatform.activation.RewardPoint
import com.actualplatform.activation.models.CalendarDate
import com.actualplatform.activation.models.OfferFeed
import com.actualplatform.activation.models.OfferGroup
import com.actualplatform.activation.models.OfferSummary
import com.actualplatform.activation.models.Store
import com.actualplatform.activation.theming.ActivationThemeProvider
import com.actualplatform.android.activation.development.R
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

/**
 * Simulates a partner integration of the Client-Side Offer Feed. Deliberately consumes only the
 * SDK's public API — [ActivationClient.api], the feed models, and [OffersWall] with an
 * [OffersWallDestination] — so this screen doubles as proof that the public contract is sufficient
 * for a host app. If it ever needs an internal import, the contract is incomplete.
 *
 * [onScanReceipt] is handed to the [OffersWall] this screen opens, so its "Scan Receipt" button
 * runs the host's camera flow. It must suspend for the whole scan: the wall opens the scan session
 * before calling it and closes the session when it returns.
 */
@Composable
internal fun OfferFeedHostScreen(
    onBack: () -> Unit,
    onScanReceipt: suspend () -> Unit,
) {
    val client = remember { ActivationClient.instance }
    val feed by client.api.offerFeed.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var refreshing by remember { mutableStateOf(false) }
    var refreshError by remember { mutableStateOf<String?>(null) }
    var destination by remember { mutableStateOf<OffersWallDestination?>(null) }
    var showPlainWall by remember { mutableStateOf(false) }

    fun refresh() {
        scope.launch {
            refreshing = true
            refreshError = null
            when (val result = client.api.refreshOfferFeed()) {
                is OfferFeedResult.Success -> Unit // offerFeed has already emitted the new value
                is OfferFeedResult.Failure -> refreshError = "${result.error}"
            }
            refreshing = false
        }
    }

    LaunchedEffect(Unit) { refresh() }

    if (destination != null || showPlainWall) {
        OffersWall(
            modifier = Modifier.fillMaxSize(),
            onScanReceipt = onScanReceipt,
            initialDestination = destination,
            onDismiss = {
                destination = null
                showPlainWall = false
            },
        )
        return
    }

    OfferFeedHostContent(
        feed = feed,
        refreshing = refreshing,
        refreshError = refreshError,
        onBack = onBack,
        onRefresh = ::refresh,
        onOfferClick = { destination = OffersWallDestination.OfferDetails(it) },
        onSeeAllClick = { destination = OffersWallDestination.Group(it) },
        onOpenWall = { showPlainWall = true },
        onOpenStaleOffer = { destination = OffersWallDestination.OfferDetails(staleOfferSummary()) },
    )
}

/** The host-rendered feed surface: stateless, so every data state can be previewed. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OfferFeedHostContent(
    feed: OfferFeed?,
    refreshing: Boolean,
    refreshError: String?,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onOfferClick: (OfferSummary) -> Unit,
    onSeeAllClick: (OfferGroup) -> Unit,
    onOpenWall: () -> Unit,
    onOpenStaleOffer: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.activations_offer_feed_title)) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(stringResource(R.string.activations_nav_back)) }
                },
                actions = {
                    IconButton(onClick = onRefresh, enabled = !refreshing) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.activations_offer_feed_refresh),
                        )
                    }
                },
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            refreshError?.let { message ->
                item {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }

            when {
                feed == null ->
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (refreshing) CircularProgressIndicator()
                            Text(
                                stringResource(
                                    if (refreshing) R.string.activations_offer_feed_loading
                                    else R.string.activations_offer_feed_not_loaded
                                )
                            )
                        }
                    }
                feed.groups.isEmpty() ->
                    item {
                        Text(
                            text = stringResource(R.string.activations_offer_feed_empty),
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                else ->
                    // Positional keys: titles are not unique and an offer may repeat within a
                    // group — either collision would crash a keyed list.
                    feed.groups.forEachIndexed { index, group ->
                        item(key = index) {
                            FeedGroupRow(
                                group = group,
                                onOfferClick = onOfferClick,
                                onSeeAllClick = { onSeeAllClick(group) },
                            )
                        }
                    }
            }

            item {
                OutlinedButton(
                    onClick = onOpenWall,
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    Text(stringResource(R.string.activations_offer_feed_open_wall))
                }
            }

            item {
                // Simulates a host feed gone stale: the id exists in no catalog, so the SDK must
                // open the wall root with the "offer unavailable" toast and no detail sheet.
                OutlinedButton(
                    onClick = onOpenStaleOffer,
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    Text(stringResource(R.string.activations_offer_feed_open_stale_offer))
                }
            }
        }
    }
}

private fun staleOfferSummary() =
    OfferSummary.ProductCoupon(
        id = -1,
        title = "Stale offer",
        subtitle = null,
        rewardValue = RewardPoint.ZERO,
        image = null,
        merchants = emptyList(),
        endDate = null,
        clipRequired = false,
        clipped = false,
        minQuantity = 1,
        tags = emptyList(),
    )

@Composable
private fun FeedGroupRow(
    group: OfferGroup,
    onOfferClick: (OfferSummary) -> Unit,
    onSeeAllClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.activations_offer_feed_group_header, group.title, group.id),
                style = MaterialTheme.typography.titleMedium,
            )
            TextButton(onClick = onSeeAllClick) {
                Text(stringResource(R.string.activations_offer_feed_see_all))
            }
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(count = group.offers.size) { index ->
                OfferSummaryCard(offer = group.offers[index], onClick = onOfferClick)
            }
        }
    }
}

/**
 * Renders every [OfferSummary] field the SDK exposes — null and empty ones included — so a host
 * integrator can see exactly what a card receives for each variant.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OfferSummaryCard(offer: OfferSummary, onClick: (OfferSummary) -> Unit) {
    Card(onClick = { onClick(offer) }, modifier = Modifier.width(260.dp)) {
        Column {
            val image = offer.image
            if (image != null) {
                AsyncImage(
                    model = image.url,
                    contentDescription = offer.title,
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier.fillMaxWidth()
                            // The card decides its own shape; width/height metadata drives it.
                            .aspectRatio(cardImageAspectRatio(image.width, image.height))
                            .clip(RoundedCornerShape(8.dp)),
                )
            }
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = formatReward(offer.rewardValue.value),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    when (offer) {
                        is OfferSummary.ProductCoupon ->
                            if (offer.minQuantity > 1) {
                                Badge(stringResource(R.string.activations_offer_feed_badge_buy, offer.minQuantity))
                            }
                        is OfferSummary.MerchantPromo ->
                            Badge(
                                stringResource(
                                    R.string.activations_offer_feed_badge_spend,
                                    formatSpend(offer.minimumSpend, offer.currency),
                                )
                            )
                    }
                }
                Text(
                    text = offer.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                // FlowRow wraps badges onto new lines instead of clipping them mid-text at the
                // card edge.
                if (offer.clipped || offer.clipRequired || offer.tags.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        if (offer.clipped) Badge(stringResource(R.string.activations_offer_feed_badge_clipped))
                        else if (offer.clipRequired) Badge(stringResource(R.string.activations_offer_feed_badge_clip))
                        offer.tags.forEach { Badge(it) }
                    }
                }
                // One line per field, null and empty included: the card is a contract viewer,
                // not a design reference.
                summaryFields(offer).forEach { (label, value) -> FieldRow(label, value) }
            }
        }
    }
}

/**
 * Label/value pairs for every field of [offer]: the shared ones first, then the variant's own. The
 * labels are the SDK's property names and stay untranslated, since the card documents the contract.
 *
 * Opts in to [ExperimentalTime] for `MerchantPromo.clipExpirationDate`, a `kotlin.time.Instant`.
 */
@OptIn(ExperimentalTime::class)
private fun summaryFields(offer: OfferSummary): List<Pair<String, String>> {
    val image = offer.image
    val shared =
        listOf(
            "kind" to
                when (offer) {
                    is OfferSummary.ProductCoupon -> "ProductCoupon"
                    is OfferSummary.MerchantPromo -> "MerchantPromo"
                },
            "id" to offer.id.toString(),
            "title" to offer.title,
            "subtitle" to (offer.subtitle ?: "null"),
            "rewardValue" to offer.rewardValue.value.toString(),
            "image.url" to (image?.url ?: "null"),
            "image.size" to (image?.let { "${it.width}×${it.height}" } ?: "null"),
            "merchants" to offer.merchants.storeNames(emptyMeaning = "[] (every store)"),
            "endDate" to (offer.endDate?.isoString ?: "null"),
            "clipRequired" to offer.clipRequired.toString(),
            "clipped" to offer.clipped.toString(),
            "tags" to if (offer.tags.isEmpty()) "[]" else offer.tags.joinToString(", "),
        )
    val own =
        when (offer) {
            is OfferSummary.ProductCoupon -> listOf("minQuantity" to offer.minQuantity.toString())
            is OfferSummary.MerchantPromo ->
                listOf(
                    "minimumSpend" to offer.minimumSpend.toString(),
                    "currency" to (offer.currency ?: "null"),
                    "redemptionLimit" to
                        (offer.redemptionLimit?.let { limit ->
                            val period = limit.periodDays?.toString() ?: "null (lifetime)"
                            "count=${limit.count}, periodDays=$period"
                        } ?: "null"),
                    "excludedMerchants" to offer.excludedMerchants.storeNames(emptyMeaning = "[]"),
                    "clipExpirationDate" to (offer.clipExpirationDate?.toString() ?: "null"),
                )
        }
    return shared + own
}

private fun List<Store>.storeNames(emptyMeaning: String): String =
    if (isEmpty()) emptyMeaning else joinToString(", ") { "${it.name} (${it.bannerId})" }

@Composable
private fun FieldRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(120.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

/** "80" for whole reward values, "80.5" otherwise — never a trailing ".0". */
private fun formatReward(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()

/** Card shape when the backend sent no usable image dimensions. */
internal const val DEFAULT_CARD_IMAGE_ASPECT_RATIO = 1.6f

/**
 * Width over height for the card image, or [DEFAULT_CARD_IMAGE_ASPECT_RATIO] unless both dimensions
 * are present. The feed reports a missing width and a missing height as 0 independently, and
 * `Modifier.aspectRatio` throws on a ratio of 0.
 */
internal fun cardImageAspectRatio(width: Int, height: Int): Float =
    if (width > 0 && height > 0) width.toFloat() / height else DEFAULT_CARD_IMAGE_ASPECT_RATIO

/** "25 USD", or bare "25" when the backend sent no currency. */
private fun formatSpend(amount: Double, currency: String?): String =
    listOfNotNull(formatReward(amount), currency).joinToString(" ")

@Composable
private fun Badge(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        modifier =
            Modifier.background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    RoundedCornerShape(4.dp),
                )
                .padding(horizontal = 6.dp, vertical = 2.dp),
        color = MaterialTheme.colorScheme.onSecondaryContainer,
    )
}

@OptIn(ActivationApi::class)
@PreviewLightDark
@Composable
private fun OfferFeedHostScreenPreview() {
    ActivationThemeProvider {
        OfferFeedHostContent(
            feed =
                OfferFeed(
                    groups =
                        listOf(
                            OfferGroup(
                                id = 1,
                                title = "Clip to Earn",
                                offers =
                                    listOf(
                                        previewOffer(1, "Crunchy Cereal", clipRequired = true),
                                        previewOffer(2, "Greek Yogurt", clipped = true),
                                    ),
                            ),
                            OfferGroup(
                                id = 2,
                                title = "Trending",
                                offers = listOf(previewOffer(3, "Sparkling Water", minQuantity = 2)),
                            ),
                        )
                ),
            refreshing = false,
            refreshError = "REQUEST_FAILED",
            onBack = {},
            onRefresh = {},
            onOfferClick = {},
            onSeeAllClick = {},
            onOpenWall = {},
            onOpenStaleOffer = {},
        )
    }
}

private fun previewOffer(
    id: Int,
    title: String,
    clipRequired: Boolean = false,
    clipped: Boolean = false,
    minQuantity: Int = 1,
) =
    OfferSummary.ProductCoupon(
        id = id,
        title = title,
        subtitle = "Any variety",
        rewardValue = RewardPoint(1.5),
        image = null,
        merchants = emptyList(),
        endDate = CalendarDate(2026, 12, 31),
        clipRequired = clipRequired,
        clipped = clipped,
        minQuantity = minQuantity,
        tags = listOf("NEW"),
    )
