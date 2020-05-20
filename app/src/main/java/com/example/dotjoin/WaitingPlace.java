package com.example.dotjoin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class WaitingPlace extends AppCompatActivity {

    private TextView roomIdTextView;
    private ImageButton copyButton;
    private Vector<TextView>playerTextViews;
    private Vector<ImageButton>playerButtonViews;
    private static int maxPlayers=4;
    private DatabaseReference mDatabase;
    private ArrayList<Player>players;
    private SharedPreferences mSharedPreferences;
    private int playerNo,boardSize;
    private Button exitRoom,startGame;
    private String roomId;
    private Vector<ImageView> playerReadyViews;

    private AdView bannerAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_place);
        Log.d("checkk","Starting OnCreate of WaitingPlace");




        //*** banner ad ****
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
//        List<String> testDeviceIds = Arrays.asList("315EA26B97DB5CBDE5501CB99E69E32A");
        bannerAdView = findViewById(R.id.bannerAdWaitingPlace);

//        RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
//        MobileAds.setRequestConfiguration(configuration);

        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAdView.loadAd(adRequest);




        Log.d("checkk","Getting Intent Extras");
        //Getting Data from previous activity
        Intent intent = getIntent();
        roomId=intent.getStringExtra("RoomId");
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

        playerButtonViews = new Vector<ImageButton>();
        playerButtonViews.add((ImageButton)findViewById(R.id.removePlayer1));
        playerButtonViews.add((ImageButton)findViewById(R.id.removePlayer2));
        playerButtonViews.add((ImageButton)findViewById(R.id.removePlayer3));
        playerButtonViews.add((ImageButton)findViewById(R.id.removePlayer4));

        playerReadyViews = new Vector<ImageView>();
        playerReadyViews.add((ImageView)findViewById(R.id.readyPlayer1));
        playerReadyViews.add((ImageView)findViewById(R.id.readyPlayer2));
        playerReadyViews.add((ImageView)findViewById(R.id.readyPlayer3));
        playerReadyViews.add((ImageView)findViewById(R.id.readyPlayer4));


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

        Log.d("checkk","setting Ready value 1");
        mDatabase.child("Rooms").child(roomId).child("players").child(playerNo+"").child("ready").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("checkk","ready value successfully set to 1");


        //Retrieving Players
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(final InstanceIdResult instanceIdResult) {
                Log.d("checkk","Successfully got FirebaseInstanceId");
                mDatabase.child("Rooms").child(roomId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("checkk","Value Event Listener OnData Change Called");
                        Room room = dataSnapshot.getValue(Room.class);

                        //null checking..for crash checking..
                        if(room==null){
                            Log.d("checkk","Room found null, finishing");
                            finish();
                        }
                        else {
                            Log.d("checkk","Room not found null, getting players");
                            players = room.getPlayers();

                            if (players == null || players.size() < 1) {
                                Log.d("checkk","Players null, finishing");
                                finish();
                            }

                            //checking if that player is present or removed by host
                            Boolean isPresent = false;
                            for (int i = 0; i < players.size(); i++) {
                                if (players.get(i).getDeviceToken().equals(instanceIdResult.getToken())) {
                                    isPresent = true;
                                }
                            }
                            if (!isPresent) {
//                                Toast.makeText(getApplicationContext(), "You are no longer member of the room", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(WaitingPlace.this, MainActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
//                                startActivity(intent);
                            }

                            //setting readyViews invisible initially
                            for(int i=0;i<4;i++){
                                playerReadyViews.elementAt(i).setVisibility(View.INVISIBLE);
                            }

                            for (int i = 0; i < players.size(); i++) {
                                playerTextViews.elementAt(i).setText(players.get(i).getName());
                                playerTextViews.elementAt(i).setVisibility(View.VISIBLE);
                                Log.d("checkk","Checking if player ready or not");
                                if (players.get(i).getReady() == 1) {
                                    Log.d("checkk","Player found ready, making bold");
//                                    playerTextViews.elementAt(i).setTextColor(ContextCompat.getColor(WaitingPlace.this, R.color.black));
//                                    playerTe  xtViews.elementAt(i).setTypeface(Typeface.DEFAULT_BOLD);
                                    playerReadyViews.elementAt(i).setVisibility(View.VISIBLE);
                                } else if (players.get(i).getReady() == 0) {
                                    Log.d("checkk","Player not ready, making thin text");
//                                    playerTextViews.elementAt(i).setTextColor(ContextCompat.getColor(WaitingPlace.this, R.color.grey));
//                                    playerTextViews.elementAt(i).setTypeface(Typeface.DEFAULT);
                                    playerReadyViews.elementAt(i).setVisibility(View.INVISIBLE);
                                }

                                Log.d("checkk","Checking if PlayerNo Changed");
                                //Updating the player no. if it has changed
                                if (players.get(i).getDeviceToken().equals(instanceIdResult.getToken())) {
                                    playerNo = i;
                                    players.get(i).setPlayerNumber(i);
                                    room.setPlayers(players);
                                    Log.d("checkk","Updating room which updated playerNo");
                                    mDatabase.child("Rooms").child(roomId).setValue(room);
                                }

                                //telling who is host in waiting place
                                if (room.getHost() == i) {
                                    playerTextViews.elementAt(i).setText(players.get(i).getName() + "  (Host)");
                                }
                            }
                            //Making TextView invisible for the players who have left
                            for (int i = players.size(); i < 4; i++) {
                                playerTextViews.elementAt(i).setText("");
                                playerTextViews.elementAt(i).setVisibility(View.INVISIBLE);
                                playerReadyViews.elementAt(i).setVisibility(View.INVISIBLE);
                            }

                            //setting start game visible for host
                            if (room.getHost() == playerNo) {
                                startGame.setVisibility(View.VISIBLE);
                                startGame.setEnabled(true);
                            }

                            Log.d("checkk","Setting Room Buttons visible for host ");
                            //setting remove players visible for host and invisible when removed
                            if (room.getHost() == playerNo) {
                                for (int i = 0; i < players.size(); i++) {
                                    if (i != playerNo) {
                                        playerButtonViews.elementAt(i).setVisibility(View.VISIBLE);
                                        playerButtonViews.elementAt(i).setEnabled(true);
                                    }
                                }
                                for (int i = players.size(); i < 4; i++) {
                                    playerButtonViews.elementAt(i).setVisibility(View.INVISIBLE);
                                    playerButtonViews.elementAt(i).setEnabled(false);
                                }
                            }

                            Log.d("checkk","Checking if game has started");
                            if (room.getIsGameStarted()) {
                                Log.d("checkk","Game has started, Removing ValueEventListener");
                                mDatabase.child("Rooms").child(roomId).removeEventListener(this);
                                Intent intent = new Intent(WaitingPlace.this, OnlineGamePlay.class);
                                intent.putExtra("RoomId", roomId);
                                intent.putExtra("PlayerNo", playerNo);
                                Log.d("checkk","finishing Waiting Place");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                                Log.d("checkk","Starting Online GamePlay");
                                startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
            }
        });


        //Starting startGame Click Listener
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("checkk","Click on Start Game detected");
                mDatabase.child("Rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         Log.d("checkk", "SingleValueEvent Listener Added");
                         mDatabase.child("Rooms").child(roomId).removeEventListener(this);
                         Room room = dataSnapshot.getValue(Room.class);
                         ArrayList<Player> players = room.getPlayers();
                         Log.d("checkk", "Checking if players are less than 2");
                         if (players.size() < 2) {
                             Toast.makeText(getApplicationContext(), "Minimum 2 players required to start game", Toast.LENGTH_SHORT).show();
                         } else if (players.size() > 4) {
                             Toast.makeText(getApplicationContext(), "Maximum 4 players can play. Remove extra players", Toast.LENGTH_SHORT).show();
                         } else {
                             Log.d("checkk", "Found suitable player no");
                             Log.d("checkk", "Started Creating Dialog to accept board size");
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
                                     mDatabase.child("Rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                                         @Override
                                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                             Log.d("checkk", "onDataChange started, SingleValueEventListener");
                                             mDatabase.child("Rooms").child(roomId).removeEventListener(this);
                                             Room room = dataSnapshot.getValue(Room.class);
                                             ArrayList<Player> players = room.getPlayers();
                                             int flag = 1;
                                             for (int i = 0; i < players.size(); i++) {
                                                 if (players.get(i).getReady() == 0) {
                                                     flag = 0;
                                                 }
                                             }
                                             if(flag==1) {
                                                 Log.d("checkk", "Initializing Board");
                                                 Board board = new Board(boardSize, boardSize, 0, 0, 0);
                                                 for (int i = 0; i < players.size(); i++) {
                                                     if (i == 0)
                                                         players.get(i).setColor(R.drawable.colour_box_blue);
                                                     else if (i == 1)
                                                         players.get(i).setColor(R.drawable.colour_box_red);
                                                     else if (i == 2)
                                                         players.get(i).setColor(R.drawable.colour_box_green);
                                                     else if (i == 3)
                                                         players.get(i).setColor(R.drawable.colour_box_yellow);
                                                 }
                                                 Log.d("checkk", "Initializing Game with players in room");
                                                 Game game = new Game(-1, room.getPlayers().size(), board, players);
                                                 room.setGame(game);
                                                 Log.d("checkk", "Setting isGameStarted true");
                                                 room.setIsGameStarted(true);
                                                 Log.d("checkk", "Updating to server, updated room");
                                                 mDatabase.child("Rooms").child(roomId).setValue(room);
                                             }
                                             else {
                                                 Toast.makeText(WaitingPlace.this,"All Players are not Ready!!",Toast.LENGTH_LONG).show();
                                             }
                                         }

                                         @Override
                                         public void onCancelled(@NonNull DatabaseError databaseError) {

                                         }
                                     });
                                 }
                             });
                             Log.d("checkk", "Creating dialog from builder");
                             AlertDialog alertDialog = sizeDialog.create();
                             alertDialog.setCanceledOnTouchOutside(false);
                             Log.d("checkk", "Showing dialog");
                             alertDialog.show();
                         }
                     }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            }
            //Ending startGame Listener
        });

        playerButtonViews.elementAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("Rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mDatabase.child("Rooms").child(roomId).removeEventListener(this);
                        Room room = dataSnapshot.getValue(Room.class);
                        players=room.getPlayers();
                        players.remove(0);
                        room.setPlayers(players);
                        mDatabase.child("Rooms").child(roomId).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(WaitingPlace.this,"Player Removed successfully",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(WaitingPlace.this,"Unable to remove player",Toast.LENGTH_SHORT).show();
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

        playerButtonViews.elementAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("Rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mDatabase.child("Rooms").child(roomId).removeEventListener(this);
                        Room room = dataSnapshot.getValue(Room.class);
                        players=room.getPlayers();
                        players.remove(1);
                        room.setPlayers(players);
                        mDatabase.child("Rooms").child(roomId).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(WaitingPlace.this,"Player Removed successfully",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(WaitingPlace.this,"Unable to remove player",Toast.LENGTH_SHORT).show();
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

        playerButtonViews.elementAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("Rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mDatabase.child("Rooms").child(roomId).removeEventListener(this);
                        Room room = dataSnapshot.getValue(Room.class);
                        players=room.getPlayers();
                        players.remove(2);
                        room.setPlayers(players);
                        mDatabase.child("Rooms").child(roomId).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(WaitingPlace.this,"Player Removed successfully",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(WaitingPlace.this,"Unable to remove player",Toast.LENGTH_SHORT).show();
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

        playerButtonViews.elementAt(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("Rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mDatabase.child("Rooms").child(roomId).removeEventListener(this);
                        Room room = dataSnapshot.getValue(Room.class);
                        players=room.getPlayers();
                        players.remove(3);
                        room.setPlayers(players);
                        mDatabase.child("Rooms").child(roomId).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(WaitingPlace.this,"Player Removed successfully",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(WaitingPlace.this,"Unable to remove player",Toast.LENGTH_SHORT).show();
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

        exitRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("checkk","Click on ExitRoom Detected");
                mDatabase.child("Rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("checkk","SingleValueEventListener, OnDataChange triggered");
                        mDatabase.child("Rooms").child(roomId).removeEventListener(this);
                        Room room = dataSnapshot.getValue(Room.class);
                        Log.d("checkk","Getting Players and removing currentPlayer");
                        players=room.getPlayers();
                        players.remove(playerNo);
                        Log.d("checkk","Checking if this is the last player");
                        if(players==null || players.size()<1){
                            Log.d("checkk","Players has become null removing room");
                            mDatabase.child("Rooms").child(roomId).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(WaitingPlace.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        Log.d("checkk","Finishing Waiting Place");
                                        finish();
                                        Log.d("checkk","Starting MainActivity");
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(WaitingPlace.this,"Unable to remove you",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Log.d("checkk","Its not the last player");
                            room.setPlayers(players);
                            Log.d("checkk","Updating room");
                            mDatabase.child("Rooms").child(roomId).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(WaitingPlace.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        Log.d("checkk","Finishing WatingPlace");
                                        finish();
                                        Log.d("checkk","Starting MainActivity");
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(WaitingPlace.this,"Unable to remove you",Toast.LENGTH_SHORT).show();
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
        });

    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView=inflater.inflate(R.layout.leaving_online_gameplay_dialog,null);
        builder.setView(dialogView);
        final Button yes = dialogView.findViewById(R.id.online_leave_dialog_YES);
        final Button no = dialogView.findViewById(R.id.online_leave_dialog_NO);

        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabase.child("Rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                            Toast.makeText(WaitingPlace.this,"You left the room successfully",Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(WaitingPlace.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(WaitingPlace.this,"Unable to remove player",Toast.LENGTH_SHORT).show();
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

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("checkk","onResume of WaitingPlace called");
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(final InstanceIdResult instanceIdResult) {
                Log.d("checkk","onResume Got FirebaseInstanceId");
                mDatabase.child("Rooms").child(roomId).child("players").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("checkk","onResume SingleValueEventListener of onResume, onDataChange trigerred");
                        mDatabase.child("Rooms").child(roomId).removeEventListener(this);
                        GenericTypeIndicator<ArrayList<Player>> t = new GenericTypeIndicator<ArrayList<Player>>() {};
                        ArrayList<Player>players=dataSnapshot.getValue(t);

                        for(int i=0; i<players.size();i++){
                            if(players.get(i).getDeviceToken().equals(instanceIdResult.getToken())){
                                Log.d("checkk","onResume Setting this player ready");
                                players.get(i).setReady(1);
                            }
                        }

                        Log.d("checkk","onResume Uploading this change to database");
                        mDatabase.child("Rooms").child(roomId).child("players").setValue(players).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("checkk","onResume Uploaded onReady set true successfully");
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("Problem","problem");
                    }
                });
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("checkk","onPause of WaitingPlace triggered");
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(final InstanceIdResult instanceIdResult) {
                Log.d("checkk","onPause Got FirebaseInstanceId");
                mDatabase.child("Rooms").child(roomId).child("players").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("checkk","onPause SingleValueEventListener onPause, onDataChange triggered");
                        mDatabase.child("Rooms").child(roomId).child("players").removeEventListener(this);
                        GenericTypeIndicator<ArrayList<Player>> t = new GenericTypeIndicator<ArrayList<Player>>() {};
                        ArrayList<Player>players=dataSnapshot.getValue(t);

                        //Host was crashing here when only he was present in room and he exits too.
                        if(players!=null &&  players.size()>0) {
                            for (int i = 0; i < players.size(); i++) {
                                if (players.get(i).getDeviceToken().equals(instanceIdResult.getToken())) {
                                    Log.d("checkk","onPause Setting this player not ready");
                                    players.get(i).setReady(0);
                                }
                            }

                            Log.d("checkk","onPause Uploading this chang to database (onPause)");
                            mDatabase.child("Rooms").child(roomId).child("players").setValue(players).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("checkk", "onPause change on Ready false successful");
                                }
                            });
                        }else{
                            Log.d("checkk","onPause players null hence finishing");
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("Problem","problem");
                    }
                });
            }
        });
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d("checkk","waitingActivity is destroyed");
//        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
//            @Override
//            public void onSuccess(final InstanceIdResult instanceIdResult) {
//                mDatabase.child("Rooms").child(roomId).child("game").child("players").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        GenericTypeIndicator<ArrayList<Player>> t = new GenericTypeIndicator<ArrayList<Player>>() {};
//                        ArrayList<Player>players=dataSnapshot.getValue(t);
//
//                        if(players!=null &&  players.size()>0) {
//                            for (int i = 0; i < players.size(); i++) {
//                                if (players.get(i).getDeviceToken().equals(instanceIdResult.getToken())) {
//                                    players.get(i).setActive(0);
//                                }
//                            }
//
//                            mDatabase.child("Rooms").child(roomId).child("game").child("players").setValue(players).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Log.d("checkk", "ready set to false");
//                                }
//                            });
//                        }else{
//                            Log.d("ceck","***reached here too***");
//                            finish();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Log.d("Problem","problem");
//                    }
//                });
//            }
//        });
//    }
}

//ToDo - Check after every players/room object use, if it null/empty, if null go back to previous activity and delete room from database
//Todo - Check if there are at least two people in the room when game starts (done)
//Todo - Delete Room after some time of inactivity (cant do)
//Todo - Timer for each turn
//Todo - Check if the user is ready or not
//Todo - AutoRotate Off
//Todo - Change repo from public to private
//Todo - App Crash if all leave (done hopefully)
//Todo - onStop of OnlineGamePlay
