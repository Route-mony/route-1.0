package com.beyondthehorizon.route.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.beyondthehorizon.route.R;
import com.beyondthehorizon.route.models.MultiContactModel;
import com.beyondthehorizon.route.utils.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MultiChoiceContactsAdapter extends RecyclerView.Adapter<MultiChoiceContactsAdapter.ContactHolder> implements Filterable {

    Context context;
    List<MultiContactModel> list;
    List<MultiContactModel> filteredVisitorArrayList;
    List<MultiContactModel> chosenContacts = new ArrayList<>();
    SharedPreferences prefs;
    SparseBooleanArray itemStateArray = new SparseBooleanArray();

    public MultiChoiceContactsAdapter(Context context, List<MultiContactModel> list) {
        this.context = context;
        this.list = list;
        this.filteredVisitorArrayList = list;
        prefs = context.getSharedPreferences(Constants.REG_APP_PREFERENCES, 0);
        itemStateArray.clear();
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.multi_contact_row_layout, parent, false);

        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactHolder holder, final int position) {

//        final MultiContactModel contact = list.get(position);
        holder.bind(position);
////        final MultiContactModel contact = list.get(position);
//        Log.e("BABA223", "onBindViewHolder: " + contact.is_route());
        holder.checkBox.setEnabled(false);
//        holder.checkBox.setChecked(list.get(position).is_selected());
//        holder.username.setText(contact.getUsername());
//        holder.single_contact.setText(contact.getPhone_number());
//        holder.checkBox.setTag(position);
//
//        if (holder.checkBox.isChecked()) {
//            holder.rowLayout.setBackgroundColor(Color.parseColor("#D5D9DB"));
////            holder.username.setTextColor(Color.parseColor("#1D1F29"));
////            holder.single_contact.setTextColor(Color.parseColor("#1D1F29"));
//        } else {
//            holder.rowLayout.setBackgroundColor(Color.parseColor("#EDEEF0"));
////            holder.username.setTextColor(Color.parseColor("#1D1F29"));
////            holder.single_contact.setTextColor(Color.parseColor("#1D1F29"));
//        }
//
//        RequestOptions requestOptions = new RequestOptions();
//        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
//        if (contact.is_route()) {
//            Glide.with(context)
//                    .load(contact.getImage())
//                    .centerCrop()
//                    .error(R.drawable.group416)
//                    .placeholder(R.drawable.group416)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
//                    .apply(requestOptions)
//                    .into(holder.circleImageView);
//        } else {
//            Glide.with(context)
//                    .load(contact.getImage())
//                    .centerCrop()
//                    .error(R.drawable.default_avatar)
//                    .placeholder(R.drawable.default_avatar)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
//                    .apply(requestOptions)
//                    .into(holder.circleImageView);
//        }
//        holder.rowLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.checkBox.performClick();
////                Integer pos = (Integer) holder.checkBox.getTag();
////                notifyDataSetChanged();
////                if (list.get(pos).is_selected()) {
////                    holder.rowLayout.setBackgroundColor(Color.parseColor("#ffffff"));
//////                    holder.username.setTextColor(Color.parseColor("#1D1F29"));
//////                    holder.single_contact.setTextColor(Color.parseColor("#1D1F29"));
////                    list.get(pos).set_selected(false);
////                } else {
////                    list.get(pos).set_selected(true);
////                    holder.rowLayout.setBackgroundColor(Color.parseColor("#889AA6"));
//////                    holder.username.setTextColor(Color.parseColor("#1D1F29"));
//////                    holder.single_contact.setTextColor(Color.parseColor("#1D1F29"));
////                }
////                chosenContacts.clear();
////                for (int j = 0; j < list.size(); j++) {
////
////                    if (list.get(j).is_selected()) {
//                        chosenContacts.add(new MultiContactModel(
//                                list.get(j).getUsername(),
//                                list.get(j).getPhone_number(),
//                                list.get(j).getImage(),
//                                "",
//                                list.get(j).is_route(),
//                                true
//                        ));
////                        holder.rowLayout.setBackgroundColor(Color.parseColor("#567845"));
//////                        holder.username.setTextColor(Color.parseColor("#1D1F29"));
//////                        holder.single_contact.setTextColor(Color.parseColor("#1D1F29"));
////                    }
////

////                }
//
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public Filter getFilter() {
        return filteredProvidersList;
    }

    private Filter filteredProvidersList = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<MultiContactModel> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList = filteredVisitorArrayList;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (MultiContactModel item : filteredVisitorArrayList) {
                    if (item.getUsername().toLowerCase().contains(filterPattern) ||
                            item.getAmount().toLowerCase().contains(filterPattern) ||
                            item.getPhone_number().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list = (ArrayList<MultiContactModel>) filterResults.values;
            notifyDataSetChanged();
        }
    };

    public class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView username, single_contact;
        CheckBox checkBox;
        CircleImageView circleImageView;
        LinearLayout rowLayout;

        final SharedPreferences.Editor editor = prefs.edit();
        final Gson gsonn = new Gson();

        public ContactHolder(View itemView) {
            super(itemView);

            rowLayout = itemView.findViewById(R.id.rowLayout);
            username = itemView.findViewById(R.id.username);
            single_contact = itemView.findViewById(R.id.contact);
            checkBox = itemView.findViewById(R.id.checkBox_select);
            circleImageView = itemView.findViewById(R.id.profile_image);

            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            // use the sparse boolean array to check
            final MultiContactModel contact = list.get(position);

//            checkBox.setChecked(list.get(position).is_selected());
            username.setText(contact.getUsername());
            single_contact.setText(contact.getPhone_number());

            if (!itemStateArray.get(position, false)) {
                checkBox.setChecked(false);
                rowLayout.setBackgroundColor(Color.parseColor("#EDEEF0"));
            } else {
                checkBox.setChecked(true);
                rowLayout.setBackgroundColor(Color.parseColor("#D5D9DB"));
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
                        .into(circleImageView);
            } else {
                Glide.with(context)
                        .load(contact.getImage())
                        .centerCrop()
                        .error(R.drawable.default_avatar)
                        .placeholder(R.drawable.default_avatar)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .apply(requestOptions)
                        .into(circleImageView);
            }
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

//            Log.e("AMADEADMAN", "bind: " + adapterPosition);

            if (!itemStateArray.get(adapterPosition, false)) {
                checkBox.setChecked(true);
                itemStateArray.put(adapterPosition, true);
                rowLayout.setBackgroundColor(Color.parseColor("#D5D9DB"));
                chosenContacts.add(new MultiContactModel(
                        list.get(adapterPosition).getId(),
                        list.get(adapterPosition).getUsername(),
                        list.get(adapterPosition).getPhone_number(),
                        list.get(adapterPosition).getImage(),
                        "0",
                        list.get(adapterPosition).is_route(),
                        true
                ));

            } else {
                checkBox.setChecked(false);
                itemStateArray.put(adapterPosition, false);
                rowLayout.setBackgroundColor(Color.parseColor("#EDEEF0"));
                chosenContacts.remove(new MultiContactModel(
                        list.get(adapterPosition).getId(),
                        list.get(adapterPosition).getUsername(),
                        list.get(adapterPosition).getPhone_number(),
                        list.get(adapterPosition).getImage(),
                        "0",
                        list.get(adapterPosition).is_route(),
                        true
                ));
            }

            String jsonn = gsonn.toJson(chosenContacts);
            editor.putString(Constants.MY_MULTI_CHOICE_SELECTED_CONTACTS, jsonn);
            editor.apply();
        }
    }

    public List<MultiContactModel> getContactsList() {
        return list;
    }
}
 
