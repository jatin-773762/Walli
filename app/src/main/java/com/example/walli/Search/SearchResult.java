package com.example.walli.Search;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.walli.Image_Object;
import com.example.walli.Model.Result;
import com.example.walli.R;
import com.example.walli.Model.Search;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SearchResult extends AppCompatActivity {

    private static final String ACCESS_KEY = "Lh5mrr5O_QuBEKHCc-rLSQn7qq8LhXtuGQRaWLHkMTg";
    private static String URL = "https://api.unsplash.com";
    private SearchAdapter search_adapter;
    private ArrayList<Image_Object> list;
    private RecyclerView recyclerView;
    private GridLayoutManager manager;
    private int pastVisibleItems, visibleItemCount, totalItemCount,page =1;
    private RequestQueue requestQueue;
    private boolean loading = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        list = new ArrayList<>();
        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(this);
        setUpFeedRecycler();

    }
    public class SearchTask extends AsyncTask<String,String,String>{

        String query;
        public SearchTask(String query) {
            this.query = query;
        }

        @Override
        protected String doInBackground(String... strings) {
            StringRequest request = new StringRequest(URL+query, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    GsonBuilder builder =new GsonBuilder();
                    Gson gson = builder.create();
                    Search search = gson.fromJson(response,Search.class);
                    if(search.getTotal()==0){
                        recyclerView.setVisibility(View.GONE);
                        TextView textView = findViewById(R.id.noresulttext);
                        textView.setText("No Result Found for "+getIntent().getStringExtra("query"));
                        textView.setVisibility(View.VISIBLE);
                    }
                    List<Result> details = search.getResults();
                    for (Result detail : details) {
                        Image_Object data = new Image_Object(detail.getId(),detail.getUrls().getThumb(),detail.getUserInfo().getName(),detail.getUserInfo().getUsername());
                        list.add(data);
                    }
                    search_adapter.notifyDataSetChanged();
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
    private void setUpFeedRecycler(){
        recyclerView = findViewById(R.id.recycler_search);
        recyclerView.setHasFixedSize(true);
        manager  = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(manager);
        search_adapter = new SearchAdapter(SearchResult.this,list);
        recyclerView.setAdapter(search_adapter);
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
                            new SearchTask("/search/photos?client_id="+ACCESS_KEY+"&query="+getIntent().getStringExtra("query")+ "&page=" + page+"&per_page=15").execute();
                        }
                    }
                }
            }
        });
        new SearchTask("/search/photos?client_id="+ACCESS_KEY+"&query="+getIntent().getStringExtra("query") + "&page=" + page+"&per_page=18").execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}

