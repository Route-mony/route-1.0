package com.beyondthehorizon.route.models.notification

import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("data") val data: Data,
    @SerializedName("status") val status: String? = "pending",
)
