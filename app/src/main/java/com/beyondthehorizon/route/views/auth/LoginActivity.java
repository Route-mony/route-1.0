package com.beyondthehorizon.route.views.auth;

import static com.beyondthehorizon.route.utils.Constants.LOGGED_IN;
import static com.beyondthehorizon.route.utils.Constants.USER_EMAIL;
import static com.beyondthehorizon.route.utils.Constants.USER_TOKEN;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.beyondthehorizon.route.databinding.ActivityLoginBinding;
import com.beyondthehorizon.route.utils.Constants;
import com.beyondthehorizon.route.utils.NetworkUtils;
import com.beyondthehorizon.route.utils.SharedPref;
import com.beyondthehorizon.route.views.MainActivity;
import com.beyondthehorizon.route.views.ServicesActivity;
import com.beyondthehorizon.route.views.base.BaseActivity;
import com.beyondthehorizon.route.views.settingsactivities.ResetPasswordActivity;

public class LoginActivity extends BaseActivity {
    private NetworkUtils networkUtils;
    private ProgressDialog progressDialog;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        networkUtils = new NetworkUtils(this);
        binding.back.setOnClickListener(view -> startNewActivity(ServicesActivity.class, null, true));
        binding.btnRetry.setOnClickListener(v -> {
            binding.llInternetDialog.setVisibility(View.GONE);
            loginRequest();
        });
        binding.btnCancel.setOnClickListener(v -> binding.llInternetDialog.setVisibility(View.GONE));

        binding.forgotPassword.setOnClickListener(v -> startNewActivity(ResetPasswordActivity.class, null, true));
    }

    public void nextPage(View view) {
        if (TextUtils.isEmpty(binding.etEmail.getText())) {
            binding.tvEmail.setError("Enter your email");
            return;
        }
        if (TextUtils.isEmpty(binding.etPassword.getText())) {
            binding.tvPassword.setError("Enter your password");
            return;
        }
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        loginRequest();
    }

    private void loginRequest() {
        if (networkUtils.isNetworkAvailable()) {
            progressDialog.show();
            Constants.sendLogInRequest(LoginActivity.this, String.valueOf(binding.etPassword.getText()), String.valueOf(binding.etEmail.getText()))
                    .setCallback((e, result) -> {
                        progressDialog.dismiss();
                        if (result != null) {
                            if (result.get("status").toString().contains("failed")) {
                                showToast(this, "A user with this email and password was not found.", 0);
                            } else {
                                String email = result.get("data").getAsJsonObject().get("email").getAsString();
                                String token = result.get("data").getAsJsonObject().get("token").getAsString();
                                SharedPref.Companion.save(this, LOGGED_IN, true);
                                SharedPref.Companion.save(this, USER_EMAIL, email);
                                SharedPref.Companion.save(this, USER_TOKEN, token);
                                startNewActivity(MainActivity.class, null, true);
                            }
                        } else {
                            showToast(this, "Unable to login. Try again later", 0);
                        }
                    });
        } else {
            binding.llInternetDialog.setVisibility(View.VISIBLE);
        }
    }

    public void prevPage(View view) {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
