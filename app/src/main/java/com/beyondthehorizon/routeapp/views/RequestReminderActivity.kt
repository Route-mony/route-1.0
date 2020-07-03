package com.beyondthehorizon.routeapp.views

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityRequestReminderBinding
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.views.receipt.ReceiptActivity
import com.beyondthehorizon.routeapp.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.routeapp.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.nav_bar_layout.*

class RequestReminderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestReminderBinding
    private lateinit var pref: SharedPreferences
    private lateinit var oldIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_reminder)

        btn_home.setOnClickListener {
            val intent = Intent(this@RequestReminderActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@RequestReminderActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@RequestReminderActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this@RequestReminderActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        pref = applicationContext.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0) // 0 - for private mode
        oldIntent = getIntent()

        var intent = Intent(this, RequestConfirmedActivity::class.java)
        var id = oldIntent.getStringExtra("Id")
        var username = oldIntent.getStringExtra("Username")
        var phone = oldIntent.getStringExtra("Phone")
        var reason = oldIntent.getStringExtra("Reason")
        var amount = oldIntent.getStringExtra("Amount")
        var status = oldIntent.getStringExtra("Status")
        var statusIcon = oldIntent.getIntExtra("StatusIcon", R.drawable.ic_pending)
        var color = mapOf(
                R.drawable.ic_approved to "#16AA05",
                R.drawable.ic_rejected to "#AA4204",
                R.drawable.ic_pending to "#FF9800"
        )

        try {
            binding.txtUsername.text = username
            binding.txtUserContact.text = phone
            binding.txtReason.text = reason
            binding.txtAmount.text = amount
            binding.status.text = status
            binding.statusIcon.setImageResource(statusIcon)

            binding.status.setTextColor(Color.parseColor(color[statusIcon]))

            if (status != "Pending") {
                binding.requestTitle.text = "Request"
                binding.btnRemind.visibility = View.GONE
            }

        } catch (ex: Exception) {
            Log.d("TAG", ex.message)
        }

        binding.btnRemind.setOnClickListener {
            intent.putExtra("Message", "We have successfully sent a reminder to $username for the request of Ksh. $amount for $reason.")
            startActivity(intent)
        }

        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }
    }
}
