package com.example.autogeotimer.data.geocoding

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleGeocodingResponse(
    val results: List<GeocodingResultItem>,
    val status: String, // "OK", "ZERO_RESULTS", "OVER_QUERY_LIMIT", "REQUEST_DENIED", "INVALID_REQUEST"
    @Json(name = "error_message") val errorMessage: String? = null
)

@JsonClass(generateAdapter = true)
data class GeocodingResultItem(
    @Json(name = "formatted_address") val formattedAddress: String,
    val geometry: Geometry,
    @Json(name = "place_id") val placeId: String,
    @Json(name = "address_components") val addressComponents: List<AddressComponent> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AddressComponent(
    @Json(name = "long_name") val longName: String,
    @Json(name = "short_name") val shortName: String,
    val types: List<String>
)

@JsonClass(generateAdapter = true)
data class LatLngDto(
    val lat: Double,
    val lng: Double
)

@JsonClass(generateAdapter = true)
data class Geometry(
//    TODO: find out wtf dto means
    val location: LatLngDto,
    @Json(name = "location_type") val locationType: String // "ROOFTOP", "APPROXIMATE", etc.
)
