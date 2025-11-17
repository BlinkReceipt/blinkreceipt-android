package com.blinkreceipt.ocr.ui.feature.results

import com.blinkreceipt.ocr.models.ProductItem

data class ResultsUiState(
    val productItems: List<ProductItem> = emptyList(),
)
