package com.beyondthehorizon.route.models.profile

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("data") val data: ProfileData? = null,
    @SerializedName("status") val status: String? = null,
) {
    fun getUserProfile(): ProfileData? {
        return data
    }

    fun getProfilePic(): String? {
        return data!!.image
    }

    fun getUsername(): String? {
        return data!!.username
    }

    fun getUserEmail(): String? {
        return data!!.email
    }

    fun getFullName(): String? {
        if (data!!.firstName != null && data.lastName != null)
            return String.format("%s %s", data.firstName, data.lastName)
        return data.username
    }

    fun getUserId(): String? {
        return data!!.id
    }

    fun getUserIdNumber(): Int? {
        return data!!.idNumber
    }

    fun getPhone(): String? {
        return data!!.phoneNumber
    }

    fun getPin(): String? {
        return data!!.pin
    }

    fun getUserWallet(): WalletData? {
        return data!!.walletData
    }

    fun getBalance(): String? {
        return data!!.walletData!!.availableBalance
    }

    fun getFormattedBalance(): String? {
        return String.format("KES %s", data!!.walletData!!.availableBalance)
    }

    fun isEmailVerified(): Boolean {
        return data!!.isEmailActive.equals("true", ignoreCase = true)
    }

    fun isPinSet(): Boolean {
        return data!!.isPinSet.equals("true", ignoreCase = true)
    }

    fun isCreditCardSet(): Boolean {
        return data!!.isCreditCardSet.equals("true", ignoreCase = true)
    }
}
