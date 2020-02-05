package com.beyondthehorizon.routeapp.payments;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.beyondthehorizon.routeapp.R;

public class PayBillActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_bill);
    }

    public void prevPage(View view) {
        onBackPressed();
    }
}
