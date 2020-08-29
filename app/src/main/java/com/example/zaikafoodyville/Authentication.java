package com.example.zaikafoodyville;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class Authentication extends AppCompatActivity {

    FirebaseDatabase rootNode;
     DatabaseReference reference;

    String verificationCodeSentByFirebase;
    Button verifyBtn;
    EditText otpEnteredByUser;
    TextView No;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        verifyBtn = findViewById(R.id.verify_btn);
        No=findViewById(R.id.pppp);
        otpEnteredByUser = findViewById(R.id.verification_code_entered_by_user);
        progressBar = findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.GONE);
        Intent intent = getIntent();
        String phoneNo = intent.getStringExtra("phoneNo");
        No.setText("Otp sent to "+phoneNo);

        sendVerificationCode(phoneNo);
    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        //Authenticate manually
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationCodeSentByFirebase =s;

        }
        //Authenticate automatically
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null)

                verifyCode(code);

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(Authentication.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String CodeByUser){
        PhoneAuthCredential credential =PhoneAuthProvider.getCredential(verificationCodeSentByFirebase,CodeByUser);

        signUpTheUserByCredential(credential);
    }

    private void signUpTheUserByCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(Authentication.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //get instance of firebase database
                            rootNode = FirebaseDatabase.getInstance();
                             reference = rootNode.getReference("users");

                            String name = getIntent().getStringExtra("name");
                            String email = getIntent().getStringExtra("email");
                            String phoneNo = getIntent().getStringExtra("phoneNo");
                            String password = getIntent().getStringExtra("password");


                            //Store data in firebase
                            UserRegister userdata = new UserRegister(name, email, phoneNo, password);
                            reference.child(phoneNo).setValue(userdata);

                             Intent intent = new Intent(getApplicationContext(),Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                             startActivity(intent);
                            Toast.makeText(Authentication.this, "success", Toast.LENGTH_SHORT).show();


                        }
                        else {
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(Authentication.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    public void VerifyOtp(View view){
        String code = otpEnteredByUser.getText().toString();

        if(code.isEmpty()||code.length()<6){
            otpEnteredByUser.setError("Incorrect OTP");
            otpEnteredByUser.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        verifyCode(code);
    }

}