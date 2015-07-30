package com.example.user.sqlite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 2015/7/30.
 */
public class listviewadapter extends BaseAdapter {
    private LayoutInflater inflater;
    List<Contact> userlist1;

    public listviewadapter(Context c,List<Contact> user){
        this.inflater=LayoutInflater.from(c);
        this.userlist1=user;
    }
    @Override
    public int getCount() {
        return userlist1.size();
    }

    @Override
    public Object getItem(int i) {
        return userlist1.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.listview_item,viewGroup,false);
        TextView id,name,phone;
        id = (TextView) view.findViewById(R.id.ID);
        name = (TextView) view.findViewById(R.id.Name);
        phone = (TextView) view.findViewById(R.id.Phone);
        Contact c=userlist1.get(i);


        id.setText(Integer.toString(c.getID()));
        name.setText(c.getName());
        phone.setText(c.getPhoneNumber());

        return view;
    }


}
