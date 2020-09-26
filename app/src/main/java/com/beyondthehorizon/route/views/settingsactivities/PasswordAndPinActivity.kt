package com.beyondthehorizon.route.views.settingsactivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.views.MainActivity
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.activity_password_and_pin.*
import kotlinx.android.synthetic.main.nav_bar_layout.*

class PasswordAndPinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_and_pin)

        changePassword.setOnClickListener {
            startActivity(Intent(this@PasswordAndPinActivity, ChangePasswordActivity::class.java))
        }
        changePin.setOnClickListener {
            startActivity(Intent(this@PasswordAndPinActivity, ChangePinActivity::class.java))
        }
        btn_home.setOnClickListener {
            val intent = Intent(this@PasswordAndPinActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@PasswordAndPinActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@PasswordAndPinActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_settings.setOnClickListener {
            val intent = Intent(this@PasswordAndPinActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        back.setOnClickListener {
            onBackPressed()
        }
    }
}
