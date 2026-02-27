package com.example.autogeotimer.ui


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.navigation

import com.example.autogeotimer.data.db.AppDb
import com.example.autogeotimer.data.repo.SessionRepository
import java.util.Timer

@Preview
@Composable
fun MyAppNavigation() { // Renamed for clarity, could be in your main screen Composable
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppDestinations.HOME_SCREEN) {
        // 'this' in_location_ide this lambda is a NavGraphBuilder

        // Pass the navController directly as InputAddress expects
        composable(AppDestinations.HOME_SCREEN) { backStackEntry ->
            HomeScreen(navController = navController)
        }

        composable(AppDestinations.INPUT_CHOICE_SCREEN) { backStackEntry ->
            InputChoice(navController = navController)
        }

        // Pass the navController directly as InputAddress expects
        composable(AppDestinations.INPUT_ADDRESS_SCREEN) { backStackEntry ->
            InputAddressPage(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${AppDestinations.SHOW_COORDINATES_SCREEN}/{postcodeState}", // Define argument
            arguments = listOf(
                navArgument("postcodeState") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val receivedPostcode = backStackEntry.arguments?.getString(
                "postcodeState"
            ) ?: "LU4 0JB"
            // val sharedViewModel = // ... get sharedViewModel, possibly scoped
            // Ensure postcode is not null, though navArgument nullable = false helps
            ShowCoordinatesScreen(
                postcode = receivedPostcode,
//                navController = navController,
                onNavigateBack = { navController.popBackStack() }
                // sharedViewModel = sharedViewModel
            )
        }

        // Add other composable destinations here
        composable(AppDestinations.USE_LOCATION_SCREEN) { backStackEntry ->
            ConfirmLocationScreen(
                onBackNavigation = { navController.popBackStack() },
                navController = navController
            )
        }

        composable(
            // route = "timer/{locationId}",
            route = "${AppDestinations.TIMER_SCREEN}/{locationId}/{locationName}",
            arguments = listOf(
                navArgument("locationId"){ type = NavType.StringType },
                navArgument("locationName"){
                    type = NavType.StringType
                    defaultValue = "Unknown Location"
                }
            ),
            // deepLinks = listOf(navDeepLink { uriPattern = "autogeotimer://timer/{locationId}" })
        ) { backStackEntry ->

            // Getting Arguments
            val locationId: String = backStackEntry.arguments?.getString("locationId")
                ?: return@composable
            val locationName: String = backStackEntry.arguments?.getString("locationName")
                ?: "Unknown Location"

            TimerScreen(
                locationId = locationId,
                placeName = locationName,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppDestinations.HISTORY_SCREEN
        ) {
            GeofenceHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}