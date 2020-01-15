package com.beyondthehorizon.routeapp.Views.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.Views.OtpVerificationActivity;
import com.beyondthehorizon.routeapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import static com.beyondthehorizon.routeapp.utils.Constants.ID_NUMBER;
import static com.beyondthehorizon.routeapp.utils.Constants.MyPhoneNumber;
import static com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.routeapp.utils.Constants.USER_EMAIL;
import static com.beyondthehorizon.routeapp.utils.Constants.UserName;

public class PhoneActivity extends AppCompatActivity {

    private static final String TAG = "PhoneActivity";
    private ImageView back;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText phone;
    private CountryCodePicker ccp;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextView exitsTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        phone = findViewById(R.id.phone);
        ccp.registerPhoneNumberTextView(phone);
        exitsTxt = findViewById(R.id.exitsTxt);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (!(pref.getString(MyPhoneNumber, "nop").compareTo("nop") == 0)) {
            phone.setText(pref.getString(MyPhoneNumber, "").substring(pref.getString(MyPhoneNumber, "").length() - 9));
        }

    }

    public void nextPage(View view) {
        String phoneNumber = phone.getText().toString().trim();
        if (phoneNumber.length() < 9) {
            phone.setError("Add a valid phone number");
            return;
        }

        progressDialog = new ProgressDialog(PhoneActivity.this);
        progressDialog.setMessage("verifying please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Constants.verifyUserEntry(PhoneActivity.this,
                "phone_number", ccp.getNumber())
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.d(TAG, "onCompleted: " + result);
                        if (result != null) {
                            progressDialog.dismiss();
                            if (result.get("status").toString().contains("failed")) {
                                exitsTxt.setVisibility(View.VISIBLE);
                                String theError = "";
                                if (result.get("errors").toString().contains("phone_number")) {
                                    theError += "This phone number is already registered with another account. Use a different phone number !";
                                }
                                exitsTxt.setText(theError);
                            } else {
                                progressDialog.dismiss();
                                exitsTxt.setVisibility(View.GONE);
                                editor.putString(MyPhoneNumber, ccp.getNumber());
                                editor.apply();
                                if (currentUser != null) {
                                    String ccphone = ccp.getNumber().substring(ccp.getNumber().length() - 9);
                                    String firephone = currentUser.getPhoneNumber().substring(currentUser.getPhoneNumber().length() - 9);
                                    if (ccphone.compareTo(firephone) == 0) {
                                        startActivity(new Intent(PhoneActivity.this, PasswordActivity.class));
                                    } else {
                                        startActivity(new Intent(PhoneActivity.this, OtpVerificationActivity.class));
                                    }
                                } else {
                                    startActivity(new Intent(PhoneActivity.this, OtpVerificationActivity.class));
                                }

                            }
                        } else {
                            progressDialog.dismiss();
                            exitsTxt.setVisibility(View.VISIBLE);
                            exitsTxt.setText("Unable to verify phone number");
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
