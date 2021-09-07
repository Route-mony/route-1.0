package com.beyondthehorizon.route.models.notification

import com.google.gson.annotations.SerializedName

data class PaginationMeta(
    @SerializedName("currentPage") val currentPage: Int? = null,
    @SerializedName("currentPageSize") val currentPageSize: Int? = null,
    @SerializedName("totalPages") val totalPages: Int? = null,
    @SerializedName("totalRecords") val totalRecords: Int? = null
)