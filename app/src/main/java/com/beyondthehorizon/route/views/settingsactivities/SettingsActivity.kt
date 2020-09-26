package com.beyondthehorizon.route.views.settingsactivities

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.bottomsheets.AddMoneyBottomsheet
import com.beyondthehorizon.route.utils.Constants.*
import com.beyondthehorizon.route.views.MainActivity
import com.beyondthehorizon.route.views.auth.LoginActivity
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import io.kommunicate.KmConversationBuilder
import io.kommunicate.Kommunicate
import io.kommunicate.callbacks.KmCallback
import io.kommunicate.users.KMUser
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.nav_bar_layout.*


class SettingsActivity : AppCompatActivity() {
    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        pref = applicationContext.getSharedPreferences(REG_APP_PREFERENCES, 0) // 0 - for private mode
        editor = pref.edit()
        btn_settings.setImageResource(R.drawable.ic_nav_settings)
        txt_settings.setTextColor(resources.getColor(R.color.colorButton))

        Kommunicate.init(this@SettingsActivity, "ba520ce1b1256908b973b5f89a451913");

        var requestOptions = RequestOptions();
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16));
        Glide.with(this@SettingsActivity)
                .load(pref.getString("ProfileImage", ""))
                .centerCrop()
                .error(R.drawable.ic_user_home_page)
                .placeholder(R.drawable.ic_user_home_page)
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
        paymentMethods.setOnClickListener {
            val addMoneyBottomsheet = AddMoneyBottomsheet()
            addMoneyBottomsheet.show(supportFragmentManager, "Add Money Options")
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

        // Show balalance
        switch1.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean(BALANCE_CHECK, isChecked)
            editor.commit()
        }

        switch1.isChecked = pref.getBoolean(BALANCE_CHECK, true)

        logOut.setOnClickListener {
            editor.clear()
            editor.apply()
            val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        //Support
        supportHelp.setOnClickListener {
            val prog = ProgressDialog(this@SettingsActivity)
            prog.setMessage("Loading...")
            prog.setCanceledOnTouchOutside(false)
            prog.show()
            val user = KMUser();
            user.userId = pref.getString(USER_ID, ""); // Pass a unique key
//            user.userId = "wesrdc6vybuinokmkmkjkmkmkmkm"; // Pass a unique key
//            user.setPassword("<PASSWORD>"); //Optional
//            user.email = "kk@gmail.com"

            Log.e("Conversation", "Success : ${pref.getString(UNIQUE_ID, "")}")
            user.imageLink = pref.getString("ProfileImage", ""); // Optional
            user.displayName = pref.getString(UserName, ""); //Optional

//            Kommunicate.login(this, user, object : KMLoginHandler {
//                override fun onSuccess(registrationResponse: RegistrationResponse?, context: Context?) {
//                    Log.e("kio", "success")
//                    // You can perform operations such as opening the conversation, creating a new conversation or update user details on success
////                    Kommunicate.openConversation(this@SettingsActivity);
//                }
//
//                override fun onFailure(registrationResponse: RegistrationResponse, exception: Exception) {
//                    // You can perform actions such as repeating the login call or throw an error message on failure
//                }
//            })

            KmConversationBuilder(this@SettingsActivity)
                    .setWithPreChat(false)
                    .setKmUser(user)
                    .launchConversation(object : KmCallback {
                        override fun onSuccess(message: Any) {
                            Log.d("Conversation", "Success : $message")
                            prog.dismiss()
                        }

                        override fun onFailure(error: Any) {
                            Log.d("Conversation", "Failure : $error")
                        }
                    })
        }
    }
}
