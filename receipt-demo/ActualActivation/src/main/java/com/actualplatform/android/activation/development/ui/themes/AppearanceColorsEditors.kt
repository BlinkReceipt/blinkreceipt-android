package com.actualplatform.android.activation.development.ui.themes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.actualplatform.activation.theming.ActivationAppearance
import com.actualplatform.activation.theming.ActivationTheme
import com.actualplatform.activation.theming.adLoadingDefaultDescriptionLabelColor
import com.actualplatform.activation.theming.adLoadingDefaultTitleLabelColor
import com.actualplatform.activation.theming.adLoadingLoadingBarBackgroundColor
import com.actualplatform.activation.theming.adLoadingLoadingBarLabelColor
import com.actualplatform.activation.theming.adLoadingLoadingBarProgressColor
import com.actualplatform.activation.theming.errorModalBackButtonLabelColor
import com.actualplatform.activation.theming.errorModalBackgroundColor
import com.actualplatform.activation.theming.errorModalDescriptionLabelColor
import com.actualplatform.activation.theming.errorModalIconBackgroundColor
import com.actualplatform.activation.theming.errorModalTitleLabelColor
import com.actualplatform.activation.theming.missedEarningsAddNewFieldLabelColor
import com.actualplatform.activation.theming.missedEarningsAlertMessageLabelColor
import com.actualplatform.activation.theming.missedEarningsAlertTitleLabelColor
import com.actualplatform.activation.theming.missedEarningsEditModalBackgroundColor
import com.actualplatform.activation.theming.missedEarningsEditModalCancelButtonLabelColor
import com.actualplatform.activation.theming.missedEarningsEditModalDatePickerColor
import com.actualplatform.activation.theming.missedEarningsEditModalInputLabelColor
import com.actualplatform.activation.theming.missedEarningsEditModalInputPlaceholderLabelColor
import com.actualplatform.activation.theming.missedEarningsEditModalInputValueLabelColor
import com.actualplatform.activation.theming.missedEarningsEditModalSaveButtonBackgroundColor
import com.actualplatform.activation.theming.missedEarningsEditModalSaveButtonLabelColor
import com.actualplatform.activation.theming.missedEarningsEditModalTitleLabelColor
import com.actualplatform.activation.theming.missedEarningsFieldEditIconColor
import com.actualplatform.activation.theming.missedEarningsListSectionTitleLabelColor
import com.actualplatform.activation.theming.missedEarningsModifiedFieldBackgroundColor
import com.actualplatform.activation.theming.missedEarningsNavigationBarTextColor
import com.actualplatform.activation.theming.missedEarningsNavigationDescriptionLabelColor
import com.actualplatform.activation.theming.missedEarningsNavigationEditButtonBackgroundColor
import com.actualplatform.activation.theming.missedEarningsNavigationEditButtonIconColor
import com.actualplatform.activation.theming.missedEarningsNavigationSaveButtonBackgroundColor
import com.actualplatform.activation.theming.missedEarningsNavigationTitleLabelColor
import com.actualplatform.activation.theming.missedEarningsTripItemLabelColor
import com.actualplatform.activation.theming.offerBackgroundColor
import com.actualplatform.activation.theming.offerBrandLabelColor
import com.actualplatform.activation.theming.offerClipButtonBackgroundColor
import com.actualplatform.activation.theming.offerClipButtonIconColor
import com.actualplatform.activation.theming.offerClippedButtonBackgroundColor
import com.actualplatform.activation.theming.offerClippedButtonIconColor
import com.actualplatform.activation.theming.offerClippedToastMessageBackgroundColor
import com.actualplatform.activation.theming.offerClippedToastMessageLabelColor
import com.actualplatform.activation.theming.offerDetailsClipLabelColor
import com.actualplatform.activation.theming.offerDetailsEarnRewardLabelColor
import com.actualplatform.activation.theming.offerDetailsFinePrintLabelColor
import com.actualplatform.activation.theming.offerDetailsSectionBodyLabelColor
import com.actualplatform.activation.theming.offerDetailsSectionHeaderTitleLabelColor
import com.actualplatform.activation.theming.offerDetailsSectionHeaderToggleLabelColor
import com.actualplatform.activation.theming.offerDetailsSectionNumberedListBadgeBackgroundColor
import com.actualplatform.activation.theming.offerDetailsSectionNumberedListBadgeLabelColor
import com.actualplatform.activation.theming.offerDetailsTagChipBorderColor
import com.actualplatform.activation.theming.offerDetailsTagChipLabelColor
import com.actualplatform.activation.theming.offerDetailsTitleLabelColor
import com.actualplatform.activation.theming.offerEligibleMerchantsLabelColor
import com.actualplatform.activation.theming.offerRewardPointsLabelColor
import com.actualplatform.activation.theming.offerTagBackgroundColor
import com.actualplatform.activation.theming.offerTagLabelColor
import com.actualplatform.activation.theming.offerWallBackgroundColor
import com.actualplatform.activation.theming.offerWallFloatingButtonBackgroundColor
import com.actualplatform.activation.theming.offerWallFloatingButtonLabelColor
import com.actualplatform.activation.theming.offerWallSectionHeaderLabelColor
import com.actualplatform.activation.theming.offerWallSectionHeaderShowMoreBackgroundColor
import com.actualplatform.activation.theming.offerWallSectionHeaderShowMoreIconColor
import com.actualplatform.activation.theming.postScanBoostClaimButtonBackgroundColor
import com.actualplatform.activation.theming.postScanBoostClaimButtonIconColor
import com.actualplatform.activation.theming.postScanBoostClaimButtonLabelColor
import com.actualplatform.activation.theming.postScanBoostDescriptionLabelColor
import com.actualplatform.activation.theming.postScanBoostSkipButtonLabelColor
import com.actualplatform.activation.theming.postScanBoostTitleLabelColor
import com.actualplatform.activation.theming.postScanFooterBackgroundColor
import com.actualplatform.activation.theming.postScanFooterButtonTitleColor
import com.actualplatform.activation.theming.postScanHeaderBackgroundColor
import com.actualplatform.activation.theming.postScanInlineProductTaskBackgroundColor
import com.actualplatform.activation.theming.postScanInlineProductTaskPointsLabelColor
import com.actualplatform.activation.theming.postScanInlineProductTaskScanAndEarnBackgroundColor
import com.actualplatform.activation.theming.postScanInlineProductTaskScanAndEarnLabelColor
import com.actualplatform.activation.theming.postScanInlineProductTaskWatchAndEarnBackgroundColor
import com.actualplatform.activation.theming.postScanInlineProductTaskWatchAndEarnLabelColor
import com.actualplatform.activation.theming.postScanMerchantNameLabelColor
import com.actualplatform.activation.theming.postScanNoBoostsLabelColor
import com.actualplatform.activation.theming.postScanPurchaseBackgroundColor
import com.actualplatform.activation.theming.postScanPurchaseInfoIconColor
import com.actualplatform.activation.theming.postScanPurchasePointsLabelColor
import com.actualplatform.activation.theming.postScanQualifiedPurchaseBackgroundColor
import com.actualplatform.activation.theming.postScanReceiptButtonBackgroundColor
import com.actualplatform.activation.theming.postScanReceiptButtonIconColor
import com.actualplatform.activation.theming.postScanSectionHeaderTitleLabelColor
import com.actualplatform.activation.theming.postScanSuccessDescriptionLabelColor
import com.actualplatform.activation.theming.postScanSuccessTitleLabelColor
import com.actualplatform.activation.theming.postScanTotalPointsBackgroundColor
import com.actualplatform.activation.theming.postScanTotalPointsLabelColor
import com.actualplatform.activation.theming.postScanTripInfoLabelColor
import com.actualplatform.activation.theming.purchaseRowLabelColorResolved
import com.actualplatform.activation.theming.purchaseRowMetadataLabelColorResolved
import com.actualplatform.activation.theming.storesHeaderBackgroundColor
import com.actualplatform.activation.theming.storesHeaderTitleLabelColor
import com.actualplatform.activation.theming.storesListBackgroundColor
import com.actualplatform.activation.theming.storesListItemBackgroundColor
import com.actualplatform.activation.theming.storesListItemDefaultIconColor
import com.actualplatform.activation.theming.storesListItemSubtitleLabelColor
import com.actualplatform.activation.theming.storesListItemTitleLabelColor
import com.actualplatform.activation.theming.storesListSectionHeaderLabelColor

