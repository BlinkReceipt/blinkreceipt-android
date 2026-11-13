package com.actualplatform.android.activation.development.ui.themes.local

import kotlinx.serialization.Serializable
import com.actualplatform.activation.theming.ActivationAppearance as ActivationAppearanceModel

/**
 * Serializable mirror of [ActivationAppearanceModel] for DataStore persistence. Mirrors the SDK
 * model's nested shape (per-screen scope → `Colors`/`Labels`) so old payloads — which only carried
 * `offersWall.labels` — keep decoding, and every color key is `Long? = null` matching the SDK's
 * override contract (null = fall back to the theme token the element already renders with).
 */
@Serializable
internal data class ActivationAppearance(
    val offersWall: OffersWall = OffersWall(),
    val loading: Loading = Loading(),
    val errorModal: ErrorModal = ErrorModal(),
    val receiptSummary: ReceiptSummary = ReceiptSummary(),
    val missedEarnings: MissedEarnings = MissedEarnings(),
) {
    @Serializable
    internal data class OffersWall(
        val colors: Colors = Colors(),
        val labels: Labels = Labels(),
    ) {
        @Serializable
        internal data class Labels(
            val scanLabel: String? = null,
            val scanExtendedLabel: String? = null,
        )

        @Serializable
        internal data class Colors(
            // ── Offer Wall ──────────────────────────────────────────────────────
            val offerWallBackground: Long? = null,
            val offerWallSectionHeaderLabel: Long? = null,
            val offerWallSectionHeaderShowMoreIcon: Long? = null,
            val offerWallSectionHeaderShowMoreBackground: Long? = null,
            val offerWallFloatingButtonBackground: Long? = null,
            val offerWallFloatingButtonLabel: Long? = null,
            val offerWallMoreMerchantsIcon: Long? = null,
            // ── Offer Card (grid + list variants) ───────────────────────────────
            val offerBackground: Long? = null,
            val offerBrandLabel: Long? = null,
            val offerEligibleMerchantsLabel: Long? = null,
            val offerRewardPointsLabel: Long? = null,
            val offerTagLabel: Long? = null,
            val offerTagBackground: Long? = null,
            // ── Clip / Clipped state ────────────────────────────────────────────
            val offerClipButtonIcon: Long? = null,
            val offerClipButtonBackground: Long? = null,
            val offerClippedButtonIcon: Long? = null,
            val offerClippedButtonBackground: Long? = null,
            val offerClippedToastMessageLabel: Long? = null,
            val offerClippedToastMessageBackground: Long? = null,
            // ── Offer Details ───────────────────────────────────────────────────
            val offerDetailsTitleLabel: Long? = null,
            val offerDetailsEarnRewardLabel: Long? = null,
            val offerDetailsClipLabel: Long? = null,
            val offerDetailsSectionHeaderTitleLabel: Long? = null,
            val offerDetailsSectionHeaderToggleLabel: Long? = null,
            val offerDetailsSectionBodyLabel: Long? = null,
            val offerDetailsFinePrintLabel: Long? = null,
            val offerDetailsTagChipLabel: Long? = null,
            val offerDetailsTagChipBorder: Long? = null,
            val offerDetailsSectionNumberedListBadgeLabel: Long? = null,
            val offerDetailsSectionNumberedListBadgeBackground: Long? = null,
            // ── Stores ───────────────────────────────────────────────────────────
            val storesHeaderBackground: Long? = null,
            val storesHeaderTitleLabel: Long? = null,
            val storesListBackground: Long? = null,
            val storesListSectionHeaderLabel: Long? = null,
            val storesListItemBackground: Long? = null,
            val storesListItemDefaultIcon: Long? = null,
            val storesListItemTitleLabel: Long? = null,
            val storesListItemSubtitleLabel: Long? = null,
        )
    }

    @Serializable
    internal data class Loading(val colors: Colors = Colors()) {
        @Serializable
        internal data class Colors(
            val adLoadingLoadingBarLabel: Long? = null,
            val adLoadingLoadingBarBackground: Long? = null,
            val adLoadingLoadingBarProgress: Long? = null,
            val adLoadingDefaultTitleLabel: Long? = null,
            val adLoadingDefaultDescriptionLabel: Long? = null,
        )
    }

    @Serializable
    internal data class ErrorModal(val colors: Colors = Colors()) {
        @Serializable
        internal data class Colors(
            val errorModalBackground: Long? = null,
            val errorModalIconBackground: Long? = null,
            val errorModalTitleLabel: Long? = null,
            val errorModalDescriptionLabel: Long? = null,
            val errorModalBackButtonLabel: Long? = null,
        )
    }

    @Serializable
    internal data class ReceiptSummary(val colors: Colors = Colors()) {
        @Serializable
        internal data class Colors(
            val postScanHeaderBackground: Long? = null,
            val postScanTotalPointsBackground: Long? = null,
            val postScanTotalPointsLabel: Long? = null,
            val postScanReceiptButtonIcon: Long? = null,
            val postScanReceiptButtonBackground: Long? = null,
            val postScanFooterBackground: Long? = null,
            val postScanFooterButtonTitle: Long? = null,
            val postScanMerchantNameLabel: Long? = null,
            val postScanTripInfoLabel: Long? = null,
            val postScanSectionHeaderTitleLabel: Long? = null,
            val postScanNoBoostsLabel: Long? = null,
            val postScanSuccessTitleLabel: Long? = null,
            val postScanSuccessDescriptionLabel: Long? = null,
            val postScanBoostTitleLabel: Long? = null,
            val postScanBoostDescriptionLabel: Long? = null,
            val postScanBoostSkipButtonLabel: Long? = null,
            val postScanBoostClaimButtonLabel: Long? = null,
            val postScanBoostClaimButtonIcon: Long? = null,
            val postScanBoostClaimButtonBackground: Long? = null,
            val postScanPurchasePointsLabel: Long? = null,
            val postScanPurchaseBackground: Long? = null,
            val postScanQualifiedPurchaseBackground: Long? = null,
            val postScanPurchaseInfoIcon: Long? = null,
            val postScanInlineProductTaskBackground: Long? = null,
            val postScanInlineProductTaskScanAndEarnBackground: Long? = null,
            val postScanInlineProductTaskWatchAndEarnBackground: Long? = null,
            val postScanInlineProductTaskScanAndEarnLabel: Long? = null,
            val postScanInlineProductTaskWatchAndEarnLabel: Long? = null,
            val postScanInlineProductTaskPointsLabel: Long? = null,
            val purchaseRowLabelColor: Long? = null,
            val purchaseRowMetadataLabelColor: Long? = null,
        )
    }

    @Serializable
    internal data class MissedEarnings(val colors: Colors = Colors()) {
        @Serializable
        internal data class Colors(
            val missedEarningsNavigationTitleLabel: Long? = null,
            val missedEarningsNavigationDescriptionLabel: Long? = null,
            val missedEarningsNavigationBarText: Long? = null,
            val missedEarningsNavigationEditButtonIcon: Long? = null,
            val missedEarningsNavigationEditButtonBackground: Long? = null,
            val missedEarningsNavigationSaveButtonIcon: Long? = null,
            val missedEarningsNavigationSaveButtonBackground: Long? = null,
            val missedEarningsFieldEditIcon: Long? = null,
            val missedEarningsAddNewFieldLabel: Long? = null,
            val missedEarningsModifiedFieldBackground: Long? = null,
            val missedEarningsListSectionTitleLabel: Long? = null,
            val missedEarningsTripItemLabel: Long? = null,
            val missedEarningsEditModalBackground: Long? = null,
            val missedEarningsEditModalTitleLabel: Long? = null,
            val missedEarningsEditModalInputLabel: Long? = null,
            val missedEarningsEditModalInputPlaceholderLabel: Long? = null,
            val missedEarningsEditModalInputValueLabel: Long? = null,
            val missedEarningsEditModalCancelButtonLabel: Long? = null,
            val missedEarningsEditModalSaveButtonLabel: Long? = null,
            val missedEarningsEditModalSaveButtonBackground: Long? = null,
            val missedEarningsEditModalDatePicker: Long? = null,
            val missedEarningsAlertTitleLabel: Long? = null,
            val missedEarningsAlertMessageLabel: Long? = null,
        )
    }
}

