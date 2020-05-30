package com.example.walli.Search.ImageSearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.walli.R;

import java.util.ArrayList;

public class ImageQueryAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> queryList;
    ArrayList<Float> confidenceList;
    public ImageQueryAdapter(@NonNull Context context, ArrayList<String> queryList,ArrayList<Float> confidenceList) {
        this.context = context;
        this.queryList = queryList;
        this.confidenceList= confidenceList;
    }

    @Override
    public int getCount() {
        return queryList.size();
    }

    @Override
    public Object getItem(int position) {
        return queryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View v, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if(v == null){
            v = LayoutInflater.from(context).inflate(R.layout.query_item_card,null);
            viewHolder = new ViewHolder();
            viewHolder.query = v.findViewById(R.id.query_text);
            viewHolder.confidence = v.findViewById(R.id.query_confidence);
            v.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) v.getTag();
        }
        viewHolder.query.setText(queryList.get(position));
        viewHolder.confidence.setText(String.valueOf(confidenceList.get(position)));
        return v;
    }


    static class ViewHolder{
        TextView query;
        TextView confidence;
    }
}
