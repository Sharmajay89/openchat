package com.service.openchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.service.openchat.activity.ChatActivity;
/*
This is use to registration and login in openchat application
 created by avishvakarma
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignup ,btnSignIn, btnResetPassword;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonSignup = (Button) findViewById(R.id.buttonSignup);
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        /*btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });*/

      /*  btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);
       // btnResetPassword.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
    }

    private void registerUser(){
        boolean isValid = true;
        //getting email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        //checking if email and passwords are empty
        boolean isValidEmailPattern = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if(TextUtils.isEmpty(email) || !isValidEmailPattern){
            Toast.makeText(this,"Please valid enter email",Toast.LENGTH_LONG).show();
            isValid = false;
        }else if(TextUtils.isEmpty(password) || password.length()<8){
            Toast.makeText(this,"Please enter valid password with minimum 8 characters.",Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if(isValid){
            //if the email and password are not empty
            //displaying a progress dialog

            progressDialog.setMessage("Registering Please Wait...");
            progressDialog.show();


            //creating a new user
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            //checking if success
                            if(task.isSuccessful()){
                                //display some message here
                                Toast.makeText(LoginActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();
                                signInUser();
                            }else{
                                //display some message here
                                Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
 private void signInUser(){

     boolean isValid = true;
     //getting email and password from edit texts
     String email = editTextEmail.getText().toString().trim();
     String password  = editTextPassword.getText().toString().trim();

     //checking if email and passwords are empty
     boolean isValidEmailPattern = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
     if(TextUtils.isEmpty(email) || !isValidEmailPattern){
         Toast.makeText(this,"Please valid enter email",Toast.LENGTH_LONG).show();
         isValid = false;
     }else if(TextUtils.isEmpty(password) || password.length()<8){
         Toast.makeText(this,"Please enter valid password with minimum 8 characters.",Toast.LENGTH_LONG).show();
         isValid = false;
     }

     if(isValid) {
         //if the email and password are not empty
         //displaying a progress dialog

         progressDialog.setMessage("Login Please Wait...");
         progressDialog.show();

         //creating a new user
         firebaseAuth.signInWithEmailAndPassword(email, password)
                 .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         //checking if success
                         if (task.isSuccessful()) {
                             //display some message here
                             Toast.makeText(LoginActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                             startActivity(new Intent(LoginActivity.this, ChatActivity.class));
                             finish();
                         } else {

                             //display some message here
                             Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                         }
                         progressDialog.dismiss();
                     }
                 });
     }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonSignup:
                //calling register method on click
                registerUser();
                break;
             case R.id.sign_in_button:
                 signInUser();
                break;
            /* case R.id.btn_reset_password:
                break;*/
        }

    }

}
