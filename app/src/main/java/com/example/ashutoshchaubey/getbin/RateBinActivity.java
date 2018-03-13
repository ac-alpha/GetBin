package com.example.ashutoshchaubey.getbin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class RateBinActivity extends AppCompatActivity {

    ArrayList<String> mUpVotedUsers;
    ArrayList<String> mDownVotedUsers;
    ImageView mUpVoteButton, mDownVoteButton,mBinImage;
    TextView mUpVotesView, mDownVotesView;
    ImageView mVerified;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference mDatabaseReference;
    DatabaseReference mMainDatabaseReference;

    String mUid;
    String mKey;
    BinInfo mBin;
    String mIsVerified;
    boolean mButtonsEnabled;
    int index;
    ArrayList<BinInfo> binsList;
    ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_bin);
        if(AccountActivity.ha!=null)
        AccountActivity.ha.removeCallbacks(AccountActivity.runnable);
        mBin=(BinInfo) getIntent().getSerializableExtra("Bin");
        mUpVotedUsers=mBin.getUpVotedUsers();
        mDownVotedUsers=mBin.getDownVotedUsers();
        mIsVerified=mBin.getIsVerified();
        mKey=mBin.getBinId();

        mMainDatabaseReference=FirebaseDatabase.getInstance().getReference().child("bins");

        TextView reminder=(TextView)findViewById(R.id.reminder);

        mButtonsEnabled=true;
        mFirebaseAuth=FirebaseAuth.getInstance();
        mUid=mFirebaseAuth.getCurrentUser().getUid();
        mUpVoteButton=(ImageView)findViewById(R.id.up_vote);
        mDownVoteButton=(ImageView)findViewById(R.id.down_vote);
        mUpVotesView=(TextView)findViewById(R.id.total_up_votes);
        mDownVotesView=(TextView)findViewById(R.id.total_down_votes);
        mVerified=(ImageView)findViewById(R.id.is_verified);
        mBinImage=(ImageView)findViewById(R.id.image_view_bin_rate);

        final ProgressBar progressBar=(ProgressBar)findViewById(R.id.pb2);

        mUpVotesView.setText(mBin.getUpVotes());
        mDownVotesView.setText(mBin.getDownVotes());
        try {
            byte [] encodeByte= Base64.decode(mBin.getImageUrl(),Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            mBinImage.setImageBitmap(bitmap);
            progressBar.setVisibility(View.GONE);
        } catch(Exception e) {
            Toast.makeText(this, "Can't Load Image", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

        for(int i=0;i<mDownVotedUsers.size();i++){
            if(mDownVotedUsers.get(i).equals(mUid)){
                mButtonsEnabled=false;
            }
        }
        for(int j=0;j<mUpVotedUsers.size();j++){
            if(mUpVotedUsers.get(j).equals(mUid)){
                mButtonsEnabled=false;
            }
        }
        if(mButtonsEnabled) {
            mUpVoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AccountActivity.binsList.remove(mBin);
                    mUpVotedUsers.add(mUid);
                    mBin.upVotedUsers.add(mUid);
                    mBin.upVotes.replace(mBin.upVotes,Integer.toString(Integer.parseInt(mBin.upVotes)+1));
                    Log.i("Helloo",mUid);
                    Log.i("Helloo",mUpVotedUsers.size()+"");
                    mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("bins");
                    int n=Integer.parseInt(mBin.getUpVotes());
                    final int k=++n;
                    Log.i("Helloo",k+"");
                    Log.i("Helloo",mBin.getBinId());
                    Query query = mDatabaseReference.orderByChild("binId").equalTo(mBin.getBinId());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                ArrayList<String> likers = (ArrayList<String>) child.child("upVotedUsers").getValue();

                                child.getRef().child("upVotedUsers").setValue(mUpVotedUsers);
                                child.getRef().child("upVotes").setValue(Integer.toString(k));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i("Helloo","Here is the mistake");
                        }
                    });
                    mUpVotesView.setText(Integer.toString(k));
                    startActivity(new Intent(RateBinActivity.this,JunkActivity.class));
                    finish();
                }
            });
            mDownVoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AccountActivity.binsList.remove(mBin);
                    mDownVotedUsers.add(mUid);
                    mBin.downVotedUsers.add(mUid);
                    mBin.downVotes.replace(mBin.downVotes,Integer.toString(Integer.parseInt(mBin.downVotes)+1));
                    Log.i("Helloo",mUid);
                    Log.i("Helloo",mUpVotedUsers.size()+"");
                    mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("bins");
                    int n=Integer.parseInt(mBin.getDownVotes());
                    final int k=++n;
                    Log.i("Helloo",k+"");
                    Log.i("Helloo",mBin.getBinId());
                    Query query = mDatabaseReference.orderByChild("binId").equalTo(mBin.getBinId());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                ArrayList<String> likers = (ArrayList<String>) child.child("downVotedUsers").getValue();

                                child.getRef().child("downVotedUsers").setValue(mDownVotedUsers);
                                child.getRef().child("downVotes").setValue(Integer.toString(k));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i("Helloo","Here is the mistake");
                        }
                    });
                    mDownVotesView.setText(Integer.toString(k));
                    startActivity(new Intent(RateBinActivity.this,JunkActivity.class));
                    finish();
                }
            });
        }else{
            reminder.setVisibility(View.VISIBLE);
        }
        AccountActivity.M=null;
    }

//    public void attachDatabaseReadListener(){
//        if(mChildEventListener==null) {
//            mChildEventListener = new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    BinInfo bin = dataSnapshot.getValue(BinInfo.class);
//                    binsList.add(bin);
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                    BinInfo bin = dataSnapshot.getValue(BinInfo.class);
//                    binsList.add(bin);
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                }
//            };
//            mMainDatabaseReference.addChildEventListener(mChildEventListener);
//        }
//    }
//
//    public void detachDatabaseReadListener(){
//        if(mChildEventListener!=null) {
//            mMainDatabaseReference.removeEventListener(mChildEventListener);
//            mChildEventListener=null;
//        }
//    }

}
