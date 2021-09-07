package com.beyondthehorizon.route.models

import com.beyondthehorizon.route.models.contacts.MultiContactModels

data class SavedGroupItem(
    var recipients: MultiContactModels,
    var status: String = "",
    var group_name: String = "",
    var total_amount: String = "",
    var view_type: Int,
)