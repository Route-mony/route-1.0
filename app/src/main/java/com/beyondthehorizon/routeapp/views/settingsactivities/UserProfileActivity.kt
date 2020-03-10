package com.beyondthehorizon.routeapp.views.settingsactivities

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.utils.Constants.*
import com.beyondthehorizon.routeapp.views.MainActivity
import com.beyondthehorizon.routeapp.views.auth.LoginActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_terms_of_use.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.activity_user_profile.back
import kotlinx.android.synthetic.main.nav_bar_layout.*

class UserProfileActivity : AppCompatActivity() {

    private val TAG = "UserProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        getProfile()

        saveUpdate.setOnClickListener {
            updateProfile()
        }


        back.setOnClickListener {
            onBackPressed()
        }

        btn_home.setOnClickListener {
            val intent = Intent(this@UserProfileActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btn_settings.setOnClickListener {
            val intent = Intent(this@UserProfileActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateProfile() {
        val pref: SharedPreferences = applicationContext.getSharedPreferences(REG_APP_PREFERENCES, 0) // 0 - for private mode
        val token = "Bearer " + pref.getString(USER_TOKEN, "")
        val progressDialog = ProgressDialog(this@UserProfileActivity)
        progressDialog.setMessage("please wait...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        updateUserProfile(this@UserProfileActivity, token,
                phone.text.toString(),
                userNameTxt.text.toString())
                .setCallback { _, result ->
                    progressDialog.dismiss()
                    Toast.makeText(this@UserProfileActivity, "Updated successfully", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "updateUserProfileSettings: " + result!!)
                }
    }

    private fun getProfile() {

        val pref: SharedPreferences = applicationContext.getSharedPreferences(REG_APP_PREFERENCES, 0) // 0 - for private mode
        val editor: SharedPreferences.Editor
        editor = pref.edit()
        val token = "Bearer " + pref.getString(USER_TOKEN, "")

        Log.d(TAG, "getProfile: $token")
        val progressDialog = ProgressDialog(this@UserProfileActivity)
        progressDialog.setMessage("please wait...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        getUserProfile(this@UserProfileActivity, token)
                .setCallback { e, result ->
                    progressDialog.dismiss()
                    Log.e(TAG, "getUserProfileSettings: " + result!!)

                    if (result != null) {

                        if (result.get("status").toString().contains("failed")) {
                            editor.clear()
                            editor.apply()
                            startActivity(Intent(this@UserProfileActivity, LoginActivity::class.java))
                        } else if (result.get("status").toString().contains("success")) {

                            val username = result.get("data").asJsonObject.get("username").toString()
                            val fname = result.get("data").asJsonObject.get("first_name").toString()
                            val lname = result.get("data").asJsonObject.get("last_name").toString()
                            val emailString = result.get("data").asJsonObject.get("email").toString()
                            val phone_number = result.get("data").asJsonObject.get("phone_number").toString()

                            phone.setText(phone_number.substring(1, phone_number.length - 1))
                            userNameTxt.setText(username.substring(1, username.length - 1))
                            email.setText(emailString.substring(1, emailString.length - 1))
                            fullNames.setText("${fname.substring(1, fname.length - 1)} ${lname.substring(1, fname.length - 1)}")

                            gender.setText("None")
                            dob.setText("0-00-1900")

                        }
                    } else {
                        val snackbar = Snackbar
                                .make(RL1, "Unable to load data ", Snackbar.LENGTH_LONG)
                        snackbar.setAction("Try again") {
                            if (pref.getString(USER_TOKEN, "")!!.isEmpty()) {
                                editor.putString(LOGGED_IN, "false")
                                editor.apply()
                                val intent = Intent(this@UserProfileActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                getProfile()
                            }
                        }
                        snackbar.show()
                    }
                }
    }
}
