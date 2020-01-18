package com.beyondthehorizon.routeapp.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.Views.auth.LoginActivity;
import com.beyondthehorizon.routeapp.Views.auth.SetTransactionPinActivity;
import com.beyondthehorizon.routeapp.utils.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import static com.beyondthehorizon.routeapp.utils.Constants.LOGGED_IN;
import static com.beyondthehorizon.routeapp.utils.Constants.MyPhoneNumber;
import static com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.routeapp.utils.Constants.TRANSACTIONS_PIN;
import static com.beyondthehorizon.routeapp.utils.Constants.USER_EMAIL;
import static com.beyondthehorizon.routeapp.utils.Constants.USER_TOKEN;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ImageView profile_pic;
    private TextView user_name, query_text, balance_title, balance_value, verify_email;
    private Button add_money_button;
    private RelativeLayout RL1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();
        setContentView(R.layout.activity_main);

        user_name = findViewById(R.id.user_name);
        query_text = findViewById(R.id.query_text);
        balance_title = findViewById(R.id.balance_title);
        balance_value = findViewById(R.id.balance_value);
        add_money_button = findViewById(R.id.add_money_button);
        verify_email = findViewById(R.id.verify_email);
        profile_pic = findViewById(R.id.profile_pic);
        RL1 = findViewById(R.id.RL1);

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

                                String email_verified = result.get("data").getAsJsonObject().get("is_email_active").toString();
                                String is_pin_set = result.get("data").getAsJsonObject().get("is_pin_set").toString();

                                if (email_verified.contains("False")) {
                                    verify_email.setVisibility(View.VISIBLE);
                                }
                                if (is_pin_set.contains("False")) {
                                    setPin();
                                }
//                            editor.putString(LOGGED_IN, "true");
//                            editor.putString(USER_EMAIL, result.get("data").getAsJsonObject().get("email").toString());
//                            editor.putString(USER_TOKEN, result.get("data").getAsJsonObject().get("token").toString());
//                            editor.apply();

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
}
