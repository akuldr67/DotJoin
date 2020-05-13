package com.example.dotjoin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PopUpChat extends Activity {

    private ImageButton sendButton;
    private EditText newMessage;

    private String roomId;
    private DatabaseReference mDatabase;

    private ArrayList<Message> mMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_chat);

        Intent intent = getIntent();
        roomId = intent.getStringExtra("RoomId");
        mDatabase= FirebaseDatabase.getInstance().getReference();


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



        //new Message
        newMessage = findViewById(R.id.writeNewText);

        //send Button
        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when send button pressed
                Toast.makeText(getApplicationContext(),"send button pressed", Toast.LENGTH_SHORT).show();

                // make a new message of this message text along with name and device token and add it roomChat in database
            }
        });

        setChatData();
    }

    private void setChatData(){

//        mDatabase.child("Rooms").child(roomId).child("roomChat").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                //assign all messages of roomChat to mMessages
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//
//        });

        Message m = new Message("Random Name1","Random Message1","abc");
        mMessages.add(m);

        m = new Message("Random Name2","Random Message2","abc");
        mMessages.add(m);

        m = new Message("Random Name3","Random Message3","abc");
        mMessages.add(m);

        m = new Message("Random Name4","Random Message4","abc");
        mMessages.add(m);

        m = new Message("Random Name5","Random Message5","abc");
        mMessages.add(m);

        m = new Message("Random Name6","Random Message6","abc");
        mMessages.add(m);

        m = new Message("Random Name7","Random Message7","abc");
        mMessages.add(m);

        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d("check"," inside initRecyclerView");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        RecyclerViewAdapterForChat adapter = new RecyclerViewAdapterForChat(this,mMessages);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //auto scroll to last message element
        recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
    }
}
