package com.beyondthehorizon.routeapp.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityMobileBinding
import com.beyondthehorizon.routeapp.utils.Constants.*

class MobileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMobileBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mobile)
        binding.phoneNumberInput.setFormat("---- ------")
        val phone = intent.getStringExtra(PHONE_NUMBER)
        editor = getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
        prefs = getSharedPreferences(REG_APP_PREFERENCES, 0)
        binding.btnSavePhone.setOnClickListener {
            var phoneNumber = binding.phoneNumberInput.text.toString().replace(" ", "").trim()
            if (phoneNumber.length < 10) {
                binding.phoneNumberInput.error = "Enter valid phone number"
            } else {
                var intent = Intent(this, FundAmountActivity::class.java)
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, LOAD_WALLET_FROM_MPESA)
                editor.apply()
                intent.putExtra(PHONE_NUMBER, phoneNumber)
                startActivity(intent)
            }
        }

        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }
    }
}
