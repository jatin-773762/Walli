package com.example.walli.Search.ImageSearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.walli.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ImageSearch extends AppCompatActivity {

    private CameraView cameraView;
    private Button button_detect;
    private AlertDialog waitingDialog;
    private FirebaseVisionImageLabeler detector;
    private ArrayList<String> query_arrayList;
    private ArrayList<Float> confidence_arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);
        query_arrayList = new ArrayList<>();
        confidence_arrayList = new ArrayList<>();
        cameraView = findViewById(R.id.camera_view);
        button_detect = findViewById(R.id.detect_btn);
        waitingDialog = new SpotsDialog.Builder().setMessage("Please wait...").setContext(this).setCancelable(false).build();
        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                waitingDialog.show();
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap= Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
                cameraView.stop();

                runDetector(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
        button_detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.start();
                cameraView.captureImage();
            }
        });
    }
    private void runDetector(Bitmap bitmap) {
        final FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        detector = FirebaseVision.getInstance().getOnDeviceImageLabeler();
        detector.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                        processDataResult(labels);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("ERROR BIG ERROR :" + e.getMessage());
                    }
                });
    }

    private void processDataResult(List<FirebaseVisionImageLabel> labels) {
        for (FirebaseVisionImageLabel label : labels) {
            confidence_arrayList.add(label.getConfidence());
            query_arrayList.add(label.getText());
        }

        if (waitingDialog.isShowing())
            waitingDialog.dismiss();
        if(query_arrayList!=null){
            Intent intent = new Intent();
            intent.putExtra("query_list",query_arrayList);
            intent.putExtra("confidence_list",confidence_arrayList);
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
        else{
            Toast.makeText(ImageSearch.this, "Nothing Found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
