package com.beyondthehorizon.route.models.contacts

import com.google.gson.annotations.SerializedName

data class MultiContactModels(
    @SerializedName("recipients")
    var contacts: MutableList<MultiContactModel>
)
