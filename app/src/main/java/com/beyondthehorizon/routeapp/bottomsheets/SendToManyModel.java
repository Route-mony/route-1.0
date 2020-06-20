package com.beyondthehorizon.routeapp.bottomsheets;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.views.FundAmountActivity;
import com.beyondthehorizon.routeapp.views.multicontactschoice.MultiContactsActivity;
import com.beyondthehorizon.routeapp.views.multicontactschoice.SendToManyGroupsActivity;
import com.beyondthehorizon.routeapp.views.requestfunds.RequestFundActivity;
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

public class SendToManyModel extends BottomSheetDialogFragment {
    private SendToManyBottomSheetListener mListener;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.custom_send_to_many_alert_dialog_layout, container, false);
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
                Intent intent = new Intent(getActivity(), SendToManyGroupsActivity.class);
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
                Intent intent = new Intent(getActivity(), SendToManyGroupsActivity.class);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SEND_MONEY);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_MOBILE_MONEY);
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
            mListener = (SendToManyBottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    public interface SendToManyBottomSheetListener {
        void onBankBottomSheetListener(String amount, String accountNumber, String bankName);
    }
}