private fun ActivationAppearanceModel.OffersWall.Colors.toLocal(): ActivationAppearance.OffersWall.Colors =
    ActivationAppearance.OffersWall.Colors(
        offerWallBackground = offerWallBackground,
        offerWallSectionHeaderLabel = offerWallSectionHeaderLabel,
        offerWallSectionHeaderShowMoreIcon = offerWallSectionHeaderShowMoreIcon,
        offerWallSectionHeaderShowMoreBackground = offerWallSectionHeaderShowMoreBackground,
        offerWallFloatingButtonBackground = offerWallFloatingButtonBackground,
        offerWallFloatingButtonLabel = offerWallFloatingButtonLabel,
        offerWallMoreMerchantsIcon = offerWallMoreMerchantsIcon,
        offerBackground = offerBackground,
        offerBrandLabel = offerBrandLabel,
        offerEligibleMerchantsLabel = offerEligibleMerchantsLabel,
        offerRewardPointsLabel = offerRewardPointsLabel,
        offerTagLabel = offerTagLabel,
        offerTagBackground = offerTagBackground,
        offerClipButtonIcon = offerClipButtonIcon,
        offerClipButtonBackground = offerClipButtonBackground,
        offerClippedButtonIcon = offerClippedButtonIcon,
        offerClippedButtonBackground = offerClippedButtonBackground,
        offerClippedToastMessageLabel = offerClippedToastMessageLabel,
        offerClippedToastMessageBackground = offerClippedToastMessageBackground,
        offerDetailsTitleLabel = offerDetailsTitleLabel,
        offerDetailsEarnRewardLabel = offerDetailsEarnRewardLabel,
        offerDetailsClipLabel = offerDetailsClipLabel,
        offerDetailsSectionHeaderTitleLabel = offerDetailsSectionHeaderTitleLabel,
        offerDetailsSectionHeaderToggleLabel = offerDetailsSectionHeaderToggleLabel,
        offerDetailsSectionBodyLabel = offerDetailsSectionBodyLabel,
        offerDetailsFinePrintLabel = offerDetailsFinePrintLabel,
        offerDetailsTagChipLabel = offerDetailsTagChipLabel,
        offerDetailsTagChipBorder = offerDetailsTagChipBorder,
        offerDetailsSectionNumberedListBadgeLabel = offerDetailsSectionNumberedListBadgeLabel,
        offerDetailsSectionNumberedListBadgeBackground = offerDetailsSectionNumberedListBadgeBackground,
        storesHeaderBackground = storesHeaderBackground,
        storesHeaderTitleLabel = storesHeaderTitleLabel,
        storesListBackground = storesListBackground,
        storesListSectionHeaderLabel = storesListSectionHeaderLabel,
        storesListItemBackground = storesListItemBackground,
        storesListItemDefaultIcon = storesListItemDefaultIcon,
        storesListItemTitleLabel = storesListItemTitleLabel,
        storesListItemSubtitleLabel = storesListItemSubtitleLabel,
    )

