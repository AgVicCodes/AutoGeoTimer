package com.example.autogeotimer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.autogeotimer.AppDestinations


@Composable
fun InputAddressPage(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    var postcodeState by remember { mutableStateOf("") }

    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 56.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Enter your Postcode")
        TextField(
            value = postcodeState,
            onValueChange = { postcodeState = it },
            label = { Text("Postcode") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 16.dp),
            singleLine = true
        )
        Button(
            onClick = {
                navController.navigate("${AppDestinations.SHOW_COORDINATES_SCREEN}/$postcodeState")
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}