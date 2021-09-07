package com.beyondthehorizon.route.models.notification

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("paginationMeta") val paginationMeta: PaginationMeta? = null,
    @SerializedName("rows") val selectedNotifications: List<SelectedNotification>? = null,
    @SerializedName("message") val message: String? = null
)
