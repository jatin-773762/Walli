package com.example.walli.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.walli.R;
import com.example.walli.User.Email_Auth.Detail_Form;
import com.example.walli.User.Email_Auth.Login;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Auth_Activity extends AppCompatActivity {

    public static final int RESULT_OK_SIGNUP = 2;
    public static final int RESULT_OK_LOGIN = -2;
    private static final int RC_SIGN_IN = 9001;
    private Button email_auth,google_signIn;
    private GoogleSignInClient mGoogleSignInClient;
    private int REQUEST_CODE_FOR_EMAIL =101;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = firebaseAuth.getCurrentUser();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_auth);
        if(user==null && savedInstanceState ==null){
            google_signIn = findViewById(R.id.google_signIn);
            email_auth = findViewById(R.id.auth_email);
            email_auth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getApplicationContext(), Login.class),REQUEST_CODE_FOR_EMAIL);
                }
            });
            google_signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();
                    mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);

                }
            });
        }
        else if(user!=null && savedInstanceState ==null){
            startActivity(new Intent(this, Profile_.class));
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (user != null) {
            updateUI();
        }
    }

    private void updateUI() {
        startActivity(new Intent(this, Profile_.class));
        finish();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_FOR_EMAIL){
            if(resultCode == RESULT_OK_LOGIN){
                user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    updateUI();
                    Toast.makeText(getApplicationContext(), "User Logged In", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "User Not Logged in", Toast.LENGTH_SHORT).show();

            }else if(resultCode == RESULT_OK_SIGNUP){
                user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    updateUI();
                    Toast.makeText(getApplicationContext(), "User Created", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "User Not Created in", Toast.LENGTH_SHORT).show();

            }
            else if(resultCode == RESULT_CANCELED) {
                System.out.println("Done Nothing");
            }
        }

        if (requestCode == RC_SIGN_IN && resultCode==RESULT_OK) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(Auth_Activity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            updateUI();
                        }
                    }
                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
