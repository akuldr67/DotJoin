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

    private View view;
    private ImageView imageView;
    private ImageView imageView2;
    private float posX,posY;
    private int a,b,c,d;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_offline);


        imageView=findViewById(R.id.imagee);
        constraintLayout=findViewById(R.id.constraint);





        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                a = imageView.getMeasuredHeight();
                b = imageView.getMeasuredWidth();
                c = imageView.getHeight();
                d = imageView.getWidth();
                int h = (c-d)/2;

                imageView2=new ImageView(getApplicationContext());
                imageView2.setImageResource(R.drawable.line);
                imageView2.setX(100);
                imageView2.setY(100+h);
                imageView2.setImageAlpha(70);
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(d,d);
                constraintLayout.addView(imageView2,params);
                return true;
            }
        });
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
