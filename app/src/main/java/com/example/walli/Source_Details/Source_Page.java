package com.example.walli.Source_Details;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.walli.Model.User_Info;
import com.example.walli.R;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

public class Source_Page extends Fragment {

    private static final String ACCESS_KEY = "Lh5mrr5O_QuBEKHCc-rLSQn7qq8LhXtuGQRaWLHkMTg";

    TabLayout tabLayout;
    ViewPager viewPager;
    private TextView source_name,source_username,source_website,source_bio;
    static String imgurl;
    static String name;
    static String user_name;
    private ImageView source_profile_picture;
    private RequestQueue requestQueue ;
    PagerAdapter adapter;
    static int photo_count;
    private static String URL = "https://api.unsplash.com";


    public Source_Page(String user_name){
        this.user_name =  user_name;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.source_page,null);
        tabLayout = view.findViewById(R.id.tablayout);
        viewPager = view.findViewById(R.id.source_viewPager);
        source_username = view.findViewById(R.id.source_username);

        source_name = view.findViewById(R.id.source_name);
        source_bio = view.findViewById(R.id.source_description);
        source_profile_picture = view.findViewById(R.id.source_profile_picture);
        source_website = view.findViewById(R.id.source_website);
        source_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent intent = builder.build();
                intent.launchUrl(getContext(), Uri.parse(source_website.getText().toString()));
            }
        });
        if(requestQueue==null){
            requestQueue = Volley.newRequestQueue(getContext());
        }
        adapter = new PagerAdapter(getFragmentManager());
        new LoadProfileTask().execute();

        return view;
    }


    private class LoadProfileTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            getStringRequest(URL+"/users/"+user_name+"?client_id="+ACCESS_KEY);
            return null;
        }
        public void getStringRequest(String url) {
            StringRequest request = new StringRequest(
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseData(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
            requestQueue.add(request);
        }

        private void parseData(String response) {
            GsonBuilder builder =new GsonBuilder();
            Gson gson = builder.create();
            User_Info info = gson.fromJson(response,User_Info.class);
            imgurl = info.getProfileImage().getSmall();
            try{
                source_name.setText(info.getName());}
            catch (NullPointerException e){
                source_name.setText("Anonymous");
            }
            try{
                source_website.setText(info.getPortfolioUrl());
                if(info.getPortfolioUrl()==null){
                    source_website.setVisibility(View.GONE);
                }
            }
            catch (NullPointerException e){
                source_website.setVisibility(View.GONE);
            }
            source_bio.setText(info.getBio());
            source_username.setText("@"+info.getUsername());
            source_username.setTypeface(Typeface.DEFAULT,Typeface.ITALIC);
            photo_count = info.getTotalPhotos();
            Picasso.get().load(info.getProfileImage().getMedium()).placeholder(R.drawable.ic_crop_original_black_24dp).fit().centerCrop().into(source_profile_picture);

            adapter.addFrag(new PhotosFragment(info.getUsername()),photo_count+" Photos");
            adapter.addFrag(new Stats(),"Stats");

            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
        }
    }
}
