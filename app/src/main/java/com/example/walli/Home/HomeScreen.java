package com.example.walli.Home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.walli.Downloads.Downloads;
import com.example.walli.Helper.InternetCheck;
import com.example.walli.Image_Object;
import com.example.walli.Model.Photo_Info;
import com.example.walli.R;
import com.example.walli.Search.ImageSearch.ImageQueryAdapter;
import com.example.walli.Search.ImageSearch.ImageSearch;
import com.example.walli.Search.SearchResult;
import com.example.walli.User.Auth_Activity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {

    private static final String ACCESS_KEY = "Lh5mrr5O_QuBEKHCc-rLSQn7qq8LhXtuGQRaWLHkMTg";
    private static final int REQUEST_FOR_IMAGE_SEARCH = 2001;
    private static String URL = "https://api.unsplash.com";
    private Home_Adapter home_adapter;
    private int page=1;
    private ArrayList<Image_Object> list;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private GridLayoutManager manager;
    private RelativeLayout relativeLayout;
    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private ImageButton imageSearch_btn;
    private LayoutInflater inflater;
    private SearchView search_bar;
    private Boolean loading = true;
    private InternetCheck internetCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        list = new ArrayList<>();
        search_bar = findViewById(R.id.search_bar);
        imageSearch_btn = findViewById(R.id.image_search_btn);
        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent(getApplicationContext(),SearchResult.class);
                i.putExtra("query",query);
                startActivity(i);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        imageSearch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(HomeScreen.this, ImageSearch.class),REQUEST_FOR_IMAGE_SEARCH);
            }
        });
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        relativeLayout = findViewById(R.id.home_rl);
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!=null&&activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            if(requestQueue == null)
                requestQueue = Volley.newRequestQueue(getApplicationContext());
            setUpFeedRecycler();
        }
        else
            createConnectionCard();

//        startService(new Intent(this, NetworkIntentService.class));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FOR_IMAGE_SEARCH){
            if(resultCode == RESULT_OK && data!=null){
                ArrayList<String> queryList = (ArrayList<String>) data.getSerializableExtra("query_list");
                ArrayList<Float> confidenceList = (ArrayList<Float>) data.getSerializableExtra("confidence_list");
                ask_for_query(queryList,confidenceList);
            }
            else if(data==null){
                Toast.makeText(HomeScreen.this, "No Data Found From Image", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(HomeScreen.this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void ask_for_query(ArrayList<String> query_list,ArrayList<Float> confidence_list) {
        Dialog dialog = new Dialog(HomeScreen.this);
        dialog.setContentView(R.layout.select_query_dialog);
        dialog.setTitle("Select Query");
        ListView listView = dialog.findViewById(R.id.list_item);
        dialog.setCancelable(true);
        final ImageQueryAdapter adapter = new ImageQueryAdapter(this,query_list,confidence_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                Intent i = new Intent(getApplicationContext(),SearchResult.class);
                i.putExtra("query",item.toString());
                startActivity(i);
            }
        });
        dialog.show();
    }


    @SuppressLint("NewApi")
    private void createConnectionCard() {
        final View view = inflater.inflate(R.layout.connection_card,relativeLayout,false);
        ImageButton refresh = view.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                internetCheck = new InternetCheck(new InternetCheck.Consumer(){
                    @Override
                    public void accept(boolean internet) {
                        if(internet){
                            View view1 = findViewById(R.id.connection_card);
                            ViewGroup viewGroup = (ViewGroup) view1.getParent();
                            viewGroup.removeView(view1);
                            if(requestQueue == null)
                                requestQueue = Volley.newRequestQueue(getApplicationContext());
                            setUpFeedRecycler();
                        }
                    }
                });
                internetCheck.execute();
            }
        });
        relativeLayout.addView(view);
    }

    private void setUpFeedRecycler(){
        recyclerView = findViewById(R.id.recycler_home);
        recyclerView.setHasFixedSize(true);
        manager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(manager);
        home_adapter = new Home_Adapter(getApplicationContext(),list);
        recyclerView.setAdapter(home_adapter);
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
                            new AsnycTaskRunner("/photos?client_id=" + ACCESS_KEY + "&page=" + page+"&per_page=12").execute();
                        }
                    }
                }
            }
        });
        new AsnycTaskRunner("/photos?client_id=" + ACCESS_KEY + "&page=" + page+"&per_page=12").execute();
    }

    private class AsnycTaskRunner extends AsyncTask<String,String,String> {

        String query;

        public AsnycTaskRunner(String query) {
            this.query = query;
        }

        @Override
        protected String doInBackground(String... strings) {
            StringRequest request = new StringRequest(URL + query, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    Photo_Info[] details = gson.fromJson(response, Photo_Info[].class);
                    for (Photo_Info detail : details) {
                        Image_Object data = new Image_Object(detail.getId(),detail.getUrls().getThumb(),detail.getUserInfo().getName(),detail.getUserInfo().getUsername());
                        list.add(data);
                    }
                    home_adapter.notifyDataSetChanged();
                    loading = false;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        MenuItem profileItem = menu.findItem(R.id.profile);
        MenuItem downloadItem = menu.findItem(R.id.downloads);
        profileItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getApplicationContext(), Auth_Activity.class));
                return true;
            }
        });

        downloadItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getApplicationContext(), Downloads.class));
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}
