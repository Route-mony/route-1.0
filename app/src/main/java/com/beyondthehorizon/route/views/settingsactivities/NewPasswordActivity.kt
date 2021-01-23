package com.beyondthehorizon.route.views.settingsactivities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.databinding.ActivityNewPasswordBinding
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.CustomProgressBar
import com.beyondthehorizon.route.utils.Utils
import com.beyondthehorizon.route.views.FundRequestedActivity

class NewPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewPasswordBinding
    private lateinit var tokenIntent: Intent
    private lateinit var progressBar:CustomProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_password)
        tokenIntent = getIntent()
        progressBar = CustomProgressBar(this)

        binding.next.setOnClickListener {
            try {
                var newPassword = binding.password.text.toString()
                var cPassword = binding.cpassword.text.toString()

                if (newPassword.isNullOrEmpty()) {
                    binding.password.setError("Please enter your current password");
                    binding.password.requestFocus();
                }
                else if (Utils.passwordValidator(newPassword)) {
                    binding.password.setError(Utils.invalidPasswordMessage());
                    binding.password.requestFocus();
                }
                else if (newPassword.isNullOrEmpty()) {
                    binding.cpassword.setError("Please confirm your password");
                    binding.cpassword.requestFocus();
                } else if (newPassword.compareTo(cPassword) == 0) {
                    var intent = Intent(this, FundRequestedActivity::class.java)
                    val token = "Bearer ${tokenIntent.getStringExtra("Token")}"
                    val email = tokenIntent.getStringExtra("Email")

                    progressBar.show("Please wait...")
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
                    binding.cpassword.setError("Your password doesn't match");
                    binding.cpassword.requestFocus();
                }
            } catch (ex: Exception) {
                Toast.makeText(this, ex.message, Toast.LENGTH_LONG)
            }
        }

        binding.back.setOnClickListener {
            onBackPressed()
        }
    }
}
