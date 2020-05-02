package com.example.dotjoin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Vector;

public class WaitingPlace extends AppCompatActivity {

    private TextView roomIdTextView;
    private ImageButton copyButton;
    private Vector<TextView>playerTextViews;
    private static int maxPlayers=4;
    private DatabaseReference mDatabase;
    private ArrayList<Player>players;
    private SharedPreferences mSharedPreferences;

    private Button exitRoom,startGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_place);

        //Getting Data from previous activity
        Intent intent = getIntent();
        final String roomId=intent.getStringExtra("RoomId");

        //finding Layout
        roomIdTextView =findViewById(R.id.waiting_roomId);
        copyButton=findViewById(R.id.waiting_copy_button);

        playerTextViews=new Vector<TextView>();
        playerTextViews.add((TextView)findViewById(R.id.waiting_player1));
        playerTextViews.add((TextView)findViewById(R.id.waiting_player2));
        playerTextViews.add((TextView)findViewById(R.id.waiting_player3));
        playerTextViews.add((TextView)findViewById(R.id.waiting_player4));

        //Getting firebase Database Reference
        mDatabase= FirebaseDatabase.getInstance().getReference();

        //Setting Room Id
        roomIdTextView.setText(roomId);

        //Copy Button
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("roomId",roomId);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(),"Room ID Copied",Toast.LENGTH_SHORT).show();
            }
        });

        mSharedPreferences=getSharedPreferences("com.example.dotjoin.file",Context.MODE_PRIVATE);
        final String name=mSharedPreferences.getString("UserName","");

        //Retrieving Players
        mDatabase.child("Rooms").child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Room room = dataSnapshot.getValue(Room.class);
                players=room.getPlayers();
                for(int i=0;i<players.size();i++){
                    playerTextViews.elementAt(i).setText(players.get(i).getName());
                    playerTextViews.elementAt(i).setVisibility(View.VISIBLE);
                }

                //setting start game visible for host
                //todo: if host exits, change host no to someone else or change some player's number to hostnumber and give that no to new player
                if(players.get(room.getHost()).getName().equals(name)) {
                    findViewById(R.id.start_game).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        exitRoom = findViewById(R.id.exit_room);
        startGame = findViewById(R.id.start_game);

//        startGame.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        exitRoom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

    }
}
//TODO - Disable back button when in this activity
//TODO - Edit Player Class add id or no so that we can check if current player is host
//TODO - Start Game Button