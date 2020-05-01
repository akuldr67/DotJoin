package com.example.dotjoin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MultiPlayerOnline extends AppCompatActivity {
    //Firebase
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_online);

        Room tempRoom = hostRoom();

    }

    protected Room hostRoom(){
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Rooms");
        Room room = new Room();
        mDatabaseRef.child(room.getRoomID()).setValue(room);
        return room;
    }

    //ToDO: check if that room exists, else show/toast error
    protected Room joinRoom(String RoomId){
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Rooms");
        Room room = new Room(RoomId);
        mDatabaseRef.child(room.getRoomID()).setValue(room);
        return room;
    }
}
