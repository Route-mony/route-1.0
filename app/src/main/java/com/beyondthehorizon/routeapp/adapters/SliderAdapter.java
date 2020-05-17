package com.beyondthehorizon.routeapp.adapters;

import android.view.View;
import android.widget.TextView;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.models.Adverts;
import com.github.islamkhsh.CardSliderAdapter;

import java.util.ArrayList;

public class SliderAdapter extends CardSliderAdapter<Adverts> {

    public SliderAdapter(ArrayList<Adverts> movies) {
        super(movies);
    }

    @Override
    public void bindView(int position, View itemContentView, Adverts item) {
        //TODO bind item object with item layout view
        // add items to arraylist
        TextView title = itemContentView.findViewById(R.id.ioTitle);
        TextView desc = itemContentView.findViewById(R.id.ioDesc);
        title.setText(item.getTitle());
        desc.setText(item.getDescription());

    }

    @Override
    public int getItemContentLayout(int position) {
        //TODO return the item layout of every position
        //This layout will be added as a child of CardView

        return R.layout.item_card_content;
    }
}
