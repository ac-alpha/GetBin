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
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddBinActivity extends AppCompatActivity {

    private static final int CHOOSE_CAMERA_RESULT = 1;
    Button mCaptureImageButton, mShareLocationButton;
    ImageView mCapturedImage, mImageCaptured, mLocationShared;
    FloatingActionButton mFabDone;
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
        mShareLocationButton=(Button)findViewById(R.id.button_add_location);
        mCapturedImage=(ImageView)findViewById(R.id.imageViewCapturedImage);
        mImageCaptured=(ImageView)findViewById(R.id.image_captured);
        mLocationShared=(ImageView)findViewById(R.id.location_added);
        mFabDone=(FloatingActionButton)findViewById(R.id.fab_all_fields_done);

        mImageCaptured.setVisibility(View.INVISIBLE);
        mLocationShared.setVisibility(View.INVISIBLE);
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

                startActivity(new Intent(AddBinActivity.this, AccountActivity.class));

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==CHOOSE_CAMERA_RESULT && resultCode==RESULT_OK){
            if(file.exists()){
                mImageCaptured.setVisibility(View.VISIBLE);
                mLocationShared.setVisibility(View.VISIBLE);
                mCapturedImage.setVisibility(View.VISIBLE);
                mCapturedImage.setImageURI(tempuri);
                Toast.makeText(this,"The image was saved at "+file.getAbsolutePath(),Toast.LENGTH_LONG).show();
                StorageReference photoRef = mPhotoStorageReference.child(tempuri.getLastPathSegment());
                photoRef.putFile(tempuri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        BinInfo newBin = new BinInfo(lat,lang,downloadUrl.toString(),"true");
                        mDatabaseReference.push().setValue(newBin);
                        mFabDone.setVisibility(View.VISIBLE);
                    }
                });
            }

        }
    }
}
