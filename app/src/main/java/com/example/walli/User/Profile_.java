package com.example.walli.User;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.walli.R;
import com.example.walli.User.Preferences.Likes;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;

public class Profile_ extends AppCompatActivity {

    private static final int PICK_PROFILE_WALLPAPER = 123;
    private ImageButton setprofile_wallpaper,profile_wallpaper;
    private Button user_stats;
    private ImageView profile_picture;
    private TextView profile_name,profile_email;
    private Button signout;
    private GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build();
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = firebaseAuth.getCurrentUser();
    private Bitmap bm;
    private Uri wallpaperUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        setprofile_wallpaper = findViewById(R.id.setprofile_wallpaper);

        profile_wallpaper = findViewById(R.id.profile_wallpaper);

        profile_email = findViewById(R.id.profile_email);

        profile_name = findViewById(R.id.profile_name);

        profile_picture = findViewById(R.id.profile_picture);

        profile_wallpaper.setImageResource(R.drawable.ic_launcher_foreground);

        user_stats = findViewById(R.id.user_stats);

        user_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile_.this, Likes.class));
            }
        });

        googleSignInClient = GoogleSignIn.getClient(getApplicationContext(),gso);

        profile_name.setText(user.getDisplayName());
        profile_email.setText(user.getEmail());
        Picasso.get().load(user.getPhotoUrl())
                .transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        int size = Math.min(source.getWidth(), source.getHeight());
                        int x = (source.getWidth() - size) / 2;
                        int y = (source.getHeight() - size) / 2;
                        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
                        if (squaredBitmap != source) {
                            source.recycle();
                        }
                        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
                        Canvas canvas = new Canvas(bitmap);
                        Paint paint = new Paint();
                        BitmapShader shader = new BitmapShader(squaredBitmap,
                                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                        paint.setShader(shader);
                        paint.setAntiAlias(true);
                        float r = size / 2f;
                        canvas.drawCircle(r, r, r, paint);
                        squaredBitmap.recycle();
                        return bitmap;
                    }
                    @Override
                    public String key() {
                        return "circle";
                    }
                })
                .placeholder(R.drawable.ic_crop_original_black_24dp)
                .into(profile_picture);
            setprofile_wallpaper.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PICK_PROFILE_WALLPAPER);
                }
                else {
                    setProfileWallpaper();

                }
            }
        });
        signout = findViewById(R.id.signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                if(googleSignInClient!=null){
                    googleSignInClient.signOut();
                }
                finish();
            }
        });
    }

    private void setProfileWallpaper() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,PICK_PROFILE_WALLPAPER);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_PROFILE_WALLPAPER && resultCode == Activity.RESULT_OK && data!=null && data.getData()!=null){
            wallpaperUri = data.getData();
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), wallpaperUri);

                profile_wallpaper.setImageBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
