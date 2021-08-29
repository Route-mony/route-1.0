package com.beyondthehorizon.route.views;

import static com.beyondthehorizon.route.utils.Constants.BALANCE_CHECK;
import static com.beyondthehorizon.route.utils.Constants.BANK_PROVIDERS;
import static com.beyondthehorizon.route.utils.Constants.CARDS;
import static com.beyondthehorizon.route.utils.Constants.LOAD_WALLET;
import static com.beyondthehorizon.route.utils.Constants.LOGGED_IN;
import static com.beyondthehorizon.route.utils.Constants.MOBILE_PROVIDERS;
import static com.beyondthehorizon.route.utils.Constants.MY_ROUTE_CONTACTS_NEW;
import static com.beyondthehorizon.route.utils.Constants.MyPhoneNumber;
import static com.beyondthehorizon.route.utils.Constants.REG_APP_PREFERENCES;
import static com.beyondthehorizon.route.utils.Constants.REQUEST_MONEY;
import static com.beyondthehorizon.route.utils.Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY;
import static com.beyondthehorizon.route.utils.Constants.REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE;
import static com.beyondthehorizon.route.utils.Constants.SEND_MONEY;
import static com.beyondthehorizon.route.utils.Constants.SEND_MONEY_TO_ROUTE;
import static com.beyondthehorizon.route.utils.Constants.TRANSACTIONS_PIN;
import static com.beyondthehorizon.route.utils.Constants.USER_ID;
import static com.beyondthehorizon.route.utils.Constants.USER_TOKEN;
import static com.beyondthehorizon.route.utils.Constants.UserName;
import static com.beyondthehorizon.route.utils.Constants.WALLET_ACCOUNT;
import static com.beyondthehorizon.route.utils.Constants.WALLET_BALANCE;
import static com.beyondthehorizon.route.utils.Constants.sendMoney;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.CursorLoader;

