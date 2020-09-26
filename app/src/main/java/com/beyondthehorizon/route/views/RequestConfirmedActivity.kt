package com.beyondthehorizon.route.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.databinding.ActivityRequestConfirmedBinding
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.views.notifications.NotificationsActivity
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.nav_bar_layout.*
import timber.log.Timber
import java.lang.Exception

class RequestConfirmedActivity : AppCompatActivity() {
   private lateinit var binding: ActivityRequestConfirmedBinding
    private lateinit var oldIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_confirmed)

        btn_home.setOnClickListener {
            val intent = Intent(this@RequestConfirmedActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@RequestConfirmedActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@RequestConfirmedActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this@RequestConfirmedActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        oldIntent = getIntent()
        try {
            binding.txtRequestInform.text = intent.getStringExtra("Message")
            binding.btnDone.setOnClickListener{
                startActivity(Intent(this, NotificationsActivity::class.java))
            }
            binding.arrowBack.setOnClickListener{
                startActivity(Intent(this, NotificationsActivity::class.java))
            }
        }
        catch (e: Exception) {
            Timber.d(e.message.toString())
        }
    }
}
