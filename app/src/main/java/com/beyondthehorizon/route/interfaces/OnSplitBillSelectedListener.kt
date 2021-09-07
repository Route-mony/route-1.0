package com.beyondthehorizon.route.interfaces

import com.beyondthehorizon.route.models.BillResponse

interface OnSplitBillSelectedListener {
    fun selectedSplitBill(billResponse: BillResponse)
}