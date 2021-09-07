package com.beyondthehorizon.route.models.providers

import com.google.gson.annotations.SerializedName

data class ProviderResponse(
    @SerializedName("data") val data: Data? = null,
    @SerializedName("status") val status: String? = null
) {
    fun getProviderResponse(): Data? {
        return data
    }

    fun getMobileProviders(): List<Mobile>? {
        return data!!.mobile!!
    }

    fun getBankProviders(): List<Bank>? {
        return data!!.bank!!
    }

    fun getWalletProviders(): List<Wallet>? {
        return data!!.wallet!!
    }
}