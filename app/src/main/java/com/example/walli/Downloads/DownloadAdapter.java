package com.example.walli.Downloads;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walli.R;

import java.util.ArrayList;
//
//public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder> {
//
//    private ArrayList<String> bitmapArrayList;
//    private ArrayList<String> nameArrayList;
//    Context context;
//
//    public DownloadAdapter(ArrayList<String> list,ArrayList<String> id_list, Context context) {
//        this.bitmapArrayList = list;
//        this.nameArrayList= id_list;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.image_cr,parent,false);
//        return new DownloadViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {
////        holder.imageView.setImageBitmap(bitmapArrayList.get(position));
//        holder.id.setText(nameArrayList.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return (bitmapArrayList!=null?bitmapArrayList.size():0);
//    }
//
//    public class DownloadViewHolder extends RecyclerView.ViewHolder{
//
//        ImageView imageView ;
//        TextView id;
//        public DownloadViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imageView = itemView.findViewById(R.id.image);
//            id = itemView.findViewById(R.id.id);
//        }
//    }
//}

public class DownloadAdapter  extends BaseAdapter {
    private Activity activity;
    private String[]                filepath;
    private static LayoutInflater   inflater    = null;
    Bitmap                          bmp         = null;

    public DownloadAdapter (Activity a, String[] fpath)
    {
        activity = a;
        filepath = fpath;
        inflater = (LayoutInflater)activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount ()
    {
        return filepath.length;
    }

    public Object getItem (int position)
    {
        return position;
    }

    public long getItemId (int position)
    {
        return position;
    }

    public View getView (int position, View convertView, ViewGroup parent)
    {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.gridview_item, null);
        ImageView image = (ImageView)vi.findViewById(R.id.image);
        int targetWidth = 100;
        int targetHeight = 100;
        BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
        bmpOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath[position], bmpOptions);
        int currHeight = bmpOptions.outHeight;
        int currWidth = bmpOptions.outWidth;
        int sampleSize = 1;
        if (currHeight > targetHeight || currWidth > targetWidth)
        {
            if (currWidth > currHeight)
                sampleSize = Math.round((float)currHeight
                        / (float)targetHeight);
            else
                sampleSize = Math.round((float)currWidth
                        / (float)targetWidth);
        }
        bmpOptions.inSampleSize = sampleSize;
        bmpOptions.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(filepath[position], bmpOptions);
        image.setImageBitmap(bmp);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        bmp = null;
        return vi;
    }
}