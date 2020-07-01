package com.beyondthehorizon.routeapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.models.Adverts;
import com.bumptech.glide.Glide;
import com.github.islamkhsh.CardSliderAdapter;

import java.util.ArrayList;

public class SliderAdapter extends CardSliderAdapter<Adverts> {
    Context context;
    public SliderAdapter(Context context, ArrayList<Adverts> movies) {
        super(movies);
        this.context = context;
    }

    @Override
    public void bindView(int position, View itemContentView, Adverts item) {
        //TODO bind item object with item layout view
        // add items to arraylist
        ImageView imageView = itemContentView.findViewById(R.id.icon);
        TextView title = itemContentView.findViewById(R.id.ioTitle);
        TextView desc = itemContentView.findViewById(R.id.ioDesc);
        Log.d("Image", item.getIcon_url().toString());
        Glide.with(context).asGif().load(item.getIcon_url()).into(imageView);
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
