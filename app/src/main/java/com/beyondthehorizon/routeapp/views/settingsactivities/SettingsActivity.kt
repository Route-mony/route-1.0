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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.back
import kotlinx.android.synthetic.main.activity_settings.profile_pic
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

        var requestOptions = RequestOptions();
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16));
        Glide.with(this@SettingsActivity)
                .load(pref.getString("ProfileImage", ""))
                .centerCrop()
                .error(R.drawable.ic_user)
                                        .placeholder(R.drawable.ic_user)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(requestOptions)
                .into(profile_pic)

        user_name.text = (pref.getString("FullName", ""))

        btn_home.setOnClickListener {
            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btn_receipt.setOnClickListener(View.OnClickListener {
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

        downLoadReceipt.setOnClickListener {
            startActivity(Intent(this@SettingsActivity, DownloadStatementActivity::class.java))
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
