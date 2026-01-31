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

import com.example.autogeotimer.AppDestinations
import com.example.autogeotimer.data.db.AppDB
import com.example.autogeotimer.data.repo.SessionRepo
import java.util.Timer

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
//    MK42 6HF
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
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
                // sharedViewModel = sharedViewModel
            )
        }

        // Add other composable destinations here
        composable(AppDestinations.USE_LOCATION_SCREEN) { backStackEntry ->
            TestScreen(onBackNavigation = { navController.popBackStack() })
        }


        composable(
            // route = "timer/{sessionId}",
            route = "${AppDestinations.TIMER_SCREEN}/{sessionId}",
            arguments = listOf(navArgument("sessionId"){ type = NavType.LongType }),
            deepLinks = listOf(navDeepLink { uriPattern = "autogeotimer://timer/{sessionId}" })
        ) {
            backStackEntry ->
            val context = LocalContext.current.applicationContext
            val repo = remember { SessionRepo(AppDB.get(context).sessionDao()) }

            val vm: TimerViewModel = viewModel (
                backStackEntry,
                factory = TimerVmFactory(repo)
            )

            val sid = backStackEntry.arguments?.getLong("sessionId") ?: return@composable
            TimerScreen(sessionId = sid, vm = vm)
        }
    }
}