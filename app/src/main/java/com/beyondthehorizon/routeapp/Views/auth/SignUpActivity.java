package com.beyondthehorizon.routeapp.Views.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.Views.MainActivity;
import com.beyondthehorizon.routeapp.utils.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import static com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    EditText first_name, sur_name, last_name, email, phone, id_number, user_name, password;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ProgressDialog progressDialog;
    private ScrollView scrollView;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        first_name = findViewById(R.id.first_name);
        sur_name = findViewById(R.id.middle_name);
        last_name = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        id_number = findViewById(R.id.id_number);
        user_name = findViewById(R.id.user_name);
        password = findViewById(R.id.password);
        scrollView = findViewById(R.id.scrollView);

        phone.setText(currentUser.getPhoneNumber());

    }

    private void signUp() {

        sharedpreferences = getSharedPreferences(REG_APP_PREFERENCES, Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        String firstName = first_name.getText().toString().trim();
        String lastName = last_name.getText().toString().trim();
        String surName = sur_name.getText().toString().trim();
        String emailAdd = email.getText().toString().trim();
        String phoneNumber = phone.getText().toString().trim();
        String username = user_name.getText().toString().trim();
        String idNumber = id_number.getText().toString().trim();
        String Password = password.getText().toString().trim();

        if (firstName.isEmpty()) {
            first_name.setError("First name cannot be empty");
            return;
        }
        if (lastName.isEmpty()) {
            last_name.setError("Last name cannot be empty");
            return;
        }
        if (surName.isEmpty()) {
            sur_name.setError("Surname cannot be empty");
            return;
        }
        if (username.isEmpty()) {
            user_name.setError("username cannot be empty");
            return;
        }
        if (emailAdd.isEmpty()) {
            email.setError("Email cannot be empty");
            return;
        }
        if (phoneNumber.length() < 10) {
            phone.setError("Add a valid phone number");
            return;
        }
        if (idNumber.isEmpty()) {
            id_number.setError("ID number cannot be empty");
            return;
        }
        if (Password.length() < 8) {
            password.setError("Password cannot be less than 8 characters");
            return;
        }

        progressDialog.show();

        Constants.sendSignInRequest(SignUpActivity.this,
                firstName, lastName,
                surName, username,
                Password, idNumber,
                phoneNumber, emailAdd)
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        Log.d("TAG", "onCompleted: " + result);

                        if (result.get("status").toString().contains("failed")) {
                            Snackbar snackbar = Snackbar
                                    .make(scrollView, result.get("errors").toString(), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else if (result == null) {
                            Snackbar snackbar = Snackbar
                                    .make(scrollView, "Error ", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
//                            SharedPreferences.Editor editor = sharedpreferences.edit();
//                            editor.putString(Companies, jObject.getString("companies"));
//                            editor.apply();
                        }

                        // do stuff with the result or error
                    }
                });
    }

    public void signUpUser(View view) {

        signUp();
    }
}