import com.beyondthehorizon.route.R;
import com.beyondthehorizon.route.bottomsheets.EnterPinBottomSheet;
import com.beyondthehorizon.route.bottomsheets.MpesaMoneyBottomModel;
import com.beyondthehorizon.route.bottomsheets.SendMoneyBottomModel;
import com.beyondthehorizon.route.bottomsheets.SendToManyModel;
import com.beyondthehorizon.route.bottomsheets.TransactionModel;
import com.beyondthehorizon.route.loan.LoanActivity;
import com.beyondthehorizon.route.models.MultiContactModel;
import com.beyondthehorizon.route.utils.Constants;
import com.beyondthehorizon.route.utils.NetworkUtils;
import com.beyondthehorizon.route.utils.Utils;
import com.beyondthehorizon.route.viewmodels.RoutViewModel;
import com.beyondthehorizon.route.views.auth.LoginActivity;
import com.beyondthehorizon.route.views.auth.SetTransactionPinActivity;
import com.beyondthehorizon.route.views.notifications.NotificationsActivity;
import com.beyondthehorizon.route.views.receipt.ReceiptActivity;
import com.beyondthehorizon.route.views.requestfunds.RequestFundActivity;
import com.beyondthehorizon.route.views.settingsactivities.SettingsActivity;
import com.beyondthehorizon.route.views.split.bill.SplitBillActivity;
import com.beyondthehorizon.route.views.transactions.main.TransactionsActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements SendMoneyBottomModel.SendMoneyBottomSheetListener,
        MpesaMoneyBottomModel.MpesaBottomSheetListener, TransactionModel.TransactionBottomSheetListener,
        EnterPinBottomSheet.EnterPinBottomSheetBottomSheetListener, SendToManyModel.SendToManyBottomSheetListener {
    private NetworkUtils networkUtils;
    private static final String TAG = "MainActivity";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ImageView profile_pic, btn_notifications;
    private TextView user_name, txt_home, query_text, balance_title, balance_value, verify_email, notifCount;
    private Button add_money_button;
    private ImageButton btn_request34;
    private ImageButton btn_fav2;
    private ImageButton btn_fav3;
    private ImageButton btn_send_to_many;
    private ImageButton btn_request2;
    private ImageButton btn_request3;
    private ImageButton btn_settings;
    private ImageButton btn_receipts;
    private ImageButton btn_transactions;
    private ImageButton btn_fav1;
    private ImageButton btn_request54;
    private ImageButton btn_loan;
    private ImageButton btn_home;
    private RelativeLayout RL1;
    private Intent intent; // Animation
    private LinearLayout mobileMoneyLayout;
    private Animation moveUp;
    private String token;
    private Utils util;
    private RoutViewModel routViewModel;
    public static final int REQUEST_READ_CONTACTS = 243;
    private LinearLayout llInternetDialog;
    private Button btnCancel, btnRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();
        util = new Utils(this);
        setContentView(R.layout.activity_main);
        networkUtils = new NetworkUtils(this);

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
        btn_loan = findViewById(R.id.btn_loan);
        btn_send_to_many = findViewById(R.id.btn_request4);
        mobileMoneyLayout = findViewById(R.id.mobileLayout);
        notifCount = findViewById(R.id.notifCount);
        balance_value.setVisibility(View.GONE);
        btn_home.setImageResource(R.drawable.ic_nav_home);
        txt_home.setTextColor(getResources().getColor(R.color.colorButton));

        moveUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_up);
        llInternetDialog = findViewById(R.id.llInternetDialog);
        btnCancel = findViewById(R.id.btn_cancel);
        btnRetry = findViewById(R.id.btn_retry);

        routViewModel = ViewModelProviders.of(this).get(RoutViewModel.class);
        intent = new Intent(this, RequestFundActivity.class);

        btnRetry.setOnClickListener(v -> {
            llInternetDialog.setVisibility(View.GONE);
            isLoggedIn();
        });

        btnCancel.setOnClickListener(v -> llInternetDialog.setVisibility(View.GONE));

        btn_request54.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
            startActivity(intent);
        });
        add_money_button.setOnClickListener(v -> {
            Intent intent = new Intent(this, FundAmountActivity.class);
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, LOAD_WALLET);
            editor.apply();
            startActivity(intent);
        });
        btn_fav1.setOnClickListener(v -> {
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, REQUEST_MONEY);
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, REQUEST_MONEY);
            editor.apply();
            startActivity(intent);
        });

        btn_notifications.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
            routViewModel.deleteNotifiCount();
            startActivity(intent);
        });

        btn_transactions.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransactionsActivity.class);
            startActivity(intent);
        });

        btn_receipts.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
            startActivity(intent);
        });

        profile_pic.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        btn_settings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });


        btn_fav3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
            startActivity(intent);
        });
        btn_request34.setOnClickListener(v -> {
            SendMoneyBottomModel sendMoneyBottomModel = new SendMoneyBottomModel();
            sendMoneyBottomModel.show(getSupportFragmentManager(), "Send Money Options");
        });

        btn_fav2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RequestFundActivity.class);
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_ACTIVITY, SEND_MONEY);
            editor.putString(REQUEST_TYPE_TO_DETERMINE_PAYMENT_TYPE, SEND_MONEY_TO_ROUTE);
            editor.apply();
            startActivity(intent);
        });

        btn_request2.setOnClickListener(v -> {
            MpesaMoneyBottomModel mpesaMoneyBottomModel = new MpesaMoneyBottomModel();
            mpesaMoneyBottomModel.show(getSupportFragmentManager(), "Mpesa Options");
        });

        btn_send_to_many.setOnClickListener(v -> {
            SendToManyModel sendToManyModel = new SendToManyModel();
            sendToManyModel.show(getSupportFragmentManager(), "Send To Many");
        });

        btn_request3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SplitBillActivity.class);
            startActivity(intent);
        });
