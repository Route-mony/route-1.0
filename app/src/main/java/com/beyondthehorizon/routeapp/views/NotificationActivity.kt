package com.beyondthehorizon.routeapp.views

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.adapters.NotificationsAdapter
import com.beyondthehorizon.routeapp.databinding.ActivityNotificationsBinding
import com.beyondthehorizon.routeapp.models.Notification
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.views.receipt.ReceiptActivity
import com.beyondthehorizon.routeapp.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.routeapp.views.transactions.main.TransactionsActivity
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.nav_bar_layout.*

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var notifications: MutableList<Notification>
    private lateinit var prefs: SharedPreferences
    private lateinit var notificationsAdapter: NotificationsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var context: Context
    private lateinit var filteredNotifications: MutableList<Notification>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notifications)

        btn_home.setOnClickListener {
            val intent = Intent(this@NotificationActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@NotificationActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@NotificationActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this@NotificationActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        recyclerView = binding.notificationsRecyclerView
        linearLayoutManager = LinearLayoutManager(this)
        context = applicationContext
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        notifications = mutableListOf()

        loadRequests("sent")

        binding.btnReceivedNotifications.setOnClickListener {
            binding.btnReceivedNotifications.setBackgroundResource(R.drawable.button_border)
            binding.btnSentNotifications.setBackgroundResource(0)

            try {
                loadRequests("received")
            } catch (e: Exception) {
                Log.d("TAG", e.message)
            }
        }

        binding.btnSentNotifications.setOnClickListener {
            binding.btnSentNotifications.setBackgroundResource(R.drawable.button_border)
            binding.btnReceivedNotifications.setBackgroundResource(0)

            try {
                loadRequests("sent")
            } catch (e: Exception) {
                Log.d("TAG", e.message)
            }
        }

        binding.arrowBack.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))

        }
    }

    /**
     * Get requests
     */

    private fun loadRequests(type: String) {
        try {
            val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
            val progressDialog = ProgressDialog(this@NotificationActivity)
            progressDialog.setMessage("please wait...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
            Constants.getFundRequests(this, type, token).setCallback { e, result ->
                progressDialog.dismiss()
                if (result != null) {
                    var statusMapper = mapOf(
                            "ok" to R.drawable.ic_approved,
                            "pending" to R.drawable.ic_pending,
                            "cancelled" to R.drawable.ic_rejected
                    )
                    var userType = ""
                    when (type) {
                        "received" -> userType = "requester"
                        "sent" -> userType = "recipient"
                    }
                    var requests = result.get("data").asJsonObject.get("rows").asJsonArray
                    notifications = mutableListOf()
                    for (item: JsonElement in requests) {
                        var id = item.asJsonObject.get("id").asString
                        var username = item.asJsonObject.get(userType).asJsonObject.get("first_name").asString + " " +
                                item.asJsonObject.get(userType).asJsonObject.get("last_name").asString
                        var phone = item.asJsonObject.get(userType).asJsonObject.get("phone_number").asString
                        var imageUrl = R.drawable.group416
                        var reason = item.asJsonObject.get("reason").asString
                        var amount = item.asJsonObject.get("amount").asString
                        var status = item.asJsonObject.get("status").asString.toLowerCase()
                        var statusIcon = statusMapper[status]
                        notifications.add(Notification(id, username, phone, imageUrl, reason, amount, status, statusIcon!!, type))
                    }
                    filteredNotifications = notifications.filter { it.type == type }.toMutableList()
                    notificationsAdapter = NotificationsAdapter(this, filteredNotifications)
                    recyclerView.adapter = notificationsAdapter
                }
                else{
                    Toast.makeText(this, "No $type requests found", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message)
        }
    }
}
