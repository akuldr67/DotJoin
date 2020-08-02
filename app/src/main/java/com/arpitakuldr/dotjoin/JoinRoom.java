package com.arpitakuldr.dotjoin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;

public class JoinRoom extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private String roomId;
    private TextInputEditText roomId_input;
    private Button submit;
    //ProgressBar
    private ProgressBar mProgressBar;
    private SharedPreferences mSharedPreferences;

    private int noOfPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);
        roomId_input=findViewById(R.id.join_room_input);
        submit = findViewById(R.id.join_room_submit);
        mProgressBar = findViewById(R.id.join_room_progress);
        mDatabaseRef= FirebaseDatabase.getInstance().getReference();


        //**************************
        //*Join Room Click Listener*
        //**************************
        // 1. Get room id from the user using Alert Dialog Box
        // 2. Check if the roomid exists
        // 3. Add the user on server in that room
        // 4. Launch the waitActivity

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                //If User does not give input
                if(roomId_input.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please Enter Room Id",Toast.LENGTH_LONG).show();
                    mProgressBar.setVisibility(View.GONE);
                }
                else{
                    //Saving User input in this string
                    roomId = roomId_input.getText().toString();
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(final InstanceIdResult instanceIdResult) {


                            mDatabaseRef.child("Rooms").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    mDatabaseRef.child("Rooms").removeEventListener(this);
                                    if(dataSnapshot.hasChild(roomId)){

                                        mDatabaseRef.child("Rooms").child(roomId).runTransaction(new Transaction.Handler() {
                                            @NonNull
                                            @Override
                                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                                                if(mutableData==null){
                                                    return Transaction.success(mutableData);
                                                }
                                                    //Getting room object from the server
                                                Room room = mutableData.getValue(Room.class);

                                                if(room==null){
                                                    Log.d("checkk","room is null");
                                                    return Transaction.success(mutableData);
                                                }

                                                    //Checking if room is full
                                                noOfPlayers=room.getPlayers().size();
                                                if(!room.getIsGameStarted()) {
                                                    if (noOfPlayers == 4) {
                                                        roomIsFullToast();
                                                        removeProgressBar();
                                                    } else {
                                                        mSharedPreferences = getSharedPreferences("com.arpitakuldr.dotjoin.file", Context.MODE_PRIVATE);
                                                        final String name = mSharedPreferences.getString("UserName", "");
                                                            //Adding the user to the server

                                                        Player player = new Player(name, 0, 0, noOfPlayers, instanceIdResult.getToken(), 1, 1, 0);
                                                        ArrayList<Player> players = room.getPlayers();
                                                        players.add(player);
                                                        mutableData.child("players").setValue(players);
                                                    }
                                                }
                                                else {
                                                    removeProgressBar();
                                                    gameRunningToast();
                                                }
                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                Log.d("checkk","b = "+b);

                                                if(databaseError==null){
                                                    //Starting Waiting Room Activity
                                                    Log.d("checkk","Transaction success");
                                                    Intent waitingRoomIntent = new Intent(JoinRoom.this, WaitingPlace.class);
                                                    waitingRoomIntent.putExtra("RoomId", roomId);
                                                    waitingRoomIntent.putExtra("PlayerNo", noOfPlayers);
                                                    mProgressBar.setVisibility(View.GONE);
                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                    startActivity(waitingRoomIntent);
                                                    finish();
                                                    MultiPlayerOnline.AcMultiPlayerOnline.finish();
                                                }
                                                else{
                                                    Log.d("checkk",databaseError.getMessage());
                                                    mProgressBar.setVisibility(View.GONE);
                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                    Toast.makeText(getApplicationContext(), "Unable to join, try Later", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        removeProgressBar();
                                        checkYourIdToast();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("checkk",databaseError.getMessage());
                                }
                            });











//                            mDatabaseRef.child("Rooms").addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    mDatabaseRef.child("Rooms").removeEventListener(this);
//                                    //Checking if the room exists
//                                    if(dataSnapshot.hasChild(roomId)){
//                                        //Getting room object from the server
//                                        Room room = dataSnapshot.child(roomId).getValue(Room.class);
//                                        //Checking if room is full
//                                        final int noOfPlayers=room.getPlayers().size();
//                                        if(!room.getIsGameStarted()) {
//                                            if (noOfPlayers == 4) {
//                                                Toast.makeText(getApplicationContext(), "This Room is full", Toast.LENGTH_SHORT).show();
//                                                mProgressBar.setVisibility(View.GONE);
//                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                            } else {
//                                                //Removing Listener to avoid infinite loop because of recursion
//                                                mSharedPreferences = getSharedPreferences("com.arpitakuldr.dotjoin.file", Context.MODE_PRIVATE);
//                                                final String name = mSharedPreferences.getString("UserName", "");
//                                                //Adding the user to the server
//
//                                                Player player = new Player(name, 0, 0, noOfPlayers, instanceIdResult.getToken(), 1, 1, 0);
//                                                ArrayList<Player> players = room.getPlayers();
//                                                players.add(player);
//                                                room.setPlayers(players);
//                                                mDatabaseRef.child("Rooms").child(roomId).child("players").child(String.valueOf(noOfPlayers)).setValue(player).addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                mDatabaseRef.child("Rooms").child(roomId).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                        if (task.isSuccessful()) {
//                                                            //Starting Waiting Room Activity
//                                                            Intent waitingRoomIntent = new Intent(JoinRoom.this, WaitingPlace.class);
//                                                            waitingRoomIntent.putExtra("RoomId", roomId);
//                                                            waitingRoomIntent.putExtra("PlayerNo", noOfPlayers);
//                                                            mProgressBar.setVisibility(View.GONE);
//                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                                            startActivity(waitingRoomIntent);
//                                                            finish();
//                                                            MultiPlayerOnline.AcMultiPlayerOnline.finish();
//                                                        } else {
//                                                            mProgressBar.setVisibility(View.GONE);
//                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                                            Toast.makeText(getApplicationContext(), "Unable to join, try Later", Toast.LENGTH_LONG).show();
//                                                        }
//                                                    }
//                                                });
//
//                                            }
//                                        }
//                                        else {
//                                            mProgressBar.setVisibility(View.GONE);
//                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                            Toast.makeText(getApplicationContext(),"Currently a game is running. Join in next round!",Toast.LENGTH_LONG).show();
//                                        }
//                                    }
//                                    else{
//                                        mProgressBar.setVisibility(View.GONE);
//                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//
//                                        Toast.makeText(getApplicationContext(),"Check your room id",Toast.LENGTH_LONG).show();
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
                        }
                    });
                }
            }
        });


    }

    void removeProgressBar(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    void roomIsFullToast(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "This Room is full", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void gameRunningToast(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Currently a game is running. Join in next round!",Toast.LENGTH_LONG).show();
            }
        });
    }

    void checkYourIdToast(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Check your room id",Toast.LENGTH_LONG).show();
            }
        });
    }
}
