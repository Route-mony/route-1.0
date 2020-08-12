package com.beyondthehorizon.routeapp.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityMobileBinding
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.utils.Utils
import com.beyondthehorizon.routeapp.views.receipt.ReceiptActivity
import com.beyondthehorizon.routeapp.views.requestfunds.RequestFundActivity
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

        val phone = intent.getStringExtra(PHONE_NUMBER)
        editor = getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
        prefs = getSharedPreferences(REG_APP_PREFERENCES, 0)

        binding.myPhone.setOnClickListener {
            var phone = prefs.getString(MyPhoneNumber, "")
            var intent = Intent(this@MobileActivity, FundAmountActivity::class.java)
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, MOBILE_TRANSACTION)
            editor.apply()
            intent.putExtra(PHONE_NUMBER, phone)
            startActivity(intent)
        }

        binding.imgSearch.setOnClickListener {
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, MOBILE_TRANSACTION)
            editor.apply()
            var intent = Intent(this@MobileActivity, RequestFundActivity::class.java)
            startActivity(intent)
        }

        binding.btnSavePhone.setOnClickListener {
            val phone = binding.phoneNumberInput.text.toString().trim()
            var intent = Intent(this@MobileActivity, FundAmountActivity::class.java)
            if (!Utils.isPhoneNumberValid(phone, "KE")) {
                binding.phoneNumberInput.error = "Enter valid phone number"
            } else {
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, MOBILE_TRANSACTION)
                editor.apply()
                intent.putExtra(PHONE_NUMBER, phone)
                startActivity(intent)
            }
        }

        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }
    }
}
