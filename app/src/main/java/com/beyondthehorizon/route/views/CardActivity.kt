package com.beyondthehorizon.route.views

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.databinding.ActivityCardBinding
import com.beyondthehorizon.route.utils.Constants.*
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.nav_bar_layout.*

class CardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardBinding

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_card)


        btn_home.setOnClickListener {
            val intent = Intent(this@CardActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@CardActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@CardActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this@CardActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.cardNumberInput.setFormat("---- ---- ---- ----")
        binding.expiryDateInput.setFormat("--/--")
        editor = getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
        prefs = getSharedPreferences(REG_APP_PREFERENCES, 0)
        binding.btnSaveCard.setOnClickListener {
            var cardNumber = binding.cardNumberInput.text.toString().replace(" ", "").trim()
            var expiryDate = binding.expiryDateInput.text.toString().replace(" ", "").trim()
            var cvvNumber = binding.cvvInput.text.toString().replace(" ", "").trim()
            var country = binding.countryInput.text.toString().replace(" ", "").trim()

            if (cardNumber.length < 16) {
                binding.cardNumberInput.error = "Enter valid card number"
                binding.cardNumberInput.requestFocus()
            } else if (expiryDate.replace("/", "").length < 4) {
                binding.expiryDateInput.error = "Enter valid expiry date"
                binding.expiryDateInput.requestFocus()
            } else if (cvvNumber.length < 3) {
                binding.cvvInput.error = "Enter valid CSV number"
                binding.cvvInput.requestFocus()
            } else if (country.isEmpty()) {
                binding.countryInput.error = "Enter country"
                binding.countryInput.requestFocus()
            } else {
                var intent = Intent(this, FundAmountActivity::class.java)
                intent.putExtra(CARD_NUMBER, cardNumber)
                intent.putExtra(EXPIRY_DATE, expiryDate)
                intent.putExtra(CVV_NUMBER, cvvNumber)
                intent.putExtra(COUNTRY, country)
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, LOAD_WALLET_FROM_CARD)
                editor.apply()
                intent.putExtra(CARD_STATUS, NEW_CARD)
                startActivity(intent)
            }
        }

        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }
    }
}
