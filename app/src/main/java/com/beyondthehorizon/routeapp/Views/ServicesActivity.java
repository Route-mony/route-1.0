package com.beyondthehorizon.routeapp.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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
        movies.add(new Movies("ORIA", "AWESOME"));
        movies.add(new Movies("ORIA", "AWESOME"));
        movies.add(new Movies("ORIA", "AWESOME"));
        movies.add(new Movies("ORIA", "AWESOME"));
        movies.add(new Movies("ORIA", "AWESOME"));
        movies.add(new Movies("ORIA", "AWESOME"));
        movies.add(new Movies("ORIA", "AWESOME"));

        CardSliderViewPager cardSliderViewPager = (CardSliderViewPager) findViewById(R.id.viewPager);
        cardSliderViewPager.setAdapter(new SliderAdapter(movies));
    }
}
