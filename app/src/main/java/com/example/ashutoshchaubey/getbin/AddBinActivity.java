package com.example.ashutoshchaubey.getbin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Category;
import com.microsoft.projectoxford.vision.contract.Face;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddBinActivity extends AppCompatActivity {

    private String isVerified="Not verified";
    private static final int REQUEST_IMAGE_CAPTURE = 20;
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
    private Bitmap mBitmap;
    private VisionServiceClient client;
    ProgressDialog progressDialog;
    String imageEncoded;
    public int noOfAttempts=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        if (client==null){
            client = new VisionServiceRestClient(getString(R.string.subscription_key), getString(R.string.subscription_apiroot));
        }

        progressDialog=new ProgressDialog(AddBinActivity.this);
        progressDialog.setMessage("Analyzing...");

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        Typeface lobster = Typeface.createFromAsset(getApplication().getAssets(), "fonts/lobster.otf");
        mTitle.setTypeface(lobster);

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

//                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "IMG_" + timeStamp + ".jpg");
//                tempuri = Uri.fromFile(file);
//                i.putExtra(MediaStore.EXTRA_OUTPUT, tempuri);
//                i.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
//                startActivityForResult(i, CHOOSE_CAMERA_RESULT);

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });



        mFabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> a = new ArrayList<String>();
                ArrayList<String> b = new ArrayList<String>();
                a.add("Hello");
                b.add("Hi");
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                BinInfo newBin = new BinInfo(user.getUid(),calculateTime(),lat,lang,imageEncoded,"0","0",isVerified,a,b);
                mDatabaseReference.push().setValue(newBin);
                Intent intent = new Intent(AddBinActivity.this, AccountActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode==CHOOSE_CAMERA_RESULT && resultCode==RESULT_OK){
//            if(file.exists()){
//                mProgressBar.setVisibility(View.VISIBLE);
//                mImageCaptured.setVisibility(View.VISIBLE);
//                mCapturedImage.setVisibility(View.VISIBLE);
//                mCapturedImage.setImageURI(tempuri);
//                Toast.makeText(this,"The image was saved at "+file.getAbsolutePath(),Toast.LENGTH_LONG).show();
//                StorageReference photoRef = mPhotoStorageReference.child(tempuri.getLastPathSegment());
//                photoRef.putFile(tempuri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                        ArrayList<String> a = new ArrayList<String>();
//                        ArrayList<String> b = new ArrayList<String>();
//                        a.add("Hello");
//                        b.add("Hi");
//                        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
//                        BinInfo newBin = new BinInfo(user.getUid(),calculateTime(),lat,lang,downloadUrl.toString(),"0","0","true",a,b);
//                        mDatabaseReference.push().setValue(newBin);
//                        mFabDone.setVisibility(View.VISIBLE);
//                        mProgressBar.setVisibility(View.INVISIBLE);
//                    }
//                });
//            }

            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                mProgressBar.setVisibility(View.VISIBLE);
                mImageCaptured.setVisibility(View.VISIBLE);
                mCapturedImage.setVisibility(View.VISIBLE);
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
                mBitmap=imageBitmap;
                mCapturedImage.setImageBitmap(imageBitmap);
                imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
//                ArrayList<String> a = new ArrayList<String>();
//                ArrayList<String> b = new ArrayList<String>();
//                a.add("Hello");
//                b.add("Hi");
//                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
//                BinInfo newBin = new BinInfo(user.getUid(),calculateTime(),lat,lang,imageEncoded,"0","0","true",a,b);
//                mDatabaseReference.push().setValue(newBin);
//                mFabDone.setVisibility(View.VISIBLE);
//                mProgressBar.setVisibility(View.INVISIBLE);
                doAnalyze();
                mProgressBar.setVisibility(View.INVISIBLE);
            }

