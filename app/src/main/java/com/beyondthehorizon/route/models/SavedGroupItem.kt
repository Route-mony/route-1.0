package com.beyondthehorizon.route.models

data class SavedGroupItem(
        var recipients: String,
        var status: String,
        var group_name: String,
        var total_amount: String,
        var view_type: Int
)