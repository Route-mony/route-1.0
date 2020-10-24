package com.beyondthehorizon.route.views.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.beyondthehorizon.route.R;
import com.beyondthehorizon.route.utils.Constants;
import com.beyondthehorizon.route.utils.NetworkUtils;
import com.beyondthehorizon.route.utils.Utils;
import com.beyondthehorizon.route.views.SignupVerifiedActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.beyondthehorizon.route.utils.Constants.FirstName;
import static com.beyondthehorizon.route.utils.Constants.ID_NUMBER;
import static com.beyondthehorizon.route.utils.Constants.LastName;
import static com.beyondthehorizon.route.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.route.utils.Constants.SurName;
import static com.beyondthehorizon.route.utils.Constants.USER_EMAIL;
import static com.beyondthehorizon.route.utils.Constants.USER_PASSWORD;
import static com.beyondthehorizon.route.utils.Constants.UserName;

public class PasswordActivity extends AppCompatActivity {

    private static final String TAG = "PasswordActivity";
    private ImageView back;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText password, c_password;
    private ProgressDialog progressDialog;
    private RelativeLayout R11;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private CheckBox checkBox;
    private TextView tvPrivacyPolicy;
    private NetworkUtils networkUtils;
    private LinearLayout llInternetDialog;
    private Button btnCancel, btnRetry;
    private ImageButton btnNext;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_password);
        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        R11 = findViewById(R.id.R11);
        password = findViewById(R.id.password);
        c_password = findViewById(R.id.c_password);
        back = findViewById(R.id.back);
        btnNext = findViewById(R.id.next);
        llInternetDialog = findViewById(R.id.llInternetDialog);
        btnRetry = findViewById(R.id.btn_retry);
        btnCancel = findViewById(R.id.btn_cancel);
        checkBox = findViewById(R.id.chkPrivacyPolicy);
        tvPrivacyPolicy = findViewById(R.id.tvPrivacyPolicy);

        tvPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnRetry.setOnClickListener(v -> {
            llInternetDialog.setVisibility(View.GONE);
            nextPage();
        });

        btnCancel.setOnClickListener(v -> llInternetDialog.setVisibility(View.GONE));

        btnNext.setOnClickListener(v -> {
            nextPage();
        });

        networkUtils = new NetworkUtils(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void nextPage() {
        String Password = password.getText().toString().trim();
        String CPassword = c_password.getText().toString().trim();
        if (Password.isEmpty()) {
            password.setError("Please enter your password");
            return;
        }
        if (Utils.passwordValidator(Password)) {
            password.setError(Utils.invalidPasswordMessage());
            return;
        }
        if (CPassword.isEmpty()) {
            c_password.setError("Password confirm your password");
            return;
        }
        if (!(Password.compareTo(CPassword) == 0)) {
            Toast.makeText(PasswordActivity.this, "Password doesn't match", Toast.LENGTH_LONG).show();
            return;
        }
        if (!checkBox.isChecked()) {
            checkBox.setButtonTintList(getColorStateList(R.color.view_color_red));
            Toast.makeText(this, "Please accept Route Privacy Policy", Toast.LENGTH_LONG).show();
            return;
        }
        editor.putString(USER_PASSWORD, Password);
        editor.apply();

        String firstName = pref.getString(FirstName, "");
        String lastName = pref.getString(LastName, "");
        String surName = pref.getString(SurName, "");
        String username = pref.getString(UserName, "");
        String idNumber = pref.getString(ID_NUMBER, "");
        String phoneNumber = currentUser.getPhoneNumber();
        String emailAdd = pref.getString(USER_EMAIL, "");

        progressDialog = new ProgressDialog(PasswordActivity.this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        if (networkUtils.isNetworkAvailable()) {
            try {
                progressDialog.show();
                Constants.sendSignInRequest(PasswordActivity.this,
                        firstName, lastName,
                        surName, username,
                        Password, idNumber,
                        phoneNumber, emailAdd)
                        .setCallback((e, result) -> {
                            Log.d(TAG, "onCompleted: " + result);

                            if (result != null) {
                                if (result.get("status").toString().contains("failed")) {
                                    progressDialog.dismiss();
                                    if (result.has("errors")) {
                                        Snackbar snackbar = Snackbar.make(R11, result.get("errors").getAsJsonArray().get(0).getAsString(), Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    }
                                } else {
                                    editor.clear();
                                    editor.apply();

                                    progressDialog.dismiss();
                                    Intent intent = new Intent(PasswordActivity.this, SignupVerifiedActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            } else {
                                progressDialog.dismiss();
                                Snackbar snackbar = Snackbar
                                        .make(R11, "Unable to verify phone number", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        });
            } catch (
                    Exception ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            llInternetDialog.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
