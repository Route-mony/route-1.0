package com.beyondthehorizon.routeapp.models

import org.json.JSONArray

data class BillResponse(
        val id: String,
        val group: String,
        val amount: String,
        val status: String,
        val recipient: JSONArray,
        val date: String
)