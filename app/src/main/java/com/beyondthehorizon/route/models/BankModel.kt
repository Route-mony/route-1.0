package com.beyondthehorizon.route.models

data class BankModel(
        var transactionType:String,
        var providerName:String,
        var category:String,
        var providerID:String
)