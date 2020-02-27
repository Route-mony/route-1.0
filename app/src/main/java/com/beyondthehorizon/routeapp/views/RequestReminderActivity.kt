package com.beyondthehorizon.routeapp.views

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityRequestReminderBinding
import com.beyondthehorizon.routeapp.utils.Constants
import java.lang.Exception

class RequestReminderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestReminderBinding
    private lateinit var pref: SharedPreferences
    private lateinit var oldIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_reminder)
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

        } catch (ex: Exception) {
            Log.d("TAG", ex.message)
        }

        binding.btnRemind.setOnClickListener{
            intent.putExtra("Message", "We have successfully sent a reminder to $username for the request of Ksh. $amount for $reason.")
            startActivity(intent)
        }
    }
}
