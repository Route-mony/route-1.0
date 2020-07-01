package com.beyondthehorizon.routeapp.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.bottomsheets.BuyAirtimeDialogFragment;
import com.beyondthehorizon.routeapp.bottomsheets.EnterPinBottomSheet;
import com.beyondthehorizon.routeapp.bottomsheets.MpesaMoneyBottomModel;
import com.beyondthehorizon.routeapp.bottomsheets.SendMoneyBottomModel;
import com.beyondthehorizon.routeapp.bottomsheets.SendToManyModel;
import com.beyondthehorizon.routeapp.bottomsheets.TransactionModel;
import com.beyondthehorizon.routeapp.databases.NotificationCount;
import com.beyondthehorizon.routeapp.utils.Constants;
import com.beyondthehorizon.routeapp.viewmodels.RoutViewModel;
import com.beyondthehorizon.routeapp.views.auth.LoginActivity;
import com.beyondthehorizon.routeapp.views.auth.SetTransactionPinActivity;
import com.beyondthehorizon.routeapp.views.notifications.NotificationsActivity;
import com.beyondthehorizon.routeapp.views.receipt.ReceiptActivity;
import com.beyondthehorizon.routeapp.views.requestfunds.RequestFundActivity;
import com.beyondthehorizon.routeapp.views.settingsactivities.SettingsActivity;
import com.beyondthehorizon.routeapp.views.split.bill.SplitBillActivity;
import com.beyondthehorizon.routeapp.views.transactions.main.TransactionsActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.beyondthehorizon.routeapp.utils.Constants.BALANCE_CHECK;
import static com.beyondthehorizon.routeapp.utils.Constants.BANK_PROVIDERS;
import static com.beyondthehorizon.routeapp.utils.Constants.CARDS;
import static com.beyondthehorizon.routeapp.utils.Constants.LOGGED_IN;
import static com.beyondthehorizon.routeapp.utils.Constants.MOBILE_PROVIDERS;
import static com.beyondthehorizon.routeapp.utils.Constants.MyPhoneNumber;
import static com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.routeapp.utils.Constants.REQUEST_MONEY;
import static com.beyondthehorizon.routeapp.utils.Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY;
import static com.beyondthehorizon.routeapp.utils.Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY;
import static com.beyondthehorizon.routeapp.utils.Constants.SEND_MONEY_TO_ROUTE;
import static com.beyondthehorizon.routeapp.utils.Constants.TRANSACTIONS_PIN;
import static com.beyondthehorizon.routeapp.utils.Constants.USER_ID;
import static com.beyondthehorizon.routeapp.utils.Constants.USER_TOKEN;
import static com.beyondthehorizon.routeapp.utils.Constants.UserName;
import static com.beyondthehorizon.routeapp.utils.Constants.sendMoney;

