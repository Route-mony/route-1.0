package com.beyondthehorizon.routeapp.payments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.beyondthehorizon.routeapp.R;

public class PaymentActivity extends AppCompatActivity {

    private LinearLayout buyGoods, payBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        payBill = findViewById(R.id.payBill);
        buyGoods = findViewById(R.id.buyGoods);

        payBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentActivity.this, PayBillActivity.class));
            }
        });

        buyGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentActivity.this, BuyGoodsActivity.class));
            }
        });
    }

    public void prevPage(View view) {
        onBackPressed();
    }
}
