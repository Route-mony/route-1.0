package com.beyondthehorizon.route.models

import com.beyondthehorizon.route.R
import retrofit2.http.Url

data class Contact (
        var id: String,
        var name: String = "",
        var contact: String = "",
        var avatar: String = "",
        var accountNumber: String = ""
)