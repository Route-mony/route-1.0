package com.beyondthehorizon.route.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.databinding.ActivityFundRequestedBinding
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.Constants.*
import com.beyondthehorizon.route.views.auth.LoginActivity
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.requestfunds.RequestFundActivity
import com.beyondthehorizon.route.views.settingsactivities.PasswordAndPinActivity
import com.beyondthehorizon.route.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.nav_bar_layout.*


class FundRequestedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFundRequestedBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var prefsEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fund_requested)
        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        prefsEditor = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0).edit()

        btn_home.setOnClickListener {
            val intent = Intent(this@FundRequestedActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@FundRequestedActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@FundRequestedActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this@FundRequestedActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }

        try {
            val activity = intent.getStringExtra(ACTIVITY_TYPE)
            binding.txtRequestInform.text = intent.getStringExtra("Message")
            prefsEditor.putString("Amount", "")
            prefsEditor.putString("Id", "")
            prefsEditor.putString("Phone", "")
            prefsEditor.putString("Username", "")
            prefsEditor.putString("accountNumber", "")
            prefsEditor.putString("walletAccountNumber", "")
            prefsEditor.apply()

            if (activity != null && activity.compareTo(ADD_MONEY_ACTIVITY) == 0) {
                binding.btnDone.visibility = View.GONE
                binding.btnNewRequest.visibility = View.GONE
                binding.arrowBack.setOnClickListener {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            } else if (activity != null && activity.compareTo(RESET_PASSWORD_ACTIVITY) == 0) {
                binding.btnDone.visibility = View.GONE
                binding.btnNewRequest.visibility = View.GONE
                binding.arrowBack.setOnClickListener {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    this@FundRequestedActivity.finish()
                }
            } else if (activity != null && activity.compareTo(RESET_PIN_ACTIVITY) == 0) {
                binding.btnDone.visibility = View.GONE
                binding.btnNewRequest.visibility = View.GONE
                binding.arrowBack.setOnClickListener {
                    val intent = Intent(this, PasswordAndPinActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    this@FundRequestedActivity.finish()
                }
            } else {
                binding.btnDone.setOnClickListener {
                    val intent = Intent(Intent(this, MainActivity::class.java))
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    this@FundRequestedActivity.finish()
                }

                binding.btnNewRequest.setOnClickListener {
                    val intent = Intent(Intent(this, RequestFundActivity::class.java))
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    this@FundRequestedActivity.finish()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(Intent(this, MainActivity::class.java))
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
