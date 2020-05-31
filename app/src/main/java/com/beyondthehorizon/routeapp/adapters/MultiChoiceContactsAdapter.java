package com.beyondthehorizon.routeapp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.beyondthehorizon.routeapp.R;
import com.beyondthehorizon.routeapp.models.MultiContactModel;
import com.beyondthehorizon.routeapp.utils.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.beyondthehorizon.routeapp.utils.Constants.MY_MULTI_CHOICE_SELECTED_CONTACTS;

public class MultiChoiceContactsAdapter extends RecyclerView.Adapter<MultiChoiceContactsAdapter.ContactHolder> {

    Context context;
    List<MultiContactModel> list;
    List<MultiContactModel> chosenContacts = new ArrayList<>();
    SharedPreferences prefs;

    public MultiChoiceContactsAdapter(Context context, List<MultiContactModel> list) {
        this.context = context;
        this.list = list;
        prefs = context.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0);
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.multi_contact_row_layout, parent, false);

        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactHolder holder, final int position) {

        final MultiContactModel contact = list.get(position);
//        final MultiContactModel contact = list.get(position);
//
        Log.e("BABA223", "onBindViewHolder: " + contact.is_route());
        holder.checkBox.setEnabled(false);
        holder.checkBox.setChecked(list.get(position).is_selected());
        holder.username.setText(contact.getUsername());
        holder.single_contact.setText(contact.getPhone_number());
        holder.checkBox.setTag(position);

        if (holder.checkBox.isChecked()) {
            holder.rowLayout.setBackgroundColor(Color.parseColor("#D5D9DB"));
//            holder.username.setTextColor(Color.parseColor("#1D1F29"));
//            holder.single_contact.setTextColor(Color.parseColor("#1D1F29"));
        } else {
            holder.rowLayout.setBackgroundColor(Color.parseColor("#EDEEF0"));
//            holder.username.setTextColor(Color.parseColor("#1D1F29"));
//            holder.single_contact.setTextColor(Color.parseColor("#1D1F29"));
        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        if (contact.is_route()) {
            Glide.with(context)
                    .load(contact.getImage())
                    .centerCrop()
                    .error(R.drawable.group416)
                    .placeholder(R.drawable.group416)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .apply(requestOptions)
                    .into(holder.circleImageView);
        } else {
            Glide.with(context)
                    .load(contact.getImage())
                    .centerCrop()
                    .error(R.drawable.default_avatar)
                    .placeholder(R.drawable.default_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .apply(requestOptions)
                    .into(holder.circleImageView);
        }
        holder.rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkBox.performClick();
                Integer pos = (Integer) holder.checkBox.getTag();
                notifyDataSetChanged();
                if (list.get(pos).is_selected()) {
                    holder.rowLayout.setBackgroundColor(Color.parseColor("#ffffff"));
//                    holder.username.setTextColor(Color.parseColor("#1D1F29"));
//                    holder.single_contact.setTextColor(Color.parseColor("#1D1F29"));
                    list.get(pos).set_selected(false);
                } else {
                    list.get(pos).set_selected(true);
                    holder.rowLayout.setBackgroundColor(Color.parseColor("#889AA6"));
//                    holder.username.setTextColor(Color.parseColor("#1D1F29"));
//                    holder.single_contact.setTextColor(Color.parseColor("#1D1F29"));
                }
                chosenContacts.clear();
                for (int j = 0; j < list.size(); j++) {

                    if (list.get(j).is_selected()) {
                        chosenContacts.add(new MultiContactModel(
                                list.get(j).getUsername(),
                                list.get(j).getPhone_number(),
                                list.get(j).getImage(),
                                "",
                                list.get(j).is_route(),
                                true
                        ));
                        holder.rowLayout.setBackgroundColor(Color.parseColor("#567845"));
//                        holder.username.setTextColor(Color.parseColor("#1D1F29"));
//                        holder.single_contact.setTextColor(Color.parseColor("#1D1F29"));
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        TextView username, single_contact;
        CheckBox checkBox;
        CircleImageView circleImageView;
        LinearLayout rowLayout;

        public ContactHolder(View itemView) {
            super(itemView);

            rowLayout = itemView.findViewById(R.id.rowLayout);
            username = itemView.findViewById(R.id.username);
            single_contact = itemView.findViewById(R.id.contact);
            checkBox = itemView.findViewById(R.id.checkBox_select);
            circleImageView = itemView.findViewById(R.id.profile_image);
        }
    }

    public List<MultiContactModel> getContactsList() {
        return list;
    }
}
 
