package com.arpitakuldr.dotjoin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class PopUpChat extends Activity {

    private ImageButton sendButton;
    private EditText newMessage;

    private String roomId;
    private DatabaseReference mDatabase;
    private SharedPreferences mSharedPreferences;
    private RecyclerViewAdapterForChat adapter;
    private ArrayList<Message> mMessages = new ArrayList<>();
    private RecyclerView recyclerView;

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
//                Toast.makeText(getApplicationContext(),"send button pressed", Toast.LENGTH_SHORT).show();

                // make a new message of this message text along with name and device token and add it roomChat in database
                //Getting User Name
                mSharedPreferences = getSharedPreferences("com.arpitakuldr.dotjoin.file", Context.MODE_PRIVATE);
                final String name = mSharedPreferences.getString("UserName", "");

                String messageText = newMessage.getText().toString();
                if(messageText.equals("")){
                    Toast.makeText(PopUpChat.this,"You didn't write anything",Toast.LENGTH_LONG);
                }
                else {
                    newMessage.setText("");
                    Message message = new Message(name, messageText);
                    DatabaseReference uniqueMessage = mDatabase.child("Rooms").child(roomId).child("Chats").push();
                    String uniqueMessageKey = uniqueMessage.getKey();
                    mDatabase.child("Rooms").child(roomId).child("Chats").child(uniqueMessageKey).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("checkk", "messageSent");
                            } else {
                                Log.d("checkk", "failed to send message");
                            }
                        }
                    });
                }
                    recyclerView.smoothScrollToPosition(adapter.getItemCount());

            }
        });

        setChatData();

    }

    private void setChatData(){
        initRecyclerView();

        Log.d("checkk","Starting Loading messages");
        Query queryToChat=mDatabase.child("Rooms").child(roomId).child("Chats");
        ChildEventListener childEventListener;
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = new Message(dataSnapshot.child("senderName").getValue().toString(),dataSnapshot.child("message").getValue().toString());
                mMessages.add(message);
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        queryToChat.addChildEventListener(childEventListener);
    }

    private void initRecyclerView(){
        Log.d("checkk"," inside initRecyclerView");
         recyclerView= findViewById(R.id.recyclerView);

         adapter= new RecyclerViewAdapterForChat(this,mMessages);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        auto scroll to last message element

        recyclerView.smoothScrollToPosition(adapter.getItemCount());

    }
}
