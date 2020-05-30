package com.example.walli.Source_Details;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.walli.Image_Object;
import com.example.walli.Model.Photo;
import com.example.walli.Model.Photo_Info;
import com.example.walli.Model.Result;
import com.example.walli.Model.Search;
import com.example.walli.Model.User_Info;
import com.example.walli.R;
import com.example.walli.Search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class PhotosFragment extends Fragment {

    private GridLayoutManager manager;
    private String username;
    private source_adapter source_adapter;
    private int pastVisibleItems, visibleItemCount, totalItemCount,page =1;
    private boolean loading=true;
    private ArrayList<Image_Object> list;
    private RequestQueue requestQueue;
    RecyclerView recyclerView;
    private static final String ACCESS_KEY = "Lh5mrr5O_QuBEKHCc-rLSQn7qq8LhXtuGQRaWLHkMTg";
    private static String URL = "https://api.unsplash.com";


    public PhotosFragment(String username) {
        this.username = username;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(getContext());
        list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.photos_recycler);
        setUpFeedRecycler();
        return view;
    }
    private void setUpFeedRecycler(){
        recyclerView.setHasFixedSize(true);
        manager  = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(manager);
        source_adapter = new source_adapter(getContext(),list);
        recyclerView.setAdapter(source_adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    visibleItemCount = manager.getChildCount();
                    totalItemCount = manager.getItemCount();
                    pastVisibleItems = manager.findFirstVisibleItemPosition();
                    if (!loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = true;
                            page = page+1;
                            new LoadTask(URL+"/users/"+username+"/photos?client_id="+ACCESS_KEY+"&page="+page+"&per_page=12").execute();
                        }
                    }
                }
            }
        });
        new LoadTask(URL+"/users/"+username+"/photos?client_id="+ACCESS_KEY+"&page="+page+"&per_page=12").execute();
    }
    public class LoadTask extends AsyncTask<Void,Void,Void> {

        String url;
        public LoadTask(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    GsonBuilder builder =new GsonBuilder();
                    Gson gson = builder.create();
                    Photo_Info[] photos = gson.fromJson(response,Photo_Info[].class);
                    for (Photo_Info detail : photos) {
                        Image_Object data = new Image_Object(detail.getId(),detail.getUrls().getThumb(),detail.getUserInfo().getName(),detail.getUserInfo().getUsername());
                        list.add(data);
                    }
                    source_adapter.notifyDataSetChanged();
                    loading=false;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            requestQueue.add(request);
            return null;
        }
    }

}

