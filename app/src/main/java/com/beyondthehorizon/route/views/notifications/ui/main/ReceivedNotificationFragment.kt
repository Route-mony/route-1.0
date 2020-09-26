package com.beyondthehorizon.route.views.notifications.ui.main

import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.adapters.NotificationsAdapter
import com.beyondthehorizon.route.databinding.FragmentReceivedNotifactionBinding
import com.beyondthehorizon.route.models.Notification
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.DateFormatter
import com.beyondthehorizon.route.utils.NetworkUtils
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.fragment_sent_notification.*
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class ReceivedNotificationFragment : Fragment() {

    private lateinit var binding: FragmentReceivedNotifactionBinding
    private lateinit var notifications: MutableList<Notification>
    private lateinit var prefs: SharedPreferences
    private lateinit var notificationsAdapter: NotificationsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var networkUtils: NetworkUtils

    private lateinit var filteredNotifications: MutableList<Notification>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_received_notifaction, container, false)
        val view = binding.root

        prefs = requireActivity().getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        recyclerView = binding.notificationsRecyclerView
        linearLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        notifications = mutableListOf()
        networkUtils = NetworkUtils(requireContext())

        loadRequests("received")

        return view
    }

    /**
     * Get requests
     */

    private fun loadRequests(type: String) {
        try {
            if(networkUtils.isNetworkAvailable) {
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
                            var date = DateFormatter.formatMonthDayYear(item.asJsonObject.get("created_at").asString)
                            var firstName = item.asJsonObject.get(userType).asJsonObject.get("first_name").asString
                            var lastName = item.asJsonObject.get(userType).asJsonObject.get("last_name").asString
                            var username = item.asJsonObject.get(userType).asJsonObject.get("username").asString
                            var phone = item.asJsonObject.get(userType).asJsonObject.get("phone_number").asString
                            var imageUrl = item.asJsonObject.get(userType).asJsonObject.get("image").asString
                            var reason = item.asJsonObject.get("reason").asString
                            var amount = item.asJsonObject.get("amount").asString
                            var status = item.asJsonObject.get("status").asString.toLowerCase()
                            var statusIcon = statusMapper[status]
                            notifications.add(Notification(id, firstName, lastName, username, phone, imageUrl, reason, amount, status, statusIcon!!, type, date))
                        }
                        filteredNotifications = notifications.filter { it.type == type }.toMutableList()
                        notificationsAdapter = NotificationsAdapter(requireActivity(), filteredNotifications)
                        recyclerView.adapter = notificationsAdapter
                    } else {
                        Toast.makeText(requireActivity(), "No $type requests found", Toast.LENGTH_LONG).show()
                    }
                }
            }
            else{
                binding.tvNoInternet.visibility = View.VISIBLE
                binding.notificationsRecyclerView.visibility = View.GONE
            }
        } catch (e: Exception) {
            Timber.d(e.message.toString())
        }
    }
}
