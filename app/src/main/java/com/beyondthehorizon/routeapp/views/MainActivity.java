package com.beyondthehorizon.routeapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.views.auth.LoginActivity;
import com.beyondthehorizon.routeapp.views.auth.SetTransactionPinActivity;
import com.beyondthehorizon.routeapp.utils.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import static com.beyondthehorizon.routeapp.utils.Constants.LOGGED_IN;
import static com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.routeapp.utils.Constants.REQUEST_MONEY;
import static com.beyondthehorizon.routeapp.utils.Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY;
import static com.beyondthehorizon.routeapp.utils.Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY_TO_BANK;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY_TO_MOBILE_MONEY;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY_TO_ROUTE;
import static com.beyondthehorizon.routeapp.utils.Constants.TRANSACTIONS_PIN;
import static com.beyondthehorizon.routeapp.utils.Constants.USER_TOKEN;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ImageView profile_pic, btn_notifications;
    private TextView user_name, query_text, balance_title, balance_value, verify_email;
    private Button add_money_button;
    private ImageButton btn_request_fund, btn_request34;
    private RelativeLayout RL1;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();
        setContentView(R.layout.activity_main);

        btn_request34 = findViewById(R.id.btn_request34);
        user_name = findViewById(R.id.user_name);
        query_text = findViewById(R.id.query_text);
        balance_title = findViewById(R.id.balance_title);
        balance_value = findViewById(R.id.balance_value);
        add_money_button = findViewById(R.id.add_money_button);
        verify_email = findViewById(R.id.verify_email);
        profile_pic = findViewById(R.id.profile_pic);
        RL1 = findViewById(R.id.RL1);
        btn_request_fund = findViewById(R.id.btn_request);
        btn_notifications = findViewById(R.id.notifications);

        intent = new Intent(this, RequestFundsActivity.class);

        btn_request_fund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, REQUEST_MONEY);
                editor.apply();
                startActivity(intent);
            }
        });

        btn_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        btn_request34.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSendMoneyDialog();
            }
        });

        isLoggedIn();


    }

    private void isLoggedIn() {
        if (pref.getString(USER_TOKEN, "").isEmpty()) {
            editor.putString(LOGGED_IN, "false");
            editor.apply();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            getProfile();
        }
    }


    private void getProfile() {
        String token = "Bearer ".concat(pref.getString(USER_TOKEN, ""));

        Log.d(TAG, "getProfile: " + token);
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Constants.getUserProfile(MainActivity.this, token)
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        Log.d(TAG, "getUserProfile: " + result);
                        if (result != null) {

                            if (result.get("status").toString().contains("failed")) {
                                Snackbar snackbar = Snackbar
                                        .make(RL1, "A user with this email and password was not found.", Snackbar.LENGTH_LONG);
                                snackbar.show();

                            } else if (result.get("status").toString().contains("success")) {

                                String name = result.get("data").getAsJsonObject().get("username").toString();
                                String username = "Hey " + name.substring(1, name.length() - 1) + " !";

                                user_name.setText(username);

                                boolean email_verified = result.get("data").getAsJsonObject().get("is_email_active").getAsBoolean();
                                String is_pin_set = result.get("data").getAsJsonObject().get("is_pin_set").toString();

                                if (!email_verified) {
                                    verify_email.setVisibility(View.VISIBLE);
                                } else {
                                    verify_email.setVisibility(View.GONE);
                                }
                                if (is_pin_set.contains("False")) {
                                    setPin();
                                }

                            }
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(RL1, "Unable to load data ", Snackbar.LENGTH_LONG);
                            snackbar.setAction("Try again", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (pref.getString(USER_TOKEN, "").isEmpty()) {
                                        editor.putString(LOGGED_IN, "false");
                                        editor.apply();
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        getProfile();
                                    }
                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

    private void setPin() {
        if (pref.getString(TRANSACTIONS_PIN, "").isEmpty()) {
            startActivity(new Intent(this, SetTransactionPinActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isLoggedIn();
    }


    private void showSendMoneyDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_send_money_alert_dialog_layout, viewGroup, false);
        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        LinearLayout toRoute = dialogView.findViewById(R.id.toRoute);
        LinearLayout toMobileMoney = dialogView.findViewById(R.id.toMobileMoney);
        LinearLayout toBank = dialogView.findViewById(R.id.toBank);

        //SEND MONEY TO ROUTE
        toRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RequestFundsActivity.class);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SEND_MONEY);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_ROUTE);
                editor.apply();
                startActivity(intent);
            }
        });
        //SEND MONEY TO MOBILE MONEY
        toMobileMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SEND_MONEY);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_MOBILE_MONEY);
                editor.apply();
                showSendMobileMoneyDialog();
                alertDialog.dismiss();
            }
        });
        //SEND MONEY TO BANK
        toBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SEND_MONEY);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_BANK);
                editor.apply();
                alertDialog.dismiss();
                showSendMoneyToBankDialog();
            }
        });
    }

    private void showSendMobileMoneyDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.send_mobile_money_dialog_layout, viewGroup, false);
        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final EditText mobileNumber = dialogView.findViewById(R.id.mobileNumber);
        Button mobileButton = dialogView.findViewById(R.id.mobileButton);
        ImageView imgSearch = dialogView.findViewById(R.id.imgSearch);

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RequestFundsActivity.class);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SEND_MONEY);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_MOBILE_MONEY);
                editor.apply();
                startActivity(intent);
            }
        });

        //SEND MONEY TO MOBILE MONEY
        mobileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobileNumber.getText().toString().isEmpty() || mobileNumber.getText().toString().length() < 10) {
                    Toast.makeText(MainActivity.this, "Enter a valid phone number", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, FundAmountActivity.class);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SEND_MONEY);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_MOBILE_MONEY);
                editor.apply();
                startActivity(intent);
            }
        });
    }

    private void showSendMoneyToBankDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.send_money_to_bank_dialog_layout, viewGroup, false);
        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final EditText accountNumber = dialogView.findViewById(R.id.accountNumber);
        Button mobileButton = dialogView.findViewById(R.id.mobileButton);
        final Spinner chooseBank = dialogView.findViewById(R.id.chooseBank);

        //SEND MONEY TO BANK
        mobileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountNumber.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter a valid phone number", Toast.LENGTH_LONG).show();
                    accountNumber.setError("Enter a valid phone number");
                    accountNumber.requestFocus();
                    return;
                }
                if (chooseBank.getSelectedItemPosition() == 0) {
                    Toast.makeText(MainActivity.this, "Choose a bank", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, FundAmountActivity.class);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SEND_MONEY);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_MOBILE_MONEY);
                editor.apply();
                startActivity(intent);
            }
        });
    }
}
