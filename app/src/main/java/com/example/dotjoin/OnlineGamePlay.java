package com.example.dotjoin;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Vector;

public class OnlineGamePlay extends AppCompatActivity {

    private ImageView boardImage;
    public float imageHeight,imageWidth;
    private ConstraintLayout rootLayout;
    private Board board;
    private Game game;
    private View view;
    private float posX,posY;
    private int noOfPlayers,boardSize,playerNo,lastUpdatedEdge,noOfChancesMissed;
    private LayoutUtils layoutUtils;
    private Vector<TextView> scoreViewVector;
    private Vector<ProgressBar> progressBars;
    private DatabaseReference mDatabase;
    private String roomId;
    private Room activeRoom;
    private Game activeGame;
    private Board activeBoard,mainBoard;
    private ArrayList<Player>players;
    private CountDownTimer timer;
    private AlertDialog alertDialog;
    private ValueEventListener mainValueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Starting","Started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game_play);

        Log.d("checkk","onCreate OnlineGamePlay started");

        //Initializing noOfChancesMissed
        noOfChancesMissed=0;

        //Setting mDatabase to firebaseRealtimeDatabase root
        mDatabase= FirebaseDatabase.getInstance().getReference();

        //Getting RoomID
        Intent intent = getIntent();
        roomId=intent.getStringExtra("RoomId");
        playerNo=intent.getIntExtra("PlayerNo",-1);
        if(playerNo==-1){
            Log.d("playerNo","Did not get the player No");
        }

        Log.d("checkk","Starting EndService");
        //Starting Service that would handle if user removes app from recents
        Intent exitingIntent=new Intent(this,EndService.class);
        exitingIntent.putExtra("RoomId",roomId);
        exitingIntent.putExtra("PlayerNo",playerNo);
        startService(exitingIntent);

        //Finding Layouts
        boardImage=findViewById(R.id.online_boardImage);
        rootLayout=findViewById(R.id.online_constraint);
        view =findViewById(R.id.online_view);

        //Getting ViewTreeObserver of the board Image
        ViewTreeObserver vto = boardImage.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("checkk","Entered Global Layout Listener");
                boardImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                layoutUtils=new LayoutUtils();

                //Getting width and height of the image view
                imageHeight = boardImage.getHeight();
                imageWidth = boardImage.getWidth();

                mDatabase.child("Rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("checkk","entered 1st singleValueEventListener in OnlineGamePlay");

                        mDatabase.child("Rooms").child(roomId).removeEventListener(this);

                        //Setting Ready for all as false
                        mDatabase.child("Rooms").child(roomId).child("players").child(playerNo + "").child("ready").setValue(0);
                        //Host is getting ready even after this

                        //Initializing Board, Game, and layoutUtils
                        Room room = dataSnapshot.getValue(Room.class);
                        if (room != null) {
                            final Game game = room.getGame();
                            final Board board = game.getBoard();
                            boardSize = board.getRows();
                            layoutUtils.drawBoard(boardSize, boardSize, OnlineGamePlay.this, rootLayout, imageWidth, 100, 100 + ((imageHeight - imageWidth) / 2));
                            mainBoard = new Board(boardSize, boardSize, 100, 100 + ((imageHeight - imageWidth) / 2), imageWidth);

                            //Getting Last updated Edge
                            lastUpdatedEdge = game.getLastEdgeUpdated();

                            //setting noOfPlayers
                            noOfPlayers = game.getNoOfPlayers();

                            //Setting params for corner text Views that display score
                            scoreViewVector = new Vector<TextView>();
                            scoreViewVector.setSize(noOfPlayers);

                            //and progress bars
                            progressBars = new Vector<>();
                            progressBars.setSize(noOfPlayers);

                            for (int i = 0; i < noOfPlayers; i++) {
                                if (i == 0) {
                                    scoreViewVector.set(i, (TextView) findViewById(R.id.online_player1));
                                    progressBars.set(i, (ProgressBar) findViewById(R.id.online_bar_player1));
                                } else if (i == 1) {
                                    scoreViewVector.set(i, (TextView) findViewById(R.id.online_player2));
                                    progressBars.set(i, (ProgressBar) findViewById(R.id.online_bar_player2));
                                } else if (i == 2) {
                                    scoreViewVector.set(i, (TextView) findViewById(R.id.online_player3));
                                    progressBars.set(i, (ProgressBar) findViewById(R.id.online_bar_player3));
                                } else if (i == 3) {
                                    scoreViewVector.set(i, (TextView) findViewById(R.id.online_player4));
                                    progressBars.set(i, (ProgressBar) findViewById(R.id.online_bar_player4));
                                }
                                progressBars.elementAt(i).setVisibility(View.VISIBLE);
                                scoreViewVector.elementAt(i).setText(game.players.get(i).getName() + " - " + game.players.get(i).getScore());
                            }

                            //Highlighting the first Player TextView
                            scoreViewVector.elementAt(0).setBackgroundResource(R.drawable.border);
                            scoreViewVector.elementAt(0).setTypeface(Typeface.DEFAULT_BOLD);
                            scoreViewVector.elementAt(0).setTextColor(ContextCompat.getColor(OnlineGamePlay.this, R.color.black));

                            Log.d("checkk","starting timer for first turn");
                            //Starting Timer for first player
                            timer = new CountDownTimer(30000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    int progress = (int) (millisUntilFinished / 1000);
                                    progressBars.elementAt(0).setProgress(progress * progressBars.elementAt(0).getMax() / 30);
                                }

                                @Override
                                public void onFinish() {
                                    Log.d("checkk","timer for first turn finished");
                                    if (playerNo == activeGame.getCurrentPlayer()) {
                                        noOfChancesMissed++;
                                        //Creating a move randomly
                                        ArrayList<Boolean> edges = board.getEdges();
                                        int edgeNo = randomEdge(edges);
                                        Log.d("checkk","Randomly making a move for the first time");
                                        executeYourTurn(edgeNo);
                                    }

                                }
                            };
                            timer.start();
                        }
                        else{
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



        mainValueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("checkk","started 2nd main value event listener in OnlineGamePlay");
                activeRoom = dataSnapshot.getValue(Room.class);
                Log.d("checkk","Checking if room is null");
                if(activeRoom!=null) {
                    Log.d("checkk","Room was not null");
                    activeGame = activeRoom.getGame();
                    activeBoard = activeGame.getBoard();
                    //Here we need to update if someone has left
                    int activePlayers=0;
                    for(int i=0;i<activeGame.getNoOfPlayers();i++){
                        if(activeGame.getPlayers().get(i).getActive()==1) {
                            activePlayers++;
                        }
                        else {
                            scoreViewVector.elementAt(i).setBackgroundResource(R.drawable.inactive_border);
                        }
                    }
                    Log.d("checkk","counted no of active players");
                    if(activePlayers<2){
                        Log.d("checkk","started exiting process because active players where not enough");
                        endGame();
                    }

                    //Checking if someone has played his/her turn
                    if (activeGame.getLastEdgeUpdated() != lastUpdatedEdge) {
                        Log.d("checkk","lastEdge Update detected");
                        lastUpdatedEdge = activeGame.getLastEdgeUpdated();
                        int previousPlayer=previousActiveUser(activeGame.getCurrentPlayer());
                        Log.d("checkk","cancelling the last timer");
                        progressBars.elementAt(previousPlayer).setProgress(progressBars.elementAt(playerNo).getMax());
                        timer.cancel();
                        //Starting Timer
                        timer = new CountDownTimer(30000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                int progress = (int) (millisUntilFinished / 1000);
                                progressBars.elementAt(activeGame.getCurrentPlayer()).setProgress(progress * progressBars.elementAt(activeGame.getCurrentPlayer()).getMax() / 30);
                            }

                            @Override
                            public void onFinish() {
                                Log.d("checkk","timer finished");
                                if (playerNo == activeGame.getCurrentPlayer()) {
                                    //Creating a move randomly
                                    noOfChancesMissed++;
                                    if (noOfChancesMissed > 2) {
                                        Log.d("checkk","making player inactive because he skipped 3 chances");
                                        activeGame.getPlayers().get(activeGame.getCurrentPlayer()).setActive(0);
                                        mDatabase.child("Rooms").child(roomId).child("game").child("players").child(activeGame.getCurrentPlayer() + "").child("active").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("checkk","Finishing the game for the player who skipped 3 chances");
                                                finish();
                                            }
                                        });
                                    } else {
                                        Log.d("checkk","Randomly making a move");
                                        ArrayList<Boolean> edges = activeBoard.getEdges();
                                        int edgeNo = randomEdge(edges);
                                        executeYourTurn(edgeNo);
                                    }
                                }
                            }
                        };
                        Log.d("checkk","Starting timer");
                        timer.start();

                        Log.d("checkk","Drawing edge on the board");
                        //Drawing the latest move
                        mainBoard.placeEdgeGivenEdgeNo(lastUpdatedEdge, OnlineGamePlay.this, rootLayout);

                        Log.d("checkk","Updating to the lates score");
                        //Setting Latest Score
                        for (int i = 0; i < activeGame.getPlayers().size(); i++)
                            scoreViewVector.elementAt(i).setText(activeGame.players.get(i).getName() + " - " + activeGame.players.get(i).getScore());

                        Log.d("checkk","Coloring the boxes");
                        //Coloring Boxes
                        if (activeGame.getNewBoxNodes() != null) {
                            for (int i = 0; i < activeGame.getNewBoxNodes().size(); i++)
                                activeGame.colourBox(mainBoard, activeGame.getNewBoxNodes().get(i), OnlineGamePlay.this, rootLayout);
                        }

                        int previousActivePlayer=previousActiveUser(activeGame.getCurrentPlayer());

                        Log.d("checkk","highlighting current player");
                        scoreViewVector.elementAt(activeGame.getCurrentPlayer()).setBackgroundResource(R.drawable.border);
                        scoreViewVector.elementAt(activeGame.getCurrentPlayer()).setTypeface(Typeface.DEFAULT_BOLD);
                        scoreViewVector.elementAt(activeGame.getCurrentPlayer()).setTextColor(ContextCompat.getColor(OnlineGamePlay.this, R.color.black));

                        Log.d("checkk","unHighlighting previous player");
                        scoreViewVector.elementAt(previousActivePlayer).setBackgroundResource(0);
                        scoreViewVector.elementAt(previousActivePlayer).setTypeface(Typeface.DEFAULT);
                        scoreViewVector.elementAt(previousActivePlayer).setTextColor(ContextCompat.getColor(OnlineGamePlay.this, R.color.grey));

                        if (activeGame.gameCompleted()) {
                            Log.d("checkk","Ending the game because all boxes are completed");
                           endGame();
                        }

                    }
                    //If its this Users turn
                    if (playerNo == activeGame.getCurrentPlayer()) {
                        view.setEnabled(true);
                        view.setOnTouchListener(new View.OnTouchListener() {
                            @SuppressLint("ClickableViewAccessibility")
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                    Log.d("checkk", "user touch detected");
                                    //Getting Coordinates of Touch
                                    posX = event.getX();
                                    posY = event.getY();
//
                                    //Getting Edge No touched
                                    int edgeNo = mainBoard.EdgeNoGivenCor(posX, posY);
                                    Log.d("checkk", "Got the edge no where user clicked");
                                    ArrayList<Boolean> edges = activeBoard.getEdges();
                                    if (edgeNo != -1 && !edges.get(edgeNo)) {
                                        Log.d("checkk","Executing turn for the edge no where user clicked");
                                        executeYourTurn(edgeNo);
                                    }
                                }
                                return true;
                            }
                        });
                    }
                }else{
                    Log.d("checkk","Room was null, therefore finishing");
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
                mDatabase.child("Rooms").child(roomId).addValueEventListener(mainValueEventListener);

            }
        });

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("checkk","onDestroy Called");
        if(alertDialog!=null){
            Log.d("checkk","dialog box was found not null hence dismissing the dialog box");
            alertDialog.dismiss();
            alertDialog=null;
        }
    }

    private int randomEdge(ArrayList<Boolean>edges){
        Vector<Integer> possibleNos=new Vector<>();
        for(int i=1;i<edges.size();i++){
            if(!edges.get(i)){
                possibleNos.add(i);
            }
        }
        return possibleNos.get((int)(possibleNos.size()*Math.random()));
    }

    private void executeYourTurn(int edgeNo){
            Log.d("checkk","Updating last edge updated");
            activeGame.setLastEdgeUpdated(edgeNo);
            Log.d("checkk","Setting that edge true");
            activeBoard.makeMoveAt(edgeNo);
            int NoOfNewBox = activeBoard.isBoxCompleted(activeGame.lastEdgeUpdated).size();
            if (NoOfNewBox == 0) {
                Log.d("checkk","no new boxes made");
                view.setEnabled(false);
                Log.d("checkk","Finding the next active player");
                activeGame.nextTurn();
                while(activeGame.getPlayers().get(activeGame.getCurrentPlayer()).getActive()==0){
                    activeGame.nextTurn();
                }

                activeGame.setNewBoxNodes(null);
            } else {
                Log.d("checkk","new boxed made");
                ArrayList<Integer> newBoxNodes = activeBoard.isBoxCompleted(activeGame.lastEdgeUpdated);
                activeGame.setNewBoxNodes(newBoxNodes);
                Log.d("checkk","updating the score");
                activeGame.increaseScore();
            }
        Log.d("checkk","Writing all these updates to the server");
            mDatabase.child("Rooms").child(roomId).setValue(activeRoom).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d("checkk","updated server successfully");
                    }
                    else{
                        Log.d("checkk","updating server failed");
                    }
                }
            });
    }

    private void endGame(){
        Log.d("checkk","Removing MainValueEventListener");
        //Removing MainValueEventListener
        mDatabase.child("Rooms").child(roomId).removeEventListener(mainValueEventListener);

        Log.d("checkk","cancelling timer");
        //Cancelling Timer
        timer.cancel();

        Log.d("checkk","setting isGameStarted false");
        //Setting isGameStarted false
        mDatabase.child("Rooms").child(roomId).child("isGameStarted").setValue(false);

        Log.d("checkk","Setting ready false");
        //Setting ready of this player false
        mDatabase.child("Rooms").child(roomId).child("players").child(playerNo + "").child("ready").setValue(0);

        Log.d("checkk","Started creating alertDialog Builder");
        //Showing Final Dialog Box
        AlertDialog.Builder builder = new AlertDialog.Builder(OnlineGamePlay.this);
        builder.setTitle(activeGame.resultString());

        Log.d("checkk","Creating Text view for dialog");
        //Showing final ScoreBoard
        TextView textView = new TextView(getApplicationContext());
        textView.setPadding(60, 50, 20, 40);
        textView.setLineSpacing(1.5f, 1.5f);
        textView.setTextSize(16);
        String result = "";
        for (int i = 0; i < noOfPlayers; i++) {
            if (i == noOfPlayers - 1) {
                result = result + activeGame.players.get(i).getName() + " - " + activeGame.players.get(i).getScore();
            } else {
                result = result + activeGame.players.get(i).getName() + " - " + activeGame.players.get(i).getScore() + "\n";
            }
        }
        Log.d("checkk","Setting result in the text view");
        textView.setText(result);
        builder.setView(textView);

        //Replay Button
        builder.setPositiveButton("Replay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("checkk","Click on Replay detected");
                Intent intent = new Intent(OnlineGamePlay.this, WaitingPlace.class);
                intent.putExtra("RoomId", roomId);
                intent.putExtra("PlayerNo", playerNo);
//                finish();
                Log.d("checkk","Starting Waiting Activity");
                startActivity(intent);
                Log.d("checkk","Finishing OnlineGamePlay Activity");
                finish();
            }
        });

        //Home Button
        builder.setNegativeButton("Home", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("checkk","Click on Home Detected");
                mDatabase.child("Rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mDatabase.child("Rooms").child(roomId).removeEventListener(this);
                        Room room = dataSnapshot.getValue(Room.class);
                        Log.d("checkk","Loaded room");
                        players = room.getPlayers();
                        Log.d("checkk","Removing player from room");
                        players.remove(playerNo);
                        if (players == null || players.size() < 1) {
                            Log.d("checkk","All players gone");
                            Log.d("checkk","Deleting room");
                            mDatabase.child("Rooms").child(roomId).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(OnlineGamePlay.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        Log.d("checkk","finishing OnlineGamePlay");
                                        finish();
                                        Log.d("checkk","Starting Main Activity");
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(OnlineGamePlay.this, "Unable to go to home page", Toast.LENGTH_SHORT).show();
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
                                        Intent intent = new Intent(OnlineGamePlay.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        Log.d("checkk","finishing OnlineGamePlay");
                                        finish();
                                        Log.d("checkk","Starting intent for MainActivity");
                                        startActivity(intent);
//                                                    finish();
                                    } else {
                                        Toast.makeText(OnlineGamePlay.this, "Unable to go to home page", Toast.LENGTH_SHORT).show();
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
        Log.d("checkk","Creating alert Dialog Box from Builder");
        alertDialog = builder.create();
        Log.d("checkk","Setting Outside touch false");
        alertDialog.setCanceledOnTouchOutside(false);
        Log.d("checkk","Checking if activity is not finished");
        if (!isFinishing())
            Log.d("checkk","Finally showing the dialog box");
            alertDialog.show();

    }

    private int previousActiveUser(int playerNo){
        int ans=activeGame.previousPlayer(playerNo);
        while(activeGame.getPlayers().get(ans).getActive()==0){
            ans=activeGame.previousPlayer(ans);
        }
        return ans;
    }

}