private fun ActivationAppearance.OffersWall.Colors.toModel(): ActivationAppearanceModel.OffersWall.Colors =
    ActivationAppearanceModel.OffersWall.Colors(
        offerWallBackground = offerWallBackground,
        offerWallSectionHeaderLabel = offerWallSectionHeaderLabel,
        offerWallSectionHeaderShowMoreIcon = offerWallSectionHeaderShowMoreIcon,
        offerWallSectionHeaderShowMoreBackground = offerWallSectionHeaderShowMoreBackground,
        offerWallFloatingButtonBackground = offerWallFloatingButtonBackground,
        offerWallFloatingButtonLabel = offerWallFloatingButtonLabel,
        offerWallMoreMerchantsIcon = offerWallMoreMerchantsIcon,
        offerBackground = offerBackground,
        offerBrandLabel = offerBrandLabel,
        offerEligibleMerchantsLabel = offerEligibleMerchantsLabel,
        offerRewardPointsLabel = offerRewardPointsLabel,
        offerTagLabel = offerTagLabel,
        offerTagBackground = offerTagBackground,
        offerClipButtonIcon = offerClipButtonIcon,
        offerClipButtonBackground = offerClipButtonBackground,
        offerClippedButtonIcon = offerClippedButtonIcon,
        offerClippedButtonBackground = offerClippedButtonBackground,
        offerClippedToastMessageLabel = offerClippedToastMessageLabel,
        offerClippedToastMessageBackground = offerClippedToastMessageBackground,
        offerDetailsTitleLabel = offerDetailsTitleLabel,
        offerDetailsEarnRewardLabel = offerDetailsEarnRewardLabel,
        offerDetailsClipLabel = offerDetailsClipLabel,
        offerDetailsSectionHeaderTitleLabel = offerDetailsSectionHeaderTitleLabel,
        offerDetailsSectionHeaderToggleLabel = offerDetailsSectionHeaderToggleLabel,
        offerDetailsSectionBodyLabel = offerDetailsSectionBodyLabel,
        offerDetailsFinePrintLabel = offerDetailsFinePrintLabel,
        offerDetailsTagChipLabel = offerDetailsTagChipLabel,
        offerDetailsTagChipBorder = offerDetailsTagChipBorder,
        offerDetailsSectionNumberedListBadgeLabel = offerDetailsSectionNumberedListBadgeLabel,
        offerDetailsSectionNumberedListBadgeBackground = offerDetailsSectionNumberedListBadgeBackground,
        storesHeaderBackground = storesHeaderBackground,
        storesHeaderTitleLabel = storesHeaderTitleLabel,
        storesListBackground = storesListBackground,
        storesListSectionHeaderLabel = storesListSectionHeaderLabel,
        storesListItemBackground = storesListItemBackground,
        storesListItemDefaultIcon = storesListItemDefaultIcon,
        storesListItemTitleLabel = storesListItemTitleLabel,
        storesListItemSubtitleLabel = storesListItemSubtitleLabel,
    )

