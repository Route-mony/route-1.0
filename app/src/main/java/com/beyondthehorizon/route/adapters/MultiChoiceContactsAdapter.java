package com.beyondthehorizon.route.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.beyondthehorizon.route.models.contacts.MultiContactModel;
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

    private Context context;
    private List<MultiContactModel> list = new ArrayList<>();
    private List<MultiContactModel> filteredVisitorArrayList = new ArrayList<>();
    private List<MultiContactModel> chosenContacts = new ArrayList<>();
    SparseBooleanArray itemStateArray = new SparseBooleanArray();

    public MultiChoiceContactsAdapter(Context context) {
        this.context = context;
        itemStateArray.clear();
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.multi_contact_row_layout, parent, false);

        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactHolder holder, final int position) {
        holder.bind(position);
        holder.checkBox.setEnabled(false);
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
                            item.getPhoneNumber().toLowerCase().contains(filterPattern)) {
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
            final MultiContactModel contact = list.get(position);
            username.setText(contact.getUsername());
            single_contact.setText(contact.getRouteUsername());

            if (!itemStateArray.get(position, false)) {
                checkBox.setChecked(false);
                rowLayout.setBackgroundColor(Color.parseColor("#EDEEF0"));
            } else {
                checkBox.setChecked(true);
                rowLayout.setBackgroundColor(Color.parseColor("#D5D9DB"));
            }
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
            if (contact.isRouteUser()) {
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

            if (!itemStateArray.get(adapterPosition, false)) {
                checkBox.setChecked(true);
                rowLayout.setBackgroundColor(Color.parseColor("#D5D9DB"));
            } else {
                checkBox.setChecked(false);
                rowLayout.setBackgroundColor(Color.parseColor("#EDEEF0"));
            }
            itemStateArray.put(adapterPosition, checkBox.isChecked());
            chosenContacts.add(new MultiContactModel(
                    "0",
                    list.get(adapterPosition).getId(),
                    list.get(adapterPosition).getImage(),
                    list.get(adapterPosition).isRoute(),
                    checkBox.isSelected() ? "True" : "False",
                    list.get(adapterPosition).getPhoneNumber(),
                    list.get(adapterPosition).getRouteUsername(),
                    list.get(adapterPosition).getUsername()
            ));
//
//            String jsonn = gsonn.toJson(chosenContacts);
//            editor.putString(Constants.MY_MULTI_CHOICE_SELECTED_CONTACTS, jsonn);
//            editor.apply();
        }
    }

}
 
