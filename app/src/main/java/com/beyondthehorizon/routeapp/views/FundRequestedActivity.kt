package com.beyondthehorizon.routeapp.views

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.substring
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityConfirmFundRequestBinding
import com.beyondthehorizon.routeapp.databinding.ActivityFundRequestedBinding
import com.beyondthehorizon.routeapp.utils.Constants
import java.lang.Exception


class FundRequestedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFundRequestedBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var prefsEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fund_requested)
        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        prefsEditor = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0).edit()

        try {
            binding.txtRequestInform.text = intent.getStringExtra("Message")
            prefsEditor.putString("Amount", "")
            prefsEditor.putString("Id", "")
            prefsEditor.putString("Phone", "")
            prefsEditor.putString("Username", "")
            prefsEditor.putString("accountNumber", "")
            prefsEditor.putString("walletAccountNumber", "")
            prefsEditor.apply()

            binding.btnDone.setOnClickListener {
                val intent = Intent(Intent(this, MainActivity::class.java))
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                this@FundRequestedActivity.finish()
            }

            binding.btnNewRequest.setOnClickListener {
                val intent = Intent(Intent(this, RequestFundsActivity::class.java))
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                this@FundRequestedActivity.finish()
            }

            binding.arrowBack.setOnClickListener {
                onBackPressed()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }
}
