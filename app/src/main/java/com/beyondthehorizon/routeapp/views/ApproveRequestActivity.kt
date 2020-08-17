package com.beyondthehorizon.routeapp.views

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.bottomsheets.EnterPinBottomSheet
import com.beyondthehorizon.routeapp.bottomsheets.EnterReasonBottomSheet
import com.beyondthehorizon.routeapp.databinding.ActivityApproveRequestBinding
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.ID_NUMBER
import com.beyondthehorizon.routeapp.views.notifications.NotificationsActivity
import com.beyondthehorizon.routeapp.views.receipt.ReceiptActivity
import com.beyondthehorizon.routeapp.views.settingsactivities.SettingsActivity
import com.beyondthehorizon.routeapp.views.transactions.main.TransactionsActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.nav_bar_layout.*
import timber.log.Timber

class ApproveRequestActivity : AppCompatActivity(), EnterPinBottomSheet.EnterPinBottomSheetBottomSheetListener, EnterReasonBottomSheet.EnterReasonBottomSheetListener {
    private lateinit var binding: ActivityApproveRequestBinding
    private lateinit var pref: SharedPreferences
    private lateinit var oldIntent: Intent
    private lateinit var id: String
    private lateinit var currentUserId: String
    private lateinit var username: String
    private lateinit var phone: String
    private lateinit var reason: String
    private lateinit var amount: String
    private lateinit var status: String
    private lateinit var provider: String
    private lateinit var cancellationStatus: String
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

        pref = applicationContext.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0) // 0 - for private mode
        oldIntent = getIntent()

        intent = Intent(this, RequestConfirmedActivity::class.java)
        id = oldIntent.getStringExtra("Id")!!
        currentUserId = pref.getString(ID_NUMBER, "").toString()
        username = oldIntent.getStringExtra("Username")!!
        phone = oldIntent.getStringExtra("Phone")!!
        reason = oldIntent.getStringExtra("Reason")!!
        amount = oldIntent.getStringExtra("Amount")!!
        status = oldIntent.getStringExtra("Status")!!
        var statusIcon = oldIntent.getIntExtra("StatusIcon", R.drawable.ic_pending)
        provider = "ROUTEWALLET"
        cancellationStatus = "CANCELLED"
        var color = mapOf(
                R.drawable.ic_approved to "#16AA05",
                R.drawable.ic_rejected to "#AA4204",
                R.drawable.ic_pending to "#FF9800"
        )

        try {
            binding.txtUsername.text = username
            binding.txtUserContact.text = phone
            binding.txtReason.text = reason
            binding.txtAmount.text = amount
            binding.status.text = status
            binding.statusIcon.setImageResource(statusIcon)

            if (status != "Pending") {
                binding.btnReject.visibility = View.GONE
                binding.btnApprove.visibility = View.GONE
            }

            binding.status.setTextColor(Color.parseColor(color[statusIcon]))

        } catch (ex: Exception) {
            Timber.d(ex.message.toString())
        }

        binding.btnApprove.setOnClickListener {
            val enterPinBottomSheet = EnterPinBottomSheet()
            enterPinBottomSheet.show(supportFragmentManager, "Approve Request")
        }

        binding.btnReject.setOnClickListener {
            val enterReasonBottomSheet = EnterReasonBottomSheet()
            enterReasonBottomSheet.show(supportFragmentManager, "Reject Reason")
        }

        binding.arrowBack.setOnClickListener {
            startActivity(Intent(applicationContext, NotificationsActivity::class.java))
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
                                } else {
                                    Snackbar.make(binding.notificationsView, result.asJsonObject.get("errors").asJsonArray[0].asString, Snackbar.LENGTH_LONG).show()
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
                    } else {
                        Snackbar.make(binding.notificationsView, result.asJsonObject.get("errors").asJsonArray[0].asString, Snackbar.LENGTH_LONG).show()
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
