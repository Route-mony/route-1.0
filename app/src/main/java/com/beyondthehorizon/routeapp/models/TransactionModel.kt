package com.beyondthehorizon.routeapp.models

data class TransactionModel(
        var created_at: String,
        var details: String,
        var withdrawn: String,
        var paymentType: String,
        var balance: String,
        var wallet_account: String,
        var reference: String,
        var description: String
)