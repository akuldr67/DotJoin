package com.example.dotjoin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeName extends AppCompatActivity {
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor myEdit;
    private String mCurrentUserName;

    private TextView currentName;
    private EditText newName;
    private Button submitButton;
    private String newNameWritten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        mSharedPreferences = getSharedPreferences("com.example.dotjoin.file", Context.MODE_PRIVATE);
        mCurrentUserName=mSharedPreferences.getString("UserName","");

        currentName = findViewById(R.id.currentNameValue);
        currentName.setText(mCurrentUserName);

        newName = findViewById(R.id.NewNameValue);

        newNameWritten = newName.getText().toString();

        submitButton = findViewById(R.id.SubmitName);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newNameWritten = newName.getText().toString();
                if(newNameWritten.equals("")){
                    Toast.makeText(getApplicationContext(),"Please enter something",Toast.LENGTH_SHORT).show();
                }
                else if(newNameWritten.length()>8){
                    Toast.makeText(getApplicationContext(),"Maximum 8 characters are allowed",Toast.LENGTH_LONG).show();
                }
                else{
                    newName.setText("");
                    myEdit=mSharedPreferences.edit();
                    mCurrentUserName = newNameWritten;
                    myEdit.putString("UserName",mCurrentUserName);
                    myEdit.apply();
                    Toast.makeText(getApplicationContext(),"Name changed Successfully",Toast.LENGTH_SHORT).show();
                    finish();
//                    startActivity(getIntent());
                }
            }
        });

    }
}
