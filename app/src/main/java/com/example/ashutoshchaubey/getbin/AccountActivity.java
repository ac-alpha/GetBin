package com.example.ashutoshchaubey.getbin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccountActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleMap.OnPolylineClickListener,OnMapReadyCallback,GoogleMap.OnMarkerClickListener ,SeekBar.OnSeekBarChangeListener {

    public String TAG ="abc";
    private GoogleMap mMap;
    public PolylineOptions lineOptions;
    public Polyline polyline=null;
    public Circle mCircle;
    public TextView rad;
    public int h=0;
    public AccountActivity obj=this;
    public static Marker m=null,M=null;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private static final int DEFAULT_ZOOM = 18;
    public SeekBar seekBar;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(29.8543, 77.8880);
    public LatLng origin;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static List<BinInfo> binsList = new ArrayList<>();
    public Bundle save;
    FirebaseAuth mFirebaseAuth;
    ChildEventListener mChildEventListener;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase mFirebaseDatabase;
    public static Handler ha;
    public static Runnable runnable;
    public int i=0;

    @Override
    protected void onResume() {

        super.onResume();
        attachDatabaseReadListener();
        if(i!=0){
        Intent intent1 = getIntent();
        finish();
        startActivity(intent1);}++i;
        Log.i("abc","222222222222   "+i);
        if(mMap!=null){
        mMap.clear();
        rad=(TextView)findViewById(R.id.rad);
        mMap.setOnMarkerClickListener(this);
        addmarker();
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();}
    }
    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
//        binsList.clear();
        Log.i("abc","111111111111");
        if(ha!=null)
        ha.removeCallbacks(AccountActivity.runnable);

    }

    public void ongps()
    {
        LocationManager locationManager ;
        boolean GpsStatus ;
        locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!GpsStatus){
            Toast.makeText(AccountActivity.this, "switch on location services to proceed", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent1);

        }
        if(!GpsStatus){
            System.exit(1);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        save=savedInstanceState;
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_account);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            seekBar = (SeekBar) findViewById(R.id.radius);
            seekBar.setOnSeekBarChangeListener(this);
            ongps();

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ha=new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                //call function
                addmarker();
                if(mMap!=null)
                    getDeviceLocation();
                ha.postDelayed(this, 2000);
                if(M!=null){
                    origin = new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude());
                    LatLng dest =M.getPosition();
                    M.showInfoWindow();
                    // Getting URL to the Google Directions API
                    String url =getUrl(origin,dest);
                    Log.d("onMapClick", url.toString());
                    FetchUrl FetchUrl = new FetchUrl();
                    // Start downloading json data from Google Directions API
                    FetchUrl.execute(url);
                    if(distance(origin.latitude, origin.longitude, dest.latitude, dest.longitude)<10 && M.getTag()!=null )
                    {
//                        flag++;
//                        prevBin=(BinInfo)M.getTag();
                        BinInfo bin=(BinInfo) M.getTag();
                        Intent i=new Intent(AccountActivity.this,RateBinActivity.class);
                        i.putExtra("Bin",bin);
                        startActivity(i);

                    }
                }
            }
        };
        ha.postDelayed(runnable, 2000);

        binsList = new ArrayList<>();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference().child("bins");

        final FloatingActionButton fabOptions = (FloatingActionButton) findViewById(R.id.fab_options);
        FloatingActionButton fabSignOut = (FloatingActionButton) findViewById(R.id.fab_sign_out);
        FloatingActionButton fabAddBins = (FloatingActionButton) findViewById(R.id.fab_add_bin);
        FloatingActionButton fabRecenter = (FloatingActionButton) findViewById(R.id.fab_recenter);


        final Animation mShowButton = AnimationUtils.loadAnimation(AccountActivity.this,R.anim.show_button);
        final Animation mHideButton = AnimationUtils.loadAnimation(AccountActivity.this,R.anim.hide_button);
        final Animation mShowLayout = AnimationUtils.loadAnimation(AccountActivity.this,R.anim.show_layout);
        final Animation mHideLayout = AnimationUtils.loadAnimation(AccountActivity.this,R.anim.hide_layout);
        final LinearLayout signOutParent = (LinearLayout)findViewById(R.id.sign_out_parent);
        final LinearLayout getBinsParent = (LinearLayout)findViewById(R.id.add_bin_parent);
        final LinearLayout recenterParent = (LinearLayout)findViewById(R.id.recenter_parent);
        fabOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(signOutParent.getVisibility()==View.VISIBLE && getBinsParent.getVisibility()==View.VISIBLE){
                    signOutParent.setVisibility(View.GONE);
                    getBinsParent.setVisibility(View.GONE);
                    recenterParent.setVisibility(View.GONE);
                    recenterParent.startAnimation(mHideLayout);
                    signOutParent.startAnimation(mHideLayout);
                    getBinsParent.startAnimation(mHideLayout);
                    fabOptions.setImageResource(R.drawable.ic_list);
                    fabOptions.startAnimation(mHideButton);
                }else{
                    signOutParent.setVisibility(View.VISIBLE);
                    getBinsParent.setVisibility(View.VISIBLE);
                    recenterParent.setVisibility(View.VISIBLE);
                    signOutParent.startAnimation(mShowLayout);
                    getBinsParent.startAnimation(mShowLayout);
                    recenterParent.startAnimation(mShowLayout);
                    fabOptions.startAnimation(mShowButton);
                    fabOptions.setImageResource(R.drawable.ic_cancel);
                }
            }
        });

        fabRecenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recenter();
            }
        });

        fabSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        fabAddBins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TO-DO
