package com.arpitakuldr.dotjoin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class SinglePlayerEndGame extends AppCompatActivity {
    private TextView Heading,Result;
    private String activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_end_game);
        Intent intent = getIntent();
        String heading = intent.getStringExtra("Heading");
        String result = intent.getStringExtra("Result");
        activity=intent.getStringExtra("Activity");
        Heading=findViewById(R.id.single_endgame_title);
        Result=findViewById(R.id.single_endgame_final_score);
        Heading.setText(heading);
        Result.setText(result);
    }

    public void onHomeClicked(View view){
//        Intent intent = new Intent(SinglePlayerEndGame.this, MainActivity.class);
//        startActivity(intent);
        if(activity.equals("Single"))
            SinglePlayer.AcSinglePlayer.finish();
        else if(activity.equals("MultiPlayerOffline"))
            MultiPlayerOffline.AcMultiPlayerOffline.finish();
        finish();
    }

    public void onReplayClicked(View view){
        if(activity.equals("Single")){
            Intent intent = new Intent(SinglePlayerEndGame.this, SinglePlayerDialog.class);
            startActivity(intent);
            SinglePlayer.AcSinglePlayer.finish();
            finish();
        }
        else if(activity.equals("MultiPlayerOffline")){
            Intent intent = new Intent(SinglePlayerEndGame.this, MultiPlayerOfflineDialog.class);
            startActivity(intent);
            MultiPlayerOffline.AcMultiPlayerOffline.finish();
            finish();
        }
    }

    public void onShareClicked(View view){
        View rootViewShare = getWindow().getDecorView().findViewById(android.R.id.content);
        if(activity.equals("Single"))
            rootViewShare = SinglePlayer.rootViewShare;
        else if(activity.equals("MultiPlayerOffline"))
            rootViewShare = MultiPlayerOffline.rootViewShare;

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
        Uri uri = FileProvider.getUriForFile(SinglePlayerEndGame.this, SinglePlayerEndGame.this.getApplicationContext().getPackageName() + ".provider", file);
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
