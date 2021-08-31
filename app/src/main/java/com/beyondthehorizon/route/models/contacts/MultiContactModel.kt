package com.beyondthehorizon.route.models.contacts

import com.google.gson.annotations.SerializedName

data class MultiContactModel(
    @SerializedName("amount") val amount: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("is_route") val isRoute: Boolean = false,
    @SerializedName("is_selected") val isSelected: Boolean = false,
    @SerializedName("phone_number") val phoneNumber: String? = null,
    @SerializedName("route_username") val routeUsername: String? = null,
    @SerializedName("username") val username: String? = null
)