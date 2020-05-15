package com.example.dotjoin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class aboutGame extends AppCompatActivity {

    private TextView aboutGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_game);

        aboutGame = findViewById(R.id.aboutGameText);

        aboutGame.setText("1. Join dotes to complete squares.\n\n" +
                "2. Tap between two dots to mark the line between those two dots.\n\n" +
                "3. More the boxes completed, more the score.\n\n" +
                "4. Host will not be able to start the game till all plyers are ready. By ready means players should have the game open on their mobile screen\n\n" +
                "5. In online game, for the first two misses, a random move will be made by the computer for every miss\n\n" +
                "6. If player does not play his/her turn for the third times, player will be kicked out of the game.\n\n");
    }
}
