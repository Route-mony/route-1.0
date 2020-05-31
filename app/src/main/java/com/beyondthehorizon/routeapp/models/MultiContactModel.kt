package com.beyondthehorizon.routeapp.models

import java.io.Serializable

data class MultiContactModel(var username: String,
                             var phone_number: String,
                             var image: String,
                             var amount: String,
                             var is_route: Boolean,
                             var is_selected: Boolean) : Serializable

