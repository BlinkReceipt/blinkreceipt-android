package com.blinkreceipt.ocr.ui.feature.results.models

import com.microblink.core.Product
import com.microblink.core.ScanResults
import kotlinx.serialization.Serializable

@Serializable
data class ProductItem(
    val upc: String,
    val name: String,
    val productNumber: String,
    val brand: String,
    val category: String,
    val price: Float,
)

fun ScanResults.toProductItems(): List<ProductItem> =
    this.products()?.map {  product ->
        product.toModel()
    } ?: emptyList()

fun Product.toModel(): ProductItem =
    ProductItem(
        upc = this.upc() ?: this.originalUpc() ?: "",
        name = this.productName() ?: this.originalProductName() ?: "",
        productNumber = this.productNumber()?.value() ?: "",
        brand = this.brand() ?: "",
        category = this.category() ?: "",
        price = this.totalPrice()?.value() ?: 0f,
    )
