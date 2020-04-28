package com.example.dotjoin;

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

public class MultiPlayerOffline extends AppCompatActivity {

    private ImageView boardImage;
    public float imageHeight,imageWidth;
    private ConstraintLayout rootLayout;
    private Board board;
    private View view;
    private float posX,posY;

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
                boardImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                imageHeight=boardImage.getHeight();
                imageWidth=boardImage.getWidth();
                board = new Board(4,4,100,100+((imageHeight-imageWidth)/2),imageWidth,imageWidth/128,imageWidth/128);
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        posX=event.getX();
                        posY=event.getY();
                        int edgeNo=board.EdgeNoGivenCor(posX,posY);
                        Log.d("cor","edge no "+edgeNo);
                        board.placeEdgeGivenEdgeNo(edgeNo,getApplicationContext(),rootLayout);
                        Log.d("cor","x = "+posX);
                        Log.d("cor","y = "+posY);
                        return true;
                    }
                });

                Log.d("size",imageWidth+"");
            }
        });


    }

}
