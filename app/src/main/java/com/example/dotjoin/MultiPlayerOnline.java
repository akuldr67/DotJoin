package com.example.dotjoin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Vector;

public class MultiPlayerOnline extends AppCompatActivity {
    //Firebase
    private DatabaseReference mDatabaseRef;

    //Buttons
    private Button createRoom,joinRoom;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_online);

        mDatabaseRef=FirebaseDatabase.getInstance().getReference();
        //Finding Buttons in Layout
        createRoom=findViewById(R.id.create_room);
        joinRoom=findViewById(R.id.join_room);

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

        //**************************
        //*Join Room Click Listener*
        //**************************
        // 1. Get room id from the user using Alert Dialog Box
        // 2. Check if the roomid exists
        // 3. Add the user on server in that room
        // 4. Launch the waitActivity
        joinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating Alert Dialog Box
                AlertDialog.Builder builder = new AlertDialog.Builder(MultiPlayerOnline.this);
                builder.setTitle("Enter Room Id");
                final EditText input = new EditText(getApplicationContext());
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //If User does not give input
                        if(input.getText().toString().equals("")){
                            Toast.makeText(getApplicationContext(),"Please Enter Room Id",Toast.LENGTH_LONG).show();
                        }
                        else{
                            //Saving User input in this string
                            final String roomId = input.getText().toString();

                            mDatabaseRef.child("Rooms").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    //Checking if the room exists
                                    if(dataSnapshot.hasChild(roomId)){
                                        //Getting room object from the server
                                        Room room = dataSnapshot.child(roomId).getValue(Room.class);
                                        //Checking if room is full
                                        if(room.getPlayers().size()==4){
                                            Toast.makeText(getApplicationContext(),"This Room is full",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            //Removing Listener to avoid infinite loop because of recursion
                                            mDatabaseRef.child("Rooms").removeEventListener(this);
                                            mSharedPreferences=getSharedPreferences("com.example.dotjoin.file",Context.MODE_PRIVATE);
                                            String name=mSharedPreferences.getString("UserName","");
                                            //TODO Get Name and Resource File of the player
                                            //Adding the user to the server
                                            Player player = new Player(name,0,0,0);
                                            ArrayList<Player> players=room.getPlayers();
                                            players.add(player);
                                            room.setPlayers(players);
                                            mDatabaseRef.child("Rooms").child(roomId).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        //Starting Waiting Room Activity
                                                        Intent waitingRoomIntent =new Intent(MultiPlayerOnline.this,WaitingPlace.class);
                                                        waitingRoomIntent.putExtra("RoomId",roomId);
                                                        startActivity(waitingRoomIntent);
                                                    }
                                                    else{
                                                        Toast.makeText(getApplicationContext(),"Unable to join, try Later",Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"Check your room id",Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });
    }

    protected void hostRoom(){
        mSharedPreferences=getSharedPreferences("com.example.dotjoin.file",Context.MODE_PRIVATE);
        String name=mSharedPreferences.getString("UserName","");
        final Room room = new Room(name);
        mDatabaseRef.child("Rooms").child(room.getRoomID()).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Room Created Successfully", Toast.LENGTH_LONG).show();
                    //Starting Waiting Room Activity
                    Intent waitingRoomIntent =new Intent(MultiPlayerOnline.this,WaitingPlace.class);
                    waitingRoomIntent.putExtra("RoomId",room.getRoomID());
                    startActivity(waitingRoomIntent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Unable to Create Room", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
