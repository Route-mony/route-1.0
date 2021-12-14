package com.beyondthehorizon.route.views.settingsactivities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.beyondthehorizon.route.R
import com.beyondthehorizon.route.views.MainActivity
import com.beyondthehorizon.route.views.receipt.ReceiptActivity
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity
import kotlinx.android.synthetic.main.activity_terms_of_use.*
import kotlinx.android.synthetic.main.nav_bar_layout.*

class PrivacyPolicyActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        val myWebView: WebView = findViewById(R.id.privacy)
        myWebView.setBackgroundColor(resources.getColor(R.color.colorPrimaryMain))
        myWebView.settings.javaScriptEnabled = true
        myWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                view?.loadUrl(
                    "javascript:(function() { " +
                            "document.getElementById('bloc-37')[0].style.display='none'; " +
                            "document.getElementById('bloc-39')[0].style.display='none'; " +
                            "document.getElementById('bloc-40')[0].style.display='none'; " +
                            "document.getElementByClassName('cc-15mo')[0].style.display='none'; " +
                            "})()"
                )

            }
        }
        myWebView.loadUrl("https://route.money/privacy-policy/")
        back.setOnClickListener {
            onBackPressed()
        }

        btn_home.setOnClickListener {
            val intent = Intent(this@PrivacyPolicyActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btn_settings.setOnClickListener {
            val intent = Intent(this@PrivacyPolicyActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_receipt.setOnClickListener {
            val intent = Intent(this@PrivacyPolicyActivity, ReceiptActivity::class.java)
            startActivity(intent)
        }

        btn_transactions.setOnClickListener {
            val intent = Intent(this@PrivacyPolicyActivity, TransactionsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
