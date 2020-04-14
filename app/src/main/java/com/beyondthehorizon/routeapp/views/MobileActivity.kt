package com.beyondthehorizon.routeapp.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityMobileBinding
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.views.receipt.ReceiptActivity
import com.beyondthehorizon.routeapp.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.routeapp.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.nav_bar_layout.*

class MobileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMobileBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mobile)


        btn_home.setOnClickListener {
            val intent = Intent(this@MobileActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@MobileActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@MobileActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this@MobileActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

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
