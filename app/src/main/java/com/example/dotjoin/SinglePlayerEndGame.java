package com.example.dotjoin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SinglePlayerEndGame extends AppCompatActivity {
    private TextView Heading,Result;
    private String activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_end_game);
        Intent intent = getIntent();
        String heading = intent.getStringExtra("Heading");
        String result = intent.getStringExtra("Result");
        activity=intent.getStringExtra("Activity");
        Heading=findViewById(R.id.single_endgame_title);
        Result=findViewById(R.id.single_endgame_final_score);
        Heading.setText(heading);
        Result.setText(result);
    }

    public void onHomeClicked(View view){
        Intent intent = new Intent(SinglePlayerEndGame.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onReplayClicked(View view){
        if(activity.equals("Single")){
            Intent intent = new Intent(SinglePlayerEndGame.this, SinglePlayerDialog.class);
            startActivity(intent);
            finish();
        }
        else if(activity.equals("MultiPlayerOffline")){
            Intent intent = new Intent(SinglePlayerEndGame.this, MultiPlayerOfflineDialog.class);
            startActivity(intent);
            finish();
        }
    }

}