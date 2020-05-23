package com.arpitakuldr.dotjoin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EndService extends Service {

    private String roomId;
    private int playerNo;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //Getting RoomID
        Log.d("checkk","During Ending RoomID "+roomId);
        Log.d("checkk","During Ending PlayerNo "+playerNo);
        //Here I will make this player inactive and not ready
        mDatabase=FirebaseDatabase.getInstance().getReference().child("Rooms").child(roomId);
        Log.d("checkk","Got Reference");
        mDatabase.child("players").child(playerNo+"").child("ready").setValue(0);
        mDatabase.child("game").child("players").child(playerNo+"").child("active").setValue(0);
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public int onStartCommand(@org.jetbrains.annotations.NotNull Intent intent, int flags, int startId) {
        if(intent!=null) {
            Log.d("service", "onStartCommand of Service called");
            roomId = intent.getStringExtra("RoomId");
            playerNo = intent.getIntExtra("PlayerNo", -1);
            Log.d("service", "roomId = " + roomId);
            Log.d("service", "PlayerNo = " + playerNo);
            if (playerNo == -1) {
                Log.d("playerNo", "Did not get the player No");
            }
            return START_STICKY_COMPATIBILITY;
        }
        return START_NOT_STICKY;
    }
}
