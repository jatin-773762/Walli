package com.example.walli.Services;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.example.walli.DownloadTask;
import com.example.walli.ImageViewer;
import com.example.walli.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.NOTIFICATION_SERVICE;


public class DownloadService extends AsyncTask<Void,Integer,Void> {

    private double file_size= 0;
    private String photo_id;
    private Parcelable receiver;
    private Context context;
    private String CHANNEL_ID = "download completed";

    private NotificationManager manager;
    NotificationCompat.Builder builder;

    public DownloadService(Context context,String photo_id){
        this.context = context;
        this.photo_id = photo_id;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Walli/");
        try{
            dir.mkdir();
        }catch (Exception e){
            e.printStackTrace();
        }
        int count;
        try{
            URL url = new URL("https://unsplash.com/photos/"+photo_id+"/download");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.connect();
            int length_file =connection.getContentLength();
            file_size = length_file;
            InputStream input = connection.getInputStream();
            OutputStream output = new FileOutputStream(Environment
                    .getExternalStorageDirectory()
                    .getAbsolutePath()+"/Walli/"+
                    photo_id+".jpg");

            byte[] data = new byte[1024];
            long total =0;
            while((count = input.read(data))!=-1){
                total+=count;
                    if(length_file>0){
                        publishProgress((int)((total*100)/length_file));}
                output.write(data,0,count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_arrow_downward_black_24dp)
                .setProgress(100,0,false)
                .setContentTitle("Downloading...")
                .setAutoCancel(true)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        manager.notify(10,builder.build());

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        builder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_arrow_downward_black_24dp)
                .setContentTitle("Downloaded")
                .setOngoing(false);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(FileProvider.getUriForFile(context,context.getPackageName()+".provider",new File(Environment
                .getExternalStorageDirectory()
                .getAbsolutePath()+"/Walli/"+
                photo_id+".jpg")));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId(CHANNEL_ID);
        }
        manager.cancel(10);
        manager.notify(1,builder.build());
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        builder.setProgress(100,values[0],false);
        builder.setContentText(""+values[0]+"%");
        manager.notify(10,builder.build());
    }


}
