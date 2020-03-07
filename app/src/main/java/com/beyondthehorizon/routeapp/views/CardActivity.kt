package com.beyondthehorizon.routeapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.androidwidgets.formatedittext.widgets.FormatEditText
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityCardBinding
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.LOAD_WALLET_FROM_CARD

class CardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_card)
        binding.cardNumberInput.setFormat("---- ---- ---- ----")
        binding.expiryDateInput.setFormat("--/--")

        binding.btnSaveCard.setOnClickListener {
            var cardNumber = binding.cardNumberInput.text.toString()
            var expDate = binding.expiryDateInput.text.toString()
            var cvvNumber = binding.cvvInput.text.toString()
            var country = binding.countryInput.text.toString()
            if (cardNumber.toString().isEmpty()){
                binding.cardNumberInput.error = "Enter card number"
                binding.cardNumberInput.requestFocus()
            }
            else if (expDate.isEmpty()) {
                binding.expiryDateInput.error = "Enter expiry date"
                binding.expiryDateInput.requestFocus()
            }
            else if (cvvNumber.isEmpty()) {
                binding.cvvInput.error = "Enter CSV number"
                binding.cvvInput.requestFocus()
            }
            else if (country.isEmpty()) {
                binding.countryInput.error = "Enter country"
                binding.countryInput.requestFocus()
            }
            else {
                var intent = Intent(this, FundAmountActivity::class.java)
                intent.putExtra(Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, LOAD_WALLET_FROM_CARD)
                startActivity(intent)
            }
        }

        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }
    }
}
