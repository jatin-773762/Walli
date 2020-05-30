package com.example.walli.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walli.ImageViewer;
import com.example.walli.Image_Object;
import com.example.walli.Model.Photo;
import com.example.walli.Model.Photo_Info;
import com.example.walli.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Random;

public class Home_Adapter extends RecyclerView.Adapter<Home_Adapter.ImageHolder>{

    private ArrayList<Image_Object> list;
    private View.OnClickListener mlistener;
    private Context context;

    public Home_Adapter(Context context,ArrayList<Image_Object> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.image_cr,parent,false);
        return new ImageHolder(view,mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        holder.id.setText(list.get(position).getId());
        Picasso.get().load(list.get(position).getUrl()).placeholder(R.color.colorAccent).fit().centerCrop().into(holder.image);
    }

    @Override
    public int getItemCount() {
        return (list!=null?list.size():0);
    }


    public class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView id;
        private ImageView image;

        public ImageHolder(@NonNull View itemView, View.OnClickListener listener) {
            super(itemView);
            context = itemView.getContext();
            mlistener =listener;
            id =itemView.findViewById(R.id.id);
            image = itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, ImageViewer.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("id",list.get(getAdapterPosition()).getId());
            i.putExtra("user_name",list.get(getAdapterPosition()).getUsername());
            i.putExtra("name",list.get(getAdapterPosition()).getName());
            context.startActivity(i);
        }
    }
}
