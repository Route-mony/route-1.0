package com.beyondthehorizon.routeapp.Adapters;

import android.view.View;
import android.widget.TextView;

import com.beyondthehorizon.routeapp.Models.Movies;
import com.beyondthehorizon.routeapp.R;
import com.github.islamkhsh.CardSliderAdapter;
import com.github.islamkhsh.CardSliderViewPager;

import java.util.ArrayList;

public class SliderAdapter extends CardSliderAdapter<Movies> {

    public SliderAdapter(ArrayList<Movies> movies) {
        super(movies);
    }

    @Override
    public void bindView(int position, View itemContentView, Movies item) {
        //TODO bind item object with item layout view
        // add items to arraylist
        TextView title = itemContentView.findViewById(R.id.ioTitle);
        TextView desc = itemContentView.findViewById(R.id.ioDesc);
        title.setText(item.getName());
        desc.setText(item.getExplanation());

    }

    @Override
    public int getItemContentLayout(int position) {
        //TODO return the item layout of every position
        //This layout will be added as a child of CardView

        return R.layout.item_card_content;
    }
}
