package com.example.walli.User.Email_Auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.walli.Helper.GenderDialog;
import com.example.walli.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Detail_Form extends AppCompatActivity{
    private static final String TAG = "User_Details";
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText user_name,user_phone;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private UserProfileChangeRequest profileUpdates;
    private ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    private StorageReference storageReference;
    private FirebaseFirestore userDb;
    private Uri mImageUri;
    private byte[] image_data;
    private ImageButton update,profile_picture;
    private String profileUrl;
    private StorageReference storageReference1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_form);
        progressBar = findViewById(R.id.progress_update_details);
        firebaseAuth = FirebaseAuth.getInstance();
        userDb = FirebaseFirestore.getInstance();
        update = findViewById(R.id.update_details);
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile");
        storageReference1 = storageReference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Profile" + ".jpg");
        profile_picture = findViewById(R.id.profile_image);
        user_name = findViewById(R.id.user_name);
        user_phone = findViewById(R.id.user_phone);


        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user_name.getText().toString().isEmpty() && !user_phone.getText().toString().isEmpty())
                    updateData();
                else{
                    if(user_phone.getText().toString().isEmpty())
                        user_phone.setError("Empty");
                    if(user_name.getText().toString().isEmpty())
                        user_name.setError("Empty");
                }
            }

        });
    }

    private void updateData() {
        progressBar.setVisibility(View.VISIBLE);
        update.setVisibility(View.GONE);
        user_phone.setEnabled(false);
        user_name.setEnabled(false);

        if (image_data == null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Drawable drawable = getResources().getDrawable(R.drawable.logo);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
                profile_picture.setImageBitmap(bitmap);
                image_data = bytes.toByteArray();
            }
            storageReference1.putBytes(image_data != null ? image_data : new byte[0]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            profileUrl = uri.toString();
                            profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(user_name.getText().toString())
                                    .setPhotoUri(uri)
                                    .build();
                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    new uploadDataOnCloud().execute();
                                }
                            });
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to Upload Picture", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    profileUrl = uri.toString();
                    profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(user_name.getText().toString())
                            .setPhotoUri(uri)
                            .build();
                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            new uploadDataOnCloud().execute();
                        }
                    });
                }
            });

        }


    }

    private void openFileChooser() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&  data!=null && data.getData()!=null) {
            mImageUri = data.getData();
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                Bitmap crpImg = Bitmap
                        .createBitmap(bm,
                                bm.getHeight()< bm.getWidth()?(int)(0.5*(bm.getWidth()- bm.getHeight())):0,
                                bm.getHeight()> bm.getWidth()?(int)(0.5*(bm.getHeight()- bm.getWidth())):0,
                                Math.min(bm.getWidth(), bm.getHeight()),
                                Math.min(bm.getWidth(), bm.getHeight()));
                Bitmap compressedImg = crpImg;
                compressedImg.compress(Bitmap.CompressFormat.JPEG,20,bytes);
                profile_picture.setImageBitmap(compressedImg);
                image_data = bytes.toByteArray();
                new uploadProfilePicOnCloud(image_data).execute();
                super.onActivityResult(requestCode, resultCode, data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class uploadDataOnCloud extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            DocumentReference documentReference = userDb.collection("Users").document(firebaseAuth.getCurrentUser().getUid());
            final Map<String, String> user = new HashMap<>();
            user.put("Name", user_name.getText().toString());
            user.put("Phone", user_phone.getText().toString());
            user.put("ProfilePicture", profileUrl);
            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess user Profile Updated");
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                    update.setVisibility(View.VISIBLE);
                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure" + e.toString());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
        }
    }

    private class uploadProfilePicOnCloud extends AsyncTask<Void,Void,Void> {

        private ProgressDialog progressDialog;
        private byte[] image_data;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(Detail_Form.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }
        public uploadProfilePicOnCloud(byte[] image_data) {
            this.image_data = image_data;
        }
        @Override
        protected Void doInBackground(Void... voids) {

            storageReference1.putBytes(image_data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to Upload Picture", Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        if(user_name.getText().toString().isEmpty())
            user_name.setText("Name");
        if(user_phone.getText().toString().isEmpty())
            user_phone.setText("123");
        updateData();
    }
}
