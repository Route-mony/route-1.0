package com.beyondthehorizon.route.views.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.beyondthehorizon.route.R;
import com.beyondthehorizon.route.utils.NetworkUtils;
import com.beyondthehorizon.route.utils.Utils;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import static com.beyondthehorizon.route.utils.Constants.BASE_URL;
import static com.beyondthehorizon.route.utils.Constants.ID_NUMBER;
import static com.beyondthehorizon.route.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.route.utils.Constants.USER_EMAIL;

public class EmailIDActivity extends AppCompatActivity {

    private ImageView back;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText email, id_number;
    private TextView exitsTxt;
    private ProgressDialog progressDialog;
    private NetworkUtils networkUtils;
    private LinearLayout llInternetDialog;
    private Button btnCancel, btnRetry;
    private ImageButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_id);

        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();

        back = findViewById(R.id.back);
        email = findViewById(R.id.email);
        exitsTxt = findViewById(R.id.exitsTxt);
        id_number = findViewById(R.id.id_number);
        networkUtils = new NetworkUtils(this);
        llInternetDialog = findViewById(R.id.llInternetDialog);
        btnCancel = findViewById(R.id.btn_cancel);
        btnRetry = findViewById(R.id.btn_retry);
        btnNext = findViewById(R.id.next);

        btnRetry.setOnClickListener(v -> {
            llInternetDialog.setVisibility(View.GONE);
            nextPage();
        });
        btnCancel.setOnClickListener(v -> llInternetDialog.setVisibility(View.GONE));

        btnNext.setOnClickListener(v->{
            nextPage();
        });
        back.setOnClickListener(v -> onBackPressed());

        if (!(pref.getString(ID_NUMBER, "nop").compareTo("nop") == 0)) {
            id_number.setText(pref.getString(ID_NUMBER, ""));
            email.setText(pref.getString(USER_EMAIL, ""));
        }
    }

    public void nextPage() {
        final String emailAdd = email.getText().toString().trim();
        final String idNumber = id_number.getText().toString().trim();

        if (idNumber.isEmpty()) {
            id_number.setError("ID number cannot be empty");
            return;
        }

        if (emailAdd.isEmpty()) {
            email.setError("Email cannot be empty");
            return;
        }
        if (!Utils.emailValidator(emailAdd)) {
            email.setError("Enter a valid email");
            return;
        }

        progressDialog = new ProgressDialog(EmailIDActivity.this);
        progressDialog.setMessage("verifying please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        String SERVER_URL = BASE_URL + "users/signup/validation";
        JsonObject json = new JsonObject();
        json.addProperty("id_number", idNumber);
        json.addProperty("email", emailAdd);

        if (networkUtils.isNetworkAvailable()) {
            try {
                progressDialog.show();
                Ion.with(EmailIDActivity.this)
                        .load(SERVER_URL)
                        .addHeader("Content-Type", "application/json")
                        .setJsonObjectBody(json)
                        .asJsonObject().setCallback((e, result) -> {

                    if (result != null) {
                        progressDialog.dismiss();
                        exitsTxt.setVisibility(View.VISIBLE);
                        if (result.get("status").toString().contains("failed")) {
                            String theError = "";
                            if (result.get("errors").toString().contains("Email")) {
                                theError += "Email is associated with another account.";
                            } else if (result.get("errors").toString().contains("id_number")) {
                                theError += "\nId number has an account already.";
                            }
                            exitsTxt.setText(theError);
                        } else {
                            progressDialog.dismiss();
                            exitsTxt.setVisibility(View.GONE);
                            editor.putString(ID_NUMBER, idNumber);
                            editor.putString(USER_EMAIL, emailAdd);
                            editor.apply();
                            startActivity(new Intent(EmailIDActivity.this, PhoneActivity.class));
                        }
                    } else {
                        progressDialog.dismiss();
                        exitsTxt.setVisibility(View.VISIBLE);
                        exitsTxt.setText("Unable to verify credentials");
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
