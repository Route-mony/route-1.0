package com.beyondthehorizon.route.bottomsheets;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.beyondthehorizon.route.R;
import com.beyondthehorizon.route.loan.LoanActivity;
import com.beyondthehorizon.route.loan.LoanSuccessActivity;
import com.beyondthehorizon.route.views.MainActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import static com.beyondthehorizon.route.utils.Constants.REG_APP_PREFERENCES;

public class LoanBottomSheetModel extends BottomSheetDialogFragment {
    private LoanBottomSheetListener mListener;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    public static final String TAG = "LoanBottomModel";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.loan_request_options, container, false);
        pref = getActivity().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();
        TextView userNAme = v.findViewById(R.id.userName);
        Button nxtApplyLoan = v.findViewById(R.id.nxt_apply_loan);
        userNAme.setText(pref.getString("FullName", ""));
        nxtApplyLoan.setOnClickListener(view -> {
            ProgressDialog pr = new ProgressDialog(requireActivity());
            pr.setMessage("Please wait..");
            pr.show();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                // yourMethod();
                pr.dismiss();
                Intent intent = new Intent(requireActivity(), LoanSuccessActivity.class);
                startActivity(intent);
            }, 1000);

        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (LoanBottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    public interface LoanBottomSheetListener {
        void lnMethodBottomSheetListener(String amount, String ben_account, String ben_ref);
    }
}
