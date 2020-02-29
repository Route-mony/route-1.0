package com.beyondthehorizon.routeapp.views

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityApproveRequestBinding
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.Constants.ID_NUMBER
import com.beyondthehorizon.routeapp.utils.Constants.TRANSACTIONS_PIN
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_confirm_pin.view.*
import kotlinx.android.synthetic.main.row_notification.view.*
import java.lang.Exception

class ApproveRequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityApproveRequestBinding
    private lateinit var confirmationPin: String
    private lateinit var cancellation_reason: String
    private lateinit var cancellation_status: String
    private lateinit var pref: SharedPreferences
    private lateinit var oldIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_approve_request)
        pref = applicationContext.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0) // 0 - for private mode
        oldIntent = getIntent()

        var intent = Intent(this, RequestConfirmedActivity::class.java)
        var id = oldIntent.getStringExtra("Id")
        var current_user_id = pref.getString(ID_NUMBER, "")
        var username = oldIntent.getStringExtra("Username")
        var phone = oldIntent.getStringExtra("Phone")
        var reason = oldIntent.getStringExtra("Reason")
        var amount = oldIntent.getStringExtra("Amount")
        var status = oldIntent.getStringExtra("Status")
        var statusIcon = oldIntent.getIntExtra("StatusIcon", R.drawable.ic_pending)
        var provider = "ROUTEWALLET"
        var cancellation_status = "CANCELLED"
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

            binding.status.setTextColor(Color.parseColor(color[statusIcon]))

        } catch (ex: Exception) {
            Log.d("TAG", ex.message)
        }

        binding.btnApprove.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_confirm_pin, null)
            val mBuilder = AlertDialog.Builder(this)
                    .setView(mDialogView)
                    .setTitle("Confirm your secret PIN")

            //show dialog
            val mAlertDialog = mBuilder.show()
            mAlertDialog.setCancelable(false)

            mDialogView.dialogButtonOK.setOnClickListener {

                //get pin text from EditTexts of custom layout and set if to main layout
                confirmationPin = mDialogView.pin.text.toString().trim()

                if (mDialogView.pin.text.isEmpty()) {
                    mDialogView.pin.error = "Enter PIN"
                    mDialogView.pin.requestFocus()
                } else {
                    //dismiss dialog
                    mAlertDialog.dismiss()
                    try {
                        val token = "Bearer " + pref.getString(Constants.USER_TOKEN, "")
                        val progressDialog = ProgressDialog(this@ApproveRequestActivity)
                        progressDialog.setMessage("please wait...")
                        progressDialog.setCanceledOnTouchOutside(false)
                        progressDialog.show()
                        Constants.verifyPin(this, confirmationPin, token).setCallback { e, result ->
                            if (result != null) {
                                if (result.asJsonObject.get("status").asString == "success") {
                                    Constants.approveFundRequests(this, id, confirmationPin, reason, provider, token).setCallback { e, result ->
                                        progressDialog.dismiss()
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
                        Log.d("TAG", e.message)
                    }
                }
            }
        }

        binding.btnReject.setOnClickListener {
            try {
                val token = "Bearer " + pref.getString(Constants.USER_TOKEN, "")
                val progressDialog = ProgressDialog(this@ApproveRequestActivity)
                val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_input_message, null)
                val mBuilder = AlertDialog.Builder(this)
                        .setView(mDialogView)
                        .setTitle("Enter Reason")

                //show dialog
                val mAlertDialog = mBuilder.show()
                mAlertDialog.setCancelable(false)

                mDialogView.dialogButtonOK.setOnClickListener {

                    //get reason for rejecting fund request
                    reason = mDialogView.message.text.toString().trim()

                    if (mDialogView.message.text.isEmpty()) {
                        mDialogView.message.error = "Enter reason"
                        mDialogView.message.requestFocus()
                    } else {
                        //dismiss dialog
                        mAlertDialog.dismiss()
                        progressDialog.setMessage("please wait...")
                        progressDialog.setCanceledOnTouchOutside(false)
                        progressDialog.show()
                        Constants.rejectFundRequests(this, id, current_user_id, cancellation_status, cancellation_reason, token).setCallback { e, result ->
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
                    }
                }
            } catch (e: Exception) {
                Log.d("TAG", e.message)
            }
        }

        binding.arrowBack.setOnClickListener {
            startActivity(Intent(applicationContext, NotificationActivity::class.java))
        }
    }
}