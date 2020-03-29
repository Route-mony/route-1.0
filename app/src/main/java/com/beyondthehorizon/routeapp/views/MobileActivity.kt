package com.beyondthehorizon.routeapp.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityMobileBinding
import com.beyondthehorizon.routeapp.utils.Constants.*

class MobileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMobileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mobile)
        binding.phoneNumberInput.setFormat("---- ------")
        val phone = intent.getStringExtra(PHONE_NUMBER)
        binding.btnSavePhone.setOnClickListener {
            var phoneNumber = binding.phoneNumberInput.text.toString().replace(" ","").trim()
            if(phoneNumber.length < 10){
                binding.phoneNumberInput.error = "Enter valid phone number"
            }
            else{
                var intent = Intent(this, FundAmountActivity::class.java)
                intent.putExtra(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, LOAD_WALLET_FROM_MPESA)
                intent.putExtra(PHONE_NUMBER, phoneNumber)
                startActivity(intent)
            }
        }

        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }
    }
}
