package com.example.shopqr;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends AppCompatActivity {
    MainActivity Context;
    public Settings(MainActivity context){
        Context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void SetGetter(View view){
        EditText text = findViewById(R.id.getGetter);
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("mail.db",MODE_PRIVATE,null);
        db.execSQL("UPDATE Mail SET getter = '" + text.getText() + "'");
        Context.SetGetter(text.getText().toString());

        Toast.makeText(getApplicationContext(),text.getText().toString(),Toast.LENGTH_SHORT);
    }
}