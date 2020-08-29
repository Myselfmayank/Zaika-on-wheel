package com.example.zaikafoodyville;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileImage extends AppCompatActivity {

    ImageView imageViewProfile;
    TextView progressText;
    ProgressBar progressBar;
    int REQUEST_CODE=101;

    Uri imageUri;
    boolean isImageSelected=false;

    DatabaseReference databaseReference;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);

        imageViewProfile = findViewById(R.id.profile_image);
        progressText = findViewById(R.id.textViewProgress);
        progressBar = findViewById(R.id.progressBar2);

        progressText.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        databaseReference= FirebaseDatabase.getInstance().getReference("users");
        storageReference= FirebaseStorage.getInstance().getReference("profileImage");


        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE && data!=null){
           imageUri=data.getData();
           isImageSelected=true;
           imageViewProfile.setImageURI(imageUri);
        }
    }

    public void btnUpload(View view) throws IOException {
        if(isImageSelected){
            uploadImage();
        }
    }





    private void uploadImage() throws IOException {
        progressText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,35,byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        final String key = getIntent().getStringExtra("phoneNo");
        //uploading image to firebaseStorage
        storageReference.child(key+".jpg").putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
              storageReference.child(key+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                  @Override
                  public void onSuccess(final Uri uri) {
                      //Store downloadable uri in database
                      databaseReference.child(key).child("imageUri").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void aVoid) {
                              Toast.makeText(ProfileImage.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                              SessionManager sessionManager = new SessionManager(ProfileImage.this);
                              sessionManager.updateImageUrl(uri.toString());


                          }
                      });
                  }
              });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress=(taskSnapshot.getBytesTransferred()*100/taskSnapshot.getTotalByteCount());
                progressBar.setProgress((int) progress);
                progressText.setText(progress+"%");
            }
        });
    }
}