/** Neutral placeholder swatch for tint-only-when-set keys, which have no theme-token fallback. */
private const val UntintedPlaceholder = 0xFF9E9E9EL

private fun androidx.compose.ui.graphics.Color.toLongColor(): Long =
    toArgb().toLong() and 0xFFFFFFFFL

@Composable
internal fun OffersWallColorsEditor(
    colors: ActivationAppearance.OffersWall.Colors,
    theme: ActivationTheme.Colors,
    onColorsChange: (ActivationAppearance.OffersWall.Colors) -> Unit,
) {
    val empty = ActivationAppearance.OffersWall.Colors()
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        SubSectionHeader("Offer Wall")
        NullableColorPickerRow(
            "offerWallBackground",
            colors.offerWallBackground,
            empty.offerWallBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerWallBackground = it))
        }
        NullableColorPickerRow(
            "offerWallSectionHeaderLabel",
            colors.offerWallSectionHeaderLabel,
            empty.offerWallSectionHeaderLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerWallSectionHeaderLabel = it))
        }
        NullableColorPickerRow(
            "offerWallSectionHeaderShowMoreIcon",
            colors.offerWallSectionHeaderShowMoreIcon,
            empty.offerWallSectionHeaderShowMoreIconColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerWallSectionHeaderShowMoreIcon = it))
        }
        NullableColorPickerRow(
            "offerWallSectionHeaderShowMoreBackground",
            colors.offerWallSectionHeaderShowMoreBackground,
            empty.offerWallSectionHeaderShowMoreBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerWallSectionHeaderShowMoreBackground = it))
        }
        NullableColorPickerRow(
            "offerWallFloatingButtonBackground",
            colors.offerWallFloatingButtonBackground,
            empty.offerWallFloatingButtonBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerWallFloatingButtonBackground = it))
        }
        NullableColorPickerRow(
            "offerWallFloatingButtonLabel",
            colors.offerWallFloatingButtonLabel,
            empty.offerWallFloatingButtonLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerWallFloatingButtonLabel = it))
        }
        NullableColorPickerRow(
            "offerWallMoreMerchantsIcon (untinted if unset)",
            colors.offerWallMoreMerchantsIcon,
            UntintedPlaceholder,
        ) {
            onColorsChange(colors.copy(offerWallMoreMerchantsIcon = it))
        }

        SubSectionHeader("Offer Card")
        NullableColorPickerRow(
            "offerBackground",
            colors.offerBackground,
            empty.offerBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerBackground = it))
        }
        NullableColorPickerRow(
            "offerBrandLabel",
            colors.offerBrandLabel,
            empty.offerBrandLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerBrandLabel = it))
        }
        NullableColorPickerRow(
            "offerEligibleMerchantsLabel",
            colors.offerEligibleMerchantsLabel,
            empty.offerEligibleMerchantsLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerEligibleMerchantsLabel = it))
        }
        NullableColorPickerRow(
            "offerRewardPointsLabel",
            colors.offerRewardPointsLabel,
            empty.offerRewardPointsLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerRewardPointsLabel = it))
        }
        NullableColorPickerRow(
            "offerTagLabel",
            colors.offerTagLabel,
            empty.offerTagLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerTagLabel = it))
        }
        NullableColorPickerRow(
            "offerTagBackground",
            colors.offerTagBackground,
            empty.offerTagBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerTagBackground = it))
        }

        SubSectionHeader("Clip / Clipped state")
        NullableColorPickerRow(
            "offerClipButtonIcon",
            colors.offerClipButtonIcon,
            empty.offerClipButtonIconColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerClipButtonIcon = it))
        }
        NullableColorPickerRow(
            "offerClipButtonBackground",
            colors.offerClipButtonBackground,
            empty.offerClipButtonBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerClipButtonBackground = it))
        }
        NullableColorPickerRow(
            "offerClippedButtonIcon",
            colors.offerClippedButtonIcon,
            empty.offerClippedButtonIconColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerClippedButtonIcon = it))
        }
        NullableColorPickerRow(
            "offerClippedButtonBackground",
            colors.offerClippedButtonBackground,
            empty.offerClippedButtonBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerClippedButtonBackground = it))
        }
        NullableColorPickerRow(
            "offerClippedToastMessageLabel",
            colors.offerClippedToastMessageLabel,
            empty.offerClippedToastMessageLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerClippedToastMessageLabel = it))
        }
        NullableColorPickerRow(
            "offerClippedToastMessageBackground",
            colors.offerClippedToastMessageBackground,
            empty.offerClippedToastMessageBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerClippedToastMessageBackground = it))
        }

        SubSectionHeader("Offer Details")
        NullableColorPickerRow(
            "offerDetailsTitleLabel",
            colors.offerDetailsTitleLabel,
            empty.offerDetailsTitleLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerDetailsTitleLabel = it))
        }
        NullableColorPickerRow(
            "offerDetailsEarnRewardLabel",
            colors.offerDetailsEarnRewardLabel,
            empty.offerDetailsEarnRewardLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerDetailsEarnRewardLabel = it))
        }
        NullableColorPickerRow(
            "offerDetailsClipLabel",
            colors.offerDetailsClipLabel,
            empty.offerDetailsClipLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerDetailsClipLabel = it))
        }
        NullableColorPickerRow(
            "offerDetailsSectionHeaderTitleLabel",
            colors.offerDetailsSectionHeaderTitleLabel,
            empty.offerDetailsSectionHeaderTitleLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerDetailsSectionHeaderTitleLabel = it))
        }
        NullableColorPickerRow(
            "offerDetailsSectionHeaderToggleLabel",
            colors.offerDetailsSectionHeaderToggleLabel,
            empty.offerDetailsSectionHeaderToggleLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerDetailsSectionHeaderToggleLabel = it))
        }
        NullableColorPickerRow(
            "offerDetailsSectionBodyLabel",
            colors.offerDetailsSectionBodyLabel,
            empty.offerDetailsSectionBodyLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerDetailsSectionBodyLabel = it))
        }
        NullableColorPickerRow(
            "offerDetailsFinePrintLabel",
            colors.offerDetailsFinePrintLabel,
            empty.offerDetailsFinePrintLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerDetailsFinePrintLabel = it))
        }
        NullableColorPickerRow(
            "offerDetailsTagChipLabel",
            colors.offerDetailsTagChipLabel,
            empty.offerDetailsTagChipLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerDetailsTagChipLabel = it))
        }
        NullableColorPickerRow(
            "offerDetailsTagChipBorder",
            colors.offerDetailsTagChipBorder,
            empty.offerDetailsTagChipBorderColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerDetailsTagChipBorder = it))
        }
        NullableColorPickerRow(
            "offerDetailsSectionNumberedListBadgeLabel",
            colors.offerDetailsSectionNumberedListBadgeLabel,
            empty.offerDetailsSectionNumberedListBadgeLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerDetailsSectionNumberedListBadgeLabel = it))
        }
        NullableColorPickerRow(
            "offerDetailsSectionNumberedListBadgeBackground",
            colors.offerDetailsSectionNumberedListBadgeBackground,
            empty.offerDetailsSectionNumberedListBadgeBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(offerDetailsSectionNumberedListBadgeBackground = it))
        }

        SubSectionHeader("Stores")
        NullableColorPickerRow(
            "storesHeaderBackground",
            colors.storesHeaderBackground,
            empty.storesHeaderBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(storesHeaderBackground = it))
        }
        NullableColorPickerRow(
            "storesHeaderTitleLabel",
            colors.storesHeaderTitleLabel,
            empty.storesHeaderTitleLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(storesHeaderTitleLabel = it))
        }
        NullableColorPickerRow(
            "storesListBackground",
            colors.storesListBackground,
            empty.storesListBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(storesListBackground = it))
        }
        NullableColorPickerRow(
            "storesListSectionHeaderLabel",
            colors.storesListSectionHeaderLabel,
            empty.storesListSectionHeaderLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(storesListSectionHeaderLabel = it))
        }
        NullableColorPickerRow(
            "storesListItemBackground",
            colors.storesListItemBackground,
            empty.storesListItemBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(storesListItemBackground = it))
        }
        NullableColorPickerRow(
            "storesListItemDefaultIcon",
            colors.storesListItemDefaultIcon,
            empty.storesListItemDefaultIconColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(storesListItemDefaultIcon = it))
        }
        NullableColorPickerRow(
            "storesListItemTitleLabel",
            colors.storesListItemTitleLabel,
            empty.storesListItemTitleLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(storesListItemTitleLabel = it))
        }
        NullableColorPickerRow(
            "storesListItemSubtitleLabel",
            colors.storesListItemSubtitleLabel,
            empty.storesListItemSubtitleLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(storesListItemSubtitleLabel = it))
        }
    }
}

