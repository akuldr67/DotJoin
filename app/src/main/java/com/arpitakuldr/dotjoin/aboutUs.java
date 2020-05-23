package com.arpitakuldr.dotjoin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class aboutUs extends AppCompatActivity {

    private TextView aboutUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        aboutUs = findViewById(R.id.aboutUsText);

        aboutUs.setText("This game is developed by - Akul Gupta & Arpit Batra \n\n" +
                "For any query or review, reach out to us by mail at arpitakuldr@gmail.com\n");
    }
}
