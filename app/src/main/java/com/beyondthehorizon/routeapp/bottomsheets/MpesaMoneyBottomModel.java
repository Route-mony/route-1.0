package com.beyondthehorizon.routeapp.bottomsheets;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.utils.Constants;
import com.beyondthehorizon.routeapp.views.FundRequestedActivity;
import com.beyondthehorizon.routeapp.views.RequestFundsActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import static com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.routeapp.utils.Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY;
import static com.beyondthehorizon.routeapp.utils.Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY_TO_BANK;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY_TO_MOBILE_MONEY;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY_TO_ROUTE;
import static com.beyondthehorizon.routeapp.utils.Constants.USER_TOKEN;

public class MpesaMoneyBottomModel extends BottomSheetDialogFragment {
    private MpesaBottomSheetListener mListener;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    public static final String TAG = "MpesaMoneyBottomModel";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mpesa_payment_options, container, false);
        pref = getActivity().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();


        final LinearLayout parentLayout = v.findViewById(R.id.parentLayout);
        final LinearLayout buyGoodsLayout = v.findViewById(R.id.buyGoodsLayout);
        final LinearLayout payBillLayout = v.findViewById(R.id.payBillLayout);

        LinearLayout buyGoods = v.findViewById(R.id.buyGoods);
        LinearLayout payBill = v.findViewById(R.id.payBill);

        //BUY GOODS
        final EditText tillNumber = v.findViewById(R.id.tillNumber);
        final EditText tillAmount = v.findViewById(R.id.tillAmount);
        Button buyGoodsButton = v.findViewById(R.id.buyGoodsButton);

        //PAYBILL
        final EditText businessNumber = v.findViewById(R.id.businessNumber);
        final EditText businessAccount = v.findViewById(R.id.businessAccount);
        final EditText businessAmount = v.findViewById(R.id.businessAmount);
        Button payBillButton = v.findViewById(R.id.payBillButton);

        buyGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentLayout.setVisibility(View.GONE);
                payBillLayout.setVisibility(View.GONE);
                buyGoodsLayout.setVisibility(View.VISIBLE);
            }
        });

        payBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentLayout.setVisibility(View.GONE);
                buyGoodsLayout.setVisibility(View.GONE);
                payBillLayout.setVisibility(View.VISIBLE);
            }
        });

        buyGoodsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String till = tillNumber.getText().toString().trim();
                String amount = tillAmount.getText().toString().trim();
                if (till.isEmpty()) {
                    tillNumber.setError("Can not be empty");
                    return;
                }
                if (amount.isEmpty()) {
                    tillAmount.setError("Can not be empty");
                    return;
                }
//                makePayments(v,
//                        amount,
//                        till,
//                        ""
//                );

                mListener.mpesaBottomSheetListener(
                        amount,
                        till,
                        ""
                );
                dismiss();
            }
        });

        payBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Businessnumber = businessNumber.getText().toString().trim();
                String businessaccount = businessAccount.getText().toString().trim();
                String amount = businessAmount.getText().toString().trim();

                if (Businessnumber.isEmpty()) {
                    businessNumber.setError("Can not be empty");
                    return;
                }
                if (businessaccount.isEmpty()) {
                    businessAccount.setError("Can not be empty");
                    return;
                }
                if (amount.isEmpty()) {
                    businessAmount.setError("Can not be empty");
                    return;
                }
//                makePayments(v,
//                        amount,
//                        Businessnumber,
//                        businessaccount
//                );
                mListener.mpesaBottomSheetListener(
                        amount,
                        Businessnumber,
                        businessaccount
                );
                dismiss();
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (MpesaBottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    public interface MpesaBottomSheetListener {
        void mpesaBottomSheetListener(String amount, String ben_account, String ben_ref);
    }
}
