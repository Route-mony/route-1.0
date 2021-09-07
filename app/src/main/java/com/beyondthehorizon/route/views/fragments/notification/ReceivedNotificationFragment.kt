package com.beyondthehorizon.route.views.fragments.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.route.adapters.NotificationsAdapter
import com.beyondthehorizon.route.databinding.FragmentReceivedNotifactionBinding
import com.beyondthehorizon.route.interfaces.ISelectedNotification
import com.beyondthehorizon.route.models.notification.NotificationResponse
import com.beyondthehorizon.route.models.notification.SelectedNotification
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.SharedPref
import com.beyondthehorizon.route.views.base.BaseFragment
import com.google.gson.Gson
import timber.log.Timber

class ReceivedNotificationFragment : BaseFragment(), ISelectedNotification {

    private var _binding: FragmentReceivedNotifactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationsAdapter: NotificationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReceivedNotifactionBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        binding.notificationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        notificationsAdapter = NotificationsAdapter(requireContext(), this)
        binding.notificationsRecyclerView.adapter = notificationsAdapter
        loadRequests("received")
        return view
    }

    /**
     * Get requests
     */

    private fun loadRequests(type: String) {
        Constants.getFundRequests(
            requireActivity(),
            type,
            SharedPref.getToken(requireContext())
        ).setCallback { e, result ->
            try {
                if (result != null && result.has("data") && result.get("data").asJsonObject.has(
                        "rows"
                    )
                ) {
                    val notification = Gson().fromJson(result, NotificationResponse::class.java)
                    if (notification != null) {
                        notificationsAdapter.updateNotifications(
                            notification.data.selectedNotifications!!,
                            false
                        )
                        binding.progressBar.visibility = View.GONE
                        binding.notificationsRecyclerView.visibility = View.VISIBLE
                    }
                } else {
                    showToast(requireContext(), "No $type requests found", 0)
                }
            } catch (e: Exception) {
                Timber.d(e.message.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun selectedNotification(notification: SelectedNotification) {
        replaceFragment(NotificationDetailsFragment(notification))
    }
}