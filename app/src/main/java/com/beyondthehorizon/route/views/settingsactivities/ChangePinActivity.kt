package com.beyondthehorizon.route.views.settingsactivities

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.databinding.ActivityChangePinBinding
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.CustomProgressBar
import com.beyondthehorizon.route.views.FundRequestedActivity
import com.beyondthehorizon.route.views.MainActivity
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_change_password.back
import kotlinx.android.synthetic.main.activity_change_password.btn_change
import kotlinx.android.synthetic.main.activity_change_pin.*
import kotlinx.android.synthetic.main.nav_bar_layout.*

class ChangePinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePinBinding
    private lateinit var prefs: SharedPreferences
    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_pin)
        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        val progressBar = CustomProgressBar()
        val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")

        btn_home.setOnClickListener {
            val intent = Intent(this@ChangePinActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btn_transactions.setOnClickListener {
            val intent = Intent(this@ChangePinActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@ChangePinActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this@ChangePinActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_change.setOnClickListener {
            try {
                val newPin = binding.newPin.text.toString();
                if (newPin.isNullOrEmpty()) {
                    binding.newPin.error = "Please enter new pin";
                    binding.newPin.requestFocus();
                } else if (newPin.length != 4) {
                    binding.newPin.error = "Please enter a valid pin";
                    binding.newPin.requestFocus();
                } else {
                    var intent = Intent(this, FundRequestedActivity::class.java)
                    progressBar.show(this, "Please wait...")
                    Constants.changePin(this, newPin, token).setCallback { e, result ->
                        progressBar.dialog.dismiss()
                        if (result.has("data")) {
                            var message = result.get("data").asJsonObject.get("message").asString
                            intent.putExtra("Message", message)
                            intent.putExtra(Constants.ACTIVITY_TYPE, Constants.RESET_PIN_ACTIVITY)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "We are unable to update your pin at the moment", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } catch (ex: Exception) {
                Toast.makeText(this, ex.message, Toast.LENGTH_LONG)
            }
        }
        back.setOnClickListener {
            onBackPressed()
        }
    }

}
