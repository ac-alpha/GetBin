package com.example.ashutoshchaubey.getbin;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class ProfileActivity extends AppCompatActivity {

    TextView mProvider, mUid, mEmail;
    TextView mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        Typeface lobster = Typeface.createFromAsset(getApplication().getAssets(), "fonts/lobster.otf");
        mTitle.setTypeface(lobster);
        TextView patv1 = (TextView) findViewById(R.id.patv1);
        patv1.setTypeface(lobster);

        mProvider = (TextView) findViewById(R.id.provider);
        mUid = (TextView) findViewById(R.id.uid);
        mName = (TextView) findViewById(R.id.userName);

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
                if (name != null) {
                    if (name.equals("")) {
                        mName.setText("Null");
                    } else {
                        mName.setText(name);
                    }
                } else {
                    mName.setText("Null");
                }

            }
            ;
        }


    }
}
