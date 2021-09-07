package com.beyondthehorizon.route.views.fragments.services.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.databinding.FragmentAmountBinding
import com.beyondthehorizon.route.interfaces.IDone
import com.beyondthehorizon.route.interfaces.bottomsheets.OnInputListener
import com.beyondthehorizon.route.models.common.Title
import com.beyondthehorizon.route.models.common.TransactionData
import com.beyondthehorizon.route.models.profile.ProfileResponse
import com.beyondthehorizon.route.utils.*
import com.beyondthehorizon.route.utils.Constants.TRANSACTION_DATA
import com.beyondthehorizon.route.views.BulkRequestActivity
import com.beyondthehorizon.route.views.base.BaseFragment
import com.beyondthehorizon.route.views.fragments.bottommenu.HomeFragment
import com.beyondthehorizon.route.views.fragments.common.SuccessFragment
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class AmountFragment(private val data: TransactionData? = null) : BaseFragment(), OnInputListener,
    IDone {
    private var _binding: FragmentAmountBinding? = null
    private val binding get() = _binding!!
    private var amount = ""
    private lateinit var networkUtils: NetworkUtils
    private lateinit var format: DecimalFormat
    private lateinit var util: Utils
    private lateinit var progressBar: CustomProgressBar
    private lateinit var userProfile: ProfileResponse
    private lateinit var transactionMapper: Map<Int, Title>
    private val locale = Locale("en", "KE")
    private val symbol = Currency.getInstance(locale).getSymbol(locale)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAmountBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        util = Utils(requireContext())
        networkUtils = NetworkUtils(requireContext())
        progressBar = CustomProgressBar(requireContext())
        userProfile = SharedPref.getData(
            requireContext(),
            Constants.USER_PROFILE,
            ProfileResponse::class.java
        ) as ProfileResponse
        transactionData = data!!
        format = NumberFormat.getCurrencyInstance(locale) as DecimalFormat
        format.isGroupingUsed = true
        format.positivePrefix = "$symbol "
        format.maximumFractionDigits = 0
        transactionMapper = mapOf(
            Pair(0, Title(getString(R.string.enter_amount_to_load), getString(R.string.load))),
            Pair(1, Title(getString(R.string.request_from), getString(R.string.request))),
            Pair(2, Title(getString(R.string.send_to), getString(R.string.send))),
            Pair(3, Title("", "")),
            Pair(4, Title("", "")),
        )
        binding.btnOne.setOnClickListener {
            binding.txtAmount.text = formatAmount("${amount}${binding.btnOne.text}")
        }

        binding.btnTwo.setOnClickListener {
            binding.txtAmount.text = formatAmount("${amount}${binding.btnTwo.text}")
        }

        binding.btnThree.setOnClickListener {
            binding.txtAmount.text = formatAmount("${amount}${binding.btnThree.text}")
        }

        binding.btnFour.setOnClickListener {
            binding.txtAmount.text = formatAmount("${amount}${binding.btnFour.text}")
        }

        binding.btnFive.setOnClickListener {
            binding.txtAmount.text = formatAmount("${amount}${binding.btnFive.text}")
        }

        binding.btnSix.setOnClickListener {
            binding.txtAmount.text = formatAmount("${amount}${binding.btnSix.text}")
        }

        binding.btnSeven.setOnClickListener {
            binding.txtAmount.text = formatAmount("${amount}${binding.btnSeven.text}")
        }

        binding.btnEight.setOnClickListener {
            binding.txtAmount.text = formatAmount("${amount}${binding.btnEight.text}")
        }

        binding.btnNine.setOnClickListener {
            binding.txtAmount.text = formatAmount("${amount}${binding.btnNine.text}")
        }

        binding.btnZero.setOnClickListener {
            binding.txtAmount.text = formatAmount("${amount}${binding.btnZero.text}")
        }

        binding.btnZeroZero.setOnClickListener {
            binding.txtAmount.text = formatAmount("${amount}${binding.btnZeroZero.text}")
        }

        binding.btnClear.setOnClickListener {
            if (amount.length <= 1) {
                amount = "";
                binding.txtAmount.text = ""
            } else {
                binding.txtAmount.text = formatAmount(amount.substring(0, amount.length - 1))
            }
        }
        binding.bulkRequest.setOnClickListener {
            bundle.putSerializable(TRANSACTION_DATA, transactionData)
            startNewActivity(BulkRequestActivity::class.java, bundle = bundle)
        }

        try {
            if (transactionMapper.containsKey(transactionData.process)) {
                val transaction = transactionMapper[transactionData.process]
                val contact = transactionData.contact!!
                binding.requestType.text = transaction!!.title
                binding.requestTitle.text = when {
                    contact.name.isNotEmpty() -> contact.name
                    else -> contact.contact
                }
                binding.btnRequest.text = transaction.button
            }

//            when {
//                transactionType.compareTo(REQUEST_MONEY) == 0 -> {
//                    binding.requestLayout.visibility = View.VISIBLE
//                    binding.requestTitle.text = userProfile.getUsername()
//                    binding.requestType.text = "Request : "
//
//                }
//                transactionType.compareTo(SEND_MONEY) == 0 -> {
//                    when (SharedPref.getString(requireContext(), REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE)) {
//                        SEND_MONEY_TO_MOBILE_MONEY -> {
//                            binding.btnRequest.text = "SEND"
//                            binding.requestTitle.text = userProfile.getPhone()
//                            binding.requestType.text = "Send To : "
//
//                        }
//                        SEND_MONEY_TO_ROUTE -> {
//                            binding.btnRequest.text = "SEND"
//                            binding.requestTitle.text = userProfile.getUsername()
//                            binding.requestType.text = "Send To : "
//
//                        }
//                        SEND_MONEY_TO_BANK -> {
//                            val title = String.format(
//                                "%s \nAccount No: %s",
//                                SharedPref.getString(requireContext(), "chosenBank"),
//                                SharedPref.getString(requireContext(), "bankAcNumber")
//                            )
//                            binding.btnRequest.text = "SEND"
//                            binding.requestTitle.text = title
//                            binding.requestType.text = "Send To : "
//
//                        }
//                    }
//                }
//                transactionType.compareTo(LOAD_WALLET) == 0 -> {
//                    binding.btnRequest.text = "LOAD"
//                    binding.requestTitle.text = "Enter Amount to load"
//                    binding.requestType.visibility = View.GONE
//                }
//                transactionType.compareTo(MOBILE_TRANSACTION) == 0 -> {
//                    phone = parentIntent.getStringExtra(PHONE_NUMBER).toString()
//                    binding.requestTitle.text = phone
//                    binding.requestType.text = "Pay From : "
//                    binding.btnRequest.text = "PAY"
//                }
//                transactionType.compareTo(BUY_AIRTIME) == 0 -> {
//                    phone = parentIntent.getStringExtra(PHONE_NUMBER).toString()
//                    request_title.text = phone
//                    requestType.text = "Buy For: "
//                    btn_request.text = "Buy Airtime"
//                }
//                transactionType.compareTo(SPLIT_BILL) == 0 -> {
//                    requestType.text = "Enter bill amount "
//                    btn_request.text = "Continue"
//                }
//            }
        } catch (ex: Exception) {
            showToast(requireContext(), ex.message!!, 0)
        }
        binding.btnRequest.setOnClickListener {
//            sendRequest()
        }

        binding.arrowBack.setOnClickListener {
            replaceFragment(HomeFragment())
        }
        return view
    }

