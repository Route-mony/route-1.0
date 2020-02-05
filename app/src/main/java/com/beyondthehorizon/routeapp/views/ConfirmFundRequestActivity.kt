package com.beyondthehorizon.routeapp.views

import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.CurrencyAmount
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityConfirmFundRequestBinding
import com.beyondthehorizon.routeapp.utils.Constants
import java.text.DecimalFormat

class ConfirmFundRequestActivity : AppCompatActivity() {
    private lateinit var format:DecimalFormat
    private lateinit var binding: ActivityConfirmFundRequestBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var amount: String
    private lateinit var username: String
    private lateinit var contact: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_fund_request)
        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        amount = prefs.getString("Amount","0").toString()
        username = prefs.getString("Username", "").toString()
        contact = prefs.getString("Phone","").toString()
        format = DecimalFormat("#,###")

        binding.txtAmount.text = format.format(amount.toInt())
        binding.txtUserContact.text = contact
        binding.txtUsername.text = username

        binding.btnRequest.setOnClickListener{
            startActivity(Intent(this, FundRequestedActivity::class.java))
        }

        binding.arrowBack.setOnClickListener{
            onBackPressed()
        }
    }
}
