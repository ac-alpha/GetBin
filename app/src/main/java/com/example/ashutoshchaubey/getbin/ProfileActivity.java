package com.example.ashutoshchaubey.getbin;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileActivity extends AppCompatActivity {

    TextView mProvider, mUid, mEmail;
    TextView mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProvider=(TextView)findViewById(R.id.provider);
        mUid=(TextView)findViewById(R.id.uid);
        mName=(TextView)findViewById(R.id.userName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();
                mProvider.setText(providerId);
                // UID specific to the provider
                String uid = profile.getUid();
                mUid.setText(uid);
                // Name, email address, and profile photo Url
                String name = profile.getDisplayName();
                if(name!=null) {
                    if (name.equals("")) {
                        mName.setText("Null");
                    } else {
                        mName.setText(name);
                    }
                }else{
                    mName.setText("Null");
                }

            };
        }


    }
}
