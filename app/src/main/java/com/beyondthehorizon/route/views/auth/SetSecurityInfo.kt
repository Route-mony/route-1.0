package com.beyondthehorizon.route.views.auth

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.utils.Constants
import com.beyondthehorizon.route.utils.Constants.verifyPin
import com.beyondthehorizon.route.utils.CustomProgressBar
import com.beyondthehorizon.route.utils.NetworkUtils
import com.beyondthehorizon.route.views.MainActivity
import kotlinx.android.synthetic.main.content_set_security_info.*
import kotlinx.android.synthetic.main.fragment_keyboard.*

class SetSecurityInfo : AppCompatActivity() {
    private var pin1set = ""
    private var pin1 = ""
    private var pin2 = ""
    private var pin3 = ""
    private var pin4 = ""
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var prefs: SharedPreferences
    private lateinit var token: String
    private lateinit var networkUtils: NetworkUtils
    private lateinit var llInternetDialog: LinearLayout
    private lateinit var btnCancel: Button
    private lateinit var btnRetry: android.widget.Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_security_info)
        editor = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0).edit()
        prefs = getSharedPreferences(Constants.REG_APP_PREFERENCES, 0)
        networkUtils = NetworkUtils(this)

        llInternetDialog = findViewById(R.id.llInternetDialog)
        btnCancel = findViewById(R.id.btn_cancel)
        btnRetry = findViewById(R.id.btn_retry)

        btnRetry.setOnClickListener { v: View? ->
            llInternetDialog.visibility = View.GONE
            sendPin()
        }

        btnCancel.setOnClickListener { v: View? -> llInternetDialog.visibility = View.GONE }

        password.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    fun inputKeyed(view: View) {
        when (view.id) {
            R.id.one -> {
                updateScreen("1")
            }
            R.id.two -> {
                updateScreen("2")
            }
            R.id.three -> {
                updateScreen("3")
            }
            R.id.four -> {
                updateScreen("4")
            }
            R.id.five -> {
                updateScreen("5")
            }
            R.id.six -> {
                updateScreen("6")
            }
            R.id.seven -> {
                updateScreen("7")
            }
            R.id.eight -> {
                updateScreen("8")
            }
            R.id.nine -> {
                updateScreen("9")
            }
            R.id.zero -> {
                updateScreen("0")
            }
            R.id.ok -> {
                sendPin()
            }
            R.id.del -> {
                when {
                    pin4 != "" -> pin4 = ""
                    pin3 != "" -> pin3 = ""
                    pin2 != "" -> pin2 = ""
                    pin1 != "" -> pin1 = ""
                }
                updateScreen("")
            }
        }
    }

    private fun sendPin() {
        if (networkUtils.isNetworkAvailable) {
            if ((pin1 != "").and(pin2 != "").and(pin3 != "").and(pin4 != "")) {
                if (pin1set == "") {
                    pin1set = pin1 + pin2 + pin3 + pin4
                    token = "Bearer " + prefs.getString(Constants.USER_TOKEN, "")
                    val progressBar = CustomProgressBar()
                    progressBar.show(this, "Please Wait...")
                    verifyPin(this@SetSecurityInfo, pin1set, token)
                            .setCallback { e, result ->
                                pin1set = ""
                                pin1 = ""
                                pin2 = ""
                                pin3 = ""
                                pin4 = ""
                                updateScreen("")
                                if (e != null) {
                                    progressBar.dialog.dismiss()
                                    Log.e("SetSecurityInfo 12356", e.toString())
                                    return@setCallback
                                }
                                if (result.has("errors")) {
                                    progressBar.dialog.dismiss()
                                    Toast.makeText(this, result.get("errors").asJsonArray[0].asString, Toast.LENGTH_LONG).show()
                                    label.setTextColor(Color.parseColor("#FA0505"))
                                } else if (result.has("data")) {
//                                        label.text = "Pin Verified"
                                    Toast.makeText(this, result.get("data").asJsonObject.get("message").asString, Toast.LENGTH_LONG).show()
                                    label.setTextColor(Color.parseColor("#40CA08"))
                                    val intent = Intent(Intent(this, MainActivity::class.java))
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    progressBar.dialog.dismiss()
                                    startActivity(intent)
                                    this@SetSecurityInfo.finish()
                                } else {
                                    Toast.makeText(this, "Account not found, please login", Toast.LENGTH_LONG).show()
                                    editor.clear()
                                    editor.apply()
                                    startActivity(Intent(this@SetSecurityInfo, LoginActivity::class.java))
                                }
                            }
                } else {
                    if (pin1set == (pin1 + pin2 + pin3 + pin4)) {
                        val intent = Intent(Intent(this, MainActivity::class.java))
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        this@SetSecurityInfo.finish()
                    } else {
                        pin1 = ""
                        pin2 = ""
                        pin3 = ""
                        pin4 = ""
                        pin1set = ""
                        label.text = getString(R.string.pin_request_invalid)
                        updateScreen("")
                        Toast.makeText(this, "PIN don\'t match", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.fill_in), Toast.LENGTH_LONG).show()
            }
        } else {
            llInternetDialog.visibility = View.VISIBLE
        }
    }

    private fun updateScreen(digit: String) {
        if (digit != "") {
            when {
                pin1 == "" -> pin1 = digit
                pin2 == "" -> pin2 = digit
                pin3 == "" -> pin3 = digit
                pin4 == "" -> pin4 = digit
            }
        }
        if (pin1 == "") {
//            pin_1.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            pin_1.background = ContextCompat.getDrawable(this@SetSecurityInfo, R.drawable.empty_dot)
        } else {
            pin_1.background = ContextCompat.getDrawable(this@SetSecurityInfo, R.drawable.filled_dot)
        }
        if (pin2 == "") {
            pin_2.background = ContextCompat.getDrawable(this@SetSecurityInfo, R.drawable.empty_dot)
        } else {
            pin_2.background = ContextCompat.getDrawable(this@SetSecurityInfo, R.drawable.filled_dot)
        }
        if (pin3 == "") {
            pin_3.background = ContextCompat.getDrawable(this@SetSecurityInfo, R.drawable.empty_dot)
        } else {
            pin_3.background = ContextCompat.getDrawable(this@SetSecurityInfo, R.drawable.filled_dot)
        }
        if (pin4 == "") {
            pin_4.background = ContextCompat.getDrawable(this@SetSecurityInfo, R.drawable.empty_dot)
        } else {
            pin_4.background = ContextCompat.getDrawable(this@SetSecurityInfo, R.drawable.filled_dot)
        }
    }
}