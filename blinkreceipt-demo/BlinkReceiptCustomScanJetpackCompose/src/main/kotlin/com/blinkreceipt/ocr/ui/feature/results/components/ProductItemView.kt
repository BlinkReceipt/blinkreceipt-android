package com.blinkreceipt.ocr.ui.feature.results.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blinkreceipt.ocr.R
import com.blinkreceipt.ocr.ui.feature.results.models.ProductItem
import com.blinkreceipt.ocr.ui.theme.BlinkReceiptDemoTheme
import java.text.NumberFormat

@Composable
fun ProductItemView(
    modifier: Modifier,
    item: ProductItem,
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .align(Alignment.Start),
            text = stringResource(R.string.results_product_item_upc, item.upc)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .align(Alignment.Start),
            text = stringResource(R.string.results_product_item_name, item.name)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .align(Alignment.Start),
            text = stringResource(R.string.results_product_item_product_number, item.productNumber)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .align(Alignment.Start),
            text = stringResource(R.string.results_product_item_brand, item.brand)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .align(Alignment.Start),
            text = stringResource(R.string.results_product_item_category, item.category)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .align(Alignment.Start),
            text = stringResource(R.string.results_product_item_price, NumberFormat.getCurrencyInstance().format(item.price))
        )
    }

}

@Preview
@Composable
internal fun ProductItemView_Preview() {
    BlinkReceiptDemoTheme {
        Surface {
            ProductItemView(
                modifier = Modifier.fillMaxWidth(),
                item = ProductItem(
                    upc = "123456789012",
                    name = "Product Name(1)",
                    productNumber = "123456789012",
                    brand = "Nestle",
                    category = "Milk",
                    price = 1_000.00f,
                )
            )
        }
    }
}
