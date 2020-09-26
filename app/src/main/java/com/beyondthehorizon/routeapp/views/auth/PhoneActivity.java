package com.beyondthehorizon.routeapp.views.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.utils.Constants;
import com.beyondthehorizon.routeapp.utils.NetworkUtils;
import com.beyondthehorizon.routeapp.utils.Utils;
import com.beyondthehorizon.routeapp.views.OtpVerificationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import static com.beyondthehorizon.routeapp.utils.Constants.MyPhoneNumber;
import static com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES;

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
    private NetworkUtils networkUtils;
    private LinearLayout llInternetDialog;
    private Button btnCancel, btnRetry;
    private ImageButton btnNext;

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
        networkUtils = new NetworkUtils(this);
        llInternetDialog = findViewById(R.id.llInternetDialog);
        btnCancel = findViewById(R.id.btn_cancel);
        btnRetry = findViewById(R.id.btn_retry);
        btnNext = findViewById(R.id.next);

        back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());

        btnRetry.setOnClickListener(v -> {
            llInternetDialog.setVisibility(View.GONE);
            nextPage();
        });

        btnCancel.setOnClickListener(v -> llInternetDialog.setVisibility(View.GONE));

        btnNext.setOnClickListener(v -> {
            nextPage();
        });

        if (!(pref.getString(MyPhoneNumber, "nop").compareTo("nop") == 0)) {
            phone.setText(pref.getString(MyPhoneNumber, "").substring(pref.getString(MyPhoneNumber, "").length() - 9));
        }

    }

    public void nextPage() {
        String phoneNumber = phone.getText().toString().trim();
        if (!Utils.isPhoneNumberValid(phoneNumber, ccp.getSelectedCountryNameCode())) {
            phone.setError("Enter a valid phone number");
            return;
        }

        progressDialog = new ProgressDialog(PhoneActivity.this);
        progressDialog.setMessage("verifying please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        if (networkUtils.isNetworkAvailable()) {
            try {
                progressDialog.show();

                Constants.verifyUserEntry(PhoneActivity.this,
                        "phone_number", ccp.getNumber())
                        .setCallback((e, result) -> {
                            Log.d(TAG, "onCompleted: " + result);
                            if (result != null) {
                                progressDialog.dismiss();
                                if (result.get("status").toString().contains("failed")) {
                                    exitsTxt.setVisibility(View.VISIBLE);
                                    String theError = "";
                                    if(result.has("errors")){
                                        theError = result.get("errors").getAsJsonArray().get(0).getAsString();
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
