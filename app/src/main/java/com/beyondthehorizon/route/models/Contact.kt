package com.beyondthehorizon.route.models

import com.beyondthehorizon.route.R

data class Contact (
    var id: String,
    var name: String = "",
    var contact: String = "",
    var avatar: Int = R.drawable.default_avatar,
    var accountNumber: String = ""
)