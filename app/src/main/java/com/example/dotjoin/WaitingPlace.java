package com.example.dotjoin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

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
    private int playerNo,boardSize;
    private Button exitRoom,startGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_place);

        //Getting Data from previous activity
        Intent intent = getIntent();
        final String roomId=intent.getStringExtra("RoomId");
        playerNo=intent.getIntExtra("PlayerNo",-1);
        if(playerNo==-1){
            Log.d("playerNo","Did not get the player No");
        }

        //finding Layout
        roomIdTextView =findViewById(R.id.waiting_roomId);
        copyButton=findViewById(R.id.waiting_copy_button);
        exitRoom = findViewById(R.id.exit_room);
        startGame = findViewById(R.id.start_game);

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

//        mSharedPreferences=getSharedPreferences("com.example.dotjoin.file",Context.MODE_PRIVATE);
//        final String name=mSharedPreferences.getString("UserName","");

        //TODO Convert All Vectors to ArrayList

        //Retrieving Players
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(final InstanceIdResult instanceIdResult) {
                mDatabase.child("Rooms").child(roomId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Room room = dataSnapshot.getValue(Room.class);
                        players=room.getPlayers();

                        for(int i=0;i<players.size();i++){
                            playerTextViews.elementAt(i).setText(players.get(i).getName());
                            playerTextViews.elementAt(i).setVisibility(View.VISIBLE);
                            //Updating the player no. if it has changed
                            if(players.get(i).getDeviceToken().equals(instanceIdResult.getToken())){
                                playerNo=i;
                                players.get(i).setPlayerNumber(i);
                                room.setPlayers(players);
                                mDatabase.child("Rooms").child(roomId).setValue(room);
                            }
                        }
                        //Making TextView invisible for the players who have left
                        for(int i=players.size();i<4;i++){
                            playerTextViews.elementAt(i).setText("");
                            playerTextViews.elementAt(i).setVisibility(View.INVISIBLE);
                        }

                        //setting start game visible for host
                        Log.d("playerNo",playerNo+"");
                        if(room.getHost()==playerNo) {
                            startGame.setVisibility(View.VISIBLE);
                            startGame.setEnabled(true);
                        }

                        if(room.getIsGameStarted()){
                            mDatabase.child("Rooms").child(roomId).removeEventListener(this);
                            Intent intent = new Intent(WaitingPlace.this,OnlineGamePlay.class);
                            intent.putExtra("RoomId",roomId);
                            intent.putExtra("PlayerNo",playerNo);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        //Starting startGame Click Listener
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("logg","hello");

                //Dialog Box that accepts board size
                CharSequence[] sizeOptions = new CharSequence[]{"3*3 ", "4*4", "5*5", "6*6", "7*7"};

                AlertDialog.Builder sizeDialog = new AlertDialog.Builder(WaitingPlace.this);
                sizeDialog.setTitle("Size");
                sizeDialog.setItems(sizeOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) boardSize = 4;
                        else if (which == 1) boardSize = 5;
                        else if (which == 2) boardSize = 6;
                        else if (which == 3) boardSize = 7;
                        else if (which == 4) boardSize = 8;


                        //Value Event Listener for room
                        mDatabase.child("Rooms").child(roomId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                mDatabase.child("Rooms").child(roomId).removeEventListener(this);
                                Room room=dataSnapshot.getValue(Room.class);
                                Board board=new Board(boardSize,boardSize,0,0,0);
                                ArrayList<Player>players=room.getPlayers();
                                for(int i=0;i<players.size();i++){
                                    if(i==0)players.get(i).setColor(R.drawable.colour_box_blue);
                                    else if(i==1)players.get(i).setColor(R.drawable.colour_box_red);
                                    else if(i==2)players.get(i).setColor(R.drawable.colour_box_green);
                                    else if(i==3)players.get(i).setColor(R.drawable.colour_box_yellow);
                                }
                                Game game=new Game(-1,room.getPlayers().size(),board,players);
                                room.setGame(game);
                                room.setIsGameStarted(true);
                                mDatabase.child("Rooms").child(roomId).setValue(room);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                AlertDialog alertDialog=sizeDialog.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
            //Ending startGame Listener
        });

        exitRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("Rooms").child(roomId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mDatabase.child("Rooms").child(roomId).removeEventListener(this);
                        Room room = dataSnapshot.getValue(Room.class);
                        players=room.getPlayers();
                        players.remove(playerNo);
                        room.setPlayers(players);
                        mDatabase.child("Rooms").child(roomId).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(WaitingPlace.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Toast.makeText(WaitingPlace.this,"Unable to remove you",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {

    }
}
//TODO - Disable back button when in this activity (Done)
//TODO - Edit Player Class add id or no so that we can check if current player is host (Done)
//TODO - Start Game Button
//ToDo - Check after retrieving players, if players null/empty, if null go back to previous activity and delete room from database
