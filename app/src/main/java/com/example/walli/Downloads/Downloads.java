package com.example.walli.Downloads;
import java.io.File;
import java.io.IOException;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.walli.ImageViewer;
import com.example.walli.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Downloads extends Activity {
    private String[]        FilePathStrings;
    private File[]          listFile;
    GridView                grid;
    DownloadAdapter         adapter;
    File                    file;
    public static Bitmap    bmp = null;
    ImageView               imageview;
    FloatingActionButton setWallpaperbtn;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        setWallpaperbtn = findViewById(R.id.setwallpaper_from_download);

        setWallpaperbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageview!=null) {
                    new setWallpaper(Downloads.this).execute();
                }
                else
                    Toast.makeText(Downloads.this, "Please Select an Image", Toast.LENGTH_SHORT).show();

            }
        });

        // Check for SD Card
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(this, "Error! No SDCARD Found!",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            // Locate the image folder in your SD Card
            file = new File(Environment.getExternalStorageDirectory()
                    .getPath() + "/Walli");
        }
        if (file.isDirectory())
        {
            listFile = file.listFiles();
            FilePathStrings = new String[listFile.length];
            for (int i = 0; i < listFile.length; i++)
            {
                FilePathStrings[i] = listFile[i].getAbsolutePath();
            }
        }
        grid = (GridView)findViewById(R.id.gridview);
        new setupTask().execute();

        grid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view,
                                     int position, long id)
            {
                imageview = (ImageView)findViewById(R.id.imageView1);
                int targetWidth = 700;
                int targetHeight = 500;
                BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
                bmpOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(FilePathStrings[position],
                        bmpOptions);
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
                bmp = BitmapFactory.decodeFile(FilePathStrings[position],
                        bmpOptions);
                imageview.setImageBitmap(bmp);
                imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                bmp = null;

            }
        });
    }

    public class setupTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            adapter = new DownloadAdapter(Downloads.this, FilePathStrings);
            grid.setAdapter(adapter);
            return null;
        }
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
                bitmap = drawableToBitmap(imageview.getDrawable());
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
}
