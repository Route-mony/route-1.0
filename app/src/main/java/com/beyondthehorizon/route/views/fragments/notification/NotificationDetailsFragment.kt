package com.beyondthehorizon.route.views.fragments.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.bottomsheets.EnterPinBottomSheet
import com.beyondthehorizon.route.bottomsheets.EnterReasonBottomSheet
import com.beyondthehorizon.route.databinding.FragmentNotificationDetailsBinding
import com.beyondthehorizon.route.interfaces.IDone
import com.beyondthehorizon.route.interfaces.bottomsheets.OnInputListener
import com.beyondthehorizon.route.models.notification.Recipient
import com.beyondthehorizon.route.models.notification.SelectedNotification
import com.beyondthehorizon.route.models.profile.ProfileResponse
import com.beyondthehorizon.route.utils.Constants.*
import com.beyondthehorizon.route.utils.SharedPref
import com.beyondthehorizon.route.views.base.BaseFragment
import com.beyondthehorizon.route.views.fragments.common.SuccessFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import timber.log.Timber
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class NotificationDetailsFragment(private val notification: SelectedNotification) : BaseFragment(), OnInputListener, IDone {
    private var _binding: FragmentNotificationDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var format: DecimalFormat
    private lateinit var locale: Locale
    private lateinit var symbol: String
    private lateinit var details: Recipient
    private lateinit var userProfile: ProfileResponse
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationDetailsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        userProfile = SharedPref.getData(
            requireContext(),
            USER_PROFILE,
            ProfileResponse::class.java
        ) as ProfileResponse
        details = when {
            notification.sentNotification!! -> {
                notification.recipient!!
            }
            else -> {
                notification.requester!!
            }
        }
        binding.arrowBack.setOnClickListener {
            replaceFragment(NotificationsFragment())
        }

        binding.btnRemind.setOnClickListener {
            transactionData.message =
                "We have successfully sent a reminder to ${details.username} for the request of Ksh. ${notification.amount} for ${notification.reason}."
            showCompleteDialog(transactionData, this)
        }
        //Format currency
        locale = Locale("en", "KE")
        symbol = Currency.getInstance(locale).getSymbol(locale)
        format = NumberFormat.getCurrencyInstance(locale) as DecimalFormat
        format.isGroupingUsed = true
        format.positivePrefix = "$symbol "
        format.maximumFractionDigits = 0
        try {
            val paymentAmount = format.format(notification.amount!!.toDouble().toInt())
            binding.txtUsername.text = String.format("%s %s", details.firstName, details.lastName)
            binding.txtReason.text = notification.reason
            binding.txtPaymentBy.text = String.format("Payment by %s", details.username)
            binding.txtAmount.text = paymentAmount
            binding.status.text = notification.status
            binding.txtDate.text = notification.createdAt
            binding.status.setBackgroundResource(notification.statusIcon!!)

            when (notification.sentNotification) {
                true -> {
                    if (notification.status.equals("pending", ignoreCase = true)) {
                        binding.requestTitle.text = resources.getString(R.string.send_a_reminder)
                        binding.llSent.visibility = View.VISIBLE
                    }
                }
                false -> {
                    if (notification.status.equals("pending", ignoreCase = true)) {
                        binding.requestTitle.text = resources.getString(R.string.request)
                        binding.llReceived.visibility = View.VISIBLE
                    }
                }
            }
            var requestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(16))
            Glide.with(requireContext())
                .load(details.image)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.ic_user_home_page)
                .placeholder(R.drawable.ic_user_home_page)
                .apply(requestOptions)
                .into(binding.imgAvatar)
        } catch (ex: Exception) {
            Timber.d(ex.message.toString())
        }

        binding.btnApprove.setOnClickListener {
            val enterPinBottomSheet = EnterPinBottomSheet(this)
            enterPinBottomSheet.show(
                (requireActivity() as AppCompatActivity).supportFragmentManager,
                "Approve Request"
            )
        }

        binding.btnReject.setOnClickListener {
            val enterReasonBottomSheet = EnterReasonBottomSheet(this)
            enterReasonBottomSheet.show(
                (requireActivity() as AppCompatActivity).supportFragmentManager,
                "Reject Reason"
            )
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun transactionComplete() {
        replaceFragment(NotificationsFragment())
    }

    override fun onReasonListener(reason: String) {
        try {
            rejectFundRequests(
                requireContext(),
                userProfile.getUserId(),
                userProfile.getUserIdNumber().toString(),
                "CANCELLED",
                notification.reason,
                SharedPref.getToken(requireContext())
            )
                .setCallback { e, result ->
                    if (result != null) {
                        if (result.asJsonObject.get("status").asString == "success") {
                            transactionData.message =
                                "You have rejected ${details.username} request of Ksh. ${notification.amount} for ${notification.reason}"
                            replaceFragment(SuccessFragment(transactionData, this))
                        } else if (result.has("errors")) {
                            showToast(
                                requireContext(),
                                result.get("errors").asJsonArray[0].asString,
                                0
                            )
                        }
                    } else {
                        showToast(requireContext(), e.message!!, 0)
                    }

                }
        } catch (e: Exception) {
            Timber.d(e.message.toString())
        }
    }

    override fun onPinListener(pin: String) {
        try {
            verifyPin(
                requireContext(),
                pin,
                SharedPref.getToken(requireContext())
            ).setCallback { e, result ->
                if (result != null) {
                    if (result.asJsonObject.get("status").asString == "success") {
                        approveFundRequests(
                            requireContext(),
                            userProfile.getUserId(),
                            pin,
                            notification.reason,
                            "ROUTEWALLET",
                            SharedPref.getToken(requireContext())
                        ).setCallback { e, result ->
                            if (result != null) {
                                if (result.asJsonObject.get("status").asString == "success") {
                                    transactionData.message =
                                        "Your approval for ${details.username} request of Ksh. ${notification.amount} for ${notification.reason} is being processed. You will be notified after the processing is done."
                                    replaceFragment(SuccessFragment(transactionData, this))
                                } else if (result.has("errors")) {
                                    showToast(
                                        requireContext(),
                                        result["errors"].asJsonArray[0].asString,
                                        0
                                    )
                                }
                            } else {
                                showToast(requireContext(), e.message!!, 0)
                            }
                        }
                    } else {
                        showToast(requireContext(), e.message!!, 0)
                    }
                }
            }
        } catch (e: Exception) {
            Timber.d(e.message.toString())
        }
    }
}