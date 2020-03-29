package com.beyondthehorizon.routeapp.models

data class Card(
        val card_number: Long,
        val expiry_date: String,
        val cvv: Int,
        val country: String
)