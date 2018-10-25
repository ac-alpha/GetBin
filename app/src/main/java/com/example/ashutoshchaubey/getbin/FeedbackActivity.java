package com.example.ashutoshchaubey.getbin;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedbackActivity extends AppCompatActivity {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

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

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("feedbacks");


        TextView tv1 = (TextView) findViewById(R.id.feedback_top);
        tv1.setTypeface(lobster);

        final EditText et1 = (EditText) findViewById(R.id.feedback_fb);

        Button submit = (Button) findViewById(R.id.submit_feedback);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et1.getText().toString().equals("")) {
                    Toast.makeText(FeedbackActivity.this, "This field can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    Feedback fb = new Feedback(et1.getText().toString());
                    mDatabaseReference.push().setValue(fb);
                    Toast.makeText(FeedbackActivity.this, "Thanks for your feedback!!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FeedbackActivity.this, MainActivity.class));
                }
            }
        });

    }
}
