package com.example.dotjoin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OnlineGamePlayEndGame extends AppCompatActivity {
    private TextView Heading,Result;
    private DatabaseReference mDatabase;
    private String roomId;
    private int playerNo;
    private ArrayList<Player> players;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        setFinishOnTouchOutside(false);

        setContentView(R.layout.activity_online_game_play_end_game);

        Intent intent = getIntent();
        String heading = intent.getStringExtra("Heading");
        String result = intent.getStringExtra("Result");
        roomId=intent.getStringExtra("RoomId");
        playerNo=intent.getIntExtra("PlayerNo",-1);
        if(playerNo==-1){
            Log.d("playerNo","Did not get the player No");
        }


        Heading=findViewById(R.id.onlinegameplay_endgame_title);
        Result=findViewById(R.id.onlinegameplay_endgame_final_score);

        Heading.setText(heading);
        Result.setText(result);

        mDatabase= FirebaseDatabase.getInstance().getReference();


    }

    public void onHomeClicked(View view){
        Log.d("checkk","Click on Home Detected");
        mDatabase.child("Rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDatabase.child("Rooms").child(roomId).removeEventListener(this);
                Room room = dataSnapshot.getValue(Room.class);
                Log.d("checkk","Loaded room");
                players = room.getPlayers();
                Log.d("checkk","Removing player from room");
                for(int i=0;i<players.size();i++){
                    if(players.get(i).getPlayerNumber()==playerNo){
                        players.remove(i);
                    }
                }
//                        players.remove(playerNo);


                if (players == null || players.size() < 1) {
                    Log.d("checkk","All players gone");
                    Log.d("checkk","Deleting room");
                    mDatabase.child("Rooms").child(roomId).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(OnlineGamePlayEndGame.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Log.d("checkk","finishing OnlineGamePlay");
                                finish();
                                Log.d("checkk","Starting Main Activity");
                                startActivity(intent);
                            } else {
                                Toast.makeText(OnlineGamePlayEndGame.this, "Unable to go to home page", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Log.d("checkk","all player not gone");
                    room.setPlayers(players);
                    Log.d("checkk","updating room with the player removed");
                    mDatabase.child("Rooms").child(roomId).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(OnlineGamePlayEndGame.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Log.d("checkk","finishing OnlineGamePlay");
                                finish();
                                Log.d("checkk","Starting intent for MainActivity");
                                startActivity(intent);
//                                                    finish();
                            } else {
                                Toast.makeText(OnlineGamePlayEndGame.this, "Unable to go to home page", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(MotionEvent.ACTION_OUTSIDE==event.getAction()){

        }
        return super.onTouchEvent(event);
    }
}
