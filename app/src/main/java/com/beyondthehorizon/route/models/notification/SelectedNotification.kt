package com.beyondthehorizon.route.models.notification

import com.beyondthehorizon.route.R
import com.google.gson.annotations.SerializedName

data class SelectedNotification(
    @SerializedName("id") val id: String? = null,
    @SerializedName("created_at") val createdAt: String? = "",
    @SerializedName("updated_at") val updatedAt: String? = "",
    @SerializedName("recipient") val recipient: Recipient? = Recipient(),
    @SerializedName("requester") val requester: Recipient? = Recipient(),
    @SerializedName("amount") val amount: Int? = 0,
    @SerializedName("reason") val reason: String? = "",
    @SerializedName("status") var status: String? = "",
    @SerializedName("cancellation_reason") val cancellationReason: String? = "",
    var statusIcon: Int? = R.drawable.round_button_pending,
    var sentNotification: Boolean? = true,
    var message: String? = ""
)
