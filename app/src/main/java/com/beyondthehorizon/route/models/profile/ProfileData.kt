package com.beyondthehorizon.route.models.profile

import com.google.gson.annotations.SerializedName

data class ProfileData(
    @SerializedName("id") val id: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("surname") val surname: String? = null,
    @SerializedName("is_pin_set") val isPinSet: String = "False",
    @SerializedName("is_email_active") val isEmailActive: String = "False",
    @SerializedName("image") val image: String? = null,
    @SerializedName("pin") val pin: String? = null,
    @SerializedName("is_credit_card_set") val isCreditCardSet: String = "False",
    @SerializedName("phone_number") val phoneNumber: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("wallet_account") val walletData: WalletData? = null,
    @SerializedName("debit_cards") val debitCards: List<Any>? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("id_number") val idNumber: Int? = null,
    @SerializedName("registration_token") val registrationToken: String? = null
)
