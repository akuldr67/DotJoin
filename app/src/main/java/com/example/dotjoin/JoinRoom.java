package com.example.dotjoin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

                                    //Checking if the room exists
                                    if(dataSnapshot.hasChild(roomId)){
                                        //Getting room object from the server
                                        Room room = dataSnapshot.child(roomId).getValue(Room.class);
                                        //Checking if room is full
                                        final int noOfPlayers=room.getPlayers().size();
                                        if(noOfPlayers==4){
                                            Toast.makeText(getApplicationContext(),"This Room is full",Toast.LENGTH_SHORT).show();
                                            mProgressBar.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        }
                                        else {
                                            //Removing Listener to avoid infinite loop because of recursion
                                            mDatabaseRef.child("Rooms").removeEventListener(this);
                                            mSharedPreferences = getSharedPreferences("com.example.dotjoin.file", Context.MODE_PRIVATE);
                                            final String name = mSharedPreferences.getString("UserName", "");
                                            //Adding the user to the server

                                            Player player = new Player(name, 0, 0, noOfPlayers,instanceIdResult.getToken(),1,1,0);
                                            ArrayList<Player> players = room.getPlayers();
                                            players.add(player);
                                            room.setPlayers(players);
                                            mDatabaseRef.child("Rooms").child(roomId).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        //Starting Waiting Room Activity
                                                        Intent waitingRoomIntent = new Intent(JoinRoom.this, WaitingPlace.class);
                                                        waitingRoomIntent.putExtra("RoomId", roomId);
                                                        waitingRoomIntent.putExtra("PlayerNo", noOfPlayers);
                                                        mProgressBar.setVisibility(View.GONE);
                                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                        startActivity(waitingRoomIntent);
                                                    } else {
                                                        mProgressBar.setVisibility(View.GONE);
                                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                        Toast.makeText(getApplicationContext(), "Unable to join, try Later", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });

                                        }
                                    }
                                    else{
                                        mProgressBar.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        Toast.makeText(getApplicationContext(),"Check your room id",Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }
        });


    }
}
