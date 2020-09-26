package com.beyondthehorizon.route.models

data class ReceiptModel(
        var receipt_id: String,
        var first_name: String,
        var last_name: String,
        var username: String,
        var image: String,
        var created_at: String,
        var amount_spent: String,
        var title: String,
        var description: String,
        var cancellation_reason: String,
        var transaction_date: String,
        var phone_number: String,
        var status: String,
        var email: String,
        var type: String,
        var view_type: Int
)