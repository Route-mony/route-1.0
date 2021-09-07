package com.beyondthehorizon.route.views.fragments.bottommenu


import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.bottomsheets.EnterPinBottomSheet
import com.beyondthehorizon.route.bottomsheets.MpesaMoneyBottomModel
import com.beyondthehorizon.route.bottomsheets.SendMoneyBottomModel
import com.beyondthehorizon.route.bottomsheets.SendToManyModel
import com.beyondthehorizon.route.databases.NotificationCount
import com.beyondthehorizon.route.databinding.FragmentHomeBinding
import com.beyondthehorizon.route.interfaces.IDone
import com.beyondthehorizon.route.interfaces.bottomsheets.OnInputListener
import com.beyondthehorizon.route.interfaces.bottomsheets.SendMoneyBottomSheetListener
import com.beyondthehorizon.route.models.SendMoneyModel
import com.beyondthehorizon.route.models.common.TransactionData
import com.beyondthehorizon.route.models.profile.ProfileResponse
import com.beyondthehorizon.route.utils.Constants.*
import com.beyondthehorizon.route.utils.SharedPref
import com.beyondthehorizon.route.viewmodels.RoutViewModel
import com.beyondthehorizon.route.views.auth.LoginActivity
import com.beyondthehorizon.route.views.auth.SetTransactionPinActivity
import com.beyondthehorizon.route.views.base.BaseFragment
import com.beyondthehorizon.route.views.fragments.common.SuccessFragment
import com.beyondthehorizon.route.views.fragments.loan.LoanFragment
import com.beyondthehorizon.route.views.fragments.notification.NotificationsFragment
import com.beyondthehorizon.route.views.fragments.services.common.AmountFragment
import com.beyondthehorizon.route.views.fragments.services.common.ContactsFragment
import com.beyondthehorizon.route.views.split.bill.SplitBillActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlin.coroutines.suspendCoroutine

