package com.example.ashutoshchaubey.getbin;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    EditText mEmailField;
    EditText mPasswordField;
    EditText mPasswordConfirmationField;
    Button mSigninButton;
    ProgressBar mProgressBar;

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        Typeface lobster = Typeface.createFromAsset(getApplication().getAssets(), "fonts/lobster.otf");
        mTitle.setTypeface(lobster);

        mProgressBar=(ProgressBar)findViewById(R.id.progress_bar_signin);
        mEmailField=(EditText) findViewById(R.id.edit_text_email_id_sign_in);
        mPasswordField=(EditText) findViewById(R.id.edit_text_password_sign_in);
        mPasswordConfirmationField=(EditText) findViewById(R.id.edit_text_cnfrm_password_sign_in);

        mProgressBar.setVisibility(View.INVISIBLE);

        //finding the button for login
        mSigninButton=(Button) findViewById(R.id.button_sign_in);

        //reating an instance of the firebaseauth type
        mFirebaseAuth = FirebaseAuth.getInstance();

        //setting onClickListener to the login button
        mSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignIn();
            }
        });

        //creating an authStateListener
        mAuthStateListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    mProgressBar.setVisibility(View.INVISIBLE);

                    // Name, email address, and profile photo Url
                    String name = user.getDisplayName();
                    String email = user.getEmail();

                    Toast.makeText(SignInActivity.this, name+" "+email, Toast.LENGTH_SHORT).show();

                    // Check if user's email is verified
                    boolean emailVerified = user.isEmailVerified();

                    startActivity(new Intent(SignInActivity.this,AccountActivity.class));

                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                    // authenticate with your backend server, if you have one. Use
                    // FirebaseUser.getToken() instead.
                    String uid = user.getUid();
                }


            }
        };



    }


    private void startSignIn(){
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        String cnfrmPassword = mPasswordConfirmationField.getText().toString();
        if(!(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(cnfrmPassword))) {
            if(!password.equals(cnfrmPassword)){
                Toast.makeText(SignInActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }else {
                mProgressBar.setVisibility(View.VISIBLE);
                mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information

                                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        }else{
            Toast.makeText(SignInActivity.this, "TextFields are empty", Toast.LENGTH_SHORT).show();
        }
    }
}
