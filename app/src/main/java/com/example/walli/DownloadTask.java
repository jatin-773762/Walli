package com.example.walli;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

import javax.net.ssl.HttpsURLConnection;

public class DownloadTask extends AsyncTask<String,String,String> {

    private String photo_id;
    private Context context;
    private ProgressDialog pDialog;
    private double file_size= 0;
    private static final int progress_bar_type = 0;
    DownloadTask(String photo_id, Context context) {
        this.context = context;
        this.photo_id = photo_id;
    }

    private Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:

                return pDialog;
            default:
                return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Downloading file. Please wait...");
        pDialog.setIndeterminate(true);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(true);
        pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(context,"download canceled",Toast.LENGTH_SHORT).show();
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Walli/"+photo_id+".jpg");
                try{
                    dir.delete();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        int count;
        try{
            URL url = new URL("https://unsplash.com/photos/"+photo_id+"/download");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.connect();
            int length_file =connection.getContentLength();
            file_size = length_file;
            InputStream input = connection.getInputStream();
            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Walli/"+photo_id+".jpg");

            byte[] data = new byte[1024];
            long total =0;
            while((count = input.read(data))!=-1){
                if(isCancelled()){
                    return null;
                }
                total+=count;
                if(length_file>0){
                publishProgress(""+(int)((total*100)/length_file));}
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
    protected void onProgressUpdate(String... values) {
        pDialog.setProgress(Integer.parseInt(values[0]));
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setMessage("file size: "+ new DecimalFormat("##.##").format(file_size/1048576)+"MB");
    }

    @Override
    protected void onPostExecute(String s) {
        pDialog.dismiss();
    }
}
