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
import com.beyondthehorizon.routeapp.views.auth.LoginActivity


class ResetPasswordOtpVerifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpVerifyBinding
    private lateinit var token: String
    private lateinit var prefs: SharedPreferences
    private lateinit var smsListener: SMSListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp_verify)
        val progressBar = CustomProgressBar()
        smsListener = SMSListener()

        binding.btnVerify.setOnClickListener {
            val otp = binding.otp.text.toString()
            if (otp.length < 0) {
                var intent = Intent(this, LoginActivity::class.java)
                val email = intent.getStringExtra("Email")
                prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
                token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
                progressBar.show(this, "Verifying otp...")
                Constants.otpVerify(this, email, otp, token).setCallback { e, result ->
                    if (result.has("data")) {
                        progressBar.dialog.dismiss()
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Invalid otp code", Toast.LENGTH_LONG).show();
                    }
                }
            }
            else{
                binding.otp.setError("OTP is invalid")
            }
    }
        smsListener.bindListener(object : OTPListener {
            override fun onOTPReceived(extractedOTP: String) {
                binding.otp.setText(extractedOTP)
            }
        })
    }

    override fun onBackPressed() {
        startActivity(Intent(this@ResetPasswordOtpVerifyActivity, LoginActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        smsListener.unbindListener()
    }
}
