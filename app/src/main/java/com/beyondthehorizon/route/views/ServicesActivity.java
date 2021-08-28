package com.beyondthehorizon.route.views;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.beyondthehorizon.route.R;
import com.beyondthehorizon.route.adapters.SliderAdapter;
import com.beyondthehorizon.route.models.Adverts;
import com.beyondthehorizon.route.utils.Constants;
import com.beyondthehorizon.route.utils.NetworkUtils;
import com.beyondthehorizon.route.views.auth.LoginActivity;
import com.beyondthehorizon.route.views.auth.UserNamesActivity;
import com.github.islamkhsh.CardSliderViewPager;
import com.google.gson.JsonArray;

import java.util.ArrayList;

import timber.log.Timber;

import static com.beyondthehorizon.route.utils.Constants.REG_APP_PREFERENCES;

public class ServicesActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private String token;
    private NetworkUtils networkUtils;
    private LinearLayout llInternetDialog;
    private Button btnCancel, btnRetry;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0);
        token = "Bearer " + pref.getString(Constants.USER_TOKEN, "");
        networkUtils = new NetworkUtils(this);
        llInternetDialog = findViewById(R.id.llInternetDialog);
        btnCancel = findViewById(R.id.btn_cancel);
        btnRetry = findViewById(R.id.btn_retry);
        getAdverts();

        btnRetry.setOnClickListener(v -> {
            llInternetDialog.setVisibility(View.GONE);
            getAdverts();
        });

        btnCancel.setOnClickListener(v -> llInternetDialog.setVisibility(View.GONE));
    }

    public void getAdverts() {
        if (networkUtils.isNetworkAvailable()) {
            try {
                Constants.getAdverts(this)
                        .setCallback((e, result) -> {
                            ArrayList<Adverts> adverts = new ArrayList<>();
                            try {
                                if (result.has("data")) {
                                    JsonArray data = result.get("data").getAsJsonObject().get("rows").getAsJsonArray();
                                    int res = data.size();
                                    for (int i = 0; i < res; i++) {
                                        String icon = data.get(i).getAsJsonObject().get("content_url").getAsString();
                                        String title = data.get(i).getAsJsonObject().get("title").getAsString();
                                        String description = data.get(i).getAsJsonObject().get("description").getAsString();
                                        adverts.add(new Adverts(Uri.parse(icon), title, description));
                                    }
                                } else {
                                    Uri icon = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + R.drawable.request);
                                    adverts.add(new Adverts(icon, "Welcome to Route", "Adverts coming soon on this screen, please login or signup to enjoy our services."));
                                }
                            } catch (Exception ex) {
                                Timber.d(ex);
                            }

                            CardSliderViewPager cardSliderViewPager = (CardSliderViewPager) findViewById(R.id.viewPager);
                            cardSliderViewPager.setAdapter(new
                                    SliderAdapter(getApplicationContext(), adverts));
                        });
            } catch (
                    Exception ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            llInternetDialog.setVisibility(View.VISIBLE);
        }
    }

    public void signUP(View view) {
        startActivity(new Intent(ServicesActivity.this, UserNamesActivity.class));
    }

    public void loginPage(View view) {
        startActivity(new Intent(ServicesActivity.this, LoginActivity.class));
    }
}