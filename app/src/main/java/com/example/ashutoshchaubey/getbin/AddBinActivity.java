package com.example.ashutoshchaubey.getbin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddBinActivity extends AppCompatActivity {

    private static final int CHOOSE_CAMERA_RESULT = 1;
    Button mCaptureImageButton;
    ImageView mCapturedImage, mImageCaptured;
    FloatingActionButton mFabDone;
    ProgressBar mProgressBar;
    File file;
    Uri tempuri;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseStorage mFirebaseStorage;
    StorageReference mPhotoStorageReference;
    String lat,lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bin);
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mFirebaseStorage=FirebaseStorage.getInstance();

        mDatabaseReference=mFirebaseDatabase.getReference().child("bins");
        mPhotoStorageReference=mFirebaseStorage.getReference().child("bin_photos");

        mCaptureImageButton=(Button)findViewById(R.id.button_capture);
        mCapturedImage=(ImageView)findViewById(R.id.imageViewCapturedImage);
        mImageCaptured=(ImageView)findViewById(R.id.image_captured);
        mFabDone=(FloatingActionButton)findViewById(R.id.fab_all_fields_done);
        mProgressBar=(ProgressBar)findViewById(R.id.progres_bar_add_bin);

        mImageCaptured.setVisibility(View.INVISIBLE);
        mCapturedImage.setVisibility(View.INVISIBLE);
        mFabDone.setVisibility(View.GONE);

        lat=getIntent().getStringExtra("lat");
        lang=getIntent().getStringExtra("long");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("MainActivity","Permission is granted");
            } else {

                Log.v("MainActivity","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }else { //permission is automatically granted on sdk<23 upon installation
            Log.v("MainActivity","Permission is granted");
        }

        mCaptureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "IMG_" + timeStamp + ".jpg");
                tempuri = Uri.fromFile(file);
                i.putExtra(MediaStore.EXTRA_OUTPUT, tempuri);
                i.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
                startActivityForResult(i, CHOOSE_CAMERA_RESULT);

            }
        });



        mFabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddBinActivity.this, AccountActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==CHOOSE_CAMERA_RESULT && resultCode==RESULT_OK){
            if(file.exists()){
                mProgressBar.setVisibility(View.VISIBLE);
                mImageCaptured.setVisibility(View.VISIBLE);
                mCapturedImage.setVisibility(View.VISIBLE);
                mCapturedImage.setImageURI(tempuri);
                Toast.makeText(this,"The image was saved at "+file.getAbsolutePath(),Toast.LENGTH_LONG).show();
                StorageReference photoRef = mPhotoStorageReference.child(tempuri.getLastPathSegment());
                photoRef.putFile(tempuri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        ArrayList<String> a = new ArrayList<String>();
                        ArrayList<String> b = new ArrayList<String>();
                        a.add("Hello");
                        b.add("Hi");
                        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                        BinInfo newBin = new BinInfo(user.getUid(),calculateTime(),lat,lang,downloadUrl.toString(),"0","0","true",a,b);
                        mDatabaseReference.push().setValue(newBin);
                        mFabDone.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }

        }
    }

    public String calculateTime() {
//        return DateFormat.getDateTimeInstance().format(new Date());
        return android.text.format.DateFormat.format("MMM dd, yyyy hh:mm:ss aaa", new java.util.Date()).toString();
//        String dt="11-01-2016 5:8 AM";
//        DateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa");
//        format.setTimeZone(TimeZone.getTimeZone("GMT"));
//        return format.toString();

    }
}
