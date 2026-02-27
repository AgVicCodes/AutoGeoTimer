package com.example.autogeotimer.data.repo

import com.example.autogeotimer.domain.Coordinates

class GeocodingRepo (private val api: String /* Actual Type: GeocodingApi */) {

// //    What does suspend do again?
//    suspend fun getCoordinates(address: String): Coordinates {
//        val response = api.geocode(address, API_KEY)
//        val result = response.results.first()
//        return Coordinates(
//            result.geometry.location.lat,
//            result.geometry.location,lng
//        )
//    }
//
//    suspend fun getAddress(lat: Double, lng: Double): String {
//        val response = api.reverseGeocode("$lat,$lng", API_KEY)
//        return response.results.first().formattedAddress
//    }

}

//  Geocoding
//  BAC8-4E68-E261	10,000	$5.00	$4.00	$3.00	$1.50	$0.38
