package com.example.autogeotimer.ui


import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.navigation.NavController

@Composable
fun InputChoice(
    modifier: Modifier = Modifier,
    navController: NavController
) {


    Column (
        modifier
            .fillMaxSize()
            .fillMaxWidth()
            .background(color = Color(0xFF0f0f0f)),
//            .background(color = Color(0xFF171123)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Select location method",
            fontSize = (24.sp),
            color = Color(0xFFFFFFFF)
        )
        Spacer( modifier.height(16.dp) )
        Button(
            onClick = { navController.navigate(AppDestinations.INPUT_ADDRESS_SCREEN) }) {
            Text("Postcode")
        }
        Spacer( modifier.height(8.dp) )
        Button(
            onClick = { navController.navigate(AppDestinations.USE_LOCATION_SCREEN) }) {
            Text("Use Current Location")
        }
        Button(
            onClick = { navController.navigate(AppDestinations.HISTORY_SCREEN) }) {
            Text("See History")
        }
    }
}