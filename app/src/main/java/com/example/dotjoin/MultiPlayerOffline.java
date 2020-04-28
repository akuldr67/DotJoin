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
    private ViewTreeObserver.OnPreDrawListener onPreDrawListener;
    private Board board;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_offline);

        //Finding Layouts
        boardImage=findViewById(R.id.boardImage);
        rootLayout=findViewById(R.id.constraint);

        //Getting ViewTreeObserver of the board Image
        ViewTreeObserver vto = boardImage.getViewTreeObserver();
        onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageHeight=boardImage.getHeight();
                imageWidth=boardImage.getWidth();
                board = new Board(3,3,100,100+((imageHeight-imageWidth)/2),imageWidth,0,0);
                board.placeEdgeGivenEdgeNo(5,getApplicationContext());
                Log.d("size",imageWidth+"");
                return true;
            }
        };
        vto.addOnPreDrawListener(onPreDrawListener);
//        view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                posX=event.getX();
//                posY=event.getY();
//                Log.d("cor","x = "+posX);
//                Log.d("cor","y = "+posY);
//                return false;
//            }
//        });
    }

}
