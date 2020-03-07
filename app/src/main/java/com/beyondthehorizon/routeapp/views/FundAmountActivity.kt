package com.beyondthehorizon.routeapp.views

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityFundAmountBinding
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.utils.CustomProgressBar
import kotlinx.android.synthetic.main.enter_pin_transaction_pin.*
import kotlinx.android.synthetic.main.enter_pin_transaction_pin.view.*
import kotlinx.android.synthetic.main.send_money_to_bank_dialog_layout.*
import java.text.DecimalFormat
import java.text.NumberFormat


class FundAmountActivity : AppCompatActivity() {

    private var username = ""
    private var amount = ""
    private lateinit var format: NumberFormat
    lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding: ActivityFundAmountBinding = DataBindingUtil.setContentView(this, R.layout.activity_fund_amount)
        var editor: SharedPreferences.Editor = getSharedPreferences(REG_APP_PREFERENCES, 0).edit()
        var prefs = getSharedPreferences(REG_APP_PREFERENCES, 0)
        var intent = Intent(this, ConfirmFundRequestActivity::class.java)
        var transactionType = prefs.getString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, "")
        pref = getSharedPreferences(REG_APP_PREFERENCES, 0)

        format = DecimalFormat("#,###")
        try {
            username = prefs.getString("Username", "").toString()
            binding.requestUserName.text = username

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

            if (transactionType!!.compareTo(SEND_MONEY) == 0) {
                binding.btnRequest.text = "SEND"
                binding.requestTitle.text = "Send Money To"
            } else if (transactionType!!.compareTo(LOAD_WALLET_FROM_CARD) == 0) {
                binding.btnRequest.text = "PAY"
                binding.requestTitle.text = "Enter Amount to Pay"
            }
            else if (transactionType!!.compareTo(LOAD_WALLET_FROM_CARD) == 0) {
                binding.btnRequest.text = "PAY"
                binding.requestTitle.text = "Enter Amount to Pay"
            }

            binding.btnRequest.setOnClickListener {
                if (binding.txtAmount.text.isNullOrEmpty()) {
                    Toast.makeText(this, "Please enter amount to request", Toast.LENGTH_LONG).show()
                } else if (amount.toInt() <= 0) {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_LONG).show()
                }

                /**HERE NOW*/
                else if (transactionType!!.compareTo(REQUEST_MONEY) == 0) {

                    editor.putString("Amount", amount)
                    editor.apply()
                    startActivity(intent)
                } else if (transactionType!!.compareTo(SEND_MONEY) == 0) {
                    showSendMoneyDialog()
                }
                else if(transactionType!!.compareTo(LOAD_WALLET_FROM_CARD) == 0){
                    var cardNumber = prefs.getString(CARD_NUMBER, "")
                    var expiryDate = prefs.getString(EXPIRY_DATE, "")
                    var cvvNumber = prefs.getString(CVV_NUMBER, "")
                    var country = prefs.getString(COUNTRY, "")

                }
                else if(transactionType!!.compareTo(LOAD_WALLET_FROM_MPESA) == 0){
                    var mobileNumber = prefs.getString(PHONE_NUMBER, "")
                }
            }

            binding.arrowBack.setOnClickListener {
                startActivity(Intent(this, RequestFundsActivity::class.java))

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

//        val pin: String = enterPin.text.toString()

        val prefs = getSharedPreferences(REG_APP_PREFERENCES, 0)
        val token = "Bearer " + prefs.getString(USER_TOKEN, "")
        var account = ""
        var provider = ""

        dialogView.dialogButtonPin.setOnClickListener {

            val pin: String = dialogView.enterPin.text.toString()
            if (pin.isEmpty()) {
                Toast.makeText(this@FundAmountActivity, "Enter pin", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            when {
                prefs.getString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, "").toString().compareTo(SEND_MONEY_TO_MOBILE_MONEY) == 0 -> {
                    account = prefs.getString("Phone", "").toString()
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
