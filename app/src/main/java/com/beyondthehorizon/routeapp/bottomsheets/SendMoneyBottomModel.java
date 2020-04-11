package com.beyondthehorizon.routeapp.bottomsheets;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.views.FundAmountActivity;
import com.beyondthehorizon.routeapp.views.RequestFundsActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.beyondthehorizon.routeapp.utils.Constants.BANK_PROVIDERS;
import static com.beyondthehorizon.routeapp.utils.Constants.PHONE_NUMBER;
import static com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.routeapp.utils.Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY;
import static com.beyondthehorizon.routeapp.utils.Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY_TO_BANK;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY_TO_MOBILE_MONEY;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY_TO_ROUTE;

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
        toRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RequestFundsActivity.class);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SEND_MONEY);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_ROUTE);
                editor.apply();
                startActivity(intent);
                dismiss();
            }
        });

        //SEND MONEY TO MOBILE MONEY
        toMobileMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                parentSend.setVisibility(View.GONE);
                bank.setVisibility(View.GONE);
                mobile.setVisibility(View.VISIBLE);
//                showSendMobileMoneyDialog();
            }
        });

        //SEND MONEY TO BANK
        toBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentSend.setVisibility(View.GONE);
                mobile.setVisibility(View.GONE);
                bank.setVisibility(View.VISIBLE);
//                showSendMoneyToBankDialog();
            }
        });

        //OPEN CONTACTS TO SEND MOBILE MONEY
        final EditText mobileNumber = v.findViewById(R.id.mobileNumber);
        Button mobileButton = v.findViewById(R.id.mobileButton);
        ImageView imgSearch = v.findViewById(R.id.imgSearch);

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RequestFundsActivity.class);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SEND_MONEY);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_MOBILE_MONEY);
                editor.apply();
                startActivity(intent);
            }
        });

        //SEND MONEY TO MOBILE MONEY
        /**SEND MONEY TO MOBILE MONEY*/
        mobileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        
        /**SEND TO BANK*/
        
        final EditText accountNumber = v.findViewById(R.id.accountNumber);
        Button bankButton = v.findViewById(R.id.bankButton);
        final Spinner chooseBank = v.findViewById(R.id.chooseBank);
        ArrayList<String> list = new ArrayList<>();
        list.add("Select bank");

        try {
            JSONArray jsonArray = new JSONArray(pref.getString(BANK_PROVIDERS, ""));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                list.add(jsonObject.getString("providerName"));
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            chooseBank.setAdapter(dataAdapter);

        } catch (JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(),
                    "Error Loading and try again", Toast.LENGTH_LONG).show();
            list.add("Error adding roles");
        }

        //SEND MONEY TO BANK
        bankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountNumber.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Enter a account number", Toast.LENGTH_LONG).show();
                    accountNumber.setError("Enter a valid account number");
                    accountNumber.requestFocus();
                    return;
                }
                if (chooseBank.getSelectedItemPosition() == 0) {
                    Toast.makeText(getActivity(), "Choose a bank", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), FundAmountActivity.class);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SEND_MONEY);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_BANK);
                editor.putString("bankAcNumber", accountNumber.getText().toString().trim());
                editor.putString("chosenBank", chooseBank.getSelectedItem().toString());
                editor.apply();
                startActivity(intent);
            }
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
        void onButtonClicked(String text);
    }
}