@Composable
internal fun LoadingColorsEditor(
    colors: ActivationAppearance.Loading.Colors,
    theme: ActivationTheme.Colors,
    onColorsChange: (ActivationAppearance.Loading.Colors) -> Unit,
) {
    val empty = ActivationAppearance.Loading.Colors()
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        NullableColorPickerRow(
            "adLoadingLoadingBarLabel",
            colors.adLoadingLoadingBarLabel,
            empty.adLoadingLoadingBarLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(adLoadingLoadingBarLabel = it))
        }
        NullableColorPickerRow(
            "adLoadingLoadingBarBackground",
            colors.adLoadingLoadingBarBackground,
            empty.adLoadingLoadingBarBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(adLoadingLoadingBarBackground = it))
        }
        NullableColorPickerRow(
            "adLoadingLoadingBarProgress",
            colors.adLoadingLoadingBarProgress,
            empty.adLoadingLoadingBarProgressColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(adLoadingLoadingBarProgress = it))
        }
        NullableColorPickerRow(
            "adLoadingDefaultTitleLabel",
            colors.adLoadingDefaultTitleLabel,
            empty.adLoadingDefaultTitleLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(adLoadingDefaultTitleLabel = it))
        }
        NullableColorPickerRow(
            "adLoadingDefaultDescriptionLabel",
            colors.adLoadingDefaultDescriptionLabel,
            empty.adLoadingDefaultDescriptionLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(adLoadingDefaultDescriptionLabel = it))
        }
    }
}

