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

//    private View tryView;
    DemoView demoView;

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

//        tryView = new View(this);
        demoView = new DemoView(this);

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

//                Paint paint = new Paint();

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

                        final float boxLength = board.getBoxLength();

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

                                        int NoOfnewBox = game.board.isBoxCompleted(game.lastEdgeUpdated).size();
                                        if (NoOfnewBox == 0) {
                                            game.nextTurn();
                                        } else {
                                            Vector<Integer> newBoxNodes = game.board.isBoxCompleted(game.lastEdgeUpdated);
                                            for(int i=0;i<NoOfnewBox;i++){
                                                float[] cor = game.board.FindCoordinatesOfNode(newBoxNodes.get(i));
                                                if(cor!=null) {
                                                    Rect r = new Rect((int)cor[0], (int)cor[1], (int)(cor[0]+boxLength), (int)(cor[1]+boxLength));
//                                                    @Override
//                                                    view.onDrawForeground(Canvas canvas);
                                                    demoView.drawRect(r);
//                                                    demoView.invalidate();
                                                }
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

//    @Override
//    protected  void onDraw(Canvas canvas)

    private class DemoView extends View{
        public DemoView(Context context){
            super(context);
//            Canvas canvas = super(canvas);
            setWillNotDraw(false);
        }

        public Rect square = new Rect(10, 10, 200, 100);

        public void drawRect(Rect r){
//            this.onDraw(canvas);
//            this.invalidate();
            Log.d("reached"," here !!!!!!");
            this.square = r;
            this.invalidate();
        }

//        @Override protected void onDraw(Canvas canvas){
        @Override protected void dispatchDraw(Canvas canvas){
//            super.onDraw(canvas);
            super.dispatchDraw(canvas);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLUE);
//            Rect r = new Rect(10, 10, 200, 100);
            canvas.drawRect(this.square,paint);
            Log.d("reached"," here tooooo !!!!!!");  //not reaching here!!! :(
        }
    }
}
