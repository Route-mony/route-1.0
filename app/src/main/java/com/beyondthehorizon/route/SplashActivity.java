package com.beyondthehorizon.route;

import static com.beyondthehorizon.route.utils.Constants.LOGGED_IN;
import static com.beyondthehorizon.route.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.route.utils.Constants.USER_TOKEN;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.beyondthehorizon.route.views.MainActivity;
import com.beyondthehorizon.route.views.ServicesActivity;
import com.beyondthehorizon.route.views.auth.LoginActivity;
import com.beyondthehorizon.route.views.auth.SetSecurityInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private SharedPreferences pref;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Initialize Firebase Auth

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        logo = findViewById(R.id.logo);

        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        Glide.with(this).asGif()
                .load(R.drawable.logo)
                .apply(new RequestOptions().override(500, 500))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .transition(withCrossFade())
                .into(logo);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                if (pref.getBoolean(LOGGED_IN, false)) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    if (pref.getString(USER_TOKEN, "").isEmpty()) {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, SetSecurityInfo.class));
                    }
                }
            } catch (Exception ex) {
                pref.edit().clear();
                pref.edit().apply();
                startActivity(new Intent(SplashActivity.this, ServicesActivity.class));
            }
            finish();
        }, 3000);
    }

    void checkPhoneAuth() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.PhoneBuilder().build());

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                startActivity(new Intent(SplashActivity.this, ServicesActivity.class));
                finish();

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}
