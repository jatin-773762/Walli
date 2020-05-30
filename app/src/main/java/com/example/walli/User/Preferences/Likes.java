package com.example.walli.User.Preferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.walli.Home.HomeScreen;
import com.example.walli.Home.Home_Adapter;
import com.example.walli.Image_Object;
import com.example.walli.Model.Photo_Info;
import com.example.walli.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Likes extends AppCompatActivity {

    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private GridLayoutManager manager;
    private LikesAdapter adapter;
    private ArrayList<Image_Object> list = new ArrayList<>();
    private static String URL = "https://api.unsplash.com";
    private static final String ACCESS_KEY = "Lh5mrr5O_QuBEKHCc-rLSQn7qq8LhXtuGQRaWLHkMTg";
    private Set<String> photos;
    private SharedPreferences sp;
    private FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);
        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        setUpFeedRecycler();
    }
    private void setUpFeedRecycler(){
        recyclerView = findViewById(R.id.likes_recycler);
        recyclerView.setHasFixedSize(true);
        manager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(manager);
        adapter = new LikesAdapter(getApplicationContext(),list);
        recyclerView.setAdapter(adapter);
    }
    private class LikedLoader extends AsyncTask<String,String,String> {

        String query;

        public LikedLoader(String query) {
            this.query = query;
        }

        @Override
        protected String doInBackground(String... strings) {
            StringRequest request = new StringRequest(URL + query, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    Photo_Info detail = gson.fromJson(response, Photo_Info.class);
                    Image_Object data = new Image_Object(detail.getId(),detail.getUrls().getThumb(),detail.getUserInfo().getName(),detail.getUserInfo().getUsername());
                    list.add(data);
                    adapter.notifyDataSetChanged();
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

    @Override
    protected void onResume() {
        super.onResume();
        photos = new HashSet<>();
        sp = getSharedPreferences("photostats",MODE_PRIVATE);
        photos = sp.getStringSet(user+"/id",null);
        list.clear();
        if(photos!=null){
        Iterator id = photos.iterator();
        while(id.hasNext())
            new LikedLoader("/photos/"+id.next()+"?client_id=" + ACCESS_KEY ).execute();}
    }
}