public class MainActivity extends AppCompatActivity implements SendMoneyBottomModel.SendMoneyBottomSheetListener,
        MpesaMoneyBottomModel.MpesaBottomSheetListener, TransactionModel.TransactionBottomSheetListener,
        EnterPinBottomSheet.EnterPinBottomSheetBottomSheetListener, SendToManyModel.SendToManyBottomSheetListener {
    private static final String TAG = "MainActivity";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ImageView profile_pic, btn_notifications;
    private TextView user_name, txt_home, query_text, balance_title, balance_value, verify_email, notifCount;
    private Button add_money_button;
    private ImageButton btn_request34, btn_fav2, btn_fav3, btn_send_to_many,btn_request2, btn_request3, btn_settings, btn_receipts, btn_transactions, btn_fav1, btn_request54, btn_buy_airtime, btn_home;
    private RelativeLayout RL1;
    private Intent intent; // Animation
    private LinearLayout mobileMoneyLayout;
    private Animation moveUp;
    private RoutViewModel routViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();
        setContentView(R.layout.activity_main);

        btn_home = findViewById(R.id.btn_home);
        txt_home = findViewById(R.id.txt_home);
        btn_request54 = findViewById(R.id.btn_request54);
        btn_fav1 = findViewById(R.id.btn_fav1);
        btn_fav2 = findViewById(R.id.btn_fav2);
        btn_fav3 = findViewById(R.id.btn_fav3);
        btn_transactions = findViewById(R.id.btn_transactions);
        btn_receipts = findViewById(R.id.btn_receipt);
        btn_settings = findViewById(R.id.btn_settings);
        btn_request2 = findViewById(R.id.btn_request2);
        btn_request3 = findViewById(R.id.btn_request3);
        btn_request34 = findViewById(R.id.btn_request34);
        user_name = findViewById(R.id.user_name);
        query_text = findViewById(R.id.query_text);
        balance_title = findViewById(R.id.balance_title);
        balance_value = findViewById(R.id.balance_value);
        add_money_button = findViewById(R.id.add_money_button);
        verify_email = findViewById(R.id.verify_email);
        profile_pic = findViewById(R.id.profile_pic);
        RL1 = findViewById(R.id.RL1);
//        btn_request_fund = findViewById(R.id.btn_request);
        btn_notifications = findViewById(R.id.notifications);
        btn_buy_airtime = findViewById(R.id.btn_request24);
        btn_send_to_many = findViewById(R.id.btn_request4);
        mobileMoneyLayout = findViewById(R.id.mobileLayout);
        notifCount = findViewById(R.id.notifCount);

        btn_home.setImageResource(R.drawable.ic_nav_home);
        txt_home.setTextColor(getResources().getColor(R.color.colorButton));

        moveUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move_up);

        routViewModel = ViewModelProviders.of(this).get(RoutViewModel.class);
//        intent = new Intent(this, RequestFundsActivity.class);
        intent = new Intent(this, RequestFundActivity.class);

        btn_request54.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
                startActivity(intent);
            }
        });
        add_money_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddMoneyActivity.class));
            }
        });
//        btn_request_fund.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, REQUEST_MONEY);
//                editor.apply();
//                startActivity(intent);
//            }
//        });
        btn_fav1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, REQUEST_MONEY);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, REQUEST_MONEY);
                editor.apply();
                startActivity(intent);
            }
        });

        btn_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
                routViewModel.deleteNotifiCount();
                startActivity(intent);
            }
        });

        btn_transactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TransactionsActivity.class);
                startActivity(intent);
            }
        });

        btn_receipts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
                startActivity(intent);
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });


        btn_fav3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
                startActivity(intent);
            }
        });
        btn_request34.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMoneyBottomModel sendMoneyBottomModel = new SendMoneyBottomModel();
                sendMoneyBottomModel.show(getSupportFragmentManager(), "Send Money Options");
            }
        });

        btn_fav2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, RequestFundsActivity.class);
                Intent intent = new Intent(MainActivity.this, RequestFundActivity.class);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SEND_MONEY);
                editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_ROUTE);
                editor.apply();
                startActivity(intent);
            }
        });

        btn_request2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                viewMpesaPaymentOption();
                MpesaMoneyBottomModel mpesaMoneyBottomModel = new MpesaMoneyBottomModel();
                mpesaMoneyBottomModel.show(getSupportFragmentManager(), "Mpesa Options");
            }
        });

        btn_send_to_many.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToManyModel sendToManyModel = new SendToManyModel();
                sendToManyModel.show(getSupportFragmentManager(), "Send To Many");
            }
        });

        btn_request3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SplitBillActivity.class);
                startActivity(intent);
            }
        });
        btn_buy_airtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuyAirtimeDialogFragment airtimeDialogFragment = new BuyAirtimeDialogFragment();
                airtimeDialogFragment.show(getSupportFragmentManager(), "Buy Airtime Options");
