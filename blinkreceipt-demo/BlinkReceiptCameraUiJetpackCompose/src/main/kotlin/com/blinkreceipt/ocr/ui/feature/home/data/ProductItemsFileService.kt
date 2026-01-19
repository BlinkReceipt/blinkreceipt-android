package com.blinkreceipt.ocr.ui.feature.home.data

import com.blinkreceipt.ocr.models.ProductItem

interface ProductItemsFileService {

    suspend fun store(
        blinkReceiptId: String,
        productItems: List<ProductItem>,
    )

    suspend fun get(
        blinkReceiptId: String,
    ): List<ProductItem>

    suspend fun delete(
        blinkReceiptId: String,
    )

}