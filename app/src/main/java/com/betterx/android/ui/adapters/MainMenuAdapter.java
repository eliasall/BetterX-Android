package com.betterx.android.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.betterx.android.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainMenuAdapter extends ArrayAdapter<String> {

    private final LayoutInflater inflater;
    private int messageCount = 0;

    public MainMenuAdapter(Context context, List<String> objects) {
        super(context, 0, objects);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.li_main_menu, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final String title = getItem(position);
        holder.menuItemTitle.setText(title);
        holder.messagesCount.setText("");

        if(position == 1 && messageCount > 0) {
            holder.messagesCount.setText(messageCount + "");
        }

        return convertView;
    }

    public void setMessagesCount(int count) {
        messageCount = count;
        notifyDataSetChanged();
    }

    public class ViewHolder {
        @Bind(R.id.menu_item_title)
        TextView menuItemTitle;
        @Bind(R.id.menu_msg_count)
        TextView messagesCount;

        public ViewHolder(View parent) {
            ButterKnife.bind(this, parent);
        }
    }
}
