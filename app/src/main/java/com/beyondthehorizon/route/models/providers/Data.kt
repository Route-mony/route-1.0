package com.beyondthehorizon.route.models.providers

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("mobile") val mobile: List<Mobile>? = null,
    @SerializedName("bank") val bank: List<Bank>? = null,
    @SerializedName("wallet") val wallet: List<Wallet>? = null
)