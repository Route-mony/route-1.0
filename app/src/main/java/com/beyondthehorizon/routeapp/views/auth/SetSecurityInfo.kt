package com.beyondthehorizon.routeapp.views.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.views.MainActivity
import com.beyondthehorizon.routeapp.views.RequestFundsActivity
import kotlinx.android.synthetic.main.content_set_security_info.*
import kotlinx.android.synthetic.main.fragment_keyboard.*
import java.security.AccessController.getContext

class SetSecurityInfo : AppCompatActivity() {
    private var pin1set = ""
    private var pin1 = ""
    private var pin2 = ""
    private var pin3 = ""
    private var pin4 = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_security_info)
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
                if ((pin1 != "").and(pin2 != "").and(pin3 != "").and(pin4 != "")) {
                    if (pin1set == "") {
                        pin1set = pin1 + pin2 + pin3 + pin4
                        pin1 = ""
                        pin2 = ""
                        pin3 = ""
                        pin4 = ""
                        label.text = getString(R.string.pin_request_verify)
                        updateScreen("")
                    } else {
                        if (pin1set == (pin1 + pin2 + pin3 + pin4)) {
//                            val profile = Session(this).profile
//                            profile.pin = pin1set
//                            Session(this).profile = profile

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
