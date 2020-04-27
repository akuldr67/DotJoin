package com.example.dotjoin;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MultiPlayerOffline extends AppCompatActivity {

    private View view;
    private ImageView imageView;
    private float posX,posY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_offline);

        view =findViewById(R.id.view);
        imageView=findViewById(R.id.imagee);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                posX=event.getX();
                posY=event.getY();
                Log.d("cor","x = "+posX);
                Log.d("cor","y = "+posY);
                imageView.layout((int)posX,(int)posY,(int)posX+300,(int)posY+300);
                imageView.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }
}