class HomeFragment : BaseFragment(), OnInputListener, SendMoneyBottomSheetListener, IDone {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var routViewModel: RoutViewModel? = null
    private lateinit var supportFragmentManager: FragmentManager
    private lateinit var profileResponse: ProfileResponse
    private lateinit var sendToManyModel: SendToManyModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        routViewModel = ViewModelProviders.of(requireActivity()).get(RoutViewModel::class.java)
        supportFragmentManager = (requireActivity() as AppCompatActivity).supportFragmentManager
        profileResponse = ProfileResponse()
        binding.btnUtilities.setOnClickListener {
            replaceFragment(ReceiptsFragment())
        }
        binding.btnAddMoney.setOnClickListener {
            transactionData.process = ADD_MONEY
            replaceFragment(AmountFragment(transactionData))
        }
        binding.btnRequest.setOnClickListener {
            transactionData.process = REQUEST_MONEY
            replaceFragment(ContactsFragment(transactionData))
        }
        binding.btnNotifications.setOnClickListener {
            routViewModel!!.deleteNotifiCount()
            replaceFragment(NotificationsFragment())
        }
        binding.ivProfilePic.setOnClickListener {
            replaceFragment(SettingsFragment())
        }
        binding.btnReceipts.setOnClickListener {
            replaceFragment(ReceiptsFragment())
        }
        binding.btnSend.setOnClickListener {
            val sendMoneyBottomModel = SendMoneyBottomModel(this)
            sendMoneyBottomModel.show(supportFragmentManager, "Send Money Options")
        }
        binding.btnRouteIt.setOnClickListener {
            transactionData.process = SEND_MONEY
            replaceFragment(ContactsFragment(transactionData))
        }
        binding.btnPayBill.setOnClickListener {
            val mpesaMoneyBottomModel = MpesaMoneyBottomModel(this)
            mpesaMoneyBottomModel.show(supportFragmentManager, "Mpesa Options")
        }
        binding.btnSendToMany.setOnClickListener {
            val sendToManyModel = SendToManyModel()
            sendToManyModel.show(supportFragmentManager, "Send To Many")
        }
        binding.btnSplitBill.setOnClickListener {
            startNewActivity(SplitBillActivity::class.java)
        }
        binding.btnRequestLoan.setOnClickListener {
            val pr = ProgressDialog(requireContext())
            pr.setMessage("Please wait..")
            pr.show()
            val handler = Handler()
            handler.postDelayed({
                pr.dismiss()
                replaceFragment(LoanFragment())
            }, 1000)
        }
        setupUserProfile()
        return view
    }

    private fun setupUserProfile() {
        if (isNetworkAvailable) {
            GlobalScope.launch(Dispatchers.Main) {
                awaitAll(async { getProfile() }, async { getNotificationCount() })
            }
        }
    }

    private suspend fun getNotificationCount(): List<NotificationCount> =
        suspendCoroutine {
            routViewModel!!.notifiCount.observe(
                viewLifecycleOwner,
                {
                    if (isAdded) {
                        requireActivity().runOnUiThread {
                            if (it.isEmpty()) {
                                binding.tvNotificationCount.visibility = View.GONE
                            } else {
                                binding.tvNotificationCount.text = it!!.size.toString()
                                binding.tvNotificationCount.visibility = View.VISIBLE
                            }
                        }
                    }
                })
        }

    private suspend fun getProfile(): Unit = suspendCoroutine {
        getUserProfile(requireContext(), SharedPref.getToken(requireContext()))
            .setCallback { e, result ->
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        if (e != null) {
                            showToast(requireContext(), e.message!!, 0)
                        } else {
                            when {
                                result.asJsonObject.has("detail") -> {
                                    showToast(requireContext(), result.get("detail").asString, 0)
                                    if (result.get("detail").asString.contains(
                                            "token expired",
                                            ignoreCase = true
                                        )
                                    ) {
                                        SharedPref.clear(requireContext())
                                        startNewActivity(LoginActivity::class.java, finish = true)
                                    }
                                }
                                result.has("data") -> {
                                    val profileResponse =
                                        Gson().fromJson(result, ProfileResponse::class.java)
                                    setProfile(profileResponse)
                                }
                                else -> {
                                    showCustomToast(requireContext(), "profile", 0)
                                }
                            }
                        }
                    }
                }
            }
    }

    private fun setProfile(profileResponse: ProfileResponse) {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
        if (profileResponse.isEmailVerified()) {
            binding.tvVerifyEmail.visibility = View.GONE
        } else {
            binding.tvVerifyEmail.visibility = View.VISIBLE
        }
        if (!profileResponse.isPinSet()) {
            startNewActivity(SetTransactionPinActivity::class.java)
        }
        binding.tvBalance.text = profileResponse.getFormattedBalance()
        binding.tvUsername.text = profileResponse.getUsername()
        Glide.with(requireContext())
            .load(profileResponse.getProfilePic())
            .apply(requestOptions)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .centerCrop()
            .skipMemoryCache(true)
            .error(R.drawable.ic_user_home_page)
            .placeholder(R.drawable.ic_user_home_page)
            .into(binding.ivProfilePic)
        SharedPref.save(requireContext(), USER_PROFILE, profileResponse)
    }

    override fun onPinListener(pin: String) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)
        try {
            progressDialog.show()
            val sendMoneyModel = transactionData.sendMoneyModel
            val bankName = when {
                sendMoneyModel!!.isBank!! -> sendMoneyModel.bankName
                else -> MPESA_TILL
            }
            sendMoney(
                requireContext(),
                sendMoneyModel.bankAccountNo,
                sendMoneyModel.amount.toString(),
                pin,
                SharedPref.getToken(requireContext()),
                bankName,
                "Payment"
            ).setCallback { _, result ->
                progressDialog.dismiss()
                if (result.has("errors")) {
                    showToast(
                        requireContext(),
                        result["errors"].asJsonArray[0].asString,
                        0
                    )
                } else {
                    transactionData.message = result["data"].asJsonObject["message"].asString
                    startNavigation(transactionData)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun startNavigation(transactionData: TransactionData) {
        replaceFragment(SuccessFragment(transactionData, this))
    }

    override fun onReasonListener(reason: String) {
        TODO("Not yet implemented")
    }

    override fun moneyModelListener(sendMoneyModel: SendMoneyModel) {
        transactionData.sendMoneyModel = sendMoneyModel
        val enterPinBottomSheet = EnterPinBottomSheet(this)
        enterPinBottomSheet.show(supportFragmentManager, "Send Money")
    }

    override fun transactionComplete() {}
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
