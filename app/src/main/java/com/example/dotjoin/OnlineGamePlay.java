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
    private int noOfPlayers,boardSize,playerNo,lastUpdatedEdge;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Starting","Started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game_play);

        //Setting mDatabase to firebaseRealtimeDatabase root
        mDatabase= FirebaseDatabase.getInstance().getReference();


        //Getting RoomID
        Intent intent = getIntent();
        roomId=intent.getStringExtra("RoomId");
        playerNo=intent.getIntExtra("PlayerNo",-1);
        if(playerNo==-1){
            Log.d("playerNo","Did not get the player No");
        }

        Log.d("RoomId","roomId = "+roomId);
        Log.d("checkk","playerNo = "+playerNo);

        //Finding Layouts
        boardImage=findViewById(R.id.online_boardImage);
        rootLayout=findViewById(R.id.online_constraint);
        view =findViewById(R.id.online_view);

        //Getting ViewTreeObserver of the board Image
        ViewTreeObserver vto = boardImage.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                boardImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                layoutUtils=new LayoutUtils();

                //Getting width and height of the image view
                imageHeight = boardImage.getHeight();
                imageWidth = boardImage.getWidth();

                mDatabase.child("Rooms").child(roomId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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

                            //Starting Timer for first player
                            timer = new CountDownTimer(30000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    int progress = (int) (millisUntilFinished / 1000);
                                    progressBars.elementAt(0).setProgress(progress * progressBars.elementAt(0).getMax() / 30);
                                }

                                @Override
                                public void onFinish() {
                                    if (playerNo == activeGame.getCurrentPlayer()) {
                                        //Creating a move randomly
                                        ArrayList<Boolean> edges = board.getEdges();
                                        int edgeNo = randomEdge(edges);
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



        mDatabase.child("Rooms").child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("checkk","playerNo "+playerNo);
                activeRoom = dataSnapshot.getValue(Room.class);
                if(activeRoom!=null) {
                    activeGame = activeRoom.getGame();
                    activeBoard = activeGame.getBoard();
                    //Checking if someone has played his/her turn
                    if (activeGame.getLastEdgeUpdated() != lastUpdatedEdge) {
                        lastUpdatedEdge = activeGame.getLastEdgeUpdated();
                        int previousPlayer;
                        if (activeGame.getCurrentPlayer() == 0)
                            previousPlayer = activeGame.getNoOfPlayers() - 1;
                        else previousPlayer = activeGame.getCurrentPlayer() - 1;
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
                                if (playerNo == activeGame.getCurrentPlayer()) {
                                    //Creating a move randomly
                                    ArrayList<Boolean> edges = activeBoard.getEdges();
                                    int edgeNo = randomEdge(edges);
                                    executeYourTurn(edgeNo);
                                }
                            }
                        };
                        timer.start();

                        //Drawing the latest move
                        mainBoard.placeEdgeGivenEdgeNo(lastUpdatedEdge, OnlineGamePlay.this, rootLayout);

                        //Setting Latest Score
                        for (int i = 0; i < activeGame.getPlayers().size(); i++)
                            scoreViewVector.elementAt(i).setText(activeGame.players.get(i).getName() + " - " + activeGame.players.get(i).getScore());

                        //Coloring Boxes
                        if (activeGame.getNewBoxNodes() != null) {
                            for (int i = 0; i < activeGame.getNewBoxNodes().size(); i++)
                                activeGame.colourBox(mainBoard, activeGame.getNewBoxNodes().get(i), OnlineGamePlay.this, rootLayout);
                        }

                        //Highlighting Current Player
                        if (activeGame.getCurrentPlayer() == 0) {

                            scoreViewVector.elementAt(0).setBackgroundResource(R.drawable.border);
                            scoreViewVector.elementAt(0).setTypeface(Typeface.DEFAULT_BOLD);
                            scoreViewVector.elementAt(0).setTextColor(ContextCompat.getColor(OnlineGamePlay.this, R.color.black));

                            scoreViewVector.elementAt(activeGame.getPlayers().size() - 1).setBackgroundResource(0);
                            scoreViewVector.elementAt(activeGame.getPlayers().size() - 1).setTypeface(Typeface.DEFAULT);
                            scoreViewVector.elementAt(activeGame.getPlayers().size() - 1).setTextColor(ContextCompat.getColor(OnlineGamePlay.this, R.color.grey));

                        } else {
                            scoreViewVector.elementAt(activeGame.getCurrentPlayer()).setBackgroundResource(R.drawable.border);
                            scoreViewVector.elementAt(activeGame.getCurrentPlayer()).setTypeface(Typeface.DEFAULT_BOLD);
                            scoreViewVector.elementAt(activeGame.getCurrentPlayer()).setTextColor(ContextCompat.getColor(OnlineGamePlay.this, R.color.black));

                            scoreViewVector.elementAt(activeGame.getCurrentPlayer() - 1).setBackgroundResource(0);
                            scoreViewVector.elementAt(activeGame.getCurrentPlayer() - 1).setTypeface(Typeface.DEFAULT);
                            scoreViewVector.elementAt(activeGame.getCurrentPlayer() - 1).setTextColor(ContextCompat.getColor(OnlineGamePlay.this, R.color.grey));

                        }

                        if (activeGame.gameCompleted()) {
                            //Setting isGameStarted false
                            mDatabase.child("Rooms").child(roomId).child("isGameStarted").setValue(false);

                            //Setting ready of this player false
                            mDatabase.child("Rooms").child(roomId).child("players").child(playerNo + "").child("ready").setValue(0);

                            //Showing Final Dialog Box
                            AlertDialog.Builder builder = new AlertDialog.Builder(OnlineGamePlay.this);
                            builder.setTitle(activeGame.resultString());

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
                            textView.setText(result);
                            builder.setView(textView);

                            //Replay Button
                            builder.setPositiveButton("Replay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(OnlineGamePlay.this, WaitingPlace.class);
                                    intent.putExtra("RoomId", roomId);
                                    intent.putExtra("PlayerNo", playerNo);
                                    finish();
                                    startActivity(intent);
//                                finish();
                                }
                            });

                            //Home Button
                            builder.setNegativeButton("Home", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDatabase.child("Rooms").child(roomId).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            mDatabase.child("Rooms").child(roomId).removeEventListener(this);
                                            Room room = dataSnapshot.getValue(Room.class);
                                            players = room.getPlayers();
                                            players.remove(playerNo);
                                            if (players == null || players.size() < 1) {
                                                mDatabase.child("Rooms").child(roomId).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Intent intent = new Intent(OnlineGamePlay.this, MainActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            finish();
                                                            startActivity(intent);
                                                        } else {
                                                            Toast.makeText(OnlineGamePlay.this, "Unable to go to home page", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                room.setPlayers(players);
                                                mDatabase.child("Rooms").child(roomId).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Intent intent = new Intent(OnlineGamePlay.this, MainActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            finish();
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
                            AlertDialog alertDialog = builder.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            if (!isFinishing())
                                alertDialog.show();

                        }

                    }
                    //If its this Users turn
                    if (playerNo == activeGame.getCurrentPlayer()) {
                        view.setEnabled(true);
                        view.setOnTouchListener(new View.OnTouchListener() {
                            @SuppressLint("ClickableViewAccessibility")
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                Log.d("checkk", "touch");
                                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                    Log.d("checkk", "touch_action_down");
                                    //Getting Coordinates of Touch
                                    posX = event.getX();
                                    posY = event.getY();
//
                                    //Getting Edge No touched
                                    int edgeNo = mainBoard.EdgeNoGivenCor(posX, posY);
                                    Log.d("checkk", "edge No " + edgeNo);
                                    ArrayList<Boolean> edges = activeBoard.getEdges();
                                    if (edgeNo != -1 && !edges.get(edgeNo)) {
                                        executeYourTurn(edgeNo);
                                    }
                                }
                                return true;
                            }
                        });
                    }
                }else{
                    finish();
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
            activeGame.setLastEdgeUpdated(edgeNo);
            activeBoard.makeMoveAt(edgeNo);
            int NoOfNewBox = activeBoard.isBoxCompleted(activeGame.lastEdgeUpdated).size();
            if (NoOfNewBox == 0) {
                view.setEnabled(false);
                activeGame.nextTurn(scoreViewVector,OnlineGamePlay.this);
                activeGame.setNewBoxNodes(null);
            } else {
                ArrayList<Integer> newBoxNodes = activeBoard.isBoxCompleted(activeGame.lastEdgeUpdated);
                activeGame.setNewBoxNodes(newBoxNodes);
                activeGame.increaseScore();
            }
            mDatabase.child("Rooms").child(roomId).setValue(activeRoom).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d("write","Successful");
                    }
                    else{
                        Log.d("write","Failed");
                    }
                }
            });
    }
}
