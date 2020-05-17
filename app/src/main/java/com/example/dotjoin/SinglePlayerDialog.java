package com.example.dotjoin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SinglePlayerDialog extends AppCompatActivity {

    private int size,difficulty;
    private Button startGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_dialog);
        Log.d("checkk","Dialog Created");
        size=4;
        difficulty=1;

        startGame=findViewById(R.id.single_start_game_button);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SinglePlayerDialog.this, SinglePlayer.class);
                intent.putExtra("size",size);
                intent.putExtra("difficulty",difficulty);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onSizeClicked(View view){
        boolean checked = ((RadioButton)view).isChecked();
        switch(view.getId()) {
            case R.id.single_size_three:
                if (checked){
                    size=4;
                }
                    break;
            case R.id.single_size_four:
                if (checked){
                    size=5;
                }
                    break;
            case R.id.single_size_five:
                if (checked){
                    size=6;
                }
                    break;
            case R.id.single_size_six:
                if (checked){
                    size=7;
                }
                    break;
            case R.id.single_size_seven:
                if (checked){
                    size=8;
                }
                    break;
        }
    }

    public void onDifficultyClicked(View view){
        boolean checked = ((RadioButton)view).isChecked();
        switch(view.getId()) {
            case R.id.single_difficulty_easy:
                if (checked){
                    difficulty=1;
                }
                break;
            case R.id.single_difficulty_hard:
                if (checked){
                    difficulty=2;
                }
                break;
        }
    }
}
