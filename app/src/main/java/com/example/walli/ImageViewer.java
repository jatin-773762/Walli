package com.example.walli;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.walli.Services.DownloadService;
import com.example.walli.Source_Details.BottomAdapter;
import com.example.walli.Model.Photo_Info;
import com.example.walli.Source_Details.Source_Page;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


public class ImageViewer extends AppCompatActivity {

    private static final String ACCESS_KEY = "Lh5mrr5O_QuBEKHCc-rLSQn7qq8LhXtuGQRaWLHkMTg";
    private String photo_id;
    private RequestQueue requestQueue;
    private ImageView imageView;
    private final String[] imgurl = {null};
    private static String URL = "https://api.unsplash.com";
    private ImageButton download,info,setWallpaper,likebtn;
    public static ImageView source_profile;
    private CardView cardView;
    private BottomSheetBehavior bottomSheetBehavior;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ViewPager bottomSheet;
    public static TextView source_fname,source_name,totalDownloads,totalLikes;
    private static int PERMISSION_CODE = 1,WALLPAPER_SET_ID=1;
    private boolean liked = false,downloaded = false;
    SharedPreferences sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        source_fname = findViewById(R.id.source_fname);
        source_name = findViewById(R.id.source_name);
        likebtn = findViewById(R.id.likebtn);
        totalDownloads = findViewById(R.id.totaldownloads);
        totalLikes = findViewById(R.id.totallikes);
        source_fname.setText(getIntent().getStringExtra("name"));
        source_name.setText(getIntent().getStringExtra("user_name"));
        cardView = findViewById(R.id.card);
        bottomSheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        info = findViewById(R.id.info_btn);
        source_profile = findViewById(R.id.source_profile_picture);
        photo_id = getIntent().getStringExtra("id");
        setWallpaper = findViewById(R.id.setWallpaper);
        setWallpaper.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(imageView.getDrawable()!=null) {
//                    final Dialog dialog = new Dialog(ImageViewer.this);
//                    dialog.setTitle("Choose");
//                    dialog.setContentView(R.layout.wallpaper_opinion);
//                    dialog.setCancelable(true);
//                    Button static_wallpaper = dialog.findViewById(R.id.static_wallpaper);
//                    Button scroll_wallpaper = dialog.findViewById(R.id.scrollable_wallpaper);
//                    scroll_wallpaper.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            new setWallpaper(ImageViewer.this).execute();
//                            dialog.dismiss();
//                        }
//                    });
//                    static_wallpaper.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            new setWallpaper(ImageViewer.this).execute();
//                            dialog.dismiss();
//                        }
//                    });
//                    dialog.show();
                    new setWallpaper(ImageViewer.this).execute();
                    }
                else
                    Toast.makeText(ImageViewer.this, "Please wait while the Image Loads", Toast.LENGTH_SHORT).show();
            }
        });

        download = findViewById(R.id.download_btn);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!downloaded){
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
                }
                else {
                    new DownloadService(getApplicationContext(),photo_id).execute();
                    download.setImageResource(R.drawable.ic_done_black_24dp);
                }
                Toast.makeText(ImageViewer.this, "Downloading...", Toast.LENGTH_SHORT).show();}
                else{
                    Toast.makeText(ImageViewer.this, "Already Downloaded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i){
                    case BottomSheetBehavior.STATE_EXPANDED:
                        cardView.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        cardView.setVisibility(View.VISIBLE);
                        break;
                }
            }
            @Override
            public void onSlide(@NonNull View view, float v) {
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!source_name.getText().toString().isEmpty()){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                    Source_Page source_page = new Source_Page(source_name.getText().toString());
                    BottomAdapter bottomAdapter = new BottomAdapter(getSupportFragmentManager());
                    bottomAdapter.addFrag(source_page);
                    bottomSheet.setAdapter(bottomAdapter);
                }
            }
        });
        imageView  = findViewById(R.id.walli_image);
        if(requestQueue==null){
            requestQueue = Volley.newRequestQueue(this);
        }
        if(photo_id!=null){
        LoadImageTask loadImageTask = new LoadImageTask("/photos/"+photo_id+"?client_id="+ACCESS_KEY);
        loadImageTask.execute();}

        likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user==null){
                    Toast.makeText(ImageViewer.this, "Please Login/Signup to save your favorites", Toast.LENGTH_SHORT).show();
                }
                else if(!liked){
                    likebtn.setImageResource(R.drawable.ic_favorite_black_24dp);
                    Toast.makeText(ImageViewer.this, "liked", Toast.LENGTH_SHORT).show();
                    liked = true;}
                else{
                    likebtn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    Toast.makeText(ImageViewer.this, "Unliked", Toast.LENGTH_SHORT).show();
                    liked = false;}
            }
        });
    }

    private class setWallpaper extends AsyncTask<Void,Void,Void>{

        ProgressDialog alertDialog;
        Bitmap bitmap;
        WallpaperManager manager;
        Context context;

        public setWallpaper(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            alertDialog = new ProgressDialog(context);
            alertDialog.setTitle("Setting Up Wallpaper");
            alertDialog.setCancelable(false);
            alertDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            alertDialog.setMessage("Processing...");
            alertDialog.setIcon(Spinner.MODE_DIALOG);
            alertDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                bitmap = drawableToBitmap(imageView.getDrawable());
                manager = WallpaperManager.getInstance(context);
                manager.setBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        public Bitmap drawableToBitmap (Drawable drawable) {
            Bitmap bitmap = null;

            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if(bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }

            if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            alertDialog.dismiss();
            Toast.makeText(context, "Wallpaper set!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Walli/");
                try{
                    dir.mkdir();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(ImageViewer.this, "cannot create folder", Toast.LENGTH_SHORT).show();
                }

                new DownloadService(getApplicationContext(),photo_id).execute();

            } else
                Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show();
        } else {
            throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }

    private class LoadImageTask extends AsyncTask<String,String,String> {
        String query;
        public LoadImageTask(String query) {
            this.query = query;
        }
        @Override
        protected String doInBackground(String... strings) {
            StringRequest request = new StringRequest(
                    URL+query,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            GsonBuilder builder =new GsonBuilder();
                            Gson gson = builder.create();
                            Photo_Info info = gson.fromJson(response,Photo_Info.class);
                            String  downloads ,likes;
                            long count = info.getDownloads();
                            long countlikes = info.getLikes();
                            if(count<1000){
                                downloads = ""+count;
                                likes = ""+countlikes;
                            }
                            else{
                                int exp = (int) (Math.log(count)/Math.log(1000));
                                downloads = String.format("%.1f%c",count/Math.pow(1000,exp),"kM".charAt(exp-1));
                                likes = String.format("%.1f%c",countlikes/Math.pow(1000,exp),"kM".charAt(exp-1));
                            }
                            totalDownloads.setText(downloads);
                            totalLikes.setText(likes);
                            Picasso.get().load(info.getUserInfo().getProfileImage().getMedium()).fit().centerCrop().into(source_profile);
                            imgurl[0] = info.getUrls().getRegular();
                            Picasso.get().load(imgurl[0]).placeholder(R.drawable.ic_crop_original_black_24dp).fit().centerInside().into(imageView);
                        }
                        },
                    new Response.ErrorListener() {
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
        sh = getSharedPreferences("photostats",MODE_PRIVATE);
        if(user!=null)
            liked = sh.getBoolean(user+"/"+photo_id+"/liked",false);
//        downloaded = sh.getBoolean(photo_id+"/downloaded",false);
        if(liked)
            likebtn.setImageResource(R.drawable.ic_favorite_black_24dp);
        File file = new File(Environment
                .getExternalStorageDirectory().getAbsolutePath()+"/walli/"+photo_id+".jpg");
        if(file.exists()){
                downloaded = true;
                download.setImageResource(R.drawable.ic_done_black_24dp);
            }

    }

    @Override
    protected void onPause() {
        super.onPause();
        sh = getSharedPreferences("photostats",MODE_PRIVATE);

        Set<String> existing_likes = sh.getStringSet(user+"/id",null);

        Set<String> photo_id_liked = new HashSet<>();
        if (existing_likes != null) {
            photo_id_liked =existing_likes;
        }

        if(liked){
            photo_id_liked.add(photo_id);
        }
        else {
            if(photo_id_liked.contains(photo_id)){
                photo_id_liked.remove(photo_id);
            }
        }
        SharedPreferences.Editor edit = sh.edit();
        if(user!=null) {
            edit.putString(user+"/"+photo_id + "/photo_id", photo_id);
            edit.putStringSet(user+"/id", photo_id_liked);
            edit.putString(user+"/"+photo_id + "/username", source_name.getText().toString());
            edit.putBoolean(user+"/"+photo_id + "/liked", liked);
        }
//        edit.putBoolean(photo_id + "/downloaded", downloaded);
        edit.apply();

    }
}
