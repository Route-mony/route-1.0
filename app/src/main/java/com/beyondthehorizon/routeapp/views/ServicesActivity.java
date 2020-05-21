package com.beyondthehorizon.routeapp.views;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.adapters.SliderAdapter;
import com.beyondthehorizon.routeapp.models.Adverts;
import com.beyondthehorizon.routeapp.utils.Constants;
import com.beyondthehorizon.routeapp.views.auth.LoginActivity;
import com.beyondthehorizon.routeapp.views.auth.UserNamesActivity;
import com.github.islamkhsh.CardSliderViewPager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import java.util.ArrayList;

import static com.beyondthehorizon.routeapp.utils.Constants.REG_APP_PREFERENCES;

public class ServicesActivity extends AppCompatActivity {
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0);
        try {
            String token = "Bearer " + pref.getString(Constants.USER_TOKEN, "");
            Constants.getAdverts(this)
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
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
                                    adverts.add(new Adverts(icon,"Welcome to Route", "Adverts coming soon on this screen, please login or signup to enjoy our services."));
                                }
                            } catch (Exception ex) {
                                Log.d("ADVERTSERROR", ex.getMessage());
                            }


                            CardSliderViewPager cardSliderViewPager = (CardSliderViewPager) findViewById(R.id.viewPager);
                            cardSliderViewPager.setAdapter(new

                                    SliderAdapter(adverts));
                        }
                    });
        } catch (
                Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void signUP(View view) {
        startActivity(new Intent(ServicesActivity.this, UserNamesActivity.class));
    }

    public void loginPage(View view) {
        startActivity(new Intent(ServicesActivity.this, LoginActivity.class));
    }
}