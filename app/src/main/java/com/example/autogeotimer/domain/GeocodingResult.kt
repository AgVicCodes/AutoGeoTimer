package com.example.autogeotimer.domain


sealed class GeocodingResult {
    data class Success(val coordinates: Coordinates) : GeocodingResult()
    data class Error(val message: String) : GeocodingResult()
    object NotFound : GeocodingResult()
}