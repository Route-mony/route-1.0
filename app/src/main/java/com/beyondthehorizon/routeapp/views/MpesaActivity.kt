package com.beyondthehorizon.routeapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityAddMoneyBinding
import com.beyondthehorizon.routeapp.databinding.ActivityMpesaBinding
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.*
import kotlinx.android.synthetic.main.activity_mpesa.*

class MpesaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMpesaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mpesa)
        binding.phoneNumberInput.setFormat("---- --- ----")
        binding.btnSavePhone.setOnClickListener {
            var phoneNumber = binding.phoneNumberInput.text.toString()
            if(phoneNumber.isNotEmpty()){
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
