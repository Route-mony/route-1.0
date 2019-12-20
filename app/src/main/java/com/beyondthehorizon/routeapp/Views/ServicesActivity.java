package com.beyondthehorizon.routeapp.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.beyondthehorizon.routeapp.Adapters.SliderAdapter;
import com.beyondthehorizon.routeapp.Models.Movies;
import com.beyondthehorizon.routeapp.R;
import com.github.islamkhsh.CardSliderViewPager;

import java.util.ArrayList;

public class ServicesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        ArrayList<Movies> movies = new ArrayList<Movies>();
        movies.add(new Movies("Route That Payment. Easy1. ",
                "Lorem Ipsum is simply dummy text of" +
                        " the printing and typesetting industry."));
        movies.add(new Movies("Route That Payment. Easy2. ",
                "Lorem Ipsum is simply dummy text of" +
                        " the printing and typesetting industry."));
        movies.add(new Movies("Route That Payment. Easy3. ",
                "Lorem Ipsum is simply dummy text of" +
                        " the printing and typesetting industry."));
        movies.add(new Movies("Route That Payment. Easy4. ",
                "Lorem Ipsum is simply dummy text of" +
                        " the printing and typesetting industry."));
        movies.add(new Movies("Route That Payment. Easy5. ",
                "Lorem Ipsum is simply dummy text of" +
                        " the printing and typesetting industry."));

        CardSliderViewPager cardSliderViewPager = (CardSliderViewPager) findViewById(R.id.viewPager);
        cardSliderViewPager.setAdapter(new SliderAdapter(movies));
    }

    public void signUP(View view) {
        startActivity(new Intent(ServicesActivity.this, SignUpActivity.class));
    }

    public void homePage(View view) {
        startActivity(new Intent(ServicesActivity.this, MainActivity.class));
    }
}