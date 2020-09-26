package com.beyondthehorizon.route.views.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondthehorizon.route.R;
import com.beyondthehorizon.route.views.MainActivity;
import com.beyondthehorizon.route.utils.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import static com.beyondthehorizon.route.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.route.utils.Constants.TRANSACTIONS_PIN;
import static com.beyondthehorizon.route.utils.Constants.USER_TOKEN;

public class SetTransactionPinActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private TextView pin1, confirmPass;
    private ProgressDialog progressDialog;
    private LinearLayout RL3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_transaction_pin);

        pin1 = findViewById(R.id.pin1);
        confirmPass = findViewById(R.id.confirmPass);
        RL3 = findViewById(R.id.RL3);
        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();
    }

    public void submitPin(View view) {
        String pin = pin1.getText().toString().trim();
        String confirmPin = confirmPass.getText().toString().trim();
        String token = "Bearer ".concat(pref.getString(USER_TOKEN, ""));
        if (pin.isEmpty() || pin.length() < 4) {
            pin1.setError("Enter a valid Pin");
            return;
        }
        if (confirmPin.isEmpty()) {
            confirmPass.setError("Enter a valid Pin");
            return;
        }
        if (!(pin.compareTo(confirmPin)==0)) {
            Toast.makeText(SetTransactionPinActivity.this, "Pin do not match", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog = new ProgressDialog(SetTransactionPinActivity.this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Constants.setUserPin(SetTransactionPinActivity.this, token, pin)
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        Log.d("TAG123", "onCompleted: " + result);

                        if (result.get("status").toString().contains("failed")) {
                            Snackbar snackbar = Snackbar
                                    .make(RL3, "A user with this email and password was not found.", Snackbar.LENGTH_LONG);
                            snackbar.show();
//                        } else if (result == null) {
//                            Snackbar snackbar = Snackbar
//                                    .make(R11, "Error ", Snackbar.LENGTH_LONG);
//                            snackbar.show();
                        } else if (result.get("status").toString().contains("success")) {

                            Toast.makeText(SetTransactionPinActivity.this,
                                    result.get("data").getAsJsonObject().get("message").toString(), Toast.LENGTH_SHORT).show();

                            editor.putString(TRANSACTIONS_PIN, "set");
                            editor.apply();
                            Intent intent = new Intent(SetTransactionPinActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                });
    }
}