//        btn_loan.setOnClickListener(v -> {
//            BuyAirtimeDialogFragment airtimeDialogFragment = new BuyAirtimeDialogFragment();
//            airtimeDialogFragment.show(getSupportFragmentManager(), "Buy Airtime Options");
//        });
        btn_loan.setOnClickListener(v -> {
            ProgressDialog pr = new ProgressDialog(MainActivity.this);
            pr.setMessage("Please wait..");
            pr.show();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                // yourMethod();
                pr.dismiss();
                Intent intent = new Intent(MainActivity.this, LoanActivity.class);
                startActivity(intent);
            }, 1000);
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
            token = "Bearer ".concat(Objects.requireNonNull(pref.getString(USER_TOKEN, "")));
            //Load wallet balance from ISW
            if (networkUtils.isNetworkAvailable()) {
                util.loadWalletBalance(token);
                getProfile();
                notificationCount();
            } else {
                llInternetDialog.setVisibility(View.VISIBLE);
            }
        }
    }

    public void checkPermissions() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                builder1.setTitle("Route Would Like to Access Your Contacts");
                builder1.setMessage(R.string.permisson_txt);
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Ok",
                        (dialog, id) -> {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        });

                builder1.setNegativeButton(
                        "No",
                        (dialog, id) -> {
                            dialog.cancel();
                            Toast.makeText(MainActivity.this, "You will not be able to access Route effectively", Toast.LENGTH_SHORT).show();
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            } else {
                loadContacts();
            }
        } catch (Exception ex) {
            Timber.d(ex.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACTS) {
            // disable speech button is permission not granted or instantiate recorder
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pref.getString(MY_ROUTE_CONTACTS_NEW, "").isEmpty()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            loadContacts();
                        }
                    }).start();
                }

            } else {
                Toast.makeText(MainActivity.this, "Permission Denied, you will not be able to request or send funds since it requires loading your contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadContacts() {
        String[] PROJECTION = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        CursorLoader cursorLoader = new CursorLoader(this, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ")ASC");
        Cursor c = cursorLoader.loadInBackground();
        if (c.moveToFirst()) {
            int numberIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            final List<MultiContactModel> myContactsList = new ArrayList<>();
            final List<MultiContactModel> myContactsList2 = new ArrayList<>();
            do {
                try {
                    myContactsList.add(new MultiContactModel("", c.getString(nameIndex), Utils.getFormattedPhoneNumber(c.getString(numberIndex), util.getCountrySymbol()), "", "", false, false, ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
            c.close();

            Gson gson = new Gson();
            String json = gson.toJson(myContactsList);
            Constants.getRegisteredRouteContacts(MainActivity.this, token, json).setCallback((e, result) -> {
                try {
                    if (result.has("data")) {
                        if (result.get("data").getAsJsonObject().has("contacts")) {
                            Gson gsonn = new Gson();
                            String jsonn;
                            jsonn = gsonn.toJson(result.getAsJsonObject("data").get("contacts"));
                            if (result.getAsJsonObject("data").get("contacts").getAsJsonArray().size() == 0) {
                                return;
                            }
                            editor.putString(Constants.MY_ALL_CONTACTS_NEW, jsonn);
                            editor.apply();
                            for (JsonElement item : result.getAsJsonObject("data").get("contacts").getAsJsonArray()) {
                                JSONObject issueObj = new JSONObject(item.toString());
                                if (issueObj.getBoolean("is_route")) {
                                    myContactsList2.add(new MultiContactModel(
                                            issueObj.get("id").toString(),
                                            issueObj.get("username").toString(),
                                            issueObj.get("phone_number").toString(),
                                            issueObj.get("image").toString(),
                                            issueObj.get("amount").toString(),
                                            issueObj.getBoolean("is_route"),
                                            issueObj.getBoolean("is_selected"),
                                            issueObj.getString("route_username")
                                    ));
                                }
                            }
                            String json2 = gsonn.toJson(myContactsList2);
                            editor.putString(MY_ROUTE_CONTACTS_NEW, json2);
                            editor.apply();
                        }
                    }
                } catch (Exception ex) {
                    Timber.d(ex.getMessage());
                }
            });
        }
    }

    private void notificationCount() {
        Log.e(TAG, "onChanged: HERE");
        routViewModel.getNotifiCount().observe(this, recentChatModels -> {
            Log.e(TAG, "onChanged: " + recentChatModels.size());
            if (recentChatModels.size() > 0) {
                notifCount.setText(String.valueOf(recentChatModels.size()));
                notifCount.setVisibility(View.VISIBLE);
            } else {
                notifCount.setVisibility(View.GONE);
            }
        });
    }

    private void getProfile() {
        checkPermissions();
        Timber.d("getProfile: " + token);
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Constants.getUserProfile(MainActivity.this, token)
                .setCallback((e, result) -> {
                    progressDialog.dismiss();
                    Timber.e("getUserProfile: " + result);
                    if (result != null) {
                        if (result.get("status").toString().contains("failed")) {
                            Snackbar snackbar = Snackbar
                                    .make(RL1, "A user with this email and password was not found.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            editor.clear();
                            editor.apply();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        } else if (result.get("status").toString().contains("success")) {
                            try {
                                String id = result.get("data").getAsJsonObject().get("id").getAsString();
                                String name = result.get("data").getAsJsonObject().get("username").getAsString();

                                String wallet_account = "";
                                String wallet_balance = "";
                                if (result.get("data").getAsJsonObject().get("wallet_account").getAsJsonObject().get("available_balance") != null) {
                                    wallet_account = result.get("data").getAsJsonObject().get("wallet_account").getAsJsonObject().get("wallet_account").getAsString();
                                    wallet_balance = result.get("data").getAsJsonObject().get("wallet_account").getAsJsonObject().get("available_balance").getAsString();
                                    if (String.valueOf(pref.getString(WALLET_BALANCE, "")).isEmpty()) {
                                        editor.putString(WALLET_BALANCE, wallet_balance);
                                        editor.commit();
                                    }
                                }
                                String username = "Hey " + name;
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
                                        .error(R.drawable.ic_user_home_page)
                                        .placeholder(R.drawable.ic_user_home_page)
                                        .into(profile_pic);
                                profile_pic.setVisibility(View.VISIBLE);
                                user_name.setText(username);
                                balance_value.setVisibility(View.VISIBLE);
                                editor.putString("FullName", fname + " " + lname);
                                editor.putString("ProfileImage", image);
                                editor.putString(USER_ID, id);
                                editor.putString(UserName, name);
                                editor.putString(MyPhoneNumber, phone);
                                editor.putString(CARDS, cards);
                                editor.putString(WALLET_ACCOUNT, wallet_account);
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

                                if (pref.getBoolean(BALANCE_CHECK, true)) {
                                    balance_value.setText(String.format("%s %s", "KES ", pref.getString(WALLET_BALANCE, "0.00")));
                                } else {
                                    balance_value.setText("");
                                }
                            } catch (Exception ex) {
                                Timber.d(ex.getMessage());
                            }
                        }
                    }
                });
    }

    private void sendRegistrationToServer() {
        FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(new OnCompleteListener<InstallationTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<InstallationTokenResult> task) {
                if (!task.isSuccessful()) {
                    if (task.getException() != null) {
                        task.getException().printStackTrace();
                    }
                    return;
                }
                String firebase_token = task.getResult().getToken();
                Constants.updateFirebaseToken(MainActivity.this, token, firebase_token).setCallback((e, result) -> {
                    if (e != null) {
                        e.printStackTrace();
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
        if (networkUtils.isNetworkAvailable()) {
            try {
                Constants.getServiceProviders(MainActivity.this, token)
                        .setCallback((e, result) -> {
                            if (result.has("data")) {
                                editor.putString(MOBILE_PROVIDERS, result.get("data").getAsJsonObject().get("mobile").toString());
                                editor.putString(BANK_PROVIDERS, result.get("data").getAsJsonObject().get("bank").toString());
                                editor.apply();

                            }
                        });
            } catch (Exception ex) {
                Timber.d(ex);
            }
        } else {
            llInternetDialog.setVisibility(View.VISIBLE);
        }
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
    }

    @Override
    public void transactionBottomSheetListener(final String amount, final String ben_account, final String ben_ref) {
    }

    @Override
    public void enterPinDialog(@NotNull String pin) {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        if (networkUtils.isNetworkAvailable()) {
            try {
                progressDialog.show();
                String ben_ref = pref.getString("ben_ref", "");
                String ben_account = pref.getString("ben_account", "");
                final String amount = pref.getString("amount21", "");
                String transactionType = pref.getString("BottomSheetListener", "");

                Timber.e("enterPinDialog: BREF " + ben_ref + " BAC " + ben_account + " amn " + amount);

                if (transactionType.compareTo("mpesaBottomSheetListener") == 0) {
                    if (ben_ref.trim().isEmpty()) {
                        Constants.sendMoney(MainActivity.this,
                                ben_account, amount, pin, token,
                                "MPESA TILL", "Payment").setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {

                                progressDialog.dismiss();
                                if (result.has("errors")) {
                                    Toast.makeText(MainActivity.this, result.get("errors").getAsJsonArray().get(0).getAsString(), Toast.LENGTH_LONG).show();
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
                                .setCallback((e, result) -> {
                                    Log.e(TAG, "onCompleted: " + result);

                                    progressDialog.dismiss();
                                    if (result.has("errors")) {
                                        Toast.makeText(MainActivity.this, result.get("errors").getAsJsonArray().get(0).getAsString(), Toast.LENGTH_LONG).show();
                                    } else {
                                        editor.remove("ben_ref");
                                        editor.apply();
                                        String message = result.get("data").getAsJsonObject().get("message").getAsString();
                                        Intent intent = new Intent(MainActivity.this, FundRequestedActivity.class);
                                        intent.putExtra("Message", message);
                                        startActivity(intent);
                                    }
                                });
                    }
                } else if (transactionType.compareTo("SendMoneyBottomModel") == 0) {

                    String bankName1 = pref.getString("bankName1", "");
                    String accountNumber = pref.getString("accountNumber1", "");
                    final String amountt = pref.getString("amount211", "");
                    sendMoney(MainActivity.this, accountNumber, amountt, pin, token, bankName1, "Payment")
                            .setCallback((e, result) -> {
                                Log.e("FundAmountActivity", result.toString());
                                progressDialog.dismiss();
                                if (result.has("errors")) {
                                    Toast.makeText(MainActivity.this, result.get("errors").getAsJsonArray().get(0).getAsString(), Toast.LENGTH_LONG).show();
                                } else {
                                    editor.putString("Amount", amountt);
                                    editor.apply();
                                    String message = result.get("data").getAsJsonObject().get("message").getAsString();
                                    Intent intent = new Intent(MainActivity.this, FundRequestedActivity.class);
                                    intent.putExtra("Message", message);
                                    startActivity(intent);
                                }
                            });
                }
            } catch (Exception ex) {
                Timber.d(ex.getMessage());
            }
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
