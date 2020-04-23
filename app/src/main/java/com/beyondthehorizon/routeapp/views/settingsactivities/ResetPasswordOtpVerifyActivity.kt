package com.beyondthehorizon.routeapp.views.settingsactivities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityOtpVerifyBinding
import com.beyondthehorizon.routeapp.utils.Common.OTPListener
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.CustomProgressBar
import com.beyondthehorizon.routeapp.utils.SMSListener
import com.beyondthehorizon.routeapp.views.FundRequestedActivity
import com.beyondthehorizon.routeapp.views.auth.LoginActivity
import com.firebase.ui.auth.ui.email.EmailActivity


class ResetPasswordOtpVerifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpVerifyBinding
    private lateinit var token: String
    private lateinit var prefs: SharedPreferences
    private lateinit var routeOTP: String
    private lateinit var otpIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp_verify)
        routeOTP = binding.otp.text.toString();
        otpIntent = getIntent()
        SMSListener().bindListener(object : OTPListener {
            override fun onOTPReceived(extractedOTP: String) {
                routeOTP = extractedOTP
                verifyOTP(extractedOTP)
            }
        })

        binding.btnVerify.setOnClickListener {
            val otp = binding.otp.text.toString()
            verifyOTP(otp);
        }
    }

    fun verifyOTP(otp: String) {
        val progressBar = CustomProgressBar()
        try {
            if (otp.toString().length == 6) {
                var intent = Intent(this, NewPasswordActivity::class.java)
                var email = otpIntent.getStringExtra("Email")
                prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
                token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
                progressBar.show(this, "Verifying otp...")
                Constants.otpVerify(this, email, otp).setCallback { e, result ->
                    progressBar.dialog.dismiss()
                    if (result.has("data")) {
                        intent.putExtra(Constants.ACTIVITY_TYPE, Constants.RESET_PASSWORD_ACTIVITY)
                        var token = result.get("data").asJsonObject.get("token").asString
                        intent.putExtra("Token", token)
                        intent.putExtra("Email", email)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Invalid otp code", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                binding.otp.setError("OTP is invalid")
            }
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG)
        }
    }

    fun getOTP(): String {
        return routeOTP;
    }

    override fun onBackPressed() {
        startActivity(Intent(this@ResetPasswordOtpVerifyActivity, ResetPasswordActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        SMSListener().unbindListener()
    }
}
