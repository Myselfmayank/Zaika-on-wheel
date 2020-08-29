package com.example.zaikafoodyville;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class UserProfile extends AppCompatActivity {

    TextInputLayout fullName, email, phoneNo, password;
    TextView fullNameLabel;
    ImageView profileImage;
    //variables to be displayed on profile
    String _Name, _Phone, _Email, _Password, imageUrl;
    DatabaseReference databaseReference;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        fullName = findViewById(R.id.profName);
        email = findViewById(R.id.profEmail);
        phoneNo = findViewById(R.id.profPhone);
        password = findViewById(R.id.profPassword);
        fullNameLabel = findViewById(R.id.full_name);
        profileImage = findViewById(R.id.profile_image);

       sessionManager = new SessionManager(this);
        HashMap<String, String> userDetails = sessionManager.getUserDetailsFromSession();

        _Name = userDetails.get(SessionManager.KEY_FullName);
        _Phone = userDetails.get(SessionManager.KEY_Phone);
        _Email = userDetails.get(SessionManager.KEY_Email);
        _Password = userDetails.get(SessionManager.KEY_Password);
        imageUrl = userDetails.get(SessionManager.KEY_ImageUrl);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

      /*
       GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            fullNameLabel.setText(signInAccount.getDisplayName());
            fullName.getEditText().setText(signInAccount.getDisplayName());
            email.getEditText().setText(signInAccount.getEmail());

        }
       */


        //show all data

        showAllUserData();


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileImage.class);
                intent.putExtra("phoneNo", _Phone);
                startActivity(intent);
                finish();
            }
        });


    }

    private void showAllUserData() {
        /*
         Intent intent = getIntent();
        _Name = intent.getStringExtra("name");
        _Phone = intent.getStringExtra("phoneNo");
        _Email = intent.getStringExtra("email");
        _Password = intent.getStringExtra("password");
        imageUrl = intent.getStringExtra("imageUri");
         */


        Picasso.get().load(imageUrl).placeholder(R.drawable.profile).into(profileImage);

       /*
       //Old method to load images

       StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://zaika-on-wheel.appspot.com/").child("profileImage").child(_Phone+".jpg");
        final File file=File.createTempFile("image","jpg");
        storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                profileImage.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfile.this, "Image failed to load", Toast.LENGTH_SHORT).show();
            }
        });*/


        fullNameLabel.setText(_Name);
        fullName.getEditText().setText(_Name);
        email.getEditText().setText(_Email);
        phoneNo.getEditText().setText(_Phone);
        password.getEditText().setText(_Password);

    }

    public void update(View view) {
        if (isNameChanged() | isPasswordChanged()) {

            sessionManager.createLoginSession(_Name,_Password);
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(this, "Data is Same!!!", Toast.LENGTH_SHORT).show();

    }

    private boolean isPasswordChanged() {
        if (!_Password.equals(password.getEditText().getText().toString()) && validatePassword()) {
            databaseReference.child(_Phone).child("password").setValue(password.getEditText().getText().toString());
            _Password = password.getEditText().getText().toString();

            //update data in sharedPreferences
            //sessionManager.createLoginSessionPass(_Password);
            return true;
        } else
            return false;
    }

    private boolean isNameChanged() {
        if (!_Name.equals(fullName.getEditText().getText().toString())) {
            databaseReference.child(_Phone).child("name").setValue(fullName.getEditText().getText().toString());
            _Name = fullName.getEditText().getText().toString();
            fullNameLabel.setText(_Name);

            //update data in sharedPreferences
           // sessionManager.createLoginSession(_Name);
            return true;
        } else
            return false;
    }

    private Boolean validatePassword() {
        String val = password.getEditText().getText().toString();
        String passwordVal = "^" +
                "(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                // "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            password.setError("Password is too weak");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }
}