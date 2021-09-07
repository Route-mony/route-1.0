package com.beyondthehorizon.route.models

import java.io.Serializable

data class Contact (
        var id: String,
        var name: String = "",
        var contact: String = "",
        var avatar: String = "",
        var accountNumber: String = "",
        var username: String = ""
): Serializable