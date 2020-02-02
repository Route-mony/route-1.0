package com.beyondthehorizon.routeapp.models

import com.beyondthehorizon.routeapp.R

data class Contact (
    var id: Int,
    var name: String = "",
    var contact: String = "",
    var avatar: Int = R.drawable.default_avatar
)