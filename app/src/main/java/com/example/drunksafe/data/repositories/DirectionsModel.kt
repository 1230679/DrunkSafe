package com.example.drunksafe.data.repositories

import com.google.gson.annotations.SerializedName

/**
 * Models for the Google Directions API response.
 */

data class DirectionsResponse(
    @SerializedName("routes")
    val routes: List<Route>,

    @SerializedName("status")
    val status: String, // ex: "OK", "NOT_FOUND", "REQUEST_DENIED"

    @SerializedName("error_message")
    val errorMessage: String? = null
)

data class Route(
    @SerializedName("overview_polyline")
    val overviewPolyline: OverviewPolyline,

    @SerializedName("legs")
    val legs: List<Leg>
)

data class OverviewPolyline(
    @SerializedName("points")
    val points: String
)

data class Leg(
    @SerializedName("distance")
    val distance: Distance,

    @SerializedName("duration")
    val duration: Duration,

    @SerializedName("start_address")
    val startAddress: String?,

    @SerializedName("end_address")
    val endAddress: String?
)

data class Distance(
    @SerializedName("text")
    val text: String,

    @SerializedName("value")
    val value: Int
)

data class Duration(
    @SerializedName("text")
    val text: String,

    @SerializedName("value")
    val value: Int
)