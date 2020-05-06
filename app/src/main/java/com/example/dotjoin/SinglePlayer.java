package com.example.dotjoin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

public class SinglePlayer extends AppCompatActivity {

    private ImageView boardImage;
    public float imageHeight,imageWidth;
    private ConstraintLayout rootLayout;
    private Board board;
    private Game game;
    private View view;
    private float posX,posY;
    private int difficultyLevel,boardSize;
    private LayoutUtils layoutUtils;
    private Vector<TextView> scoreViewVector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

//        //Finding Layouts
//        boardImage=findViewById(R.id.boardImage);
//        rootLayout=findViewById(R.id.constraint);
//        view =findViewById(R.id.view);
//
//        //Getting ViewTreeObserver of the board Image
//        ViewTreeObserver vto = boardImage.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//
//                boardImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                layoutUtils=new LayoutUtils();
//
//                //Dialog Box that accepts no. of players
//                String[] options = new String[]{"Easy","Hard"};
//
//                final AlertDialog.Builder noOfUsersDialog = new AlertDialog.Builder(SinglePlayer.this);
//                noOfUsersDialog.setTitle("Select difficulty level");
//                noOfUsersDialog.setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == 0) difficultyLevel = 1;
//                        else if (which == 1) difficultyLevel = 2;
//
//                        //Dialog Box that accepts board size
//                        CharSequence[] sizeOptions = new CharSequence[]{"3*3 ", "4*4", "5*5", "6*6", "7*7"};
//
//                        AlertDialog.Builder sizeDialog = new AlertDialog.Builder(SinglePlayer.this);
//                        sizeDialog.setTitle("Size");
//                        sizeDialog.setItems(sizeOptions, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (which == 0) boardSize = 4;
//                                else if (which == 1) boardSize = 5;
//                                else if (which == 2) boardSize = 6;
//                                else if (which == 3) boardSize = 7;
//                                else if (which == 4) boardSize = 8;
//
//                                scoreViewVector=new Vector<TextView>();
//                                scoreViewVector.setSize(2);
//
//                                //Getting width and height of the image view
//                                imageHeight = boardImage.getHeight();
//                                imageWidth = boardImage.getWidth();
//
//                                //Initializing Board, Game, and layoutUtils
//                                layoutUtils.drawBoard(boardSize, boardSize, SinglePlayer.this, rootLayout, imageWidth, 100, 100 + ((imageHeight - imageWidth) / 2));
//                                board = new Board(boardSize, boardSize, 100, 100 + ((imageHeight - imageWidth) / 2), imageWidth);
//                                game = new Game(0, 2,  board);
//
//                                game.players.elementAt(0).setName("You");
//                                game.players.elementAt(1).setName("Computer");
//
//                                //Setting params for corner text Views that display score
//                                scoreViewVector.set(0,(TextView) findViewById(R.id.player1));
//                                scoreViewVector.elementAt(0).setText(game.players.elementAt(0).getName()+" - "+game.players.elementAt(0).getScore());
//                                scoreViewVector.set(1,(TextView) findViewById(R.id.computer_player));
//                                scoreViewVector.elementAt(1).setText(game.players.elementAt(1).getName()+" - "+game.players.elementAt(1).getScore());
//
//
//                                //Highlighting the first Player TextView
//                                scoreViewVector.elementAt(0).setBackgroundResource(R.drawable.border);
//                                scoreViewVector.elementAt(0).setTypeface(Typeface.DEFAULT_BOLD);
//                                scoreViewVector.elementAt(0).setTextColor(ContextCompat.getColor(SinglePlayer.this,R.color.black));
//
//                                //Touch Listener
//                                view.setOnTouchListener(new View.OnTouchListener() {
//                                    @Override
//                                    public boolean onTouch(View v, MotionEvent event) {
//                                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//
//                                            //Getting Coordinates of Touch
//                                            posX = event.getX();
//                                            posY = event.getY();
//
//                                            //Getting Edge No touched
//                                            int edgeNo = game.board.EdgeNoGivenCor(posX, posY);
//                                            Vector<Boolean> edges = board.getEdges();
//                                            if (edgeNo != -1 && !edges.get(edgeNo)) {
//                                                game.setLastEdgeUpdated(edgeNo);
//                                                game.board.makeMoveAt(edgeNo);
//                                                game.board.placeEdgeGivenEdgeNo(game.lastEdgeUpdated, getApplicationContext(), rootLayout);
//
//                                                int NoOfNewBox = game.board.isBoxCompleted(game.lastEdgeUpdated).size();
//                                                if (NoOfNewBox == 0) {
//                                                    game.nextTurn(scoreViewVector,SinglePlayer.this);
//                                                } else {
//                                                    Vector<Integer> newBoxNodes = game.board.isBoxCompleted(game.lastEdgeUpdated);
//                                                    for (int i = 0; i < NoOfNewBox; i++) {
//                                                        game.colourBox(newBoxNodes.get(i), getApplicationContext(), rootLayout);
//                                                    }
//                                                    game.increaseScore();
//                                                }
//                                                scoreViewVector.elementAt(game.getCurrentPlayer()).setText(game.players.elementAt(game.getCurrentPlayer()).getName()+" - "+game.players.elementAt(game.getCurrentPlayer()).getScore());
//
//                                                if (game.isGameCompleted()) {
//                                                    endGame();
//                                                }
//
//                                                Log.d("current "," player no "+game.currentPlayer);
//
////                                                int computerContinuousTurn = 0;
//                                                while(game.currentPlayer == 1){
////                                                    if(computerContinuousTurn > 0){
////                                                        try{
////                                                            Log.d("Currently ","Sleeping!!!!");
////                                                            Thread.sleep(1000);
////                                                        }catch (Exception e){
////                                                            e.printStackTrace();
////                                                        }
////                                                        new Handler().postDelayed(new Runnable() {
////                                                            @Override
////                                                            public void run() {
////                                                                Log.d("Currently ","Sleeping!!!!");
////                                                            }
////                                                        }, 1000);
////                                                    }
//                                                    BoardHelperForAI b = new BoardHelperForAI(game.board);
//                                                    if(difficultyLevel == 1)
//                                                        edgeNo = b.giveNextEdgeNoEasy();
//                                                    else
//                                                        edgeNo = b.giveNextEdgeNoHard();
//                                                    game.setLastEdgeUpdated(edgeNo);
//                                                    game.board.makeMoveAt(edgeNo);
//                                                    game.board.placeEdgeGivenEdgeNo(game.lastEdgeUpdated, getApplicationContext(), rootLayout);
//
//                                                    int NoOfNewBoxComp = game.board.isBoxCompleted(game.lastEdgeUpdated).size();
//                                                    if (NoOfNewBoxComp == 0) {
//                                                        game.nextTurn(scoreViewVector,SinglePlayer.this);
//                                                    } else {
//                                                        Vector<Integer> newBoxNodes = game.board.isBoxCompleted(game.lastEdgeUpdated);
//                                                        for (int i = 0; i < NoOfNewBoxComp; i++) {
//                                                            game.colourBox(newBoxNodes.get(i), getApplicationContext(), rootLayout);
//                                                        }
//                                                        game.increaseScore();
//                                                    }
//                                                    scoreViewVector.elementAt(game.getCurrentPlayer()).setText(game.players.elementAt(game.getCurrentPlayer()).getName()+" - "+game.players.elementAt(game.getCurrentPlayer()).getScore());
//
////                                                    computerContinuousTurn++;
//                                                    if (game.isGameCompleted()) {
//                                                        endGame();
//                                                    }
//                                                }
//                                            }
//                                            Log.d("pos", "current player " + game.currentPlayer + "\n");
//                                        }
//                                        return true;
//                                    }
//                                });
//                            }
//                        });
//                        AlertDialog alertDialog=sizeDialog.create();
//                        alertDialog.setCanceledOnTouchOutside(false);
//                        alertDialog.show();
//                    }
//                });
//                AlertDialog alertDialog = noOfUsersDialog.create();
//                alertDialog.setCanceledOnTouchOutside(false);
//                alertDialog.show();
//            }
//        });
    }

