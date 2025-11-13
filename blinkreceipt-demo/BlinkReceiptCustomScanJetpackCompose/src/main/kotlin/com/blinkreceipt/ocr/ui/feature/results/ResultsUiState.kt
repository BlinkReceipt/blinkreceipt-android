package com.blinkreceipt.ocr.ui.feature.results

import com.blinkreceipt.ocr.ui.feature.results.models.ProductItem

data class ResultsUiState(
    val productItems: List<ProductItem> = emptyList(),
)
