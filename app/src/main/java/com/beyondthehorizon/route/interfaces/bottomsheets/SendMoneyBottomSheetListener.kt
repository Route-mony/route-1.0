package com.beyondthehorizon.route.interfaces.bottomsheets

import com.beyondthehorizon.route.models.SendMoneyModel

interface SendMoneyBottomSheetListener {
    fun moneyModelListener(sendMoneyModel: SendMoneyModel)
}