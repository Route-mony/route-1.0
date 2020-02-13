package com.beyondthehorizon.routeapp.views.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.utils.Constants;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import static com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.routeapp.utils.Constants.UserName;

public class UserNameActivity extends AppCompatActivity {

    private static final String TAG = "UserNameActivity";
    private EditText user_name;
    private TextView exitsTxt;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);

        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();
        user_name = findViewById(R.id.user_name);
        exitsTxt = findViewById(R.id.exitsTxt);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (!(pref.getString(UserName, "nop").compareTo("nop") == 0)) {
            user_name.setText(pref.getString(UserName, ""));
        }
    }

    public void nextPage(View view) {
        final String username = user_name.getText().toString().trim();

        if (username.isEmpty()) {
            user_name.setError("username cannot be empty");
            return;
        }

        progressDialog = new ProgressDialog(UserNameActivity.this);
        progressDialog.setMessage("verifying please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Constants.verifyUserEntry(UserNameActivity.this,
                "username", username)
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        Log.d(TAG, "onCompleted: " + result);
                        if (result != null) {
                            progressDialog.dismiss();
                            if (result.get("status").toString().contains("failed")) {
                                exitsTxt.setVisibility(View.VISIBLE);
                                String theError = "";
                                if (result.get("errors").toString().contains("username")) {
                                    theError += "username is taken.";
                                }
                                exitsTxt.setText(theError);
                            } else {
                                progressDialog.dismiss();
                                exitsTxt.setVisibility(View.GONE);
                                editor.putString(UserName, username);
                                editor.apply();
                                startActivity(new Intent(UserNameActivity.this, EmailIDActivity.class));
                            }
                        } else {
                            progressDialog.dismiss();
                            exitsTxt.setVisibility(View.VISIBLE);
                            exitsTxt.setText("Unable to verify username");
                        }
                    }
                });

    }
}
