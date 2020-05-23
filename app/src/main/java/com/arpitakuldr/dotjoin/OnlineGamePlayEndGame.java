package com.arpitakuldr.dotjoin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class OnlineGamePlayEndGame extends AppCompatActivity {
    private TextView Heading,Result;
    private DatabaseReference mDatabase;
    private String roomId;
    private int playerNo;
    private ArrayList<Player> players;
    private ProgressBar endGameProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        setFinishOnTouchOutside(false);

        setContentView(R.layout.activity_online_game_play_end_game);

        endGameProgressBar = findViewById(R.id.online_game_end_progressbar);

        Intent intent = getIntent();
        String heading = intent.getStringExtra("Heading");
        String result = intent.getStringExtra("Result");
        roomId=intent.getStringExtra("RoomId");
        playerNo=intent.getIntExtra("PlayerNo",-1);
        if(playerNo==-1){
            Log.d("playerNo","Did not get the player No");
        }


        Heading=findViewById(R.id.onlinegameplay_endgame_title);
        Result=findViewById(R.id.onlinegameplay_endgame_final_score);

        Heading.setText(heading);
        Result.setText(result);

        mDatabase= FirebaseDatabase.getInstance().getReference();


    }

    public void onHomeClicked(View view){
        endGameProgressBar.setVisibility(View.VISIBLE);
        Log.d("checkk","Click on Home Detected");
        mDatabase.child("Rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDatabase.child("Rooms").child(roomId).removeEventListener(this);
                Room room = dataSnapshot.getValue(Room.class);
                Log.d("checkk","Loaded room");
                players = room.getPlayers();
                Log.d("checkk","Removing player from room");
                for(int i=0;i<players.size();i++){
                    if(players.get(i).getPlayerNumber()==playerNo){
                        players.remove(i);
                    }
                }
//                        players.remove(playerNo);


                if (players == null || players.size() < 1) {
                    Log.d("checkk","All players gone");
                    Log.d("checkk","Deleting room");
                    mDatabase.child("Rooms").child(roomId).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(OnlineGamePlayEndGame.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Log.d("checkk","Starting Main Activity");
                                endGameProgressBar.setVisibility(View.GONE);
                                startActivity(intent);
                                OnlineGamePlay.AcOnlineGamePlay.finish();
                                Log.d("checkk","finishing OnlineGamePlay");
                                finish();

                            } else {
                                Toast.makeText(OnlineGamePlayEndGame.this, "Unable to go to home page", Toast.LENGTH_SHORT).show();
                                endGameProgressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    Log.d("checkk","all player not gone");
                    room.setPlayers(players);
                    Log.d("checkk","updating room with the player removed");
                    mDatabase.child("Rooms").child(roomId).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(OnlineGamePlayEndGame.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Log.d("checkk","Starting intent for MainActivity");
                                endGameProgressBar.setVisibility(View.GONE);
                                startActivity(intent);
                                OnlineGamePlay.AcOnlineGamePlay.finish();
                                Log.d("checkk","finishing OnlineGamePlay");
                                finish();
                            } else {
                                Toast.makeText(OnlineGamePlayEndGame.this, "Unable to go to home page", Toast.LENGTH_SHORT).show();
                                endGameProgressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(MotionEvent.ACTION_OUTSIDE==event.getAction()){

        }
        return super.onTouchEvent(event);
    }




    public void onShareClicked(View view){
        View rootViewShare = OnlineGamePlay.rootViewShare;

        Bitmap b = getScreenShot(rootViewShare);
        File f = store(b,"screenshotShare.png");
        shareImage(f);

    }

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public File store(Bitmap bm, String fileName){
//        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File imagePath = new File(this.getExternalCacheDir() + "/screenshot.png");
//        File dir = new File(dirPath);
//        if(!dir.exists())
//            dir.mkdirs();
//        File file = new File(dirPath, fileName);
        try {
//            FileOutputStream fOut = new FileOutputStream(file);
            FileOutputStream fOut = new FileOutputStream(imagePath);
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    private void shareImage(File file){
//        Uri uri = Uri.fromFile(file);
        Uri uri = FileProvider.getUriForFile(OnlineGamePlayEndGame.this, OnlineGamePlayEndGame.this.getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(Intent.createChooser(intent, "Share Screenshot"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "No App Available", Toast.LENGTH_SHORT).show();
        }
    }


}
