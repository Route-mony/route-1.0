package com.beyondthehorizon.routeapp.views.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.utils.Constants;
import com.beyondthehorizon.routeapp.utils.Utils;
import com.beyondthehorizon.routeapp.views.SignupVerifiedActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.future.FutureCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.beyondthehorizon.routeapp.utils.Constants.FirstName;
import static com.beyondthehorizon.routeapp.utils.Constants.ID_NUMBER;
import static com.beyondthehorizon.routeapp.utils.Constants.LastName;
import static com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.routeapp.utils.Constants.SurName;
import static com.beyondthehorizon.routeapp.utils.Constants.USER_EMAIL;
import static com.beyondthehorizon.routeapp.utils.Constants.USER_PASSWORD;
import static com.beyondthehorizon.routeapp.utils.Constants.UserName;

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
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void nextPage(View view) {
        String Password = password.getText().toString().trim();
        String CPassword = c_password.getText().toString().trim();
        if(Password.isEmpty()){
            password.setError("Please enter your password");
            return;
        }
        if(!Utils.passwordValidator(Password)){
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
        progressDialog.show();

        Constants.sendSignInRequest(PasswordActivity.this,
                firstName, lastName,
                surName, username,
                Password, idNumber,
                phoneNumber, emailAdd)
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.d(TAG, "onCompleted: " + result);
                        if (result != null) {
                            if (result.get("status").toString().contains("failed")) {
                                progressDialog.dismiss();
                                String theError = "";
                                if (result.get("errors").toString().contains("Email")) {
                                    theError += "Email already exist.";
                                } else if (result.get("errors").toString().contains("username")) {
                                    theError += "\nUsername already exist.";
                                } else if (result.get("errors").toString().contains("id_number")) {
                                    theError += "\nId number already exist.";
                                } else if (result.get("errors").toString().contains("phone_number")) {
                                    theError += "\nPhone number already exist.";

                                } else if (result.get("errors").toString().contains("surname")) {
                                    theError += "\nInvalid surname e.g avoid using special characters or numbers";
                                }
                                Snackbar snackbar = Snackbar
                                        .make(R11, theError, Snackbar.LENGTH_LONG);
                                snackbar.show();
                            } else {
                                editor.clear();
                                editor.apply();

                                progressDialog.dismiss();
//                                String email = result.get("data").getAsJsonObject().get("user").getAsJsonObject().get("email").toString();
//                                String token = result.get("data").getAsJsonObject().get("user").getAsJsonObject().get("token").toString();
//
//                                editor.putString(LOGGED_IN, "true");
//                                editor.putString(USER_EMAIL, email.substring(1, email.length() - 1));
//                                editor.putString(USER_TOKEN, token.substring(1, token.length() - 1));
//                                editor.apply();

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
