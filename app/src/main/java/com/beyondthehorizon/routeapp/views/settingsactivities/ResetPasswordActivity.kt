package com.beyondthehorizon.routeapp.views.settingsactivities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityResetPasswordBinding
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.CustomProgressBar
import com.beyondthehorizon.routeapp.views.auth.LoginActivity

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var prefs: SharedPreferences
    private var REQUEST_CODE_READ_SMS: Int = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password)
        val progressBar = CustomProgressBar()

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECEIVE_SMS), REQUEST_CODE_READ_SMS)

        binding.next.setOnClickListener {
            try {
                val email = binding.email.text.toString()
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    var intent = Intent(this, ResetPasswordOtpVerifyActivity::class.java)
                    prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
                    progressBar.show(this, "Verifying email...")
                    Constants.resetPassword(this, email).setCallback { e, result ->
                        progressBar.dialog.dismiss()
                        if (result.has("data")) {
                            intent.putExtra("Email", email)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "This email is not yet registered", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    binding.email.setError("Email is invalid")
                }
            }
            catch (ex: Exception){
                Toast.makeText(this, ex.message, Toast.LENGTH_LONG)
            }
        }
        binding.back.setOnClickListener {
            onBackPressed()
        }
    }
}
