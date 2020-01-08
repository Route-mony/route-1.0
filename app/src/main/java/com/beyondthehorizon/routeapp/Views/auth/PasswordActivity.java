package com.beyondthehorizon.routeapp.Views.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.Views.MainActivity;
import com.beyondthehorizon.routeapp.Views.OtpVerificationActivity;
import com.beyondthehorizon.routeapp.Views.SignupVerifiedActivity;
import com.beyondthehorizon.routeapp.utils.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.beyondthehorizon.routeapp.utils.Constants.FirstName;
import static com.beyondthehorizon.routeapp.utils.Constants.ID_NUMBER;
import static com.beyondthehorizon.routeapp.utils.Constants.LOGGED_IN;
import static com.beyondthehorizon.routeapp.utils.Constants.LastName;
import static com.beyondthehorizon.routeapp.utils.Constants.MyPhoneNumber;
import static com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.routeapp.utils.Constants.SurName;
import static com.beyondthehorizon.routeapp.utils.Constants.USER_EMAIL;
import static com.beyondthehorizon.routeapp.utils.Constants.USER_PASSWORD;
import static com.beyondthehorizon.routeapp.utils.Constants.UserName;

public class PasswordActivity extends AppCompatActivity {

    private ImageView back;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText password;
    private ProgressDialog progressDialog;
    private RelativeLayout R11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();

        R11 = findViewById(R.id.R11);
        password = findViewById(R.id.password);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void nextPage(View view) {
        String Password = password.getText().toString().trim();
        if (Password.length() < 8) {
            password.setError("Password cannot be less than 8 characters");
            return;
        }
        if (!(isValidPassword(Password))) {
            password.setError("Password must contain at least a number,a uppercase and lowercase alphabet");
            return;
        }
        editor.putString(USER_PASSWORD, Password);
        editor.apply();

        String firstName = pref.getString(FirstName, "");
        String lastName = pref.getString(LastName, "");
        String surName = pref.getString(SurName, "");
        String username = pref.getString(UserName, "");
        String idNumber = pref.getString(ID_NUMBER, "");
        String phoneNumber = pref.getString(MyPhoneNumber, "");
        String emailAdd = pref.getString(USER_EMAIL, "");

        progressDialog = new ProgressDialog(PasswordActivity.this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Constants.sendSignInRequest(PasswordActivity.this,
                firstName, lastName,
                surName, username,
                Password, idNumber,
                "+25472389434343", emailAdd)
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        Log.d("TAG", "onCompleted: " + result);

                        if (result.get("status").toString().contains("failed")) {
                            Snackbar snackbar = Snackbar
                                    .make(R11, result.get("errors").toString(), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else if (result == null) {
                            Snackbar snackbar = Snackbar
                                    .make(R11, "Error ", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            editor.putString(LOGGED_IN, "true");
                            editor.apply();

                            Intent intent = new Intent(PasswordActivity.this, SignupVerifiedActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                });
    }

    //*****************************************************************
    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
