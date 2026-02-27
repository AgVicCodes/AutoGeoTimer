package com.example.autogeotimer.ui


import androidx.compose.ui.Modifier

import androidx.compose.runtime.Composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import kotlinx.coroutines.delay
import kotlin.coroutines.coroutineContext


@Preview
@Composable
fun HomeScreenPreview() {

    var navController = rememberNavController()

    HomeScreen(navController = navController)
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {

    LaunchedEffect(Unit) {
        delay(5_000)
        navController.navigate(AppDestinations.INPUT_CHOICE_SCREEN) {
            popUpTo(AppDestinations.HOME_SCREEN) { inclusive = true }
            launchSingleTop = true
        }
    }


    Column(
        modifier.fillMaxSize().background(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0f0f0f), Color(0xFF3d3d3d))
//                colors = listOf(Color(0xFF171123), Color(0xFF33305E))
            )
        ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(
            text = "Welcome to My \nGeocoding App✨",
            fontSize = 32.sp,
            lineHeight = 1.5.em,
            color = Color(0xFFF7F7F7),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Cursive
        )
    }
}