//                TransactionModel airtimeModel = new TransactionModel();
//                airtimeModel.show(getSupportFragmentManager(), "Airtime Options");
            }
        });

        isLoggedIn();
    }

    private void isLoggedIn() {
        if (pref.getString(USER_TOKEN, "").isEmpty()) {
            editor.putString(LOGGED_IN, "false");
            editor.apply();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            getProfile();
            notificationCount();
        }
    }

    private void notificationCount() {
        Log.e(TAG, "onChanged: HERE");
        routViewModel.getNotifiCount().observe(this, new Observer<List<NotificationCount>>() {
            @Override
            public void onChanged(List<NotificationCount> recentChatModels) {
                Log.e(TAG, "onChanged: " + recentChatModels.size());
                if (recentChatModels.size() > 0) {
//                    if (Integer.parseInt(recentChatModels.get(0).notif_count) > 0) {
                    notifCount.setText(String.valueOf(recentChatModels.size()));
                    notifCount.setVisibility(View.VISIBLE);
//                    }
                } else {
                    notifCount.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getProfile() {
        String token = "Bearer ".concat(pref.getString(USER_TOKEN, ""));

        Log.d(TAG, "getProfile: " + token);
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Constants.getUserProfile(MainActivity.this, token)
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        Log.e(TAG, "getUserProfile: " + result);
                        if (result != null) {

                            if (result.get("status").toString().contains("failed")) {
                                Snackbar snackbar = Snackbar
                                        .make(RL1, "A user with this email and password was not found.", Snackbar.LENGTH_LONG);
                                snackbar.show();
                                editor.clear();
                                editor.apply();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            } else if (result.get("status").toString().contains("success")) {

                                String id = result.get("data").getAsJsonObject().get("id").getAsString();
                                String name = result.get("data").getAsJsonObject().get("username").getAsString();
                                String wallet_balance = result.get("data").getAsJsonObject().get("wallet_account").getAsJsonObject().get("available_balance").getAsString();
                                String username = "Hey " + name + " !";
                                String phone = result.get("data").getAsJsonObject().get("phone_number").getAsString();
                                String cards = result.get("data").getAsJsonObject().get("debit_cards").getAsJsonArray().toString();

                                String fname = result.get("data").getAsJsonObject().get("first_name").getAsString();
                                String lname = result.get("data").getAsJsonObject().get("last_name").getAsString();
                                String image = result.get("data").getAsJsonObject().get("image").getAsString();

                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
                                Glide.with(MainActivity.this)
                                        .load(image)
                                        .apply(requestOptions)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .error(R.drawable.ic_user)
                                        .placeholder(R.drawable.ic_user)
                                        .into(profile_pic);
                                user_name.setText(username);
                                if (pref.getBoolean(BALANCE_CHECK, false)) {
                                    balance_value.setText("KES ......");
                                } else {
                                    balance_value.setText("KES " + wallet_balance);
                                }
                                editor.putString("FullName", fname + " " + lname);
                                editor.putString("ProfileImage", image);
                                editor.putString(USER_ID, id);
                                editor.putString(UserName, name);
                                editor.putString(MyPhoneNumber, phone);
                                editor.putString(CARDS, cards);
                                editor.apply();

                                getServiceProviders();
                                sendRegistrationToServer();
                                boolean email_verified = result.get("data").getAsJsonObject().get("is_email_active").getAsBoolean();
                                String is_pin_set = result.get("data").getAsJsonObject().get("is_pin_set").toString();

                                if (!email_verified) {
                                    verify_email.setVisibility(View.VISIBLE);
                                    verify_email.setText(R.string.verify_email);
                                } else {
                                    verify_email.setVisibility(View.GONE);
                                }
                                if (is_pin_set.contains("False")) {
                                    setPin();
                                }
                            }
                        } else {
                            final Snackbar snackbar = Snackbar
                                    .make(RL1, "Unable to load data ", Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("Try again", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar.dismiss();
                                    if (pref.getString(USER_TOKEN, "").isEmpty()) {
                                        editor.putString(LOGGED_IN, "false");
                                        editor.apply();
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        getProfile();
                                    }
                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

    private void sendRegistrationToServer() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String firebase_token = task.getResult().getToken();
                        String token = "Bearer ".concat(pref.getString(USER_TOKEN, ""));

                        Constants.updateFirebaseToken(MainActivity.this, token, firebase_token)
                                .setCallback(new FutureCallback<JsonObject>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result) {

                                    }
                                });
                    }
                });
    }

    private void setPin() {
        if (pref.getString(TRANSACTIONS_PIN, "").isEmpty()) {
            startActivity(new Intent(this, SetTransactionPinActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isLoggedIn();
    }

    private void getServiceProviders() {

        String token = "Bearer ".concat(pref.getString(USER_TOKEN, ""));

        Constants.getServiceProviders(MainActivity.this, token)
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        if (result.get("status").toString().compareTo("\"success\"") == 0) {
                            Log.e(TAG, "onCompleted: " + result);
                            editor.putString(MOBILE_PROVIDERS, result.get("data").getAsJsonObject().get("mobile").toString());
                            editor.putString(BANK_PROVIDERS, result.get("data").getAsJsonObject().get("bank").toString());
                            editor.apply();

                        }
                    }
                });
    }


    @Override
    public void mpesaBottomSheetListener(final String amount, final String ben_account, final String ben_ref) {

        editor.putString("amount21", amount);
        editor.putString("ben_account", ben_account);
        editor.putString("ben_ref", ben_ref);
        editor.putString("BottomSheetListener", "mpesaBottomSheetListener");
        editor.apply();

        EnterPinBottomSheet enterPinBottomSheet = new EnterPinBottomSheet();
        enterPinBottomSheet.show(getSupportFragmentManager(), "Mpesa Options");

//        final String token = "Bearer ".concat(pref.getString(USER_TOKEN, ""));
//
//        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
//        progressDialog.setMessage("Please wait");
//        progressDialog.setCanceledOnTouchOutside(false);
//        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
//        ViewGroup viewGroup = findViewById(android.R.id.content);
//
//        View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.enter_pin_transaction_pin, viewGroup, false);
//
//        final EditText enterPin = dialogView.findViewById(R.id.enterPin);
//        Button dialogButtonPin = dialogView.findViewById(R.id.dialogButtonPin);
//        //Now we need an AlertDialog.Builder object
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        //setting the view of the builder to our custom view that we already inflated
//        builder.setView(dialogView);
//
//        //finally creating the alert dialog and displaying it
//        final AlertDialog alertDialog = builder.create();
//        alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
//        alertDialog.show();
//
//        dialogButtonPin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (enterPin.getText().toString().trim().isEmpty()) {
//                    enterPin.setError("Enter Pin");
//                    return;
//                }
//                progressDialog.show();
//
//                if (ben_ref.trim().isEmpty()) {
//                    Constants.sendMoney(MainActivity.this,
//                            ben_account, amount, enterPin.getText().toString().trim(), token,
//                            "MPESA TILL", "Payment").setCallback(new FutureCallback<JsonObject>() {
//                        @Override
//                        public void onCompleted(Exception e, JsonObject result) {
//
//                            progressDialog.dismiss();
//                            if (result.has("errors")) {
//                                Toast.makeText(MainActivity.this, result.get("errors").getAsJsonArray().toString(), Toast.LENGTH_LONG).show();
//                            } else {
//                                String message = result.get("data").getAsJsonObject().get("message").getAsString();
//                                Intent intent = new Intent(MainActivity.this, FundRequestedActivity.class);
//                                intent.putExtra("Message", message);
//                                startActivity(intent);
//                                alertDialog.dismiss();
//                            }
//                        }
//                    });
//                } else {
//                    Constants.sendMoneyBeneficiary(MainActivity.this,
//                            ben_account, amount, enterPin.getText().toString().trim(), token,
//                            "MPESA PAYBILL", ben_ref.trim(), "Payment").setCallback(new FutureCallback<JsonObject>() {
//                        @Override
//                        public void onCompleted(Exception e, JsonObject result) {
//                            Log.e(TAG, "onCompleted: " + result);
//
//                            progressDialog.dismiss();
//                            if (result.has("errors")) {
//                                Toast.makeText(MainActivity.this, result.get("errors").getAsJsonObject().toString(), Toast.LENGTH_LONG).show();
//                            } else {
//                                String message = result.get("data").getAsJsonObject().get("message").getAsString();
//                                Intent intent = new Intent(MainActivity.this, FundRequestedActivity.class);
//                                intent.putExtra("Message", message);
//                                startActivity(intent);
//                                alertDialog.dismiss();
//                            }
//                        }
//                    });
//                }
//            }
//        });
    }

    @Override
    public void transactionBottomSheetListener(final String amount, final String ben_account, final String ben_ref) {
        final String token = "Bearer ".concat(pref.getString(USER_TOKEN, ""));
    }

    @Override
    public void enterPinDialog(@NotNull String pin) {

        final String token = "Bearer ".concat(pref.getString(USER_TOKEN, ""));
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
//        editor.putString("mpesaBottomSheetListener", "mpesaBottomSheetListener");

        String ben_ref = pref.getString("ben_ref", "");
        String ben_account = pref.getString("ben_account", "");
        final String amount = pref.getString("amount21", "");

        String transactionType = pref.getString("BottomSheetListener", "");

        Log.e(TAG, "enterPinDialog: BREF " + ben_ref + " BAC " + ben_account + " amn " + amount);

        if (transactionType.compareTo("mpesaBottomSheetListener") == 0) {
            if (ben_ref.trim().isEmpty()) {
                Constants.sendMoney(MainActivity.this,
                        ben_account, amount, pin, token,
                        "MPESA TILL", "Payment").setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        progressDialog.dismiss();
                        if (result.has("errors")) {
                            Toast.makeText(MainActivity.this, result.get("errors").getAsJsonArray().toString(), Toast.LENGTH_LONG).show();
                        } else {
                            editor.remove("ben_ref");
                            editor.apply();
                            String message = result.get("data").getAsJsonObject().get("message").getAsString();
                            Intent intent = new Intent(MainActivity.this, FundRequestedActivity.class);
                            intent.putExtra("Message", message);
                            startActivity(intent);
                        }
                    }
                });
            } else {
                Constants.sendMoneyBeneficiary(MainActivity.this,
                        ben_account, amount, pin, token,
                        "MPESA PAYBILL", ben_ref.trim(), "Payment")
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                Log.e(TAG, "onCompleted: " + result);

                                progressDialog.dismiss();
                                if (result.has("errors")) {
                                    Toast.makeText(MainActivity.this, result.get("errors").getAsJsonObject().toString(), Toast.LENGTH_LONG).show();
                                } else {
                                    editor.remove("ben_ref");
                                    editor.apply();
                                    String message = result.get("data").getAsJsonObject().get("message").getAsString();
                                    Intent intent = new Intent(MainActivity.this, FundRequestedActivity.class);
                                    intent.putExtra("Message", message);
                                    startActivity(intent);
                                }
                            }
                        });
            }
        } else if (transactionType.compareTo("SendMoneyBottomModel") == 0) {

            String bankName1 = pref.getString("bankName1", "");
            String accountNumber = pref.getString("accountNumber1", "");
            final String amountt = pref.getString("amount211", "");

            sendMoney(MainActivity.this, accountNumber, amountt, pin, token, bankName1, "Payment")
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            Log.e("FundAmountActivity", result.toString());
                            progressDialog.dismiss();
                            if (result.has("errors")) {
                                Toast.makeText(MainActivity.this,
                                        result.get("errors").getAsString(), Toast.LENGTH_LONG).show();
                            } else {
                                editor.putString("Amount", amountt);
                                editor.apply();
                                String message = result.get("data").getAsJsonObject().get("message").getAsString();
                                Intent intent = new Intent(MainActivity.this, FundRequestedActivity.class);
                                intent.putExtra("Message", message);
                                startActivity(intent);
                            }
                        }
                    });
        }
    }

    @Override
    public void onBankBottomSheetListener(String amount, String accountNumber, String bankName) {
        editor.putString("amount211", amount);
        editor.putString("accountNumber1", accountNumber);
        editor.putString("bankName1", bankName);
        editor.putString("BottomSheetListener", "SendMoneyBottomModel");
        editor.apply();

        EnterPinBottomSheet enterPinBottomSheet = new EnterPinBottomSheet();
        enterPinBottomSheet.show(getSupportFragmentManager(), "Mpesa Options");
    }

}
