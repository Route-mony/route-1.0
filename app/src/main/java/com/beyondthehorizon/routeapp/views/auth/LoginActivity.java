package com.beyondthehorizon.routeapp.views.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.utils.Constants;
import com.beyondthehorizon.routeapp.views.MainActivity;
import com.beyondthehorizon.routeapp.views.settingsactivities.ResetPasswordActivity;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import static com.beyondthehorizon.routeapp.utils.Constants.LOGGED_IN;
import static com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.routeapp.utils.Constants.USER_EMAIL;
import static com.beyondthehorizon.routeapp.utils.Constants.USER_TOKEN;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private ProgressDialog progressDialog;
    private RelativeLayout R11;
    private TextView forgotPassword;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        forgotPassword = findViewById(R.id.forgotPassword);
        R11 = findViewById(R.id.R11);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    public void nextPage(View view) {
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();
        if (Email.isEmpty()) {
            email.setError("Enter your email");
            return;
        }
        if (Password.isEmpty()) {
            password.setError("Enter your password");
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
        progressDialog.show();

        Constants.sendLogInRequest(LoginActivity.this,
                Password, Email)
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        Log.d("TAG", "onCompleted: " + result);

                        if (result != null) {
                            if (result.get("status").toString().contains("failed")) {
                                Toast.makeText(LoginActivity.this, "A user with this email and password was not found.", Toast.LENGTH_LONG).show();
                            } else {
                                String email = result.get("data").getAsJsonObject().get("email").getAsString();
                                String token = result.get("data").getAsJsonObject().get("token").getAsString();

                                editor.putString(LOGGED_IN, "true");
                                editor.putString(USER_EMAIL, email);
                                editor.putString(USER_TOKEN, token);
                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Unable to login. Try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void prevPage(View view) {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
