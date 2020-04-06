package com.beyondthehorizon.routeapp.views.settingsactivities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.beyondthehorizon.routeapp.R
import com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES
import com.beyondthehorizon.routeapp.views.MainActivity
import com.beyondthehorizon.routeapp.views.auth.LoginActivity
import com.beyondthehorizon.routeapp.views.receipt.ReceiptActivity
import com.beyondthehorizon.routeapp.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.nav_bar_layout.*

class SettingsActivity : AppCompatActivity() {
    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        pref = applicationContext.getSharedPreferences(REG_APP_PREFERENCES, 0) // 0 - for private mode
        btn_settings.setImageResource(R.drawable.ic_group663_active)
        txt_settings.setTextColor(resources.getColor(R.color.colorButton))

        user_name.text = (pref.getString("FullName", ""))

        btn_home.setOnClickListener {
            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btn_pool.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SettingsActivity, ReceiptActivity::class.java)
            startActivity(intent)
        })

        btn_transactions.setOnClickListener {
            val intent = Intent(this@SettingsActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }
        back.setOnClickListener {
            onBackPressed()
        }
        inviteFriends.setOnClickListener {
            val intent = Intent(this@SettingsActivity, InviteFriendActivity::class.java)
            intent.putExtra("TYPE", "Invite")
            startActivity(intent)
        }
        pinAndPass.setOnClickListener {
            startActivity(Intent(this@SettingsActivity, PasswordAndPinActivity::class.java))
        }
        termsAndConditions.setOnClickListener {
            startActivity(Intent(this@SettingsActivity, TermsOfUseActivity::class.java))
        }

        hd.setOnClickListener {
            startActivity(Intent(this@SettingsActivity, UserProfileActivity::class.java))
        }

        logOut.setOnClickListener {
            editor = pref.edit()
            editor.clear()
            editor.apply()
            val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
