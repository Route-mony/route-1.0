package com.beyondthehorizon.routeapp.views.notifications.ui.main

import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.adapters.NotificationsAdapter
import com.beyondthehorizon.routeapp.databinding.FragmentSentNotificationBinding
import com.beyondthehorizon.routeapp.models.Notification
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Utils
import com.google.gson.JsonElement
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class SentNotificationFragment : Fragment() {
    private lateinit var binding: FragmentSentNotificationBinding
    private lateinit var notifications: MutableList<Notification>
    private lateinit var prefs: SharedPreferences
    private lateinit var notificationsAdapter: NotificationsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var filteredNotifications: MutableList<Notification>
    private lateinit var utils: Utils

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for requireActivity() fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_sent_notification, container, false)
        val view = binding.root

        prefs = requireActivity().getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        recyclerView = binding.notificationsRecyclerView
        linearLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        notifications = mutableListOf()
        utils = Utils(requireContext())

        loadRequests("sent")

        return view
    }

    /**
     * Get requests
     */

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadRequests(type: String) {
        try {
            val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
            val progressDialog = ProgressDialog(requireActivity())
            progressDialog.setMessage("please wait...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
            Constants.getFundRequests(requireActivity(), type, token).setCallback { e, result ->
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
                        var date = item.asJsonObject.get("created_at").asString.substring(0, 11)
                        var cancellationReason = item.asJsonObject.get("cancellation_reason").asString
                        var firstName = item.asJsonObject.get(userType).asJsonObject.get("first_name").asString
                        var lastName = item.asJsonObject.get(userType).asJsonObject.get("last_name").asString
                        var username = item.asJsonObject.get(userType).asJsonObject.get("username").asString
                        var phone = item.asJsonObject.get(userType).asJsonObject.get("phone_number").asString
                        var imageUrl = item.asJsonObject.get(userType).asJsonObject.get("image").asString
                        var reason = item.asJsonObject.get("reason").asString
                        var amount = item.asJsonObject.get("amount").asString
                        var status = item.asJsonObject.get("status").asString.toLowerCase()
                        var statusIcon = statusMapper[status]
                        notifications.add(Notification(id, firstName, lastName, username, phone, imageUrl, reason, amount, status, statusIcon!!, type, date, cancellationReason))
                    }
                    filteredNotifications = notifications.filter { it.type == type }.toMutableList()
                    notificationsAdapter = NotificationsAdapter(requireActivity(), filteredNotifications)
                    recyclerView.adapter = notificationsAdapter
                } else {
                    Toast.makeText(requireActivity(), "No $type requests found", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Timber.d(e.message.toString())
        }
    }
}