@Composable
internal fun ErrorModalColorsEditor(
    colors: ActivationAppearance.ErrorModal.Colors,
    theme: ActivationTheme.Colors,
    onColorsChange: (ActivationAppearance.ErrorModal.Colors) -> Unit,
) {
    val empty = ActivationAppearance.ErrorModal.Colors()
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        NullableColorPickerRow(
            "errorModalBackground",
            colors.errorModalBackground,
            empty.errorModalBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(errorModalBackground = it))
        }
        NullableColorPickerRow(
            "errorModalIconBackground",
            colors.errorModalIconBackground,
            empty.errorModalIconBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(errorModalIconBackground = it))
        }
        NullableColorPickerRow(
            "errorModalTitleLabel",
            colors.errorModalTitleLabel,
            empty.errorModalTitleLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(errorModalTitleLabel = it))
        }
        NullableColorPickerRow(
            "errorModalDescriptionLabel",
            colors.errorModalDescriptionLabel,
            empty.errorModalDescriptionLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(errorModalDescriptionLabel = it))
        }
        NullableColorPickerRow(
            "errorModalBackButtonLabel",
            colors.errorModalBackButtonLabel,
            empty.errorModalBackButtonLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(errorModalBackButtonLabel = it))
        }
    }
}

@Composable
internal fun ReceiptSummaryColorsEditor(
    colors: ActivationAppearance.ReceiptSummary.Colors,
    theme: ActivationTheme.Colors,
    onColorsChange: (ActivationAppearance.ReceiptSummary.Colors) -> Unit,
) {
    val empty = ActivationAppearance.ReceiptSummary.Colors()
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        NullableColorPickerRow(
            "postScanHeaderBackground",
            colors.postScanHeaderBackground,
            empty.postScanHeaderBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanHeaderBackground = it))
        }
        NullableColorPickerRow(
            "postScanTotalPointsBackground",
            colors.postScanTotalPointsBackground,
            empty.postScanTotalPointsBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanTotalPointsBackground = it))
        }
        NullableColorPickerRow(
            "postScanTotalPointsLabel",
            colors.postScanTotalPointsLabel,
            empty.postScanTotalPointsLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanTotalPointsLabel = it))
        }
        NullableColorPickerRow(
            "postScanReceiptButtonIcon",
            colors.postScanReceiptButtonIcon,
            empty.postScanReceiptButtonIconColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanReceiptButtonIcon = it))
        }
        NullableColorPickerRow(
            "postScanReceiptButtonBackground",
            colors.postScanReceiptButtonBackground,
            empty.postScanReceiptButtonBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanReceiptButtonBackground = it))
        }
        NullableColorPickerRow(
            "postScanFooterBackground",
            colors.postScanFooterBackground,
            empty.postScanFooterBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanFooterBackground = it))
        }
        NullableColorPickerRow(
            "postScanFooterButtonTitle",
            colors.postScanFooterButtonTitle,
            empty.postScanFooterButtonTitleColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanFooterButtonTitle = it))
        }
        NullableColorPickerRow(
            "postScanMerchantNameLabel",
            colors.postScanMerchantNameLabel,
            empty.postScanMerchantNameLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanMerchantNameLabel = it))
        }
        NullableColorPickerRow(
            "postScanTripInfoLabel",
            colors.postScanTripInfoLabel,
            empty.postScanTripInfoLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanTripInfoLabel = it))
        }
        NullableColorPickerRow(
            "postScanSectionHeaderTitleLabel",
            colors.postScanSectionHeaderTitleLabel,
            empty.postScanSectionHeaderTitleLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanSectionHeaderTitleLabel = it))
        }
        NullableColorPickerRow(
            "postScanNoBoostsLabel",
            colors.postScanNoBoostsLabel,
            empty.postScanNoBoostsLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanNoBoostsLabel = it))
        }
        NullableColorPickerRow(
            "postScanSuccessTitleLabel",
            colors.postScanSuccessTitleLabel,
            empty.postScanSuccessTitleLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanSuccessTitleLabel = it))
        }
        NullableColorPickerRow(
            "postScanSuccessDescriptionLabel",
            colors.postScanSuccessDescriptionLabel,
            empty.postScanSuccessDescriptionLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanSuccessDescriptionLabel = it))
        }
        NullableColorPickerRow(
            "postScanBoostTitleLabel",
            colors.postScanBoostTitleLabel,
            empty.postScanBoostTitleLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanBoostTitleLabel = it))
        }
        NullableColorPickerRow(
            "postScanBoostDescriptionLabel",
            colors.postScanBoostDescriptionLabel,
            empty.postScanBoostDescriptionLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanBoostDescriptionLabel = it))
        }
        NullableColorPickerRow(
            "postScanBoostSkipButtonLabel",
            colors.postScanBoostSkipButtonLabel,
            empty.postScanBoostSkipButtonLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanBoostSkipButtonLabel = it))
        }
        NullableColorPickerRow(
            "postScanBoostClaimButtonLabel",
            colors.postScanBoostClaimButtonLabel,
            empty.postScanBoostClaimButtonLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanBoostClaimButtonLabel = it))
        }
        NullableColorPickerRow(
            "postScanBoostClaimButtonIcon",
            colors.postScanBoostClaimButtonIcon,
            empty.postScanBoostClaimButtonIconColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanBoostClaimButtonIcon = it))
        }
        NullableColorPickerRow(
            "postScanBoostClaimButtonBackground",
            colors.postScanBoostClaimButtonBackground,
            empty.postScanBoostClaimButtonBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanBoostClaimButtonBackground = it))
        }
        NullableColorPickerRow(
            "postScanPurchasePointsLabel",
            colors.postScanPurchasePointsLabel,
            empty.postScanPurchasePointsLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanPurchasePointsLabel = it))
        }
        NullableColorPickerRow(
            "postScanPurchaseBackground",
            colors.postScanPurchaseBackground,
            empty.postScanPurchaseBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanPurchaseBackground = it))
        }
        NullableColorPickerRow(
            "postScanQualifiedPurchaseBackground",
            colors.postScanQualifiedPurchaseBackground,
            empty.postScanQualifiedPurchaseBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanQualifiedPurchaseBackground = it))
        }
        NullableColorPickerRow(
            "postScanPurchaseInfoIcon",
            colors.postScanPurchaseInfoIcon,
            empty.postScanPurchaseInfoIconColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanPurchaseInfoIcon = it))
        }
        NullableColorPickerRow(
            "postScanInlineProductTaskBackground",
            colors.postScanInlineProductTaskBackground,
            empty.postScanInlineProductTaskBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanInlineProductTaskBackground = it))
        }
        NullableColorPickerRow(
            "postScanInlineProductTaskScanAndEarnBackground",
            colors.postScanInlineProductTaskScanAndEarnBackground,
            empty.postScanInlineProductTaskScanAndEarnBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanInlineProductTaskScanAndEarnBackground = it))
        }
        NullableColorPickerRow(
            "postScanInlineProductTaskWatchAndEarnBackground",
            colors.postScanInlineProductTaskWatchAndEarnBackground,
            empty.postScanInlineProductTaskWatchAndEarnBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanInlineProductTaskWatchAndEarnBackground = it))
        }
        NullableColorPickerRow(
            "postScanInlineProductTaskScanAndEarnLabel",
            colors.postScanInlineProductTaskScanAndEarnLabel,
            empty.postScanInlineProductTaskScanAndEarnLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanInlineProductTaskScanAndEarnLabel = it))
        }
        NullableColorPickerRow(
            "postScanInlineProductTaskWatchAndEarnLabel",
            colors.postScanInlineProductTaskWatchAndEarnLabel,
            empty.postScanInlineProductTaskWatchAndEarnLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanInlineProductTaskWatchAndEarnLabel = it))
        }
        NullableColorPickerRow(
            "postScanInlineProductTaskPointsLabel",
            colors.postScanInlineProductTaskPointsLabel,
            empty.postScanInlineProductTaskPointsLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(postScanInlineProductTaskPointsLabel = it))
        }
        NullableColorPickerRow(
            "purchaseRowLabelColor",
            colors.purchaseRowLabelColor,
            empty.purchaseRowLabelColorResolved(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(purchaseRowLabelColor = it))
        }
        NullableColorPickerRow(
            "purchaseRowMetadataLabelColor",
            colors.purchaseRowMetadataLabelColor,
            empty.purchaseRowMetadataLabelColorResolved(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(purchaseRowMetadataLabelColor = it))
        }
    }
}

