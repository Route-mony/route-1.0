package com.beyondthehorizon.route.models.providers

import com.google.gson.annotations.SerializedName

data class Wallet(
    @SerializedName("transactionType") val transactionType: Int? = null,
    @SerializedName("providerName") val providerName: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("providerID") val providerID: Int? = null
)