package com.beyondthehorizon.routeapp.views

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.substring
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityConfirmFundRequestBinding
import com.beyondthehorizon.routeapp.databinding.ActivityFundRequestedBinding
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.ACTIVITY_TYPE
import com.beyondthehorizon.routeapp.utils.Constants.ADD_MONEY_ACTIVITY
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


        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }

        try {
            binding.txtRequestInform.text = intent.getStringExtra("Message")
            prefsEditor.putString("Amount", "")
            prefsEditor.putString("Id", "")
            prefsEditor.putString("Phone", "")
            prefsEditor.putString("Username", "")
            prefsEditor.putString("accountNumber", "")
            prefsEditor.putString("walletAccountNumber", "")
            prefsEditor.apply()

            if (intent.getStringExtra(ACTIVITY_TYPE).compareTo(ADD_MONEY_ACTIVITY) == 0) {
                binding.btnDone.visibility = View.GONE
                binding.btnNewRequest.visibility = View.GONE
                binding.arrowBack.setOnClickListener {
                    val intent = Intent(this, AddMoneyActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    this@FundRequestedActivity.finish()
                }
            }

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
