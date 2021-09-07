package com.beyondthehorizon.route.models.contacts

import com.google.gson.annotations.SerializedName

data class ContactsResponse(
    @SerializedName("data") val data: Data? = null,
    @SerializedName("status") val status: String? = null
) {
    fun getContactModels(): List<MultiContactModel>? {
        if (data == null)
            return data
        return data.multiContactModels
    }
}
