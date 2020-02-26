package com.beyondthehorizon.routeapp.models

data class BankModel(
        var transactionType:String,
        var providerName:String,
        var category:String,
        var providerID:String
)