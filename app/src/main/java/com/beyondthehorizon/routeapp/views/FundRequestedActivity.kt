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
import java.text.DecimalFormat

class FundRequestedActivity : AppCompatActivity() {
    private lateinit var format:DecimalFormat
    private lateinit var binding: ActivityFundRequestedBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var amount: String
    private lateinit var username: String
    private lateinit var contact: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fund_requested)
        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        format = DecimalFormat("#,###")
        intent = Intent(this, RequestFundsActivity::class.java)

        try {
            amount = format.format(prefs.getString("Amount", "0").toString().toInt())
            username = prefs.getString("Username", "").toString()
            contact = prefs.getString("Phone", "").toString()

            binding.txtRequestConfirm.text = "You requested KES ${amount} from ${username}."
            binding.txtRequestInform.text = "We'll let ${username.substring(0, username.indexOf(' '))} know right away that you requested a payment. You can see the details i your activity in case you need them later"

            binding.btnDone.setOnClickListener{
                startActivity(intent)
            }

            binding.btnNewRequest.setOnClickListener{
                startActivity(intent)
            }

            binding.arrowBack.setOnClickListener{
                onBackPressed()
            }
        }
        catch (e:Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }
}
