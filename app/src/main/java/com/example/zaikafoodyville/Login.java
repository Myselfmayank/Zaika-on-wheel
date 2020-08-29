package com.example.zaikafoodyville;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    Button signup_btn,signIn_btn,forgetPass;
    ImageView image;
    ProgressBar progressBar;
    TextView logo,slogan;
    TextInputLayout number,password;

    @Override
    protected void onStart() {
        super.onStart();
     SessionManager manager = new SessionManager(this);

     if(manager.checkLogin()){
         startActivity(new Intent(Login.this,Dashboard.class));
         finish();
     }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        //hooks
        signup_btn=findViewById(R.id.signup);
        image=findViewById(R.id.logoImage);
        logo=findViewById(R.id.logo);
        slogan=findViewById(R.id.slogan);
        number=findViewById(R.id.phoneNo);
        password=findViewById(R.id.password);
        signIn_btn=findViewById(R.id.signin);
        forgetPass=findViewById(R.id.forget);
        progressBar=findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);



        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(Login.this,Signup.class);
                Pair[] pairs=new Pair[7];
                pairs[0]=new Pair<View,String>(image,"logoImage");
                pairs[1]=new Pair<View,String>(signup_btn,"signupTran");
                pairs[2]=new Pair<View,String>(logo,"logoText");
                pairs[3]=new Pair<View,String>(slogan,"logoDesc");
                pairs[4]=new Pair<View,String>(number,"userTran");
                pairs[5]=new Pair<View,String>(password,"passTran");
                pairs[6]=new Pair<View,String>(signIn_btn,"btnTran");

                ActivityOptions options = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
                    startActivity(intent, options.toBundle());
                }

            }
        });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Login.this, "This is not available yet,contact developer to get your password", Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(Intent.ACTION_SENDTO);
                intent1.setData(Uri.parse("mailto:"));
                intent1.putExtra(Intent.EXTRA_EMAIL,new String[]{"maayankraj@gmail.com"});
                intent1.putExtra(Intent.EXTRA_SUBJECT,"To get forgotten Password");
                startActivity(intent1);
            }
        });

    }

    private Boolean validatePhoneNo() {
        String val = number.getEditText().getText().toString();

        if (val.isEmpty()) {
            number.setError("Field cannot be empty");
            progressBar.setVisibility(View.GONE);
            return false;
        }
        else {
            number.setError(null);
            number.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = password.getEditText().getText().toString();

        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            progressBar.setVisibility(View.GONE);
            return false;

        }
        else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    public void SignIn(View view){

        /*
          if(!isConnected(this)){
            showCustomDialog();
        }
         */
        //validate Login Info
        progressBar.setVisibility(View.VISIBLE);
        if(!validatePhoneNo()|!validatePassword()){
            return;
        }
        else {
            isUser();
        }
    }



    /*
    Check internet connection


     private boolean isConnected(Login login) {
        ConnectivityManager connectivityManager = (ConnectivityManager) login.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifiConn!=null&&wifiConn.isConnected())||(mobileConn!=null&&mobileConn.isConnected())){
            return true;
        }
        else
            return false;
    }
     */


    private void isUser() {

        final String enteredPhone=number.getEditText().getText().toString().trim();
        final String enteredPassword=password.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Query checkUser = reference.orderByChild("phoneNo").equalTo(enteredPhone);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    //removes the existing error details if any
                    number.setError(null);
                    number.setErrorEnabled(false);

                    String passwordFromDB = dataSnapshot.child(enteredPhone).child("password").getValue(String.class);

                    if(passwordFromDB.equals(enteredPassword)){

                        //removes the existing error details if any
                        password.setError(null);
                        password.setErrorEnabled(false);

                        String nameFromDB = dataSnapshot.child(enteredPhone).child("name").getValue(String.class);
                        String emailFromDB = dataSnapshot.child(enteredPhone).child("email").getValue(String.class);
                        String phoneNoFromDB = dataSnapshot.child(enteredPhone).child("phoneNo").getValue(String.class);
                        String imageUri = dataSnapshot.child(enteredPhone).child("imageUri").getValue(String.class);

                        //Creation Of session or sharedPreferences
                        SessionManager sessionManager= new SessionManager(Login.this);
                        sessionManager.createLoginSession(nameFromDB,emailFromDB,phoneNoFromDB,passwordFromDB,imageUri);

                        //Start Profile Activity
                      Intent intent = new Intent(getApplicationContext(),Dashboard.class);
                        /*
                        intent.putExtra("name",nameFromDB);
                        intent.putExtra("email",emailFromDB);
                        intent.putExtra("phoneNo",phoneNoFromDB);
                        intent.putExtra("password",passwordFromDB);
                        intent.putExtra("imageUri",imageUri);
                         */

                        Toast.makeText(Login.this,"Successful",Toast.LENGTH_SHORT).show();

                        startActivity(intent);
                        finish();

                    }
                    else {
                        password.setError("Incorrect Password");
                        password.requestFocus();
                        progressBar.setVisibility(View.GONE);
                    }
                }
                else{
                    number.setError("No such user exist");
                    number.requestFocus();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}