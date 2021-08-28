package com.beyondthehorizon.route.views.settingsactivities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.databinding.ActivityOtpVerifyBinding
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.CustomProgressBar

class ResetPasswordOtpVerifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpVerifyBinding
    private lateinit var token: String
    private lateinit var prefs: SharedPreferences
    private lateinit var routeOTP: String
    private lateinit var otpIntent: Intent
    private lateinit var progressBar:CustomProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp_verify)
        routeOTP = binding.otpCode.text.toString();
        otpIntent = getIntent()
        progressBar = CustomProgressBar(this)

        binding.next.setOnClickListener {
            val otp = binding.otpCode.text.toString()
            verifyOTP(otp);
        }
        binding.back.setOnClickListener {
            onBackPressed()
        }
    }

    fun verifyOTP(otp: String) {
        try {
            if (otp.length == 6) {
                var intent = Intent(this, NewPasswordActivity::class.java)
                var email = otpIntent.getStringExtra("Email")
                prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
                token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
                progressBar.show("Verifying otp...")
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
                binding.otpCode.setError("OTP is invalid")
            }
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG)
        }
    }
}
