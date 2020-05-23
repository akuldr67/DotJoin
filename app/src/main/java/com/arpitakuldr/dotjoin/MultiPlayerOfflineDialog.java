package com.arpitakuldr.dotjoin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class MultiPlayerOfflineDialog extends AppCompatActivity {

    private int size,noOfPlayers;
    private Button startGame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_offline_dialog);

        size=4;
        noOfPlayers=2;

        startGame=findViewById(R.id.multi_start_game_button);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MultiPlayerOfflineDialog.this, MultiPlayerOffline.class);
                intent.putExtra("size",size);
                intent.putExtra("noOfPlayers",noOfPlayers);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onMultiSizeClicked(View view){
        boolean checked = ((RadioButton)view).isChecked();
        switch(view.getId()) {
            case R.id.multi_size_three:
                if (checked){
                    size=4;
                }
                break;
            case R.id.multi_size_four:
                if (checked){
                    size=5;
                }
                break;
            case R.id.multi_size_five:
                if (checked){
                    size=6;
                }
                break;
            case R.id.multi_size_six:
                if (checked){
                    size=7;
                }
                break;
            case R.id.multi_size_seven:
                if (checked){
                    size=8;
                }
                break;
        }
    }

    public void onMultiNoPlayersClicked(View view){
        boolean checked = ((RadioButton)view).isChecked();
        switch(view.getId()) {
            case R.id.multi_no_players_two:
                if (checked){
                    noOfPlayers=2;
                }
                break;
            case R.id.multi_no_players_three:
                if (checked){
                    noOfPlayers=3;
                }
                break;
            case R.id.multi_no_players_four:
                if (checked){
                    noOfPlayers=4;
                }
                break;
        }
    }
}
