package com.beyondthehorizon.routeapp.Views.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.Views.OtpVerificationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import static com.beyondthehorizon.routeapp.utils.Constants.ID_NUMBER;
import static com.beyondthehorizon.routeapp.utils.Constants.MyPhoneNumber;
import static com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.routeapp.utils.Constants.USER_EMAIL;

public class PhoneActivity extends AppCompatActivity {

    ImageView back;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    EditText phone;
    CountryCodePicker ccp;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

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

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void nextPage(View view) {
        String phoneNumber = phone.getText().toString().trim();
        if (phoneNumber.length() < 9) {
            phone.setError("Add a valid phone number");
            return;
        }

        if (currentUser != null) {
            String regPhone = currentUser.getPhoneNumber().substring(currentUser.getPhoneNumber().length() - 9);
            String newPhone = phoneNumber.substring(phoneNumber.length() - 9);

            if (regPhone.contains(newPhone)) {
                startActivity(new Intent(PhoneActivity.this, PasswordActivity.class));
            } else {
                new AlertDialog.Builder(PhoneActivity.this, R.style.MyDialogTheme)
                        .setTitle("Change Phone Number")
                        .setMessage("Change registered phone number from " + currentUser.getPhoneNumber() +
                                " to " + ccp.getFullNumberWithPlus() + " ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                startActivity(new Intent(PhoneActivity.this, OtpVerificationActivity.class));
                                editor.putString(MyPhoneNumber, ccp.getFullNumberWithPlus());
                                editor.apply();
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(PhoneActivity.this, PasswordActivity.class));

                                editor.putString(MyPhoneNumber, currentUser.getPhoneNumber());
                                editor.apply();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        } else {
            startActivity(new Intent(PhoneActivity.this, OtpVerificationActivity.class));
            editor.putString(MyPhoneNumber, ccp.getFullNumberWithPlus());
            editor.apply();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
