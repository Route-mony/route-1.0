package com.beyondthehorizon.route.models.contacts

import com.google.gson.annotations.SerializedName

data class ContactsResponse(
    @SerializedName("data") val data: Data? = null,
    @SerializedName("status") val status: String? = null
)
