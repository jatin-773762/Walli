package com.example.walli.User.Email_Auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.walli.R;
import com.example.walli.User.Auth_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText email,password;
    private static final int REQUEST_CODE_FOR_DETAILS = 203;
    Button login,create_ac,signup,existing_login;
    ProgressDialog progressDialog;
    private String user_email,user_passowrd;
    private Intent returnUser = new Intent();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login_btn);
        signup = findViewById(R.id.signup_btn);
        existing_login = findViewById(R.id.existing_login);
        create_ac = findViewById(R.id.create_account);
        create_ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup.setVisibility(View.VISIBLE);
                login.setVisibility(View.GONE);
                existing_login.setVisibility(View.VISIBLE);
                create_ac.setVisibility(View.GONE);
            }
        });
        existing_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup.setVisibility(View.GONE);
                login.setVisibility(View.VISIBLE);
                existing_login.setVisibility(View.GONE);
                create_ac.setVisibility(View.VISIBLE);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_email = email.getText().toString();
                user_passowrd = password.getText().toString();
                if(!user_email.isEmpty()&&!user_passowrd.isEmpty()){
                    progressDialog.setMessage("Creating Account...");
                    progressDialog.show();
                    Create_account(user_email,user_passowrd);}
                else
                {
                    if(user_passowrd.isEmpty()){
                        password.setError("Empty Field");
                    }
                    if(user_email.isEmpty()){
                        email.setError("Empty Field");
                    }
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_email = email.getText().toString();
                user_passowrd = password.getText().toString();
                if(!user_email.isEmpty()&&!user_passowrd.isEmpty()){
                    progressDialog.setMessage("Logging In...");
                    progressDialog.show();
                    Login_account(user_email,user_passowrd);}
                else
                {
                    if(user_passowrd.isEmpty()){
                        password.setError("Empty Field");
                    }
                    if(user_email.isEmpty()){
                        email.setError("Empty Field");
                    }
                }
            }
        });
    }

    private void Login_account(String user_email, String user_passowrd) {
        auth.signInWithEmailAndPassword(user_email,user_passowrd)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Authentication Successfull", Toast.LENGTH_SHORT).show();
                            Intent returnUser = new Intent();
                            setResult(Auth_Activity.RESULT_OK_LOGIN, returnUser);
                            progressDialog.dismiss();
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            Intent returnUser = new Intent();
                            setResult(Activity.RESULT_CANCELED, returnUser);
                            progressDialog.dismiss();
                        }
                    }
                })
                .addOnFailureListener(Login.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Authentication Failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_CANCELED,returnUser);
                        progressDialog.dismiss();
                    }
                });
    }

    private void Create_account(String user_email, String user_passowrd) {
        auth.createUserWithEmailAndPassword(user_email,user_passowrd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this,"Authentication Successfull",Toast.LENGTH_SHORT).show();
                            setResult(Auth_Activity.RESULT_OK_SIGNUP,returnUser);
                            progressDialog.dismiss();
                            startActivityForResult(new Intent(getApplicationContext(), Detail_Form.class),REQUEST_CODE_FOR_DETAILS);
                            finish();
                        }
                        else {
                            Toast.makeText(Login.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
                            setResult(Activity.RESULT_CANCELED,returnUser);
                            progressDialog.dismiss();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this,"Authentication Failed due to "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_CANCELED,returnUser);
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_FOR_DETAILS){
            if(resultCode == RESULT_OK){
                finish();
            }
            else if(resultCode == RESULT_CANCELED){
                System.out.println("Done Nothing");
            }
        }
    }

    //    public class LoginAsync extends AsyncTask<Void,Void,Void>{
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            progressDialog.show();
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            progressDialog.dismiss();
//            finish();
//        }
//    }
//    public class SignUpAsync extends AsyncTask<Void,Void,Void>{
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            auth.createUserWithEmailAndPassword(user_email,user_passowrd)
//                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if(task.isSuccessful()){
//                                Toast.makeText(Login.this,"Authentication Successfull",Toast.LENGTH_SHORT).show();
//                                setResult(Auth_Activity.RESULT_OK_SIGNUP,returnUser);
//
//                            }
//                            else {
//                                Toast.makeText(Login.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
//                                setResult(Activity.RESULT_CANCELED,returnUser);
//
//                            }
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(Login.this,"Authentication Failed due to "+e.getMessage(),Toast.LENGTH_SHORT).show();
//                        }
//                    });
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = new ProgressDialog(Login.this);
//            progressDialog.setIndeterminate(true);
//            progressDialog.setCancelable(false);
//            progressDialog.setMessage("Creating Account...");
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progressDialog.show();
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            progressDialog.dismiss();
//            finish();
//        }
//    }



}
