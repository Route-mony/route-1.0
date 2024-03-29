package com.beyondthehorizon.route.views.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.beyondthehorizon.route.R;

import static com.beyondthehorizon.route.utils.Constants.FirstName;
import static com.beyondthehorizon.route.utils.Constants.LastName;
import static com.beyondthehorizon.route.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.route.utils.Constants.SurName;

public class UserNamesActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText first_name, sur_name, last_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_names);
        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();

        first_name = findViewById(R.id.first_name);
        sur_name = findViewById(R.id.middle_name);
        last_name = findViewById(R.id.last_name);
    }

    public void nextPage(View view) {

        String firstName = first_name.getText().toString().trim();
        String lastName = last_name.getText().toString().trim();
        String surName = sur_name.getText().toString().trim();

        if (firstName.isEmpty()) {
            first_name.setError("First name cannot be empty");
            return;
        }
        if (lastName.isEmpty()) {
            last_name.setError("Last name cannot be empty");
            return;
        }
//        if (surName.isEmpty()) {
//            sur_name.setError("Surname cannot be empty");
//            return;
//        }

        editor.putString(FirstName, firstName);
        editor.putString(LastName, lastName);
        editor.putString(SurName, surName);
        editor.apply();
        startActivity(new Intent(UserNamesActivity.this, UserNameActivity.class));
    }
}
