package com.example.zaikafoodyville;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;


public class Signup extends AppCompatActivity {

    //variables
    TextInputLayout regName,regPassword,regEmail,regPhoneNo;
    Button signInBtn,signUpBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //hooks
        signInBtn=findViewById(R.id.regSignIn);
        signUpBtn=findViewById(R.id.regSignUp);
        regName=findViewById(R.id.regName);
        regPassword=findViewById(R.id.regPassword);
        regEmail=findViewById(R.id.regEmail);
        regPhoneNo=findViewById(R.id.regPhone);


        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(Signup.this,Login.class);
                startActivity(intent);

            }
        });
    }

    private Boolean validateName() {
        String val = regName.getEditText().getText().toString();

        if (val.isEmpty()) {
            regName.setError("Field cannot be empty");
            return false;
        }
        else {
            regName.setError(null);
            regName.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validatePhoneNo() {
        String val = regPhoneNo.getEditText().getText().toString();

        if (val.isEmpty()) {
            regPhoneNo.setError("Field cannot be empty");
            return false;
        }
        else {
            regPhoneNo.setError(null);
            regPhoneNo.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = regEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            regEmail.setError("Field cannot be empty");
            return false;
        }
        else if (!val.matches(emailPattern)) {
            regEmail.setError("Invalid email address");
            return false;
        }
        else {
            regEmail.setError(null);
            regEmail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = regPassword.getEditText().getText().toString();
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
            regPassword.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            regPassword.setError("Password is too weak");
            return false;
        } else {
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }
    }



    public void signUpMethod (View view){

        if(!validateName()|!validateEmail()|!validatePassword()|!validatePhoneNo())
          return;
            //rootNode = FirebaseDatabase.getInstance();
           // reference = rootNode.getReference("users");

            //Get all data of user
            String name = regName.getEditText().getText().toString();
            String email = regEmail.getEditText().getText().toString();
            String phoneNo = regPhoneNo.getEditText().getText().toString();
            String password = regPassword.getEditText().getText().toString();


            Intent intent = new Intent(getApplicationContext(),Authentication.class);
            intent.putExtra("phoneNo",phoneNo);
            intent.putExtra("email",email);
            intent.putExtra("name",name);
            intent.putExtra("password",password);

            startActivity(intent);

            //store data in firebase
           // UserRegister userdata = new UserRegister(name, email, phoneNo, password);
            //reference.child(phoneNo).setValue(userdata);



    }

}