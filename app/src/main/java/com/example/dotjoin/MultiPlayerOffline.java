package com.example.dotjoin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

public class MultiPlayerOffline extends AppCompatActivity {

    private ImageView boardImage;
    public float imageHeight,imageWidth;
    private ConstraintLayout rootLayout;
    private Board board;
    private Game game;
    private View view;
    private float posX,posY;
    private TextView currentPlayerName;
    private int noOfPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_offline);

        //Finding Layouts
        boardImage=findViewById(R.id.boardImage);
        rootLayout=findViewById(R.id.constraint);
        view =findViewById(R.id.view);

        //Getting ViewTreeObserver of the board Image
        ViewTreeObserver vto = boardImage.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                boardImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                //Dialog Box that accepts no. of players
                CharSequence[] options = new CharSequence[]{"2","3","4"};

                final AlertDialog.Builder noOfUsersDialog = new AlertDialog.Builder(MultiPlayerOffline.this);
                noOfUsersDialog.setTitle("Number of Players");
                noOfUsersDialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0)noOfPlayers=2;
                        else if(which==1)noOfPlayers=3;
                        else if(which==2)noOfPlayers=4;

                        currentPlayerName=findViewById(R.id.current_player_name);

                        imageHeight=boardImage.getHeight();
                        imageWidth=boardImage.getWidth();
                        board = new Board(4,4,100,100+((imageHeight-imageWidth)/2),imageWidth,imageWidth/128,imageWidth/128);

                        Vector<String>playerNames=new Vector<String>();
                        for(int i=0;i<noOfPlayers;i++){
                            playerNames.add("Player "+(i+1));
                        }
                        game =new Game(0,noOfPlayers,playerNames,board);
                        currentPlayerName.setText(game.namesOfPlayers.get(game.currentPlayer)+"'s Turn");

                        view.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if(event.getAction()==MotionEvent.ACTION_DOWN) {

                                    posX = event.getX();
                                    posY = event.getY();

                                    int edgeNo = game.board.EdgeNoGivenCor(posX, posY);
                                    boolean[] edges = board.getEdgesArray();

                                    if(edgeNo!=-1 && !edges[edgeNo]) {
                                        game.setLastEdgeUpdated(edgeNo);
                                        game.board.makeMoveAt(edgeNo);
                                        game.board.placeEdgeGivenEdgeNo(game.lastEdgeUpdated, getApplicationContext(), rootLayout);

                                        int NoOfNewBox = game.board.isBoxCompleted(game.lastEdgeUpdated).size();
                                        if (NoOfNewBox == 0) {
                                            game.nextTurn();
                                        } else {
                                            Vector<Integer> newBoxNodes = game.board.isBoxCompleted(game.lastEdgeUpdated);
                                            for(int i=0;i<NoOfNewBox;i++){
                                                game.colourBox(newBoxNodes.get(i), getApplicationContext(), rootLayout);
                                            }
                                            game.increaseScore();
                                        }

                                        currentPlayerName.setText(game.namesOfPlayers.get(game.currentPlayer)+"'s turn");
                                        if (game.isGameCompleted()) {

                                            //Showing Final Dialog Box
                                            AlertDialog.Builder builder = new AlertDialog.Builder(MultiPlayerOffline.this);
                                            builder.setTitle("Game Over");

                                            //Showing final ScoreBoard
                                            TextView textView = new TextView(getApplicationContext());
                                            textView.setPadding(60,50,20,40);
                                            textView.setLineSpacing(1.5f,1.5f);
                                            textView.setTextSize(16);
                                            String result="";
                                            for(int i=0;i<noOfPlayers;i++){
                                                if(i==noOfPlayers-1){
                                                    result=result+game.namesOfPlayers.get(i)+" - "+game.scoreBoard.get(i);
                                                }
                                                else {
                                                    result = result + game.namesOfPlayers.get(i) + " - " + game.scoreBoard.get(i) + "\n";
                                                }
                                            }
                                            textView.setText(result);
                                            builder.setView(textView);

                                            //Replay Button
                                            builder.setPositiveButton("Replay", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(MultiPlayerOffline.this,MultiPlayerOffline.class);
                                                    startActivity(intent);
                                                }
                                            });

                                            //Home Button
                                            builder.setNegativeButton("Home", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(MultiPlayerOffline.this,MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                            builder.show();
                                        }
                                    }
                                    Log.d("pos", "current player " + game.currentPlayer + "\n");
                                }
                                return true;
                            }
                        });
                    }
                });
                AlertDialog alertDialog = noOfUsersDialog.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });
    }
}