private fun ActivationAppearanceModel.Loading.Colors.toLocal(): ActivationAppearance.Loading.Colors =
    ActivationAppearance.Loading.Colors(
        adLoadingLoadingBarLabel = adLoadingLoadingBarLabel,
        adLoadingLoadingBarBackground = adLoadingLoadingBarBackground,
        adLoadingLoadingBarProgress = adLoadingLoadingBarProgress,
        adLoadingDefaultTitleLabel = adLoadingDefaultTitleLabel,
        adLoadingDefaultDescriptionLabel = adLoadingDefaultDescriptionLabel,
    )

private fun ActivationAppearance.Loading.Colors.toModel(): ActivationAppearanceModel.Loading.Colors =
    ActivationAppearanceModel.Loading.Colors(
        adLoadingLoadingBarLabel = adLoadingLoadingBarLabel,
        adLoadingLoadingBarBackground = adLoadingLoadingBarBackground,
        adLoadingLoadingBarProgress = adLoadingLoadingBarProgress,
        adLoadingDefaultTitleLabel = adLoadingDefaultTitleLabel,
        adLoadingDefaultDescriptionLabel = adLoadingDefaultDescriptionLabel,
    )

private fun ActivationAppearanceModel.ErrorModal.Colors.toLocal(): ActivationAppearance.ErrorModal.Colors =
    ActivationAppearance.ErrorModal.Colors(
        errorModalBackground = errorModalBackground,
        errorModalIconBackground = errorModalIconBackground,
        errorModalTitleLabel = errorModalTitleLabel,
        errorModalDescriptionLabel = errorModalDescriptionLabel,
        errorModalBackButtonLabel = errorModalBackButtonLabel,
    )

