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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.models.ReceiptModel;
import com.beyondthehorizon.routeapp.utils.Constants;
import com.beyondthehorizon.routeapp.views.FundAmountActivity;
import com.beyondthehorizon.routeapp.views.RequestFundsActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.beyondthehorizon.routeapp.utils.Constants.BANK_PROVIDERS;
import static com.beyondthehorizon.routeapp.utils.Constants.MOBILE_PROVIDERS;
import static com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.routeapp.utils.Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY;
import static com.beyondthehorizon.routeapp.utils.Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY_TO_BANK;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY_TO_MOBILE_MONEY;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY_TO_ROUTE;
import static com.beyondthehorizon.routeapp.utils.Constants.USER_TOKEN;

public class ReceiptDetailsBottomModel extends BottomSheetDialogFragment {
    private ReceiptDetailsBottomSheetListener mListener;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private LinearLayout attachment;
    private View L1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.receipt_details_alert_dialog_layout, container, false);
        pref = getActivity().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();

        TextView title = v.findViewById(R.id.title);
        TextView title1 = v.findViewById(R.id.title1);
        attachment = v.findViewById(R.id.attachment);
        L1 = v.findViewById(R.id.L1);
        TextView receipt_date = v.findViewById(R.id.receipt_date);
        TextView receipt_amount = v.findViewById(R.id.receipt_amount);
        TextView receipt_desc = v.findViewById(R.id.receipt_desc);
        TextView receipt_attach = v.findViewById(R.id.receipt_attach);
        final Button btnCancel = v.findViewById(R.id.btnCancel);
        final Button btnOk = v.findViewById(R.id.btnOk);
        TextView receipt_status = v.findViewById(R.id.receipt_status);
        final ImageView receipt_image = v.findViewById(R.id.receipt_image);
        LinearLayout buttonsLayout = v.findViewById(R.id.buttonsLayout);

        Gson gson = new Gson();
        final ReceiptModel receiptModel = gson.fromJson(pref.getString(Constants.VISITING_HISTORY_PROFILE, ""),
                ReceiptModel.class);

        final String img = receiptModel.getImage();
        if (img.trim().isEmpty()) {
            receipt_image.setVisibility(View.GONE);
            attachment.setVisibility(View.GONE);
            L1.setVisibility(View.GONE);
        } else {
            receipt_attach.setText("Image Attached");
        }

        if (receiptModel.getType().contains("sent")) {
            buttonsLayout.setVisibility(View.GONE);
        }
        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //before inflating the custom alert dialog layout, we will get the current activity viewgroup
                ViewGroup viewGroup = v.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.upload_image_layout, viewGroup, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                ImageView myImage11 = dialogView.findViewById(R.id.myImage11);
                Button deleteService11 = dialogView.findViewById(R.id.deleteService11);
                Button saveImage11 = dialogView.findViewById(R.id.saveImage11);

                Glide.with(getActivity())
                        .load(receiptModel.getImage())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(R.color.input_back)
                        .into(myImage11);

                deleteService11.setVisibility(View.GONE);
                saveImage11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        title.setText(receiptModel.getTitle());
        title1.setText(receiptModel.getTitle() + " Expense");
        receipt_date.setText(receiptModel.getTransaction_date());
        receipt_amount.setText(receiptModel.getAmount_spent());
        receipt_desc.setText(receiptModel.getDescription());

        if (receiptModel.getStatus().contains("ok")) {
            receipt_status.setText("Approved");
        } else {
            receipt_status.setText(receiptModel.getStatus());
        }

        if (pref.getString(Constants.TRANS_TYPE, "").compareTo("ReceivedReceiptFragment") == 0) {
            if (receiptModel.getStatus().contains("ok")) {
                btnOk.setText("Ok");
                btnCancel.setVisibility(View.GONE);
            } else {
                btnCancel.setText("Reject");
                btnOk.setText("Approve");
            }
        } else if (pref.getString(Constants.TRANS_TYPE, "").compareTo("SentReceiptFragment") == 0) {
            btnCancel.setText("Reject");
            btnOk.setText("Approve");
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                if (btnOk.getText().toString().contains("Approve")) {
                    String token = "Bearer ".concat(pref.getString(USER_TOKEN, ""));
                    progressDialog.show();
                    Constants.approveUserReceipt(getActivity(), token, receiptModel.getReceipt_id())
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    progressDialog.dismiss();
                                    if (result.get("status").getAsString().contains("success")) {
                                        Toast.makeText(getActivity(), "Receipt Approved Successfully", Toast.LENGTH_LONG).show();
                                        dismiss();
                                    }else if (result.has("errors")) {
                                        Toast.makeText(requireContext(),result.get("errors").getAsJsonArray().get(0).getAsString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnCancel.getText().toString().contains("Reject" +
                        "")) {

                    if (receiptModel.getStatus().compareTo("pending") != 0) {
                        Toast.makeText(getActivity(), "You cannot cancel a receipt that has been " + receiptModel.getStatus(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final String token = "Bearer ".concat(pref.getString(USER_TOKEN, ""));

                    //before inflating the custom alert dialog layout, we will get the current activity viewgroup
                    ViewGroup viewGroup = v.findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.cancel_recipt_request, viewGroup, false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setView(dialogView);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();

                    final EditText reject_reason = dialogView.findViewById(R.id.reject_reason);
                    Button deleteService11 = dialogView.findViewById(R.id.deleteService11);
                    Button saveImage11 = dialogView.findViewById(R.id.saveImage11);

                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Please wait...");

                    deleteService11.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    saveImage11.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (reject_reason.getText().toString().isEmpty()) {
                                Toast.makeText(getActivity(), "Add Cancel reason", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                            Constants.rejectUserReceipt(getActivity(), token, receiptModel.getReceipt_id(),
                                    reject_reason.getText().toString().trim())
                                    .setCallback(new FutureCallback<JsonObject>() {
                                        @Override
                                        public void onCompleted(Exception e, JsonObject result) {
//                                            Log.e(TAG, "onCompleted: "+result.toString() );
                                            progressDialog.dismiss();
                                            if (result.get("status").getAsString().contains("success")) {
                                                Toast.makeText(getActivity(), "Successful", Toast.LENGTH_LONG).show();
                                                alertDialog.dismiss();
                                                dismiss();
                                            } else if (result.has("errors")) {
                                                Toast.makeText(requireContext(), result.get("errors").getAsJsonArray().get(0).getAsString(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    });

                } else {
                    dismiss();
                }
            }
        });

        return v;
    }

    public interface ReceiptDetailsBottomSheetListener {
        void onButtonClicked(String text);
    }
}
