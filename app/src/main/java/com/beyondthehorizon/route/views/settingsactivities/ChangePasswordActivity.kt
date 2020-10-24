package com.beyondthehorizon.route.views.settingsactivities

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.databinding.ActivityChangePasswordBinding
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.CustomProgressBar
import com.beyondthehorizon.route.utils.Utils
import com.beyondthehorizon.route.views.FundRequestedActivity
import com.beyondthehorizon.route.views.MainActivity
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.nav_bar_layout.*

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var prefs: SharedPreferences
    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        val progressBar = CustomProgressBar()
        val token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")


        btn_home.setOnClickListener {
            val intent = Intent(this@ChangePasswordActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btn_transactions.setOnClickListener {
            val intent = Intent(this@ChangePasswordActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@ChangePasswordActivity, ReceiptActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_settings.setOnClickListener {
            val intent = Intent(this@ChangePasswordActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_change.setOnClickListener {
            try {
                val currentPassword = binding.currentPassword.text;
                val newPassword = binding.newPassword.text;
                val cpassword = binding.ConfirmNewPassword.text;
                if (currentPassword.isNullOrEmpty()) {
                    binding.currentPassword.setError("Please enter your current password");
                    binding.currentPassword.requestFocus();
                } else if (newPassword.isNullOrEmpty()) {
                    binding.newPassword.setError("Please enter a new password");
                    binding.newPassword.requestFocus();
                } else if (Utils.passwordValidator(newPassword.toString())) {
                    binding.newPassword.setError(Utils.invalidPasswordMessage())
                    binding.newPassword.requestFocus();
                } else if (cpassword.toString() == newPassword.toString()) {
                    val oldPassword = currentPassword.toString();
                    val newPassword = newPassword.toString();
                    var intent = Intent(this, FundRequestedActivity::class.java)
                    progressBar.show(this, "Please wait...")
                    Constants.changePassword(this, newPassword, oldPassword, token).setCallback { e, result ->
                        progressBar.dialog.dismiss()
                        if (result.has("data")) {
                            var message = result.get("data").asJsonObject.get("message").asString
                            intent.putExtra("Message", message)
                            intent.putExtra(Constants.ACTIVITY_TYPE, Constants.RESET_PASSWORD_ACTIVITY)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "The previous password provided does not match the existing", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    binding.ConfirmNewPassword.setError("Your password doesn't match");
                    binding.ConfirmNewPassword.requestFocus();
                }
            } catch (ex: Exception) {
                Toast.makeText(this, ex.message, Toast.LENGTH_LONG)
            }
        }
        back.setOnClickListener {
            onBackPressed()
        }
    }
}
