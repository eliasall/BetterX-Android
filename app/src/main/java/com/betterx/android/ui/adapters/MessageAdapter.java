package com.betterx.android.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.betterx.android.R;
import com.betterx.android.dataModel.Message;
import com.betterx.android.dataModel.MessageType;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessageAdapter extends ArrayAdapter<Message> {

    private final LayoutInflater inflater;

    public MessageAdapter(Context context, List<Message> objects) {
        super(context, 0, objects);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return MessageType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        final Message message = getItem(position);
        return message.messageType.ordinal();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            final MessageType type = MessageType.values()[getItemViewType(position)];
            final int layoutId = type == MessageType.SENT ? R.layout.li_msg_sended : R.layout.li_msg_received;
            convertView = inflater.inflate(layoutId, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final Message message = getItem(position);
        holder.message.setText(message.msg);
        return convertView;
    }

    protected class ViewHolder {
        @Bind(R.id.msg)
        TextView message;

        public ViewHolder(View parent) {
            ButterKnife.bind(this, parent);
        }
    }

}
