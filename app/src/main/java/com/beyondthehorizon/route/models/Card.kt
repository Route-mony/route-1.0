package com.beyondthehorizon.route.models

data class Card(
        val card_number: String,
        val expiry_date: String,
        val cvv: String,
        val country: String
)