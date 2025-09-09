package com.example.autogeotimer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.autogeotimer.AppDestinations

@Preview
@Composable
fun MyAppNavigation() { // Renamed for clarity, could be in your main screen Composable
    val navController = rememberNavController()
    // val sharedViewModel: SharedViewModel = viewModel() // Or however you get your ViewModel

    // Use the NavHost from androidx.navigation.compose.NavHost
    NavHost(navController = navController, startDestination = AppDestinations.INPUT_ADDRESS_SCREEN) {
        // 'this' inside this lambda is a NavGraphBuilder

        // Pass the navController directly as InputAddress expects
        composable(AppDestinations.INPUT_ADDRESS_SCREEN) { backStackEntry ->
            InputAddressPage(navController = navController)
        }

        composable(
            route = "${AppDestinations.SHOW_COORDINATES_SCREEN}/{postcodeState}", // Define argument
            arguments = listOf(
                navArgument("postcodeState") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val recievedPostcode = backStackEntry.arguments?.getString("postcodeState") ?: "LU4 0JB"
            // val sharedViewModel = // ... get sharedViewModel, possibly scoped
            // Ensure postcode is not null, though navArgument nullable = false helps
            ShowCoordinatesScreen(
                postcode = recievedPostcode,
                onNavigateBack = { navController.popBackStack() }
                // sharedViewModel = sharedViewModel
            )
        }
        // Add other composable destinations here
    }
}