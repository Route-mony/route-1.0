package com.beyondthehorizon.routeapp.Views.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.beyondthehorizon.routeapp.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.beyondthehorizon.routeapp.utils.Constants.FirstName;
import static com.beyondthehorizon.routeapp.utils.Constants.ID_NUMBER;
import static com.beyondthehorizon.routeapp.utils.Constants.LastName;
import static com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.routeapp.utils.Constants.SurName;
import static com.beyondthehorizon.routeapp.utils.Constants.USER_EMAIL;
import static com.beyondthehorizon.routeapp.utils.Constants.UserName;

public class EmailIDActivity extends AppCompatActivity {

    ImageView back;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    EditText email, id_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_id);

        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();

        back = findViewById(R.id.back);
        email = findViewById(R.id.email);
        id_number = findViewById(R.id.id_number);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void nextPage(View view) {
        String emailAdd = email.getText().toString().trim();
        String idNumber = id_number.getText().toString().trim();

        if (idNumber.isEmpty()) {
            id_number.setError("ID number cannot be empty");
            return;
        }

        if (emailAdd.isEmpty()) {
            email.setError("Email cannot be empty");
            return;
        }
        if (!(isEmailValid(emailAdd))) {
            email.setError("Enter a valid email");
            return;
        }

        editor.putString(ID_NUMBER, idNumber);
        editor.putString(USER_EMAIL, emailAdd);
        editor.apply();
        startActivity(new Intent(EmailIDActivity.this, PhoneActivity.class));
    }

    public static boolean isEmailValid(String email) {

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