//        }
    }

    public String calculateTime() {
//        return DateFormat.getDateTimeInstance().format(new Date());
        return android.text.format.DateFormat.format("MMM dd, yyyy hh:mm:ss aaa", new java.util.Date()).toString();
//        String dt="11-01-2016 5:8 AM";
//        DateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa");
//        format.setTimeZone(TimeZone.getTimeZone("GMT"));
//        return format.toString();

    }

    public void doAnalyze() {
        mProgressBar.setVisibility(View.VISIBLE);
        try {
            new doRequest().execute();
        } catch (Exception e)
        {
            Toast.makeText(this, "Some error occurred...", Toast.LENGTH_SHORT).show();
        }
    }

    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();
        String[] features = {"Tags"};
        String[] details = {};

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        AnalysisResult v = this.client.analyzeImage(inputStream, features, details);

        String result = gson.toJson(v);
        Log.d("result", result);

        return result;
    }

    private class doRequest extends AsyncTask<String, String, String> {
        // Store error message
        private Exception e = null;

        ProgressDialog progressDialog=new ProgressDialog(AddBinActivity.this);

        public doRequest() {
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                return process();
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            progressDialog.setMessage("Finding Bin...");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            // Display based on error existence
            progressDialog.dismiss();
            if (e != null) {
                Toast.makeText(AddBinActivity.this, "Some error occurred...", Toast.LENGTH_SHORT).show();
                this.e = null;
            } else {
                Gson gson = new Gson();
                AnalysisResult result = gson.fromJson(data, AnalysisResult.class);

//                imageDesc.append("Image format: " + result.metadata.format + "\n");
//                imageDesc.append("Image width: " + result.metadata.width + ", height:" + result.metadata.height + "\n");
//                imageDesc.append("Clip Art Type: " + result.imageType.clipArtType + "\n");
//                imageDesc.append("Line Drawing Type: " + result.imageType.lineDrawingType + "\n");
//                imageDesc.append("Is Adult Content:" + result.adult.isAdultContent + "\n");
//                imageDesc.append("Adult score:" + result.adult.adultScore + "\n");
//                imageDesc.append("Is Racy Content:" + result.adult.isRacyContent + "\n");
//                imageDesc.append("Racy score:" + result.adult.racyScore + "\n\n") ;
//                imageDesc.append("Tags:"+result.tags);

//                for (Category category: result.categories) {
//                    imageDesc.append("Category: " + category.name + ", score: " + category.score + "\n");
//                }
//
//                imageDesc.append("\n");
//                int faceCount = 0;
//                for (Face face: result.faces) {
//                    faceCount++;
//                    imageDesc.append("face " + faceCount + ", gender:" + face.gender + "(score: " + face.genderScore + "), age: " + + face.age + "\n");
//                    imageDesc.append("    left: " + face.faceRectangle.left +  ",  top: " + face.faceRectangle.top + ", width: " + face.faceRectangle.width + "  height: " + face.faceRectangle.height + "\n" );
//                }
//                if (faceCount == 0) {
//                    imageDesc.append("No face is detected");
//                }
//                imageDesc.append("\n");
//
//                imageDesc.append("\nDominant Color Foreground :" + result.color.dominantColorForeground + "\n");
//                imageDesc.append("Dominant Color Background :" + result.color.dominantColorBackground + "\n");
//
//                imageDesc.append("\n--- Raw Data ---\n\n");
//                imageDesc.append(data);
                if(data.contains("bin")||data.contains("cup")||data.contains("container")||data.contains("trash")){
//                    imageDesc.append("The image contains a bin!! Thanks for your help...");
//                    Toast.makeText(AddBinActivity.this, "The image contains a bin!! Thanks for your help...", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(AddBinActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(AddBinActivity.this);
                    }
                    builder.setTitle("Congratulations!!")
                            .setMessage("We found a bin in the captured image. Thanks for your help!! Click on the double tick icon")
                            .setPositiveButton("OK", null)
                            .show();
                    mFabDone.setVisibility(View.VISIBLE);
                    isVerified="Verified";
                }else{
                    noOfAttempts++;
                    if(noOfAttempts==1){
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(AddBinActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(AddBinActivity.this);
                        }
                        builder.setTitle("Oops")
                                .setMessage("We couldn't find a bin in the captured image... Please recapture... Hold the camera close to the bin...")
                                .setPositiveButton("OK", null)
                                .show();
//                        Toast.makeText(AddBinActivity.this, "We couldn't find a bin in the captured image... Please recapture... Hold the camera close to the bin...", Toast.LENGTH_SHORT).show();
                    }else{
                        final AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(AddBinActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(AddBinActivity.this);
                        }
                        builder.setTitle("Oops!!")
                                .setMessage("We still can't find a bin in the image... If you still want to add it then press the double tick icon")
                                .setPositiveButton("OK",null)
                                .show();
                        mFabDone.setVisibility(View.VISIBLE);
//                        Toast.makeText(AddBinActivity.this, "We still can't find a bin in the image...I", Toast.LENGTH_SHORT).show();
                    }
//                    imageDesc.append("We couldn't find a bin in the captured image... Please recapture... Hold the camera close to the bin...");
                }
//                imageDesc.append("/n"+data);
            }

        }
    }

}
