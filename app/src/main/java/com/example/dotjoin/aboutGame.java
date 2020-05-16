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

        aboutGame.setText("1. Tap between two dots to mark the line between those two dots.\n\n" +
                "2. More the boxes completed, more the score.\n\n" +
                "3. Host will not be able to start the game until all players are ready. Player is called ready if the game is open on player's mobile screen.\n\n" +
                "4. In online game, for the first two misses, a random move will be made by the computer.\n\n" +
                "5. If player does not play the turn for the third time, player will be kicked out of the game.\n");
    }
}
