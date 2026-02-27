package com.example.autogeotimer.data.geocoding

import com.example.autogeotimer.BuildConfig
import com.example.autogeotimer.domain.Coordinates
import com.example.autogeotimer.domain.GeocodingResult
import com.example.autogeotimer.domain.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

object GeocodingService {

    private val apiKey get() = BuildConfig.GOOGLE_GEOCODING_API_KEY

    /**
     * Forward geocoding: address/postcode string -> coordinates.
     * Returns a sealed GeocodingResult so callers can distinguish
     * network errors, zero-results, and rate-limit issues.
     */

    suspend fun geocode(query: String, regionBias: String = "gb"): GeocodingResult {

        if (apiKey.isBlank()) {
            return GeocodingResult.Error("Missing Google Geocoding API key")
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = NetworkModule.geocodingApi.geocode(
                    address = query,
                    apiKey = apiKey,
                    region = regionBias
                )

                when (response.status) {
                    "OK" -> {
                        val first = response.results.firstOrNull()
                        if (first != null) {
                            GeocodingResult.Success(
                                Coordinates(first.geometry.location.lat, first.geometry.location.lng)
                            )
                        } else {
                            GeocodingResult.NotFound
                        }
                    }
                    "ZERO_RESULTS" -> GeocodingResult.NotFound
                    "OVER_QUERY_LIMIT" -> GeocodingResult.Error("Rate limit exceeded, try again shortly")
                    "REQUEST_DENIED" -> GeocodingResult.Error("Request denied: ${response.errorMessage ?: "check API key/billing"}")
                    "INVALID_REQUEST" -> GeocodingResult.Error("Invalid request: missing/malformed address")
                    else -> GeocodingResult.Error("Unknown status: ${response.status}")
                }
            } catch (e: IOException) {
                GeocodingResult.Error("Network error: ${e.message}")
            } catch (e: HttpException) {
                GeocodingResult.Error("Server error: ${e.code()}")
            } catch (e: Exception) {
                GeocodingResult.Error("Unexpected error: ${e.message}")
            }
        }

    }

    /**
     * Convenience wrapper matching your old function signature —
     * returns nullable Coordinates directly for simpler call sites.
     */

    suspend fun convertAddressToCoordinates(query: String): Coordinates? {
        return when (val result = geocode(query)) {
            is GeocodingResult.Success -> result.coordinates
            else -> null
        }
    }

    /**
     * Returns the formatted address string (equivalent to your old
     * convertPostcodeToAddress), sourced from Google's formatted_address.
     */
    suspend fun convertPostcodeToAddress(query: String): Location? {
        if (apiKey.isBlank()) return null

        return withContext(Dispatchers.IO) {
            try {
                val response = NetworkModule.geocodingApi.geocode(
                    address = query,
                    apiKey = apiKey,
                    region = "gb"
                )
                response.results.firstOrNull()?.let { Location(it.formattedAddress) }
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Reverse geocoding: coordinates -> human-readable address.
     * Useful if you want to auto-name locations from "Current Location".
     */
    suspend fun reverseGeocode(lat: Double, lng: Double): Location? {
        if (apiKey.isBlank()) return null

        return withContext(Dispatchers.IO) {
            try {
                val response = NetworkModule.geocodingApi.reverseGeocode(
                    latlng = "$lat,$lng",
                    apiKey = apiKey
                )
                response.results.firstOrNull()?.let { Location(it.formattedAddress) }
            } catch (e: Exception) {
                null
            }
        }
    }

}