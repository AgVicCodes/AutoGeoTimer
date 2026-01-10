package com.example.autogeotimer.ui


import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.autogeotimer.AppDestinations

@Preview
@Composable
fun MyAppNavigation() { // Renamed for clarity, could be in your main screen Composable
    val navController = rememberNavController()
    // val sharedViewModel: SharedViewModel = viewModel() // Or however you get your ViewModel

//    // Use the NavHost from androidx.navigation.compose.NavHost
//    NavHost(navController = navController, startDestination = AppDestinations.INPUT_ADDRESS_SCREEN) {
//        // 'this' inside this lambda is a NavGraphBuilder
//
//        // Pass the navController directly as InputAddress expects
//        composable(AppDestinations.INPUT_ADDRESS_SCREEN) { backStackEntry ->
//            InputAddressPage(navController = navController)
//        }
//
    // Use the NavHost from androidx.navigation.compose.NavHost
    NavHost(navController = navController, startDestination = AppDestinations.HOME_SCREEN) {
        // 'this' inside this lambda is a NavGraphBuilder

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
            val receivedPostcode = backStackEntry.arguments?.getString("postcodeState") ?: "LU4 0JB"
            // val sharedViewModel = // ... get sharedViewModel, possibly scoped
            // Ensure postcode is not null, though navArgument nullable = false helps
            ShowCoordinatesScreen(
                postcode = receivedPostcode,
                onNavigateBack = { navController.popBackStack() }
                // sharedViewModel = sharedViewModel
            )
        }

        // Add other composable destinations here
        composable(AppDestinations.USE_LOCATION_SCREEN) { backStackEntry ->
            TestScreen(onBackNavigation = { navController.popBackStack() })
        }
    }
}