package com.example.ashutoshchaubey.getbin;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JunkActivity extends AppCompatActivity {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_junk);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        TextView tv = (TextView) findViewById(R.id.textView);

        Typeface lobster = Typeface.createFromAsset(getApplication().getAssets(), "fonts/lobster.otf");
        tv.setTypeface(lobster);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("feedbacks");

        final EditText feedbackR = (EditText) findViewById(R.id.feedback_rated);

        Button skip = (Button) findViewById(R.id.skip_rated);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(JunkActivity.this, AccountActivity.class);
                startActivity(homeIntent);
                finish();
            }
        });

        Button submitFeedback = (Button) findViewById(R.id.submit_rated);
        submitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (feedbackR.getText().toString().equals("")) {
                    Toast.makeText(JunkActivity.this, "Please enter some text or press skip", Toast.LENGTH_SHORT).show();
                } else {
                    Feedback fb = new Feedback(feedbackR.getText().toString());
                    mDatabaseReference.push().setValue(fb);
                    startActivity(new Intent(JunkActivity.this, MainActivity.class));
                }
            }
        });

    }
}