//    protected void endGame(){
//
//        //Showing Final Dialog Box
//        AlertDialog.Builder builder = new AlertDialog.Builder(SinglePlayer.this);
//        builder.setTitle(game.getResult());
//
//        //Showing final ScoreBoard
//        TextView textView = new TextView(getApplicationContext());
//        textView.setPadding(60, 50, 20, 40);
//        textView.setLineSpacing(1.5f, 1.5f);
//        textView.setTextSize(16);
//        String result = "";
//        for (int i = 0; i < 2; i++) {
//            if (i == 1) {
//                result = result + game.players.elementAt(i).getName() + " - " + game.players.elementAt(i).getScore();
//            } else {
//                result = result + game.players.elementAt(i).getName() + " - " + game.players.elementAt(i).getScore() + "\n";
//            }
//        }
//        textView.setText(result);
//        builder.setView(textView);
//
//        //Replay Button
//        builder.setPositiveButton("Replay", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(SinglePlayer.this, SinglePlayer.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        //Home Button
//        builder.setNegativeButton("Home", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(SinglePlayer.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//        AlertDialog alertDialog=builder.create();
//        alertDialog.setCanceledOnTouchOutside(false);
//        alertDialog.show();
//    }
}

//ToDo: sleep (if required later) computer for some time in continuous manner after every consecutive turn of computer, not in one go
