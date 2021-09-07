package com.beyondthehorizon.route.views.fragments.notification

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.beyondthehorizon.route.adapters.NotificationsAdapter
import com.beyondthehorizon.route.databinding.FragmentSentNotificationBinding
import com.beyondthehorizon.route.interfaces.ISelectedNotification
import com.beyondthehorizon.route.models.notification.NotificationResponse
import com.beyondthehorizon.route.models.notification.SelectedNotification
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.SharedPref
import com.beyondthehorizon.route.views.base.BaseFragment
import com.google.gson.Gson
import timber.log.Timber


class SentNotificationFragment : BaseFragment(), ISelectedNotification {
    private var _binding: FragmentSentNotificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationsAdapter: NotificationsAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSentNotificationBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        binding.notificationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        notificationsAdapter = NotificationsAdapter(requireContext(), this)
        binding.notificationsRecyclerView.adapter = notificationsAdapter
        loadRequests("sent")
        return view
    }

    /**
     * Get requests
     */

    private fun loadRequests(type: String) {
        try {
            Constants.getFundRequests(
                requireActivity(),
                type,
                SharedPref.getToken(requireContext())
            ).setCallback { e, result ->
                if (result != null && result.has("data") && result.get("data").asJsonObject.has("rows")) {
                    val notification = Gson().fromJson(result, NotificationResponse::class.java)
                    if (notification != null) {
                        notificationsAdapter.updateNotifications(
                            notification.data.selectedNotifications!!,
                            true
                        )
                        binding.progressBar.visibility = View.GONE
                        binding.notificationsRecyclerView.visibility = View.VISIBLE
                    }
                } else {
                    showToast(
                        requireContext(),
                        "No $type requests found",
                        0
                    )
                }
            }
        } catch (e: Exception) {
            Timber.d(e.message.toString())
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
