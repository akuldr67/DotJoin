package com.example.dotjoin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_offline);

        //Finding Layouts
        boardImage=findViewById(R.id.boardImage);
        rootLayout=findViewById(R.id.constraint);
        view =findViewById(R.id.view);


        Log.d("size",boardImage.getMeasuredHeight()+"");
        //Getting ViewTreeObserver of the board Image
        ViewTreeObserver vto = boardImage.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                currentPlayerName=findViewById(R.id.current_player_name);

                boardImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                imageHeight=boardImage.getHeight();
                imageWidth=boardImage.getWidth();
                board = new Board(4,4,100,100+((imageHeight-imageWidth)/2),imageWidth,imageWidth/128,imageWidth/128);

                Vector<String>playerNames=new Vector<String>();
                playerNames.add("Akul");
                playerNames.add("Arpit");

                game =new Game(0,2,playerNames,board);
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

                                Log.d("pos", "Boxes made " + game.board.isBoxCompleted(game.lastEdgeUpdated).size());

                                if (game.board.isBoxCompleted(game.lastEdgeUpdated).size() == 0) {
                                    Log.d("pos", "Taking next turn");
                                    game.nextTurn();
                                } else {
                                    game.increaseScore();
                                }

                                currentPlayerName.setText(game.namesOfPlayers.get(game.currentPlayer));
                                if (game.isGameCompleted()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MultiPlayerOffline.this);
                                    builder.setTitle("Game Over");
                                    TextView textView = new TextView(getApplicationContext());
                                    textView.setText(game.namesOfPlayers.get(0) + " - " + game.scoreBoard.get(0) + "\n" + game.namesOfPlayers.get(1) + " - " + game.scoreBoard.get(1));
                                    builder.setView(textView);
                                    builder.show();
                                }
                            }
                            Log.d("pos", "current player " + game.currentPlayer + "\n");
                        }
                        return true;
                    }
                });

                Log.d("size",imageWidth+"");
            }
        });


    }

}
