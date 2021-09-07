package com.beyondthehorizon.route.models.contacts

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("message") val message: String? = null,
    @SerializedName("contacts") val multiContactModels: List<MultiContactModel>? = null
)
