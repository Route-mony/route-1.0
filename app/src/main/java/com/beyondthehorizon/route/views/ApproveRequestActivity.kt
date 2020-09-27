package com.beyondthehorizon.route.views

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.bottomsheets.EnterPinBottomSheet
import com.beyondthehorizon.route.bottomsheets.EnterReasonBottomSheet
import com.beyondthehorizon.route.databinding.ActivityApproveRequestBinding
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.Constants.ID_NUMBER
import com.beyondthehorizon.route.views.notifications.NotificationsActivity
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_approve_request.*
import kotlinx.android.synthetic.main.nav_bar_layout.*
import timber.log.Timber
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ApproveRequestActivity : AppCompatActivity(), EnterPinBottomSheet.EnterPinBottomSheetBottomSheetListener, EnterReasonBottomSheet.EnterReasonBottomSheetListener {
    private lateinit var binding: ActivityApproveRequestBinding
    private lateinit var format: DecimalFormat
    private lateinit var locale: Locale
    private lateinit var symbol: String
    private lateinit var pref: SharedPreferences
    private lateinit var oldIntent: Intent
    private lateinit var id: String
    private lateinit var currentUserId: String
    private lateinit var username: String
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var phone: String
    private lateinit var reason: String
    private lateinit var amount: String
    private lateinit var status: String
    private lateinit var provider: String
    private lateinit var cancellationStatus: String
    private lateinit var date: String
    private lateinit var imageUrl: String
    private lateinit var requestType: String
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_approve_request)
        progressDialog = ProgressDialog(this)

        btn_home.setOnClickListener {
            val intent = Intent(this@ApproveRequestActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        btn_transactions.setOnClickListener {
            val intent = Intent(this@ApproveRequestActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@ApproveRequestActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this@ApproveRequestActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        arrow_back.setOnClickListener {
            val intent = Intent(this@ApproveRequestActivity, NotificationsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_remind.setOnClickListener {
            var intent = Intent(this, RequestConfirmedActivity::class.java)
            intent.putExtra("Message", "We have successfully sent a reminder to $username for the request of Ksh. $amount for $reason.")
            startActivity(intent)
        }

        pref = applicationContext.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0) // 0 - for private mode
        oldIntent = getIntent()

        //Format currency
        locale = Locale("en", "KE")
        symbol = Currency.getInstance(locale).getSymbol(locale)
        format = NumberFormat.getCurrencyInstance(locale) as DecimalFormat
        format.isGroupingUsed = true
        format.positivePrefix = "$symbol "
        format.maximumFractionDigits = 0
        intent = Intent(this, RequestConfirmedActivity::class.java)
        id = oldIntent.getStringExtra("Id")!!
        currentUserId = pref.getString(ID_NUMBER, "").toString()
        firstName = oldIntent.getStringExtra("FirstName")!!
        lastName = oldIntent.getStringExtra("LastName")!!
        username = oldIntent.getStringExtra("Username")!!
        phone = oldIntent.getStringExtra("Phone")!!
        reason = oldIntent.getStringExtra("Reason")!!
        amount = oldIntent.getStringExtra("Amount")!!
        status = oldIntent.getStringExtra("Status")!!
        date = oldIntent.getStringExtra("Date")!!
        imageUrl = oldIntent.getStringExtra("Avatar")!!
        requestType = oldIntent.getStringExtra("RequestType")!!
        var statusIcon = oldIntent.getIntExtra("StatusIcon", R.drawable.ic_pending)
        provider = "ROUTEWALLET"
        cancellationStatus = "CANCELLED"
        var color = mapOf(
                R.drawable.ic_approved to "#16AA05",
                R.drawable.ic_rejected to "#AA4204",
                R.drawable.ic_pending to "#FF9800"
        )

        try {
            val paymentAmount = format.format(amount.toDouble().toInt())
            binding.txtUsername.text = String.format("%s %s", firstName, lastName)
            binding.txtReason.text = reason
            binding.txtPaymentBy.text = String.format("%s %s", "Payment by", username)
            binding.txtAmount.text = paymentAmount
            binding.status.text = status
            binding.txtDate.text = date

            when {
                status.toLowerCase() == "pending" -> {
                    binding.status.setBackgroundResource(R.drawable.round_button_pending)
                }
                status.toLowerCase() == "done" -> {
                    binding.status.setBackgroundResource(R.drawable.round_button_green)
                }
                else -> {
                    binding.status.setBackgroundResource(R.drawable.round_button_danger)
                }
            }

            when (requestType.toLowerCase()) {
                "sent" -> {
                    if (status.toLowerCase() == "pending") {
                        binding.requestTitle.text = resources.getString(R.string.send_a_reminder)
                        binding.llSent.visibility = View.VISIBLE
                        binding.llReceived.visibility = View.GONE
                    } else {
                        binding.llSent.visibility = View.GONE
                        binding.llReceived.visibility = View.GONE
                    }
                }
                "received" -> {
                    binding.llSent.visibility = View.GONE
                    if (status.toLowerCase() != "pending") {
                        binding.llReceived.visibility = View.GONE
                    }
                }
            }

            binding.status.setTextColor(Color.parseColor(color[statusIcon]))

            var requestOptions = RequestOptions();
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
            if (imageUrl.isEmpty()) {
                Glide.with(this@ApproveRequestActivity)
                        .load(R.drawable.default_avatar)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .error(R.drawable.ic_user_home_page)
                        .placeholder(R.drawable.ic_user_home_page)
                        .apply(requestOptions)
                        .into(img_avatar)
                return
            }
            Glide.with(this@ApproveRequestActivity)
                    .load(imageUrl)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.ic_user_home_page)
                    .placeholder(R.drawable.ic_user_home_page)
                    .apply(requestOptions)
                    .into(img_avatar)

        } catch (ex: Exception) {
            Timber.d(ex.message.toString())
            Log.d("ERROR", ex.message)
        }

        binding.btnApprove.setOnClickListener {
            val enterPinBottomSheet = EnterPinBottomSheet()
            enterPinBottomSheet.show(supportFragmentManager, "Approve Request")
        }

        binding.btnReject.setOnClickListener {
            val enterReasonBottomSheet = EnterReasonBottomSheet()
            enterReasonBottomSheet.show(supportFragmentManager, "Reject Reason")
        }
    }

    override fun enterPinDialog(pin: String) {
        try {
            val token = "Bearer " + pref.getString(Constants.USER_TOKEN, "")
            progressDialog.setMessage("please wait...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
            Constants.verifyPin(this, pin, token).setCallback { e, result ->
                progressDialog.dismiss()
                if (result != null) {
                    if (result.asJsonObject.get("status").asString == "success") {
                        Constants.approveFundRequests(this, id, pin, reason, provider, token).setCallback { e, result ->
                            if (result != null) {
                                if (result.asJsonObject.get("status").asString == "success") {
                                    intent.putExtra("Message", "Your approval for $username request of Ksh. $amount for $reason is being processed. You will be notified after the processing is done.")
                                    startActivity(intent)
                                } else if (result.has("errors")) {
                                    Toast.makeText(this@ApproveRequestActivity, result["errors"].asJsonArray[0].asString, Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Snackbar.make(binding.notificationsView, "Unable to process your request, please try again later", Snackbar.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Snackbar.make(binding.notificationsView, "Incorrect pin provided", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        } catch (e: Exception) {
            Timber.d(e.message.toString())
        }
    }

    override fun enterReasonDialog(reason: String) {
        try {
            val token = "Bearer " + pref.getString(Constants.USER_TOKEN, "")
            progressDialog.show()
            Constants.rejectFundRequests(this, id, currentUserId, cancellationStatus, reason, token).setCallback { e, result ->
                progressDialog.dismiss()
                if (result != null) {
                    if (result.asJsonObject.get("status").asString == "success") {
                        intent.putExtra("Message", "You have rejected $username request of Ksh. $amount for $reason")
                        startActivity(intent)
                    } else if (result.has("errors")) {
                        Snackbar.make(binding.notificationsView, result.get("errors").asJsonArray[0].asString, Snackbar.LENGTH_LONG).show()
                    }
                } else {
                    Snackbar.make(binding.notificationsView, "An error has occurred, please try again later", Snackbar.LENGTH_LONG).show()
                }

            }
        } catch (e: Exception) {
            Timber.d(e.message.toString())
        }
    }
}
