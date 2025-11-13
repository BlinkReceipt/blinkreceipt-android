@file:OptIn(ExperimentalMaterial3Api::class)

package com.blinkreceipt.ocr.ui.feature.results

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blinkreceipt.ocr.R
import com.blinkreceipt.ocr.ui.feature.results.components.ProductItemView
import com.blinkreceipt.ocr.ui.feature.results.models.ProductItem
import com.blinkreceipt.ocr.ui.theme.BlinkReceiptDemoTheme

private val LocalResultsAction = staticCompositionLocalOf<(ResultsAction) -> Unit> { {} }

@Composable
fun ResultsRoute(
    modifier: Modifier,
    viewModel: ResultsViewModel = hiltViewModel(),
    onNavigateBack: (() -> Unit) = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(lifecycleOwner, viewModel) {
        viewModel.events.collect { event ->
            when(event) {
                is ResultsEvent.OnDismiss -> {
                    onNavigateBack()
                }
            }
        }
    }

    CompositionLocalProvider(
        LocalResultsAction provides { viewModel.handleAction(it) }
    ) {
        ResultsContent(
            modifier = modifier,
            uiState = uiState,
        )
    }
}

@Composable
internal fun ResultsContent(
    modifier: Modifier,
    uiState: ResultsUiState,
) {
    val action = LocalResultsAction.current

    // Intercept System Back Button
    BackHandler { action(ResultsAction.Dismiss) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.results_topappbar_title))
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .clickable(
                                onClick = {
                                    action(ResultsAction.Dismiss)
                                },
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(),
                            )
                            .clipToBounds(),
                        painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                        contentDescription = null,
                    )
                }
            )
        }
    ) { innerPadding ->
        ProductsListView(
            modifier = Modifier.padding(innerPadding),
            items = uiState.productItems,
        )
    }


}

@Composable
fun ProductsListView(
    modifier: Modifier = Modifier,
    items: List<ProductItem>,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        items(
            items = items,
            key = { it.hashCode() },
        ) { item ->
            ProductItemView(
                modifier = Modifier,
                item = item,
            )
        }
    }
}

@PreviewScreenSizes
@Composable
fun ResultsContent_Preview() {
    BlinkReceiptDemoTheme {
        Surface {
            ResultsContent(
                modifier = Modifier,
                uiState = ResultsUiState(
                    productItems = listOf(
                        ProductItem(
                            upc = "123456789012",
                            name = "Product Name(1)",
                            productNumber = "123456789012",
                            brand = "Nestle",
                            category = "Milk",
                            price = 1_000.00f,
                        ),
                        ProductItem(
                            upc = "234567890121",
                            name = "Product Name(2)",
                            productNumber = "234567890121",
                            brand = "Nokia",
                            category = "Gadget",
                            price = 2_000.00f,
                        ),
                    )
                )
            )
        }
    }
}
