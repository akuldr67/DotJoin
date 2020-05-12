package com.example.dotjoin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class PopUpChat extends Activity {

    private ImageButton sendButton;
    private EditText newMessage;

    //
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mMessage = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_chat);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.6));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);



        setChatData();

        //send Button
        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when send button pressed
                Toast.makeText(getApplicationContext(),"send button pressed", Toast.LENGTH_SHORT).show();
            }
        });

        //new Message
        newMessage = findViewById(R.id.writeNewText);

    }

    private void setChatData(){
        mNames.add("Random Name1");
        mMessage.add("Random Message1");

        mNames.add("Random Name2");
        mMessage.add("Random Message2");

        mNames.add("Random Name3");
        mMessage.add("Random Message3");

        mNames.add("Random Name4");
        mMessage.add("Random Message4");

        mNames.add("Random Name5");
        mMessage.add("Random Message5");

        mNames.add("Random Name1");
        mMessage.add("Random Message1");

        mNames.add("Random Name2");
        mMessage.add("Random Message2");

        mNames.add("Random Name3");
        mMessage.add("Random Message3");

        mNames.add("Random Name4");
        mMessage.add("Random Message4");

        mNames.add("Random Name5");
        mMessage.add("Random Message5");

        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d("check"," inside initRecyclerView");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        RecyclerViewAdapterForChat adapter = new RecyclerViewAdapterForChat(this,mNames,mMessage);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
