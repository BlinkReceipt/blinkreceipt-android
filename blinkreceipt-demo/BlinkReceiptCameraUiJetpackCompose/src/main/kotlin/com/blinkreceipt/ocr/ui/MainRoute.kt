package com.blinkreceipt.ocr.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blinkreceipt.ocr.ui.feature.customscan.CustomScanRoute
import com.blinkreceipt.ocr.ui.feature.home.HomeRoute
import com.blinkreceipt.ocr.ui.feature.oobcamera.OobCameraRoute
import com.blinkreceipt.ocr.ui.feature.results.ResultsRoute

@Composable
fun MainRoute(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainDestinations.Home,
    ) {

        composable<MainDestinations.Home> {
            HomeRoute(
                modifier = modifier.fillMaxSize(),
                onNavigateToCustomCameraScan = {
                    navController.navigate(MainDestinations.CustomScan)
                },
                onNavigateToOobCameraScan = {
                    navController.navigate(MainDestinations.OobCameraScan)
                },
            )
        }

        composable<MainDestinations.CustomScan> {
            CustomScanRoute(
                modifier = modifier.fillMaxSize(),
                onScanResults = { blinkreceiptId ->
                    navController.navigate(MainDestinations.Results(blinkreceiptId)) {
                        launchSingleTop = true
                        popUpTo(MainDestinations.Home)
                    }
                }
            )
        }

        composable<MainDestinations.OobCameraScan> {
            OobCameraRoute(
                modifier = modifier.fillMaxSize(),
                onScanResults = { blinkreceiptId ->
                    navController.navigate(MainDestinations.Results(blinkreceiptId)) {
                        launchSingleTop = true
                        popUpTo(MainDestinations.Home)
                    }
                },
                onCancelled = {
                    navController.popBackStack()
                }
            )
        }

        composable<MainDestinations.Results> {
            ResultsRoute(
                modifier = Modifier.fillMaxSize(),
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

    }

}
