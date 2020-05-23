package com.arpitakuldr.dotjoin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class ExitGameDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit_game_dialog);
    }

    public void onYesClicked(View view){
            this.finishAffinity();
    }

    public void onNoClicked(View view){
        finish();
    }
}