private fun ActivationAppearance.ErrorModal.Colors.toModel(): ActivationAppearanceModel.ErrorModal.Colors =
    ActivationAppearanceModel.ErrorModal.Colors(
        errorModalBackground = errorModalBackground,
        errorModalIconBackground = errorModalIconBackground,
        errorModalTitleLabel = errorModalTitleLabel,
        errorModalDescriptionLabel = errorModalDescriptionLabel,
        errorModalBackButtonLabel = errorModalBackButtonLabel,
    )

private fun ActivationAppearanceModel.ReceiptSummary.Colors.toLocal(): ActivationAppearance.ReceiptSummary.Colors =
    ActivationAppearance.ReceiptSummary.Colors(
        postScanHeaderBackground = postScanHeaderBackground,
        postScanTotalPointsBackground = postScanTotalPointsBackground,
        postScanTotalPointsLabel = postScanTotalPointsLabel,
        postScanReceiptButtonIcon = postScanReceiptButtonIcon,
        postScanReceiptButtonBackground = postScanReceiptButtonBackground,
        postScanFooterBackground = postScanFooterBackground,
        postScanFooterButtonTitle = postScanFooterButtonTitle,
        postScanMerchantNameLabel = postScanMerchantNameLabel,
        postScanTripInfoLabel = postScanTripInfoLabel,
        postScanSectionHeaderTitleLabel = postScanSectionHeaderTitleLabel,
        postScanNoBoostsLabel = postScanNoBoostsLabel,
        postScanSuccessTitleLabel = postScanSuccessTitleLabel,
        postScanSuccessDescriptionLabel = postScanSuccessDescriptionLabel,
        postScanBoostTitleLabel = postScanBoostTitleLabel,
        postScanBoostDescriptionLabel = postScanBoostDescriptionLabel,
        postScanBoostSkipButtonLabel = postScanBoostSkipButtonLabel,
        postScanBoostClaimButtonLabel = postScanBoostClaimButtonLabel,
        postScanBoostClaimButtonIcon = postScanBoostClaimButtonIcon,
        postScanBoostClaimButtonBackground = postScanBoostClaimButtonBackground,
        postScanPurchasePointsLabel = postScanPurchasePointsLabel,
        postScanPurchaseBackground = postScanPurchaseBackground,
        postScanQualifiedPurchaseBackground = postScanQualifiedPurchaseBackground,
        postScanPurchaseInfoIcon = postScanPurchaseInfoIcon,
        postScanInlineProductTaskBackground = postScanInlineProductTaskBackground,
        postScanInlineProductTaskScanAndEarnBackground = postScanInlineProductTaskScanAndEarnBackground,
        postScanInlineProductTaskWatchAndEarnBackground = postScanInlineProductTaskWatchAndEarnBackground,
        postScanInlineProductTaskScanAndEarnLabel = postScanInlineProductTaskScanAndEarnLabel,
        postScanInlineProductTaskWatchAndEarnLabel = postScanInlineProductTaskWatchAndEarnLabel,
        postScanInlineProductTaskPointsLabel = postScanInlineProductTaskPointsLabel,
        purchaseRowLabelColor = purchaseRowLabelColor,
        purchaseRowMetadataLabelColor = purchaseRowMetadataLabelColor,
    )

