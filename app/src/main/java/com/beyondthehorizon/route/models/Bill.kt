package com.beyondthehorizon.route.models

import com.beyondthehorizon.route.models.contacts.MultiContactModel
import com.google.gson.annotations.SerializedName

data class Bill(
    @SerializedName("id") var id: String? = "",
    @SerializedName("recipient") var recipient: MultiContactModel,
    @SerializedName("amount") var amount: Int? = 0,
    @SerializedName("created_at") var createdAt: String? = ""
)