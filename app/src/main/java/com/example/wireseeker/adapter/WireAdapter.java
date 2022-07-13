package com.example.wireseeker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.wireseeker.R;
import com.example.wireseeker.database.Wire;

import java.util.List;

public class WireAdapter extends BaseAdapter {
    private final List<Wire> data;
    private final LayoutInflater layoutInflater;
    private final int wordNum;

    public WireAdapter(Context context, List<Wire> data, int wordNum){
        this.data = data;
        this.wordNum = wordNum;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    static class ViewHolder
    {
        TextView tvTableName;
        TextView tvWireNum;
        TextView tvPoint;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.textview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvTableName = convertView.findViewById(R.id.table_name);
            viewHolder.tvWireNum = convertView.findViewById(R.id.wire_num);
            viewHolder.tvPoint = convertView.findViewById(R.id.point);
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvTableName.setText(data.get(position).getWireTable());
        viewHolder.tvWireNum.setText(data.get(position).getWireNum());
        viewHolder.tvPoint.setText(data.get(position).getPoint());
        viewHolder.tvTableName.setTextSize(wordNum);
        viewHolder.tvWireNum.setTextSize(wordNum);
        viewHolder.tvPoint.setTextSize(wordNum);
        return convertView;
    }

    @Override
    public int getCount()
    {

        if (data == null)
        {
            return 0;
        }

        return data.size();
    }

    @Override
    public Wire getItem(int position)
    {
        return data.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }


}
