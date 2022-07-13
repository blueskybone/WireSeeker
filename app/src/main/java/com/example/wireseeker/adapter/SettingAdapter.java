package com.example.wireseeker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.wireseeker.R;
import com.example.wireseeker.database.Wire;

import java.util.ArrayList;
import java.util.List;

public class SettingAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private final int VIEW_TYPE = 2;
    private final int TYPE_1 = 0;
    private final int TYPE_2 = 1;
    private ArrayList ListString;
    public SettingAdapter(Context context, ArrayList<String> listString){
        ListString = listString;
        inflater = LayoutInflater.from(context);
    }

    static class viewHolder1
    {
        TextView textView;
        Switch switch_theme;
    }
    static class viewHolder2
    {
        TextView textView;
    }
    // override other abstract methods here

    @Override
    public int getCount() {
        return ListString.size();
    }

    @Override
    public Object getItem(int i) {
        return ListString.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE;
    }
        @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        int p = position % 6;
        Log.e("1","p "+ p);
        Log.e("2","position "+ position);
        if(p == 0)
            return TYPE_1;
        else if(p == 1)
            return TYPE_2;
        else
        return TYPE_1;
        }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        Log.e("3","getview_________");
        viewHolder1 holder1 = null;
        viewHolder2 holder2 = null;
        int type = getItemViewType(position);

        if (convertView == null) {
            Log.e("convertView = ", " NULL");
            switch (type)
            {
                case TYPE_1:
                    convertView = inflater.inflate(R.layout.list_item1, container, false);
                    holder1 = new viewHolder1();
                    holder1.textView = (TextView)convertView.findViewById(R.id.textView_theme);
                    holder1.switch_theme = (Switch)convertView.findViewById(R.id.switch_theme);
                    convertView.setTag(holder1);
                    break;
                case TYPE_2:
                    convertView = inflater.inflate(R.layout.list_item2, container, false);
                    holder2 = new viewHolder2();
                    holder2.textView = (TextView)convertView.findViewById(R.id.textView_word);
                    convertView.setTag(holder2);
                    break;
                default:break;
            }
        }
        else {
            //有convertView，按样式，取得不用的布局
            switch (type) {
                case TYPE_1:
                    holder1 = (viewHolder1) convertView.getTag();
                    break;
                case TYPE_2:
                    holder2 = (viewHolder2) convertView.getTag();
                    break;
                default:
                    break;
            }
        }
        return convertView;
    }

}