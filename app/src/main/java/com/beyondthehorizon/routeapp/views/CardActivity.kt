package com.beyondthehorizon.routeapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityCardBinding
import com.beyondthehorizon.routeapp.utils.Constants.*

class CardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_card)
        binding.cardNumberInput.setFormat("---- ---- ---- ----")
        binding.expiryDateInput.setFormat("--/--")

        binding.btnSaveCard.setOnClickListener {
            var cardNumber = binding.cardNumberInput.text.toString().replace(" ","").trim()
            var expiryDate = binding.expiryDateInput.text.toString().replace(" ","").trim()
            var cvvNumber = binding.cvvInput.text.toString().replace(" ","").trim()
            var country = binding.countryInput.text.toString().replace(" ","").trim()

            if (cardNumber.length < 16){
                binding.cardNumberInput.error = "Enter valid card number"
                binding.cardNumberInput.requestFocus()
            }
            else if (expiryDate.replace("/","").length < 4) {
                binding.expiryDateInput.error = "Enter valid expiry date"
                binding.expiryDateInput.requestFocus()
            }
            else if (cvvNumber.length < 3) {
                binding.cvvInput.error = "Enter valid CSV number"
                binding.cvvInput.requestFocus()
            }
            else if (country.isEmpty()) {
                binding.countryInput.error = "Enter country"
                binding.countryInput.requestFocus()
            }
            else {
                var intent = Intent(this, FundAmountActivity::class.java)
                intent.putExtra(CARD_NUMBER, cardNumber)
                intent.putExtra(EXPIRY_DATE, expiryDate)
                intent.putExtra(CVV_NUMBER, cvvNumber)
                intent.putExtra(COUNTRY, country)
                intent.putExtra(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, LOAD_WALLET_FROM_CARD)
                intent.putExtra(CARD_STATUS, NEW_CARD)
                startActivity(intent)
            }
        }

        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }
    }
}
