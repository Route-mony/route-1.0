package com.beyondthehorizon.routeapp.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityAddMoneyBinding
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.*

class AddMoneyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddMoneyBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_money)
        prefs = applicationContext.getSharedPreferences(REG_APP_PREFERENCES, 0)
        editor = applicationContext.getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
        binding.numberOne.text = prefs.getString(MyPhoneNumber, "")
        binding.mobileNumberTwo.text = prefs.getString(MyPhoneNumber, "")
        binding.cardOne.setOnClickListener{
            var intent = Intent(this, FundAmountActivity::class.java)
            intent.putExtra(CARD_NUMBER, "")
            intent.putExtra(EXPIRY_DATE, "")
            intent.putExtra(CVV_NUMBER, "")
            intent.putExtra(COUNTRY, "")
            intent.putExtra(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, LOAD_WALLET_FROM_CARD)
            startActivity(intent)
        }

        binding.cardTwo.setOnClickListener{
            var intent = Intent(this, FundAmountActivity::class.java)
            intent.putExtra(CARD_NUMBER, "")
            intent.putExtra(EXPIRY_DATE, "")
            intent.putExtra(CVV_NUMBER, "")
            intent.putExtra(COUNTRY, "")
            intent.putExtra(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, LOAD_WALLET_FROM_CARD)
            startActivity(intent)
        }

        binding.mobileOne.setOnClickListener{
            var intent = Intent(this, FundAmountActivity::class.java)
            intent.putExtra(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, Constants.LOAD_WALLET_FROM_MPESA)
            intent.putExtra(PHONE_NUMBER, MyPhoneNumber)
            startActivity(intent)
        }

        binding.mobileTwo.setOnClickListener{
            var intent = Intent(this, FundAmountActivity::class.java)
            intent.putExtra(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, Constants.LOAD_WALLET_FROM_MPESA)
            intent.putExtra(PHONE_NUMBER, MyPhoneNumber)
            startActivity(intent)
        }

        binding.addCard.setOnClickListener{
            startActivity(Intent(this, CardActivity::class.java))
        }

        binding.addBank.setOnClickListener{
            startActivity(Intent(this, MpesaActivity::class.java))
        }

        binding.arrowBack.setOnClickListener{
            onBackPressed()
        }
    }
}
