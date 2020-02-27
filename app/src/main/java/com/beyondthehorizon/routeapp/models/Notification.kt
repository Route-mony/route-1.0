package com.beyondthehorizon.routeapp.models

import com.beyondthehorizon.routeapp.R

data class Notification (
    var id: String,
    var username: String,
    var phone: String,
    var avatar: Int,
    var reason: String,
    var amount: String,
    var status: String,
    var statusIcon: Int = R.drawable.ic_pending,
    var message: String = username.split(" ")[0] + " has requested you Ksh. ${amount} for ${reason}"
)