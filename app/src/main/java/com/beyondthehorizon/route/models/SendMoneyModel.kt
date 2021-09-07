package com.beyondthehorizon.route.models

data class SendMoneyModel(
    val isRoute: Boolean? = false,
    val isMobile: Boolean? = false,
    var isBank: Boolean? = false,
    val mobile: String? = "",
    var bankName: String? = "",
    var bankAccountNo: String? = "",
    val amount: Int? = 0
)