package com.beyondthehorizon.route.models

import com.beyondthehorizon.route.models.contacts.MultiContactModel
import com.google.gson.annotations.SerializedName

data class BillResponse(
    @SerializedName("bills") var bills: List<Bill>? = null,
    @SerializedName("requester") var requester: MultiContactModel? = null,
    @SerializedName("reason") var reason: String? = "",
    @SerializedName("created_at") var createdAt: String? = "",
    @SerializedName("updated_at") var updatedAt: String? = "",
    @SerializedName("general_status") var generalStatus: String? = "",
    @SerializedName("total_requested") var totalRequested: Int? = 0,
    @SerializedName("split_bill_id") var splitBillId: String? = "",
    @SerializedName("cancellation_reason") var cancellationReason: String? = ""
)