private fun ActivationAppearance.ReceiptSummary.Colors.toModel(): ActivationAppearanceModel.ReceiptSummary.Colors =
    ActivationAppearanceModel.ReceiptSummary.Colors(
        postScanHeaderBackground = postScanHeaderBackground,
        postScanTotalPointsBackground = postScanTotalPointsBackground,
        postScanTotalPointsLabel = postScanTotalPointsLabel,
        postScanReceiptButtonIcon = postScanReceiptButtonIcon,
        postScanReceiptButtonBackground = postScanReceiptButtonBackground,
        postScanFooterBackground = postScanFooterBackground,
        postScanFooterButtonTitle = postScanFooterButtonTitle,
        postScanMerchantNameLabel = postScanMerchantNameLabel,
        postScanTripInfoLabel = postScanTripInfoLabel,
        postScanSectionHeaderTitleLabel = postScanSectionHeaderTitleLabel,
        postScanNoBoostsLabel = postScanNoBoostsLabel,
        postScanSuccessTitleLabel = postScanSuccessTitleLabel,
        postScanSuccessDescriptionLabel = postScanSuccessDescriptionLabel,
        postScanBoostTitleLabel = postScanBoostTitleLabel,
        postScanBoostDescriptionLabel = postScanBoostDescriptionLabel,
        postScanBoostSkipButtonLabel = postScanBoostSkipButtonLabel,
        postScanBoostClaimButtonLabel = postScanBoostClaimButtonLabel,
        postScanBoostClaimButtonIcon = postScanBoostClaimButtonIcon,
        postScanBoostClaimButtonBackground = postScanBoostClaimButtonBackground,
        postScanPurchasePointsLabel = postScanPurchasePointsLabel,
        postScanPurchaseBackground = postScanPurchaseBackground,
        postScanQualifiedPurchaseBackground = postScanQualifiedPurchaseBackground,
        postScanPurchaseInfoIcon = postScanPurchaseInfoIcon,
        postScanInlineProductTaskBackground = postScanInlineProductTaskBackground,
        postScanInlineProductTaskScanAndEarnBackground = postScanInlineProductTaskScanAndEarnBackground,
        postScanInlineProductTaskWatchAndEarnBackground = postScanInlineProductTaskWatchAndEarnBackground,
        postScanInlineProductTaskScanAndEarnLabel = postScanInlineProductTaskScanAndEarnLabel,
        postScanInlineProductTaskWatchAndEarnLabel = postScanInlineProductTaskWatchAndEarnLabel,
        postScanInlineProductTaskPointsLabel = postScanInlineProductTaskPointsLabel,
        purchaseRowLabelColor = purchaseRowLabelColor,
        purchaseRowMetadataLabelColor = purchaseRowMetadataLabelColor,
    )

private fun ActivationAppearanceModel.MissedEarnings.Colors.toLocal(): ActivationAppearance.MissedEarnings.Colors =
    ActivationAppearance.MissedEarnings.Colors(
        missedEarningsNavigationTitleLabel = missedEarningsNavigationTitleLabel,
        missedEarningsNavigationDescriptionLabel = missedEarningsNavigationDescriptionLabel,
        missedEarningsNavigationBarText = missedEarningsNavigationBarText,
        missedEarningsNavigationEditButtonIcon = missedEarningsNavigationEditButtonIcon,
        missedEarningsNavigationEditButtonBackground = missedEarningsNavigationEditButtonBackground,
        missedEarningsNavigationSaveButtonIcon = missedEarningsNavigationSaveButtonIcon,
        missedEarningsNavigationSaveButtonBackground = missedEarningsNavigationSaveButtonBackground,
        missedEarningsFieldEditIcon = missedEarningsFieldEditIcon,
        missedEarningsAddNewFieldLabel = missedEarningsAddNewFieldLabel,
        missedEarningsModifiedFieldBackground = missedEarningsModifiedFieldBackground,
        missedEarningsListSectionTitleLabel = missedEarningsListSectionTitleLabel,
        missedEarningsTripItemLabel = missedEarningsTripItemLabel,
        missedEarningsEditModalBackground = missedEarningsEditModalBackground,
        missedEarningsEditModalTitleLabel = missedEarningsEditModalTitleLabel,
        missedEarningsEditModalInputLabel = missedEarningsEditModalInputLabel,
        missedEarningsEditModalInputPlaceholderLabel = missedEarningsEditModalInputPlaceholderLabel,
        missedEarningsEditModalInputValueLabel = missedEarningsEditModalInputValueLabel,
        missedEarningsEditModalCancelButtonLabel = missedEarningsEditModalCancelButtonLabel,
        missedEarningsEditModalSaveButtonLabel = missedEarningsEditModalSaveButtonLabel,
        missedEarningsEditModalSaveButtonBackground = missedEarningsEditModalSaveButtonBackground,
        missedEarningsEditModalDatePicker = missedEarningsEditModalDatePicker,
        missedEarningsAlertTitleLabel = missedEarningsAlertTitleLabel,
        missedEarningsAlertMessageLabel = missedEarningsAlertMessageLabel,
    )

