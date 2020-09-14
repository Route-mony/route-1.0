package com.beyondthehorizon.routeapp.views

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.BuildConfig
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.bottomsheets.EnterPinBottomSheet
import com.beyondthehorizon.routeapp.databinding.ActivityFundAmountBinding
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.utils.CustomProgressBar
import com.beyondthehorizon.routeapp.utils.Utils
import com.beyondthehorizon.routeapp.views.multicontactschoice.MultiContactsActivity
import com.beyondthehorizon.routeapp.views.receipt.ReceiptActivity
import com.beyondthehorizon.routeapp.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.routeapp.views.transactions.main.TransactionsActivity
import com.interswitchgroup.mobpaylib.MobPay
import com.interswitchgroup.mobpaylib.model.*
import kotlinx.android.synthetic.main.activity_fund_amount.*
import kotlinx.android.synthetic.main.nav_bar_layout.*
import timber.log.Timber
import java.security.SecureRandom
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.properties.Delegates


class FundAmountActivity : AppCompatActivity(), EnterPinBottomSheet.EnterPinBottomSheetBottomSheetListener {

    private var username = ""
    private var amount = ""
    private lateinit var parentIntent: Intent
    private lateinit var childIntent: Intent
    private lateinit var format: DecimalFormat
    private lateinit var prefs: SharedPreferences
    private lateinit var binding: ActivityFundAmountBinding
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var transactionType: String
    private lateinit var cardStatus: String
    private lateinit var phone: String
    private lateinit var token: String
    private lateinit var util: Utils
    private var lower by Delegates.notNull<Int>()
    private var upper by Delegates.notNull<Int>()
    private lateinit var secureRandom: SecureRandom
    private lateinit var merchantId: String
    private lateinit var domain: String
    private lateinit var transactionRef: String
    private lateinit var terminalId: String
    private lateinit var currency: String
    private lateinit var walletAccount: String
    private lateinit var orderId: String
    private lateinit var preauth: String
    private lateinit var customerId: String
    private lateinit var customerEmail: String
    private lateinit var clientId: String
    private lateinit var clientSecret: String
    private lateinit var merchant: Merchant
    private lateinit var payment: Payment
    private lateinit var customer: Customer

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fund_amount)
        util = Utils(this)
        val config = MobPay.Config();

        btn_home.setOnClickListener {
            val intent = Intent(this@FundAmountActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@FundAmountActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@FundAmountActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this@FundAmountActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        editor = getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
        prefs = getSharedPreferences(REG_APP_PREFERENCES, 0)
        parentIntent = intent
        childIntent = Intent(this, ConfirmFundRequestActivity::class.java)
//        transactionType = parentIntent.getStringExtra(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY)
        transactionType = prefs.getString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, "").toString()
        token = "Bearer " + prefs.getString(USER_TOKEN, "")
        val progressBar = CustomProgressBar()
        var transactionMessage = ""

        //Format currency
        val locale = Locale("en", "KE")
        val symbol = Currency.getInstance(locale).getSymbol(locale)
        format = NumberFormat.getCurrencyInstance(locale) as DecimalFormat
        format.isGroupingUsed = true
        format.positivePrefix = "$symbol "
        format.maximumFractionDigits = 0

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
            startActivity(Intent(this, BulkRequestActivity::class.java))
        }
        try {
            if (transactionType.compareTo(REQUEST_MONEY) == 0) {
                binding.requestLayout.visibility = View.VISIBLE
                username = prefs.getString("Username", "").toString()
                binding.requestTitle.text = username
                binding.requestType.text = "Request : "

            } else if (transactionType.compareTo(SEND_MONEY) == 0) {
                if (prefs.getString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "").toString().compareTo(SEND_MONEY_TO_MOBILE_MONEY) == 0) {
                    phone = parentIntent.getStringExtra(PHONE_NUMBER).toString()
                    binding.btnRequest.text = "SEND"
                    binding.requestTitle.text = " $phone"
                    binding.requestType.text = "Send To : "

                } else if (prefs.getString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "").toString().compareTo(SEND_MONEY_TO_ROUTE) == 0) {
                    username = prefs.getString("Username", "").toString()
                    binding.btnRequest.text = "SEND"
                    binding.requestTitle.text = username
                    binding.requestType.text = "Send To : "

                } else if (prefs.getString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "").toString().compareTo(SEND_MONEY_TO_BANK) == 0) {
                    username = prefs.getString("chosenBank", "").toString() + "\nAccount No: " + prefs.getString("bankAcNumber", "").toString()
                    binding.btnRequest.text = "SEND"
                    binding.requestTitle.text = username
                    binding.requestType.text = "Send To : "

                }
            } else if (transactionType.compareTo(LOAD_WALLET_FROM_CARD) == 0 || transactionType.compareTo(LOAD_WALLET_FROM_MPESA) == 0) {
                binding.btnRequest.text = "LOAD"
                binding.requestTitle.text = "Enter Amount to load"
                binding.requestType.visibility = View.GONE
                lower = 100000000
                upper = 999999999
                secureRandom = SecureRandom()
                merchantId = BuildConfig.MERCHANT_ID
                domain = BuildConfig.DOMAIN
                transactionRef = ((Math.random() * (upper - lower)).toInt() + lower).toString()
                terminalId = BuildConfig.TERMINAL_ID
                currency = "KES"
                walletAccount = prefs.getString(WALLET_ACCOUNT, "").toString()
                orderId = walletAccount
                preauth = "1"
                customerId = prefs.getString(USER_ID, "").toString()
                customerEmail = prefs.getString(USER_EMAIL, "").toString()
                clientId = BuildConfig.CLIENT_ID
                clientSecret = BuildConfig.CLIENT_SECRETE
                merchant = Merchant(merchantId, domain);
                customer = Customer(customerId)
                customer.email = customerEmail
            } else if (transactionType.compareTo(MOBILE_TRANSACTION) == 0) {
                phone = parentIntent.getStringExtra(PHONE_NUMBER).toString()
                binding.requestTitle.text = phone
                binding.requestType.text = "Pay From : "
                binding.btnRequest.text = "PAY"
            } else if (transactionType.compareTo(BUY_AIRTIME) == 0) {
                phone = parentIntent.getStringExtra(PHONE_NUMBER).toString()
                request_title.text = phone
                requestType.text = "Buy For: "
                btn_request.text = "Buy Airtime"
            } else if (transactionType.compareTo(SPLIT_BILL) == 0) {
                requestType.text = "Enter bill amount "
                btn_request.text = "Continue"
            }
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
        binding.btnRequest.setOnClickListener {
            try {
                when {
                    binding.txtAmount.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter amount", Toast.LENGTH_LONG).show()
                    }
                    amount.toInt() <= 0 -> {
                        Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_LONG).show()
                    }

                    /**HERE NOW*/
                    transactionType.compareTo(REQUEST_MONEY) == 0 -> {
                        editor.putString("Amount", amount)
                        editor.apply()
                        if (binding.requestNarration.text.toString().trim().isEmpty()) {
                            Toast.makeText(this, "Please enter your reason", Toast.LENGTH_LONG).show();
                            return@setOnClickListener
                        }
                        val userId = prefs.getString("Id", "").toString()
                        val token = "Bearer " + prefs.getString(USER_TOKEN, "")
                        val intent = Intent(this, FundRequestedActivity::class.java)
                        requestFund(this, userId, amount, binding.requestNarration.text.toString().trim(), token).setCallback { e, result ->
                            if (result.get("data") != null) {
                                val message = result.get("data").asJsonObject.get("message").asString
                                intent.putExtra("Message", message)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else if (result.has("errors")) {
                                Toast.makeText(this@FundAmountActivity, result["errors"].asJsonArray[0].asString, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    transactionType.compareTo(SEND_MONEY) == 0 -> {
                        val enterPinBottomSheet = EnterPinBottomSheet()
                        enterPinBottomSheet.show(supportFragmentManager, "Enter Pin")

                    }

                    /**
                     * LOAD WALLET FROM DEBIT CARD
                     */
                    transactionType.compareTo(LOAD_WALLET_FROM_CARD) == 0 -> {
                        try {
                            payment = Payment("${amount.toInt() * 100}", transactionRef, "MOBILE", terminalId, "CRD", currency, orderId)
                            payment.preauth = preauth
                            val country = parentIntent.getStringExtra(COUNTRY)
                            val cardNumber = parentIntent.getStringExtra(CARD_NUMBER)
                            val expDate = parentIntent.getStringExtra(EXPIRY_DATE)
                            val expYear = expDate!!.substring(3, 5)
                            val expMonth = expDate.substring(0, 2)
                            val cvvNumber = parentIntent.getStringExtra(CVV_NUMBER)
                            cardStatus = parentIntent.getStringExtra(CARD_STATUS).toString()
                            val card = Card(cardNumber, cvvNumber, expYear, expMonth)
                            val mobPay = MobPay.getInstance(this@FundAmountActivity, clientId, clientSecret, config)
                            progressBar.show(this, "Processing payment...")
                            mobPay.makeCardPayment(
                                    card,
                                    merchant,
                                    payment,
                                    customer, {
                                Timber.d(it.transactionOrderId)
                                if (cardStatus.compareTo(NEW_CARD) == 0) {
                                    progressBar.show(this, "Updating route ...")
                                    addPaymentCard(this, cardNumber, expDate, cvvNumber, country, token)
                                            .setCallback { e, result ->
                                                progressBar.dialog.dismiss()
                                                try {
                                                    if (result != null) {
                                                        if (result.has("errors")) {
                                                            Toast.makeText(this@FundAmountActivity, result["errors"].asJsonArray[0].asString, Toast.LENGTH_LONG).show()
                                                        } else {
                                                            transactionMessage = result.get("data").asJsonObject.get("message").asString
                                                            Toast.makeText(this@FundAmountActivity, transactionMessage, Toast.LENGTH_LONG).show()
                                                        }

                                                    } else if (e != null) {
                                                        Timber.d(e.toString())
                                                        Toast.makeText(this@FundAmountActivity, e.message, Toast.LENGTH_LONG).show()
                                                    }
                                                } catch (ex: Exception) {
                                                    Timber.d(ex.message.toString())
                                                }
                                            }
                                }
                                //Load wallet balance from ISW
                                util.loadWalletBalance(token)

                                transactionMessage = "Ksh. ${amount} was successfully loaded to your route wallet  from card number ${cardNumber}. Transaction reference no:\t${it.transactionOrderId}. It might take 3 to 5 minutes to reflect the new balance."
                                val intent = Intent(this, FundRequestedActivity::class.java)
                                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, "")
                                editor.apply()
                                intent.putExtra("Message", transactionMessage)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra(ACTIVITY_TYPE, ADD_MONEY_ACTIVITY)
                                startActivity(intent)

                            }, {
                                progressBar.dialog.dismiss()
                                Timber.d(it.message.toString())
                                Toast.makeText(this@FundAmountActivity, it.message, Toast.LENGTH_LONG).show()
                            });
                        } catch (e: Exception) {
                            progressBar.dialog.dismiss()
                            Toast.makeText(this@FundAmountActivity, e.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    /**
                     * LOAD WALLET FROM MPESA
                     */
                    transactionType.compareTo(LOAD_WALLET_FROM_MPESA) == 0 -> {
                        try {
                            payment = Payment("${amount.toInt() * 100}", transactionRef, "MOBILE", terminalId, "CRD", currency, orderId)
                            payment.preauth = preauth
                            var mobileNumber = parentIntent.getStringExtra(PHONE_NUMBER)
                            var mobile = Mobile(mobileNumber, Mobile.Type.MPESA)
                            val merchant = Merchant(merchantId, domain);
                            val payment = Payment("${amount.toInt() * 100}", transactionRef, "MOBILE", terminalId, "MMO", currency, orderId)
                            payment.setPreauth(preauth)
                            var mobPay: MobPay = MobPay.getInstance(this@FundAmountActivity, clientId, clientSecret, null)
                            progressBar.show(this, "Processing payment...")
                            mobPay.makeMobileMoneyPayment(
                                    mobile,
                                    merchant,
                                    payment,
                                    customer, {
                                progressBar.dialog.dismiss()

                                //Load wallet balance from ISW
                                util.loadWalletBalance(token)

                                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, "")
                                editor.apply()
                                transactionMessage = "Ksh. $amount was successfully loaded to your route wallet  from mobile number ${mobileNumber}. Transaction reference no:\t${it.transactionOrderId}. It might take 3 to 5 minutes to reflect the new balance."
                                val intent = Intent(this, FundRequestedActivity::class.java)
                                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, "")
                                editor.apply()
                                intent.putExtra("Message", transactionMessage)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra(ACTIVITY_TYPE, ADD_MONEY_ACTIVITY)
                                startActivity(intent)
                            }, {
                                progressBar.dialog.dismiss()
                                Log.d("INTERSWITCH_MESSAGE", it.toString())
                                Toast.makeText(this@FundAmountActivity, it.message, Toast.LENGTH_LONG).show()

                            })
                        } catch (e: Exception) {
                            progressBar.dialog.dismiss()
                            Toast.makeText(this@FundAmountActivity, e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                    transactionType.compareTo(BUY_AIRTIME) == 0 -> {
                        try {
                            val intent = Intent(this, FundRequestedActivity::class.java)
                            val phone = parentIntent.getStringExtra(PHONE_NUMBER)
                            //TODO: Call the api to purchase airtime with the required parameters

                            //Dummy setup
                            val transactionMessage = "You have successfully purchased Ksh. $amount of airtime for $phone"
                            intent.putExtra("Message", transactionMessage)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra(ACTIVITY_TYPE, BUY_AIRTIME_ACTIVITY)
                            startActivity(intent)
                        } catch (ex: Exception) {
                            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
                        }
                    }
                    transactionType.compareTo(SPLIT_BILL) == 0 -> {
                        try {
                            val intent = Intent(this, MultiContactsActivity::class.java)
                            editor.putString(BILL_AMOUNT, amount)
                            editor.apply()
                            startActivity(intent)
                        } catch (ex: Exception) {
                            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (ex: Exception) {
                Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
            }
        }

        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun formatAmount(amt: String): String {
        if (amt.length <= 7) {
            amount = amt
        }
        return format.format(amount.toInt())
    }

    override fun enterPinDialog(pin: String) {
        var account = ""
        var provider = ""
        when {
            prefs.getString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "").toString().compareTo(SEND_MONEY_TO_MOBILE_MONEY) == 0 -> {
//                    account = parentIntent.getStringExtra(PHONE_NUMBER)
                account = prefs.getString(PHONE_NUMBER, "").toString()
                provider = "MPESA WALLET"
            }
            prefs.getString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "").toString().compareTo(SEND_MONEY_TO_BANK) == 0 -> {
                account = prefs.getString("bankAcNumber", "").toString()
                provider = prefs.getString("chosenBank", "").toString()
            }
            prefs.getString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "").toString().compareTo(SEND_MONEY_TO_ROUTE) == 0 -> {
                account = prefs.getString("walletAccountNumber", "")!!
                provider = "ROUTEWALLET"
            }
        }

        if (account.isEmpty()) {
            Toast.makeText(this@FundAmountActivity, "User not registered or haven't verified their email", Toast.LENGTH_LONG).show()
            return
        }

        val progressBar = CustomProgressBar()
        progressBar.show(this, "Please Wait...")
        sendMoney(this@FundAmountActivity, account, amount, pin, token, provider, "Payment")
                .setCallback { e, result ->
                    Timber.e(result.toString())
                    progressBar.dialog.dismiss()
                    if (result.has("errors")) {
                        Toast.makeText(this@FundAmountActivity, result["errors"].asJsonArray[0].asString, Toast.LENGTH_LONG).show()
                    } else {
                        //Load wallet balance from ISW
                        util.loadWalletBalance(token)

                        editor.putString("Amount", amount)
                        editor.apply()
                        val message = result.get("data").asJsonObject.get("message").asString
                        val intent = Intent(this, FundRequestedActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("Message", message)
                        startActivity(intent)
                        finish()
                    }
                }
    }

    companion object {
        private val TAG = this.javaClass.simpleName
    }
}
