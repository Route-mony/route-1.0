package com.beyondthehorizon.routeapp.views.split.bill

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.views.FundAmountActivity
import kotlinx.android.synthetic.main.activity_split_bill.*

class SplitBillActivity : AppCompatActivity() {
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_bill)
        editor = getSharedPreferences(REG_APP_PREFERENCES, 0).edit()

        rlNewSplitBill.setOnClickListener{
            var intent: Intent = Intent(this, FundAmountActivity::class.java)
            editor.putString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SPLIT_BILL)
            editor.putString(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_ROUTE)
            editor.apply()
            startActivity(intent)
        }
    }
}
