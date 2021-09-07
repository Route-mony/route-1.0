package com.beyondthehorizon.route.models.notification

import com.google.gson.annotations.SerializedName

data class Recipient(
    @SerializedName("id") val id: String? = "",
    @SerializedName("first_name") val firstName: String? = "",
    @SerializedName("last_name") val lastName: String? = "",
    @SerializedName("surname") val surname: String? = "",
    @SerializedName("email") val email: String? = "",
    @SerializedName("username") val username: String? = "",
    @SerializedName("phone_number") val phoneNumber: String? = "",
    @SerializedName("image") val image: String? = "",
)
