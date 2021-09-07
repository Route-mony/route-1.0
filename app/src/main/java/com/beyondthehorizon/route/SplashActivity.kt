package com.beyondthehorizon.route

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.beyondthehorizon.route.databinding.ActivitySplashBinding
import com.beyondthehorizon.route.utils.Constants.LOGGED_IN
import com.beyondthehorizon.route.utils.Constants.USER_TOKEN
import com.beyondthehorizon.route.utils.SharedPref
import com.beyondthehorizon.route.views.MainActivity
import com.beyondthehorizon.route.views.ServicesActivity
import com.beyondthehorizon.route.views.auth.LoginActivity
import com.beyondthehorizon.route.views.auth.SetSecurityInfo
import com.beyondthehorizon.route.views.base.BaseActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashActivity : BaseActivity() {
    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private var logo: ImageView? = null
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth!!.currentUser
        logo = findViewById(R.id.logo)
        Glide.with(this).asGif()
            .load(R.drawable.logo)
            .apply(RequestOptions().override(500, 500))
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.logo)
        val handler = Handler()
        handler.postDelayed({
            try {
                if (SharedPref.getBoolean(this, LOGGED_IN)) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                } else {
                    if (SharedPref.getString(this, USER_TOKEN)!!.isEmpty()) {
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    } else {
                        startActivity(Intent(this@SplashActivity, SetSecurityInfo::class.java))
                    }
                }
            } catch (ex: Exception) {
                SharedPref.clear(this)
                startActivity(Intent(this@SplashActivity, ServicesActivity::class.java))
            }
            finish()
        }, 3000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                startActivity(Intent(this@SplashActivity, ServicesActivity::class.java))
                finish()
            }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 100
    }
}