package com.betterx.android.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.betterx.android.R;
import com.betterx.android.dataModel.ContactToShare;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShareWithContactAdapter extends ArrayAdapter<ContactToShare> {

    private final LayoutInflater inflater;

    private final List<ContactToShare> selectedContacts;

    public ShareWithContactAdapter(Context context, List<ContactToShare> objects) {
        super(context, 0, objects);
        inflater = LayoutInflater.from(context);
        selectedContacts = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.li_contacts_to_share_with, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final ContactToShare contact = getItem(position);
        final boolean isSelected = selectedContacts.contains(contact);
        Picasso.with(getContext()).load(contact.photoUri).into(holder.cover);
        holder.email.setText(contact.email);
        holder.name.setText(contact.name);
        convertView.setBackgroundColor(isSelected ? Color.LTGRAY : Color.TRANSPARENT);

        return convertView;
    }

    public void changeSelection(ContactToShare contactToShare) {
        if(!selectedContacts.remove(contactToShare)) {
            selectedContacts.add(contactToShare);
        }
        notifyDataSetChanged();
    }

    public List<ContactToShare> getSelectedContacts() {
        return selectedContacts;
    }

    protected class ViewHolder {
        @Bind(R.id.contact_cover)
        ImageView cover;
        @Bind(R.id.contact_name)
        TextView name;
        @Bind(R.id.contact_email)
        TextView email;

        public ViewHolder(View parent) {
            ButterKnife.bind(this, parent);
        }
    }

}