//    private fun sendRequest() {
//        when {
//            binding.txtAmount.text.isNullOrEmpty() -> {
//                showToast(requireContext(), getString(R.string.enter_amount), 0)
//            }
//            amount.toInt() <= 0 -> {
//                showToast(requireContext(), getString(R.string.enter_a_valid_amount), 0)
//            }
//
//            /**HERE NOW*/
//            transactionType.compareTo(Constants.REQUEST_MONEY) == 0 -> {
//                try {
//                    if (binding.requestNarration.text.toString().trim().isEmpty()) {
//                        showToast(
//                            requireContext(),
//                            getString(R.string.please_enter_your_reason),
//                            0
//                        )
//                        return
//                    }
//                    val intent =    Intent(requireContext(), FundRequestedActivity::class.java)
//                    Constants.requestFund(
//                        requireContext(),
//                        userProfile.getUserId(),
//                        amount,
//                        binding.requestNarration.text.toString().trim(),
//                        SharedPref.getToken(requireContext())
//                    ).setCallback { error, result ->
//                        if (result != null) {
//                            if (result.get("data") != null) {
//                                transactionData.amount = amount
//                                transactionData.message =
//                                    result.get("data").asJsonObject.get("message").asString
//                                bundle.putSerializable(TRANSACTION_DATA, transactionData)
//                                intent.flags =
//                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                startActivity(intent)
//                            } else if (result.has("errors")) {
//                                showToast(
//                                    requireContext(),
//                                    result["errors"].asJsonArray[0].asString,
//                                    0
//                                )
//                            }
//                        } else {
//                            Timber.d(error)
//                        }
//                    }
//                } catch (e: Exception) {
//                    showToast(requireContext(), e.message!!, Toast.LENGTH_LONG)
//                }
//            }
//            transactionType.compareTo(Constants.SEND_MONEY) == 0 -> {
//                val enterPinBottomSheet = EnterPinBottomSheet(this)
//                enterPinBottomSheet.show(
//                    (requireActivity() as AppCompatActivity).supportFragmentManager,
//                    "Enter Pin"
//                )
//
//            }
//
//            /**
//             * LOAD WALLET
//             */
//            transactionType.compareTo(Constants.LOAD_WALLET) == 0 -> {
//                binding.btnRequest.visibility = View.GONE
//                binding.progressBar.visibility = View.VISIBLE
//                // Initialize payment and merchant details
//                val merchant = Merchant(BuildConfig.MERCHANT_ID, BuildConfig.DOMAIN)
//                val random = System.currentTimeMillis().toString()
//                val payment = Payment(
//                    "${amount.toInt() * 100}",
//                    "RT${random.substring(random.length - 13, random.length)}",
//                    "MOBILE",
//                    BuildConfig.TERMINAL_ID,
//                    "CRD",
//                    "KES",
//                    "${BuildConfig.ORDER_ID_PREFIX}${random.substring(random.length - 3)}"
//                )
//                payment.preauth = "1"
//
//                //Customer object
//                val customer = Customer(userProfile.getUserId())
//                customer.email = userProfile.getUserEmail()
//
//                //MobPay config
//                val config = MobPay.Config()
//                config.iconUrl =
//                    "https://res.cloudinary.com/dz9lcxyoy/image/upload/v1630391332/Route/Logo-PNG-1_yie91q.png"
//                val mobPay = MobPay.getInstance(
//                    requireActivity(),
//                    BuildConfig.CLIENT_ID,
//                    BuildConfig.CLIENT_SECRETE,
//                    config
//                )
//                mobPay.pay(
//                    requireActivity(),
//                    merchant,
//                    payment,
//                    customer, {
//                        binding.btnRequest.visibility = View.VISIBLE
//                        binding.progressBar.visibility = View.GONE
//                        transactionData.message =
//                            "Ksh. $amount was successfully loaded to your route wallet. Transaction reference no:\t${it.transactionOrderId}. It might take 3 to 5 minutes to reflect the new balance."
//                        val intent = Intent(
//                            requireContext(),
//                            FundRequestedActivity::class.java
//                        )
//                        bundle.putSerializable(TRANSACTION_DATA, transactionData)
//                        intent.putExtras(bundle)
//                        startNewActivity(FundRequestedActivity::class.java)
//                    }, {
//                        binding.btnRequest.visibility = View.VISIBLE
//                        binding.progressBar.visibility = View.GONE
//                        showToast(requireContext(), it.message!!, 0)
//                    }
//                )
//            }
//            transactionType.compareTo(Constants.SPLIT_BILL) == 0 -> {
//                try {
//                    val intent =
//                        Intent(requireContext(), MultiContactsActivity::class.java)
//                    editor.putString(Constants.BILL_AMOUNT, amount)
//                    editor.apply()
//                    startActivity(intent)
//                } catch (ex: Exception) {
//                    Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG)
//                        .show()
//                }
//            }
//        }
//    }

    private fun formatAmount(amt: String): String {
        if (amt.toInt() in 0..1000001) {
            amount = amt
        }
        return when (amount.toInt()) {
            0 -> ""
            else -> format.format(amount.toInt())
        }
    }

    override fun onPinListener(pin: String) {
        var account = ""
        var provider = ""
        when (Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE) {
            Constants.SEND_MONEY_TO_MOBILE_MONEY -> {
                account = userProfile.getPhone().toString()
                provider = getString(R.string.mpesa_wallet)
            }
            Constants.SEND_MONEY_TO_BANK -> {
                account = SharedPref.getString(requireContext(), "bankAcNumber")!!
                provider = SharedPref.getString(requireContext(), "chosenBank")!!
            }
            Constants.SEND_MONEY_TO_ROUTE -> {
                account = SharedPref.getString(requireContext(), "walletAccountNumber")!!
                provider = getString(R.string.route_wallet)
            }
        }

        if (account.isEmpty()) {
            showToast(
                requireContext(),
                "User not registered or haven't verified their email",
                0
            )
            return
        }

        progressBar.show("Please Wait...")
        Constants.sendMoney(
            requireContext(),
            account,
            amount,
            pin,
            SharedPref.getToken(requireContext()),
            provider,
            "Payment"
        )
            .setCallback { _, result ->
                progressBar.dialog.dismiss()
                if (result.has("errors")) {
                    showToast(
                        requireContext(),
                        result["errors"].asJsonArray[0].asString,
                        0
                    )
                } else {
                    transactionData.amount = amount
                    transactionData.message =
                        result.get("data").asJsonObject.get("message").asString
                    replaceFragment(SuccessFragment(transactionData, this))
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onReasonListener(reason: String) {
        TODO("Not yet implemented")
    }

    override fun transactionComplete() {
        replaceFragment(HomeFragment())
    }
}