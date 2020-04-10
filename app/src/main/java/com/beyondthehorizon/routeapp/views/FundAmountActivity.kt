package com.beyondthehorizon.routeapp.views

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityFundAmountBinding
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.utils.CustomProgressBar
import com.interswitchgroup.mobpaylib.MobPay
import com.interswitchgroup.mobpaylib.model.*
import kotlinx.android.synthetic.main.enter_pin_transaction_pin.view.*
import kotlinx.android.synthetic.main.row_notification.*
import java.security.SecureRandom
import java.text.DecimalFormat
import java.text.NumberFormat


class FundAmountActivity : AppCompatActivity() {

    private var username = ""
    private var amount = ""
    private lateinit var parentIntent: Intent
    private lateinit var childIntent: Intent
    private lateinit var format: NumberFormat
    private lateinit var prefs: SharedPreferences
    private lateinit var binding: ActivityFundAmountBinding
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var transactionType: String
    private lateinit var cardStatus:String
    private lateinit var phone: String
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fund_amount)
        editor = getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
        prefs = getSharedPreferences(REG_APP_PREFERENCES, 0)
        parentIntent = getIntent()
        childIntent = Intent(this, ConfirmFundRequestActivity::class.java)
        transactionType = parentIntent.getStringExtra(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY)
        val token = "Bearer " + prefs.getString(USER_TOKEN, "")
        val progressBar = CustomProgressBar()
        var transactionMessage = ""

        format = DecimalFormat("#,###")
        try {

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
                    binding.txtAmount.text = ""
                } else {
                    binding.txtAmount.text = formatAmount(amount.removeRange(amount.lastIndex - 1, amount.lastIndex))
                }
            }
            if (transactionType.compareTo(REQUEST_MONEY) == 0) {
                username = prefs.getString("Username", "").toString()

                binding.requestTitle.text = "Request $username"
            } else if (transactionType.compareTo(SEND_MONEY) == 0) {
                phone = parentIntent.getStringExtra(PHONE_NUMBER)
                binding.btnRequest.text = "SEND"
                binding.requestTitle.text = "Send To ${phone}"
            } else if (transactionType.compareTo(LOAD_WALLET_FROM_CARD) == 0 || transactionType.compareTo(LOAD_WALLET_FROM_MPESA) == 0) {
                binding.btnRequest.text = "PAY"
                binding.requestTitle.text = "Enter Amount to Pay"
            } else if (transactionType.compareTo(MOBILE_TRANSACTION) == 0) {
                phone = parentIntent.getStringExtra(PHONE_NUMBER)
                binding.requestTitle.text = "Buy airtime for ${phone}"
            }

            binding.btnRequest.setOnClickListener {
                val lower = 100000000
                val upper = 999999999
                val secureRandom = SecureRandom()
                var merchantId = "ROUTEK0001"
                var domain = "ISWKE"
                var amount = amount
                var transactionRef = ((Math.random() * (upper - lower)).toInt() + lower).toString()
                var terminalId = "3TLP0001"
                var currency = "KES"
                var orderId = "ROUTE_TZD_${secureRandom.nextInt(10000)}"
                var preauth = "1"
                var customerId = prefs.getString(USER_ID, "")
                var customerEmail = prefs.getString(USER_EMAIL, "")
                var clientId = "IKIAF9CED95CD2EA93B367E5E1B580A1EDB06F9EEF6D"
                var clientSecret = "g9n6CRhxzmADCz5H9IaxtmfFxfFh+jGVFCVae4+1Kko="


                var merchant = Merchant(merchantId, domain);
                var payment = Payment(amount, transactionRef, "MOBILE", terminalId, "CRD", currency, orderId)
                payment.setPreauth(preauth)
                var customer = Customer(customerId)
                customer.setEmail(customerEmail)

                if (binding.txtAmount.text.isNullOrEmpty()) {
                    Toast.makeText(this, "Please enter amount to request", Toast.LENGTH_LONG).show()
                } else if (amount.toInt() <= 0) {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_LONG).show()
                }

                /**HERE NOW*/
                else if (transactionType.compareTo(REQUEST_MONEY) == 0) {
                    startActivity(childIntent)
                } else if (transactionType.compareTo(SEND_MONEY) == 0) {
                    showSendMoneyDialog()
                } else if (transactionType.compareTo(LOAD_WALLET_FROM_CARD) == 0) {
                    var country = parentIntent.getStringExtra(COUNTRY)
                    var cardNumber = parentIntent.getStringExtra(CARD_NUMBER)
                    var expDate = parentIntent.getStringExtra(EXPIRY_DATE)
                    var expYear = expDate.substring(3, 5)
                    var expMonth = expDate.substring(0, 2)
                    var cvvNumber = parentIntent.getStringExtra(CVV_NUMBER)
                    cardStatus = parentIntent.getStringExtra(CARD_STATUS)

                    try {
                        var card = Card(cardNumber, cvvNumber, expYear, expMonth);
                        lateinit var mobPay: MobPay;

                        var config = MobPay.Config();
                        mobPay = MobPay.getInstance(this@FundAmountActivity, clientId, clientSecret, config)

                        progressBar.show(this, "Interswitch payment processing ...")
                        mobPay.makeCardPayment(
                                card,
                                merchant,
                                payment,
                                customer, {
                            progressBar.dialog.dismiss()
                            Log.d("INTERSWITCH_MESSAGE", it.transactionOrderId)

                            if(cardStatus.compareTo(NEW_CARD) == 0) {
                                progressBar.show(this, "Updating route ...")
                                addPaymentCard(this, cardNumber, expDate, cvvNumber, country, token)
                                        .setCallback { e, result ->
                                            progressBar.dialog.dismiss()
                                            try {
                                                if (result != null) {
                                                    if (result.has("errors")) {
                                                        var error = result.get("errors").asJsonObject.get("card_number").asJsonArray.get(0).asString
                                                        Toast.makeText(this@FundAmountActivity, error, Toast.LENGTH_LONG).show()
                                                    } else {
                                                        transactionMessage = result.get("data").asJsonObject.get("message").asString
                                                        Toast.makeText(this@FundAmountActivity, transactionMessage, Toast.LENGTH_LONG).show()
                                                    }

                                                } else if (e != null) {
                                                    Log.d("INTERSWITCH_MESSAGE", e.toString())
                                                    Toast.makeText(this@FundAmountActivity, e.message, Toast.LENGTH_LONG).show()
                                                }
                                            } catch (ex: Exception) {
                                                Log.d("INTERSWITCH_MESSAGE", ex.message)
                                            }
                                        }
                            }
                            transactionMessage = "Ksh. ${amount} was successfully loaded to your route wallet  from card number ${cardNumber}. Transaction reference no:\t${it.transactionOrderId}"
                            val intent = Intent(this, FundRequestedActivity::class.java)
                            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, "")
                            editor.apply()
                            intent.putExtra("Message", transactionMessage)
                            intent.putExtra(ACTIVITY_TYPE, ADD_MONEY_ACTIVITY)
                            startActivity(intent)

                        }, {
                            progressBar.dialog.dismiss()
                            Log.d("INTERSWITCH_MESSAGE", it.message)
                            Toast.makeText(this@FundAmountActivity, it.message, Toast.LENGTH_LONG).show()
                        });
                    } catch (e: Exception) {
                        Log.d("INTERSWITCH_MESSAGE", e.message)
                        Toast.makeText(this@FundAmountActivity, e.message, Toast.LENGTH_LONG).show();
                    }

                } else if (transactionType.compareTo(MOBILE_TRANSACTION) == 0) {
                    try {
                        var mobileNumber = parentIntent.getStringExtra(PHONE_NUMBER)
                        var mobile = Mobile(mobileNumber, Mobile.Type.MPESA)
                        var mobPay: MobPay
                        mobPay = MobPay.getInstance(this@FundAmountActivity, clientId, clientSecret, null)
                        progressBar.show(this, "Processing payment...")
                        mobPay.makeMobileMoneyPayment(
                                mobile,
                                merchant,
                                payment,
                                customer, {
                            progressBar.dialog.dismiss()
                            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, "")
                            editor.apply()
                            transactionMessage = "Ksh. ${amount} was successfully loaded to your route wallet  from mobile number ${mobileNumber}. Transaction reference no:\t${it.transactionOrderId}"
                            val intent = Intent(this, FundRequestedActivity::class.java)
                            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, "")
                            editor.apply()
                            intent.putExtra("Message", transactionMessage)
                            intent.putExtra(ACTIVITY_TYPE, ADD_MONEY_ACTIVITY)
                            startActivity(intent)
                        }, {
                            progressBar.dialog.dismiss()
                            Log.d("INTERSWITCH_MESSAGE", it.toString())
                            Toast.makeText(this@FundAmountActivity, it.message, Toast.LENGTH_LONG).show()

                        })
                    }
                catch (e: Exception) {
                        Toast.makeText(this@FundAmountActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            binding.arrowBack.setOnClickListener {
                onBackPressed()
            }
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }

    }

    private fun formatAmount(amt: String): String {
        amount = amt
        return format.format(amt.toInt())
    }

    private fun showSendMoneyDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView = LayoutInflater.from(this).inflate(R.layout.enter_pin_transaction_pin, viewGroup, false)
        //Now we need an AlertDialog.Builder object
        val builder = AlertDialog.Builder(this)
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)
        //finally creating the alert dialog and displaying it
        val alertDialog = builder.create()
        alertDialog.show()

        // val pin: String = enterPin.text.toString()
        var account = ""
        var provider = ""

        dialogView.dialogButtonPin.setOnClickListener {

            val pin: String = dialogView.enterPin.text.toString()
            if (pin.isEmpty()) {
                Toast.makeText(this@FundAmountActivity, "Enter pin", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            when {
                transactionType.compareTo(SEND_MONEY_TO_MOBILE_MONEY) == 0 -> {
                    account = parentIntent.getStringExtra(PHONE_NUMBER)
                    provider = "MPESA WALLET"
                }
                prefs.getString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "").toString().compareTo(SEND_MONEY_TO_BANK) == 0 -> {
                    account = prefs.getString("bankAcNumber", "").toString()
                    provider = prefs.getString("chosenBank", "").toString()
                }
                prefs.getString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "").toString().compareTo(SEND_MONEY_TO_ROUTE) == 0 -> {
                    account = prefs.getString("walletAccountNumber", "").toString()
                    provider = "ROUTEWALLET"
                }
            }

            Log.e("FundAmountActivity", "$account P $provider")
            if (account.isEmpty()) {
                Toast.makeText(this@FundAmountActivity, "User not registered or haven't verified their email", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val progressBar = CustomProgressBar()
            progressBar.show(this, "Please Wait...")
            sendMoney(this@FundAmountActivity, account, amount, pin, token, provider, "Payment")
                    .setCallback { e, result ->
                        Log.e("FundAmountActivity", result.toString())
                        progressBar.dialog.dismiss()
                        if (result.has("errors")) {
                            Toast.makeText(this@FundAmountActivity, result.get("errors").asJsonObject.toString(), Toast.LENGTH_LONG).show()
                        } else {
                            editor.putString("Amount", amount)
                            editor.apply()
                            val message = result.get("data").asJsonObject.get("message").asString
                            val intent = Intent(this, FundRequestedActivity::class.java)
                            intent.putExtra("Message", message)
                            startActivity(intent)
                            alertDialog.dismiss()
                            finish()
                        }
                    }
        }
    }
}
