package com.example.autogeotimer.data.geocoding

import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleGeocodingApi {

    @GET("maps/api/geocode/json")
    suspend fun geocode(
        @Query("address") address: String,
        @Query("key") apiKey: String,
        @Query("region") region: String? = null, // e.g. "gb" biases results to UK
        @Query("components") components: String? = null // e.g. "country:GB" restricts to UK
    ): GoogleGeocodingResponse

    @GET("maps/api/geocode/json")
    suspend fun reverseGeocode(
        @Query("latlng") latlng: String, // "lat,lng"
        @Query("key") apiKey: String
    ): GoogleGeocodingResponse

}