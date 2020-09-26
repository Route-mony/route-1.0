package com.beyondthehorizon.route.bottomsheets;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.beyondthehorizon.route.R;
import com.beyondthehorizon.route.utils.Constants;
import com.beyondthehorizon.route.views.FundAmountActivity;
import com.beyondthehorizon.route.views.FundRequestedActivity;
import com.beyondthehorizon.route.views.requestfunds.RequestFundActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.beyondthehorizon.route.utils.Constants.BANK_PROVIDERS;
import static com.beyondthehorizon.route.utils.Constants.PHONE_NUMBER;
import static com.beyondthehorizon.route.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.route.utils.Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY;
import static com.beyondthehorizon.route.utils.Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE;
import static com.beyondthehorizon.route.utils.Constants.SEND_MONEY;
import static com.beyondthehorizon.route.utils.Constants.SEND_MONEY_TO_BANK;
import static com.beyondthehorizon.route.utils.Constants.SEND_MONEY_TO_MOBILE_MONEY;
import static com.beyondthehorizon.route.utils.Constants.SEND_MONEY_TO_ROUTE;
import static com.beyondthehorizon.route.utils.Constants.sendMoney;

public class SendMoneyBottomModel extends BottomSheetDialogFragment {
    private SendMoneyBottomSheetListener mListener;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.custom_send_money_alert_dialog_layout, container, false);
        pref = getActivity().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();
        final LinearLayout parentSend = v.findViewById(R.id.sendLayout);
        final LinearLayout bank = v.findViewById(R.id.bankLayout);
        final LinearLayout mobile = v.findViewById(R.id.mobileLayout);
        LinearLayout toRoute = v.findViewById(R.id.toRoute);
        LinearLayout toMobileMoney = v.findViewById(R.id.toMobileMoney);
        LinearLayout toBank = v.findViewById(R.id.toBank);
        //SEND MONEY TO ROUTE
        toRoute.setOnClickListener(v13 -> {
            Intent intent = new Intent(getActivity(), RequestFundActivity.class);
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SEND_MONEY);
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_ROUTE);
            editor.apply();
            startActivity(intent);
            dismiss();
        });
        //SEND MONEY TO MOBILE MONEY
        toMobileMoney.setOnClickListener(v14 -> {
            parentSend.setVisibility(View.GONE);
            bank.setVisibility(View.GONE);
            mobile.setVisibility(View.VISIBLE);
//                showSendMobileMoneyDialog();
        });
        //SEND MONEY TO BANK
        toBank.setOnClickListener(v15 -> {
            parentSend.setVisibility(View.GONE);
            mobile.setVisibility(View.GONE);
            bank.setVisibility(View.VISIBLE);
//                showSendMoneyToBankDialog();
        });
        //OPEN CONTACTS TO SEND MOBILE MONEY
        final EditText mobileNumber = v.findViewById(R.id.mobileNumber);
        Button mobileButton = v.findViewById(R.id.mobileButton);
        ImageView imgSearch = v.findViewById(R.id.imgSearch);
        imgSearch.setOnClickListener(v16 -> {
            Intent intent = new Intent(getActivity(), RequestFundActivity.class);
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SEND_MONEY);
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_MOBILE_MONEY);
            editor.apply();
            startActivity(intent);
        });

        /**SEND MONEY TO MOBILE MONEY*/
        mobileButton.setOnClickListener(v12 -> {
            if (mobileNumber.getText().toString().isEmpty() || mobileNumber.getText().toString().length() < 10) {
                Toast.makeText(getActivity(), "Enter a valid phone number", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(getActivity(), FundAmountActivity.class);
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SEND_MONEY);
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_MOBILE_MONEY);
            editor.putString(PHONE_NUMBER, mobileNumber.getText().toString());
            intent.putExtra(PHONE_NUMBER, mobileNumber.getText().toString());
            editor.apply();
            startActivity(intent);
        });

        /**SEND TO BANK*/
        final EditText accountNumber = v.findViewById(R.id.accountNumber);
        final EditText amount = v.findViewById(R.id.amount);
        Button bankButton = v.findViewById(R.id.bankButton);
//        final Spinner chooseBank = v.findViewById(R.id.chooseBank);
        final AutoCompleteTextView findBank = v.findViewById(R.id.findBank);
        ArrayList<String> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(pref.getString(BANK_PROVIDERS, ""));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                list.add(jsonObject.getString("providerName"));
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.custom_list_item, R.id.text_view_list_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            findBank.setAdapter(dataAdapter);
        } catch (JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(),
                    "Error Loading and try again", Toast.LENGTH_LONG).show();
            list.add("Error adding roles");
        }
        //SEND MONEY TO BANK
        bankButton.setOnClickListener(v1 -> {
            if (accountNumber.getText().toString().trim().isEmpty()) {
                Toast.makeText(getActivity(), "Enter a account number", Toast.LENGTH_LONG).show();
                accountNumber.setError("Enter a valid account number");
                accountNumber.requestFocus();
                return;
            }
            if (findBank.getText().toString().trim().isEmpty()) {
                Toast.makeText(getActivity(), "Select bank", Toast.LENGTH_LONG).show();
                findBank.setError("Select bank");
                findBank.requestFocus();
                return;
            }
            if (amount.getText().toString().trim().isEmpty()) {
                Toast.makeText(getActivity(), "Add Amount", Toast.LENGTH_LONG).show();
                amount.setError("Add Amount");
                amount.requestFocus();
                return;
            }
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SEND_MONEY);
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_BANK);
            editor.putString("bankAcNumber", accountNumber.getText().toString().trim());
            editor.putString("chosenBank", findBank.getText().toString().trim());
            editor.apply();


            mListener.onBankBottomSheetListener(amount.getText().toString().trim(), accountNumber.getText().toString().trim(),
                    findBank.getText().toString().trim());
            dismiss();
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (SendMoneyBottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    public interface SendMoneyBottomSheetListener {
        void onBankBottomSheetListener(String amount, String accountNumber, String bankName);
    }
}
