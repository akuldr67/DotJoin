package com.example.dotjoin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    //Firebase
    private DatabaseReference mDatabaseRef;

    //Layout
    private Button singlePlayer;
    private Button multiPlayerOffline;
    private Button multiPlayerOnline;

    //Shared Prefrences
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor myEdit;

    String mCurrentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabaseRef= FirebaseDatabase.getInstance().getReference();
        mDatabaseRef.child("Rooms");

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

        mSharedPreferences = getSharedPreferences("com.example.dotjoin.file",Context.MODE_PRIVATE);

        mCurrentUserName=mSharedPreferences.getString("UserName","");
        if(mCurrentUserName.equals("")){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Your Name");
            final EditText input = new EditText(getApplicationContext());
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(input.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"Please Enter Your Name",Toast.LENGTH_LONG);
                    }
                    else{
                        myEdit=mSharedPreferences.edit();
                        mCurrentUserName=input.getText().toString();
                        myEdit.putString("UserName",mCurrentUserName);
                        myEdit.apply();
                    }
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }


    }
}
