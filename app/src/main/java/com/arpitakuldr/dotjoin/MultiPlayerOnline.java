package com.arpitakuldr.dotjoin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MultiPlayerOnline extends AppCompatActivity {
    //Firebase
    private DatabaseReference mDatabaseRef;
    private String roomId;

    //Buttons
    private LinearLayout createRoom,joinRoom;
    private SharedPreferences mSharedPreferences;

    //ProgressBar
    private ProgressBar mProgressBar;
    private LinearLayout linearLayout;
    private AdView bannerAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_online);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
//        List<String> testDeviceIds = Arrays.asList("315EA26B97DB5CBDE5501CB99E69E32A");
        bannerAdView = findViewById(R.id.bannerAdMultiPlayerOnline);

//        RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
//        MobileAds.setRequestConfiguration(configuration);

        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAdView.loadAd(adRequest);

        ;

        mDatabaseRef=FirebaseDatabase.getInstance().getReference();

        //Finding Buttons in Layout
        createRoom=findViewById(R.id.create_room);
        joinRoom=findViewById(R.id.join_room);
        mProgressBar=findViewById(R.id.multi_player_online_progress_bar);

        //Setting Click Listeners for Buttons

        //****************************
        //*Create Room Click Listener*
        //****************************
        // 1. Create a random String for room id
        // 2. Create room object with that string as room id and Current User Added in it as a Player
        // 3. Upload this room object to the server
        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This function creates a new room at server with the user who created already present in it and then launches waiting activity
                hostRoom();
            }
        });

        joinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MultiPlayerOnline.this,JoinRoom.class);
                startActivity(intent);

//                //Creating Alert Dialog Box
//                AlertDialog.Builder builder = new AlertDialog.Builder(MultiPlayerOnline.this);
//                builder.setTitle("Enter Room Id");
//                final EditText input = new EditText(getApplicationContext());
//                builder.setView(input);
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                AlertDialog alertDialog = builder.create();
//                alertDialog.setCanceledOnTouchOutside(false);
//                alertDialog.show();
            }
        });
    }

    protected void hostRoom(){
        mProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mSharedPreferences=getSharedPreferences("com.arpitakuldr.dotjoin.file",Context.MODE_PRIVATE);
        final String name=mSharedPreferences.getString("UserName","");
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                Log.d("token",instanceIdResult.getToken());
                final Room room = new Room(name,instanceIdResult.getToken());
                mDatabaseRef.child("Rooms").child(room.getRoomID()).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Room Created Successfully", Toast.LENGTH_LONG).show();
                            //Starting Waiting Room Activity
                            Intent waitingRoomIntent =new Intent(MultiPlayerOnline.this,WaitingPlace.class);
                            waitingRoomIntent.putExtra("RoomId",room.getRoomID());
                            waitingRoomIntent.putExtra("PlayerNo",0);
                            mProgressBar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            startActivity(waitingRoomIntent);
                        }
                        else{
                            mProgressBar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(getApplicationContext(),"Unable to Create Room", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}


//ToDo - Add margin above line in join room alert builder
//ToDo - Change big names to same with ... while showing scores in a game
