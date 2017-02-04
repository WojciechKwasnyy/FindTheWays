package com.workspaceapp.findtheway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Sebastian on 29.01.2017.
 */

class UserListAdapter extends BaseAdapter {

    Context context;
    List<User> data;
    private static LayoutInflater inflater = null;

    public UserListAdapter(Context context, List<User> data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row, null);
        TextView text = (TextView) vi.findViewById(R.id.choseuserTV);
        text.setText(data.get(position).getDisplayname());
        ImageView providerIV = (ImageView) vi.findViewById(R.id.imageViewprovider);
        if(data.get(position).getProvider().equals("google.com"))
        {
            providerIV.setImageResource(R.drawable.googleicon);
        }
        else if(data.get(position).getProvider().equals("facebook.com"))
        {
            providerIV.setImageResource(R.drawable.facebookicon);
        }
        else if (data.get(position).getProvider().equals("password"))
        {

        }
        return vi;
    }
}
