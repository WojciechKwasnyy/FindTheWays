package com.workspaceapp.findtheway;

import android.app.Activity;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sebastian on 04.02.2017.
 */

public class ChatAdapter extends BaseAdapter {
    List <Message> messages;
    Context context;

    public ChatAdapter(List<Message> messages, Context context)
    {
        this.messages = messages;
        this.context = context;
    }
    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = messages.get(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(message.getSender().equals(Config.getInstance().userID))
        {
            convertView = mInflater.inflate(R.layout.message_row_out, null);
            TextView bodyTV = (TextView) convertView.findViewById(R.id.textbody);
            bodyTV.setText(message.getBody());
        }
        else
        {
            convertView = mInflater.inflate(R.layout.message_row_in, null);
            TextView bodyTV = (TextView) convertView.findViewById(R.id.textbody);
            bodyTV.setText(message.getBody());
        }

        return convertView;
    }
}
