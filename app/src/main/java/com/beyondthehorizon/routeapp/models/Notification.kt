package com.beyondthehorizon.routeapp.models

import com.beyondthehorizon.routeapp.R

data class Notification (
    var id: String,
    var firstName: String = "",
    var lastName: String = "",
    var username: String = "",
    var phone: String = "",
    var avatar: String = "",
    var reason: String = "",
    var amount: String = "",
    var status: String = "",
    var statusIcon: Int = R.drawable.ic_pending,
    var type: String = "",
    var date: String = "",
    var cancellation_reason:String = ""
)