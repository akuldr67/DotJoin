package com.example.dotjoin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    //Firebase
    private DatabaseReference mDatabaseRef;

    //Layout
    private Button singlePlayer;
    private Button multiPlayerOffline;
    private Button multiPlayerOnline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabaseRef= FirebaseDatabase.getInstance().getReference();
        mDatabaseRef.child("Rooms").child("Key").setValue("Hello There");

        //finding Layouts
        singlePlayer = findViewById(R.id.main_button_single_player);
        multiPlayerOffline=findViewById(R.id.main_button_multi_offline);
        multiPlayerOnline=findViewById(R.id.main_button_multi_online);

        //Creating Intent for each button in respective onClick listeners

        //singlePlayer
        singlePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SinglePlayer.class);
                startActivity(intent);
            }
        });

        //multiPlayerOffline
        multiPlayerOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MultiPlayerOffline.class);
                startActivity(intent);
            }
        });

        //multiPlayerOnline
        multiPlayerOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,MultiPlayerOnline.class);
                startActivity(intent);
            }
        });

    }
}