@Composable
internal fun MissedEarningsColorsEditor(
    colors: ActivationAppearance.MissedEarnings.Colors,
    theme: ActivationTheme.Colors,
    onColorsChange: (ActivationAppearance.MissedEarnings.Colors) -> Unit,
) {
    val empty = ActivationAppearance.MissedEarnings.Colors()
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        NullableColorPickerRow(
            "missedEarningsNavigationTitleLabel",
            colors.missedEarningsNavigationTitleLabel,
            empty.missedEarningsNavigationTitleLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsNavigationTitleLabel = it))
        }
        NullableColorPickerRow(
            "missedEarningsNavigationDescriptionLabel",
            colors.missedEarningsNavigationDescriptionLabel,
            empty.missedEarningsNavigationDescriptionLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsNavigationDescriptionLabel = it))
        }
        NullableColorPickerRow(
            "missedEarningsNavigationBarText",
            colors.missedEarningsNavigationBarText,
            empty.missedEarningsNavigationBarTextColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsNavigationBarText = it))
        }
        NullableColorPickerRow(
            "missedEarningsNavigationEditButtonIcon",
            colors.missedEarningsNavigationEditButtonIcon,
            empty.missedEarningsNavigationEditButtonIconColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsNavigationEditButtonIcon = it))
        }
        NullableColorPickerRow(
            "missedEarningsNavigationEditButtonBackground",
            colors.missedEarningsNavigationEditButtonBackground,
            empty.missedEarningsNavigationEditButtonBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsNavigationEditButtonBackground = it))
        }
        NullableColorPickerRow(
            "missedEarningsNavigationSaveButtonIcon (untinted if unset)",
            colors.missedEarningsNavigationSaveButtonIcon,
            UntintedPlaceholder,
        ) {
            onColorsChange(colors.copy(missedEarningsNavigationSaveButtonIcon = it))
        }
        NullableColorPickerRow(
            "missedEarningsNavigationSaveButtonBackground",
            colors.missedEarningsNavigationSaveButtonBackground,
            empty.missedEarningsNavigationSaveButtonBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsNavigationSaveButtonBackground = it))
        }
        NullableColorPickerRow(
            "missedEarningsFieldEditIcon",
            colors.missedEarningsFieldEditIcon,
            empty.missedEarningsFieldEditIconColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsFieldEditIcon = it))
        }
        NullableColorPickerRow(
            "missedEarningsAddNewFieldLabel",
            colors.missedEarningsAddNewFieldLabel,
            empty.missedEarningsAddNewFieldLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsAddNewFieldLabel = it))
        }
        NullableColorPickerRow(
            "missedEarningsModifiedFieldBackground",
            colors.missedEarningsModifiedFieldBackground,
            empty.missedEarningsModifiedFieldBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsModifiedFieldBackground = it))
        }
        NullableColorPickerRow(
            "missedEarningsListSectionTitleLabel",
            colors.missedEarningsListSectionTitleLabel,
            empty.missedEarningsListSectionTitleLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsListSectionTitleLabel = it))
        }
        NullableColorPickerRow(
            "missedEarningsTripItemLabel",
            colors.missedEarningsTripItemLabel,
            empty.missedEarningsTripItemLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsTripItemLabel = it))
        }
        NullableColorPickerRow(
            "missedEarningsEditModalBackground",
            colors.missedEarningsEditModalBackground,
            empty.missedEarningsEditModalBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsEditModalBackground = it))
        }
        NullableColorPickerRow(
            "missedEarningsEditModalTitleLabel",
            colors.missedEarningsEditModalTitleLabel,
            empty.missedEarningsEditModalTitleLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsEditModalTitleLabel = it))
        }
        NullableColorPickerRow(
            "missedEarningsEditModalInputLabel",
            colors.missedEarningsEditModalInputLabel,
            empty.missedEarningsEditModalInputLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsEditModalInputLabel = it))
        }
        NullableColorPickerRow(
            "missedEarningsEditModalInputPlaceholderLabel",
            colors.missedEarningsEditModalInputPlaceholderLabel,
            empty.missedEarningsEditModalInputPlaceholderLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsEditModalInputPlaceholderLabel = it))
        }
        NullableColorPickerRow(
            "missedEarningsEditModalInputValueLabel",
            colors.missedEarningsEditModalInputValueLabel,
            empty.missedEarningsEditModalInputValueLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsEditModalInputValueLabel = it))
        }
        NullableColorPickerRow(
            "missedEarningsEditModalCancelButtonLabel",
            colors.missedEarningsEditModalCancelButtonLabel,
            empty.missedEarningsEditModalCancelButtonLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsEditModalCancelButtonLabel = it))
        }
        NullableColorPickerRow(
            "missedEarningsEditModalSaveButtonLabel",
            colors.missedEarningsEditModalSaveButtonLabel,
            empty.missedEarningsEditModalSaveButtonLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsEditModalSaveButtonLabel = it))
        }
        NullableColorPickerRow(
            "missedEarningsEditModalSaveButtonBackground",
            colors.missedEarningsEditModalSaveButtonBackground,
            empty.missedEarningsEditModalSaveButtonBackgroundColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsEditModalSaveButtonBackground = it))
        }
        NullableColorPickerRow(
            "missedEarningsEditModalDatePicker",
            colors.missedEarningsEditModalDatePicker,
            empty.missedEarningsEditModalDatePickerColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsEditModalDatePicker = it))
        }
        NullableColorPickerRow(
            "missedEarningsAlertTitleLabel",
            colors.missedEarningsAlertTitleLabel,
            empty.missedEarningsAlertTitleLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsAlertTitleLabel = it))
        }
        NullableColorPickerRow(
            "missedEarningsAlertMessageLabel",
            colors.missedEarningsAlertMessageLabel,
            empty.missedEarningsAlertMessageLabelColor(theme).toLongColor(),
        ) {
            onColorsChange(colors.copy(missedEarningsAlertMessageLabel = it))
        }
    }
}
