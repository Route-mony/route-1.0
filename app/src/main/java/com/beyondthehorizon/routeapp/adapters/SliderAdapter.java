package com.beyondthehorizon.routeapp.adapters;

import android.content.ContentResolver;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.models.Adverts;
import com.github.islamkhsh.CardSliderAdapter;

import java.io.IOException;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;

public class SliderAdapter extends CardSliderAdapter<Adverts> {

    public SliderAdapter(ArrayList<Adverts> movies) {
        super(movies);
    }

    @Override
    public void bindView(int position, View itemContentView, Adverts item) {
        //TODO bind item object with item layout view
        // add items to arraylist
        ImageView imageView = itemContentView.findViewById(R.id.icon);
        TextView title = itemContentView.findViewById(R.id.ioTitle);
        TextView desc = itemContentView.findViewById(R.id.ioDesc);

        ContentResolver contentResolver = new ContentResolver(null) {
            @Nullable
            @Override
            public String[] getStreamTypes(@NonNull Uri url, @NonNull String mimeTypeFilter) {
                return super.getStreamTypes(url, mimeTypeFilter);
            }
        };
        try {
            GifDrawable gifFromUri = new GifDrawable( contentResolver, item.getIcon_url() );
        } catch (IOException e) {
            e.printStackTrace();
        }
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
