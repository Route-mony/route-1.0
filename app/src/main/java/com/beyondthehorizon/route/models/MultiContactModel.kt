package com.beyondthehorizon.route.models

import java.io.Serializable

data class MultiContactModel(
        var id: String?=null,
        var username: String,
        var phone_number: String,
        var image: String,
        var amount: String,
        var is_route: Boolean,
        var is_selected: Boolean,
        var route_username: String = "") : Serializable
