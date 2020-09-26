package com.beyondthehorizon.route.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.databinding.ActivityConfirmFundRequestBinding
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.Constants.PHONE_NUMBER
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.nav_bar_layout.*
import java.text.DecimalFormat

class ConfirmFundRequestActivity : AppCompatActivity() {
    private lateinit var format: DecimalFormat
    private lateinit var binding: ActivityConfirmFundRequestBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var amount: String
    private lateinit var username: String
    private lateinit var userId: String
    private lateinit var contact: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_fund_request)

        btn_home.setOnClickListener {
            val intent = Intent(this@ConfirmFundRequestActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@ConfirmFundRequestActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@ConfirmFundRequestActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this@ConfirmFundRequestActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        amount = prefs.getString("Amount", "0").toString()
        username = prefs.getString("Username", "").toString()
        userId = prefs.getString("Id", "").toString()
        contact = prefs.getString(PHONE_NUMBER, "").toString()
        format = DecimalFormat("#,###")

        binding.txtAmount.text = format.format(amount.toInt())
        binding.txtUserContact.text = contact
        binding.txtUsername.text = username

        binding.btnRequest.setOnClickListener {
            var reason = binding.txtReason.text.toString()
            if (isEmpty(reason)) {
                binding.txtReason.error = "Please enter your reason"
            } else {
                val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
                var intent = Intent(this, FundRequestedActivity::class.java)
                Constants.requestFund(this, userId, amount, reason, token).setCallback { e, result ->
                    if (result.get("data") != null) {
                        var message = result.get("data").asJsonObject.get("message").asString
                        intent.putExtra("Message", message)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "User not registered or haven't confirmed their email", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }
    }
}