//                if(binsList.size()>0){
//                    Toast.makeText(AccountActivity.this, "Hurray", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(AccountActivity.this, "Mar gayii apnii toh", Toast.LENGTH_SHORT).show();
//                }
                Log.i("AccountActivity",binsList.size()+" FOCUS AT THIS");
                Intent i=new Intent(AccountActivity.this, AddBinActivity.class);
                i.putExtra("lat",Double.toString(mLastKnownLocation.getLatitude()));
                i.putExtra("long",Double.toString(mLastKnownLocation.getLongitude()));
                startActivity(i);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    protected void onStart() {
        ongps();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            finish();
        } else {
            super.onBackPressed();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            finish();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.account, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//
//            signOut();
//
//            return true;
//        } else if (id == R.id.action_verify_mail) {
//
//
//            final FirebaseUser user = mFirebaseAuth.getCurrentUser();
//            if(!user.isEmailVerified()){
//            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(AccountActivity.this, "Email sent to "+user.getEmail().toString(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });}else{
//                Toast.makeText(AccountActivity.this, "Email already verified", Toast.LENGTH_SHORT).show();
//            }
//
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            startActivity(new Intent(AccountActivity.this, ProfileActivity.class));
            finish();
        } else if (id == R.id.nav_signout) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut(){
        mFirebaseAuth.signOut();
        LoginManager.getInstance().logOut();
        if(mFirebaseAuth.getCurrentUser()!=null) {
            Toast.makeText(this, "User" + mFirebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
        }
        startActivity(new Intent(AccountActivity.this,MainActivity.class));
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if(mCircle!=null)
        {
            mCircle.remove();
        }
        makecircle();
    }

    public void makecircle()
    {
        if(mCircle!=null)
            mCircle.remove();
        LatLng origin = new LatLng(mLastKnownLocation.getLatitude(),
                mLastKnownLocation.getLongitude());
        double radiusInMeters = seekBar.getProgress()+100;
        //outline
        rad.setText((""+(int)(radiusInMeters)));
        int strokeColor = 0x7f18D300;
        try {
            CircleOptions circleOptions = new CircleOptions().center(origin).radius(radiusInMeters).fillColor(Color.argb(0x2f,0x8C,0xEB,0x50)).strokeColor(strokeColor).strokeWidth(2);
            mCircle = mMap.addCircle(circleOptions);
        }
        catch (Exception e){}
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(mCircle!=null)
        {
            mCircle.remove();
        }
        makecircle();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        M=marker;

        origin = new LatLng(mLastKnownLocation.getLatitude(),
                mLastKnownLocation.getLongitude());
        LatLng dest = marker.getPosition();
        M.showInfoWindow();
        return false;
    }

    public void addmarker()
    {
        // Add a marker in roorkee
        // and move the map's camera to the same location.
        setcurrentmarker();
        double lat,lang,dist;
        Log.i("AccountActivity","sjhfasvgfhvasfjhbasjhb////////////");
        for(int i=0;i<binsList.size();i++){
            lat=Double.parseDouble(binsList.get(i).getLatitude());
            lang=Double.parseDouble(binsList.get(i).getLongitude());
            Log.i("Daaaaaaataaaaa",lat+"     "+lang);
            Location targetLocation = new Location("");//provider name is unnecessary
            targetLocation.setLatitude(lat);//your coords of course
            targetLocation.setLongitude(lang);
            dist= mLastKnownLocation.distanceTo(targetLocation);
            Log.i("AccountActivity",dist+"    "+rad.getText().toString());
            if(dist<Double.parseDouble(rad.getText().toString())){
                Log.i("AccountActivity",dist+"    "+rad.getText().toString());
                LatLng loc = new LatLng(lat,lang);
                Marker y=mMap.addMarker(new MarkerOptions().position(loc)
                        .title("upvote : "+binsList.get(i).getUpVotes()+" downvote : "+binsList.get(i).getDownVotes()));
                y.setTag(binsList.get(i));
            }
        }
    }

    public void setcurrentmarker()
    {
        if(m!=null)
            m.remove();
        try {
            m=mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                    .title("My Position").icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.mypos))));
        }
        catch(Exception e){}
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        LatLng a=M.getPosition();
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+a.latitude+","+a.longitude+"&mode=w");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        rad=(TextView)findViewById(R.id.rad);
        mMap.setOnMarkerClickListener(this);
        addmarker();
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.setMyLocationEnabled(false);
                            if(mCircle==null){
                                try{
                                origin =new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude());}catch (Exception e){ongps();}
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        origin, DEFAULT_ZOOM));}
                            makecircle();
                            setcurrentmarker();
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute (String result)   {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        public class DataParser {

            /**
             * Receives a JSONObject and returns a list of lists containing latitude and longitude
             */
            public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

                List<List<HashMap<String, String>>> routes = new ArrayList<>();
                JSONArray jRoutes;
                JSONArray jLegs;
                JSONArray jSteps;

                try {

                    jRoutes = jObject.getJSONArray("routes");

                    /** Traversing all routes */
                    for (int i = 0; i < jRoutes.length(); i++) {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                        List path = new ArrayList<>();

                        /** Traversing all legs */
                        for (int j = 0; j < jLegs.length(); j++) {
                            jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                            /** Traversing all steps */
                            for (int k = 0; k < jSteps.length(); k++) {
                                String polyline = "";
                                polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                                List<LatLng> list = decodePoly(polyline);

                                /** Traversing all points */
                                for (int l = 0; l < list.size(); l++) {
                                    HashMap<String, String> hm = new HashMap<>();
                                    hm.put("lat", Double.toString((list.get(l)).latitude));
                                    hm.put("lng", Double.toString((list.get(l)).longitude));
                                    path.add(hm);
                                }
                            }
                            routes.add(path);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                }


                return routes;
            }


            /**
             * Method to decode polyline points
             * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
             */
            private List<LatLng> decodePoly(String encoded) {

                List<LatLng> poly = new ArrayList<>();
                int index = 0, len = encoded.length();
                int lat = 0, lng = 0;

                while (index < len) {
                    int b, shift = 0, result = 0;
                    do {
                        b = encoded.charAt(index++) - 63;
                        result |= (b & 0x1f) << shift;
                        shift += 5;
                    } while (b >= 0x20);
                    int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                    lat += dlat;

                    shift = 0;
                    result = 0;
                    do {
                        b = encoded.charAt(index++) - 63;
                        result |= (b & 0x1f) << shift;
                        shift += 5;
                    } while (b >= 0x20);
                    int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                    lng += dlng;

                    LatLng p = new LatLng((((double) lat / 1E5)),
                            (((double) lng / 1E5)));
                    poly.add(p);
                }

                return poly;
            }
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            if(polyline!=null)
            {
                polyline.remove();
            }
            lineOptions = null;
            // Traversing through all the routes
            try{
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point;
                        point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(15);
                    lineOptions.color(Color.argb(0x7f, 0x18, 0xD3, 0x22));
                    Log.d("onPostExecute", "onPostExecute lineoptions decoded");
                }



                // Drawing polyline in the Google Map for the i-th route
                if(lineOptions != null) {
                    polyline=mMap.addPolyline(lineOptions);
                    M.showInfoWindow();
                    polyline.setClickable(true);
                    mMap.setOnPolylineClickListener(obj);
                }
                else {
                    Log.d("onPostExecute","without Polylines drawn");
                }}catch(Exception e){}
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    public void recenter()
    {
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(origin, DEFAULT_ZOOM));
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist*1000);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public void attachDatabaseReadListener(){
        if(mChildEventListener==null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    BinInfo bin = dataSnapshot.getValue(BinInfo.class);
                    if (!binsList.contains(bin)){
                        binsList.add(bin);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    BinInfo bin = dataSnapshot.getValue(BinInfo.class);
                    if (!binsList.contains(bin)){
                        binsList.add(bin);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    public void detachDatabaseReadListener(){
        if(mChildEventListener!=null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener=null;
        }
    }

}
