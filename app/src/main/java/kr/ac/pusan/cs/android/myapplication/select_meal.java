package kr.ac.pusan.cs.android.myapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class select_meal extends AppCompatActivity {
    Button breakfast, lunch, dinner;
    MyDBHelper helper;
    SQLiteDatabase db;
    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_meal);
        getSupportActionBar().hide();
        breakfast = findViewById(R.id.breakfast);
        lunch = findViewById(R.id.lunch);
        dinner = findViewById(R.id.dinner);
        helper = new MyDBHelper(this);
        db = helper.getWritableDatabase();
        text = (TextView) findViewById(R.id.header);

        Intent readIntent = getIntent();
        String date = readIntent.getStringExtra("date");
        text.setText(date);
        Intent insertIntent = new Intent(select_meal.this, insert_food_2.class);
        insertIntent.putExtra("date", date);
        breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertIntent.putExtra("type","breakfast");
                startActivity(insertIntent);
            }
        });

        lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertIntent.putExtra("type","lunch");
                startActivity(insertIntent);
            }
        });

        dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertIntent.putExtra("type","dinner");
                startActivity(insertIntent);
            }
        });
    }
}