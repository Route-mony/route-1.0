package com.beyondthehorizon.routeapp.views.settingsactivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.databinding.ActivityNewPasswordBinding
import com.beyondthehorizon.routeapp.databinding.ActivityOtpVerifyBinding
import com.beyondthehorizon.routeapp.utils.Constants
import com.beyondthehorizon.routeapp.utils.CustomProgressBar
import com.beyondthehorizon.routeapp.views.FundRequestedActivity
import com.beyondthehorizon.routeapp.views.OtpVerificationActivity

class NewPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewPasswordBinding
    private lateinit var tokenIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_password)
        tokenIntent = getIntent()
        val password_regex: Regex = """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%!\-_?&])(?=\S+$).{8,}""".toRegex()

        binding.btnReset.setOnClickListener {
            try {
                var newPassword = binding.password.text.toString()
                var cPassword = binding.confirmPassword.text.toString()

                if (!password_regex.matches(newPassword)) {
                    binding.password.setError("Please enter a valid password");
                    binding.password.requestFocus();
                } else if (!password_regex.matches(newPassword)) {
                    binding.confirmPassword.setError("Please enter a valid password");
                    binding.confirmPassword.requestFocus();
                } else if (newPassword.compareTo(cPassword) == 0) {
                    var intent = Intent(this, FundRequestedActivity::class.java)
                    val token = "Bearer ${tokenIntent.getStringExtra("Token")}"
                    val email = tokenIntent.getStringExtra("Email")
                    val progressBar = CustomProgressBar()

                    progressBar.show(this, "Please wait...")
                    Constants.updatePassword(this, newPassword, token).setCallback { e, result ->
                        progressBar.dialog.dismiss()
                        if (result.has("data")) {
                            intent.putExtra("Message", "Your Route account registered with email ${email} has been reset successfully. Please login to verify.")
                            intent.putExtra(Constants.ACTIVITY_TYPE, Constants.RESET_PASSWORD_ACTIVITY)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "The previous password provided does not match the existing", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    binding.confirmPassword.setError("Your password doesn't match");
                    binding.confirmPassword.requestFocus();
                }
            } catch (ex: Exception) {
                Toast.makeText(this, ex.message, Toast.LENGTH_LONG)
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@NewPasswordActivity, OtpVerificationActivity::class.java))
    }
}