private fun ActivationAppearance.MissedEarnings.Colors.toModel(): ActivationAppearanceModel.MissedEarnings.Colors =
    ActivationAppearanceModel.MissedEarnings.Colors(
        missedEarningsNavigationTitleLabel = missedEarningsNavigationTitleLabel,
        missedEarningsNavigationDescriptionLabel = missedEarningsNavigationDescriptionLabel,
        missedEarningsNavigationBarText = missedEarningsNavigationBarText,
        missedEarningsNavigationEditButtonIcon = missedEarningsNavigationEditButtonIcon,
        missedEarningsNavigationEditButtonBackground = missedEarningsNavigationEditButtonBackground,
        missedEarningsNavigationSaveButtonIcon = missedEarningsNavigationSaveButtonIcon,
        missedEarningsNavigationSaveButtonBackground = missedEarningsNavigationSaveButtonBackground,
        missedEarningsFieldEditIcon = missedEarningsFieldEditIcon,
        missedEarningsAddNewFieldLabel = missedEarningsAddNewFieldLabel,
        missedEarningsModifiedFieldBackground = missedEarningsModifiedFieldBackground,
        missedEarningsListSectionTitleLabel = missedEarningsListSectionTitleLabel,
        missedEarningsTripItemLabel = missedEarningsTripItemLabel,
        missedEarningsEditModalBackground = missedEarningsEditModalBackground,
        missedEarningsEditModalTitleLabel = missedEarningsEditModalTitleLabel,
        missedEarningsEditModalInputLabel = missedEarningsEditModalInputLabel,
        missedEarningsEditModalInputPlaceholderLabel = missedEarningsEditModalInputPlaceholderLabel,
        missedEarningsEditModalInputValueLabel = missedEarningsEditModalInputValueLabel,
        missedEarningsEditModalCancelButtonLabel = missedEarningsEditModalCancelButtonLabel,
        missedEarningsEditModalSaveButtonLabel = missedEarningsEditModalSaveButtonLabel,
        missedEarningsEditModalSaveButtonBackground = missedEarningsEditModalSaveButtonBackground,
        missedEarningsEditModalDatePicker = missedEarningsEditModalDatePicker,
        missedEarningsAlertTitleLabel = missedEarningsAlertTitleLabel,
        missedEarningsAlertMessageLabel = missedEarningsAlertMessageLabel,
    )

internal fun ActivationAppearanceModel.toLocal(): ActivationAppearance =
    ActivationAppearance(
        offersWall = ActivationAppearance.OffersWall(
            colors = offersWall.colors.toLocal(),
            labels = ActivationAppearance.OffersWall.Labels(
                scanLabel = offersWall.labels.scanLabel,
                scanExtendedLabel = offersWall.labels.scanExtendedLabel,
            ),
        ),
        loading = ActivationAppearance.Loading(colors = loading.colors.toLocal()),
        errorModal = ActivationAppearance.ErrorModal(colors = errorModal.colors.toLocal()),
        receiptSummary = ActivationAppearance.ReceiptSummary(colors = receiptSummary.colors.toLocal()),
        missedEarnings = ActivationAppearance.MissedEarnings(colors = missedEarnings.colors.toLocal()),
    )

internal fun ActivationAppearance.toModel(): ActivationAppearanceModel =
    ActivationAppearanceModel(
        offersWall = ActivationAppearanceModel.OffersWall(
            colors = offersWall.colors.toModel(),
            labels = ActivationAppearanceModel.OffersWall.Labels(
                scanLabel = offersWall.labels.scanLabel,
                scanExtendedLabel = offersWall.labels.scanExtendedLabel,
            ),
        ),
        loading = ActivationAppearanceModel.Loading(colors = loading.colors.toModel()),
        errorModal = ActivationAppearanceModel.ErrorModal(colors = errorModal.colors.toModel()),
        receiptSummary = ActivationAppearanceModel.ReceiptSummary(colors = receiptSummary.colors.toModel()),
        missedEarnings = ActivationAppearanceModel.MissedEarnings(colors = missedEarnings.colors.toModel()),
    )
