package kr.ac.pusan.cs.android.myapplication;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Choose_module extends AppCompatActivity implements View.OnClickListener {
    MyDBHelper helper;
    SQLiteDatabase db;
    public static String UPDATEIS = "X";
    Button btdiary, btlist, btfood, bthealth;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_choose_module);
        View textView = (View) findViewById(R.id.icHeader);
        TextView title =(TextView) textView.findViewById(R.id.header);
        btdiary = (Button) findViewById(R.id.btdiary);
        btdiary.setOnClickListener(this);
        btlist = (Button) findViewById(R.id.btlist);
        btlist.setOnClickListener(this);
        btfood = (Button) findViewById(R.id.btfood);
        btfood.setOnClickListener(this);
        bthealth = (Button) findViewById(R.id.bthealth);
        bthealth.setOnClickListener(this);
        Intent intent = getIntent();
        date = intent.getExtras().getString("date");
    }

    @Override
    public void onClick(View v) {
        try {
            helper = new MyDBHelper(Choose_module.this);
            db = helper.getReadableDatabase();
        } catch(SQLiteException e) {

        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("SELECT * FROM diary WHERE ddate = #date#");
        String query = buffer.toString();
        query = query.replace("#date#", "'" + date + "'");
        //해당 날짜의 데이터 있는지 없는지 알기 위한 것
        Cursor cursor;
        cursor = db.rawQuery(query, null);
        switch(v.getId()) {
            case R.id.btdiary:
                if(cursor.moveToNext()) {
                    String no = cursor.getString(0);
                    String getdate = cursor.getString(1);
                    String img = cursor.getString(3);
                    String content = cursor.getString(4);
                    cursor.close();
                    db.close();

                    Intent readIntent = new Intent(Choose_module.this, Diary.class);
                    readIntent.putExtra("no",no);
                    readIntent.putExtra("date",date);
                    readIntent.putExtra("img",img);
                    readIntent.putExtra("content", content);
                    readIntent.putExtra("FLAG", "UPDATESIGN");
                    Choose_module.UPDATEIS = "0";
                    startActivity(readIntent);
                }
                else {
                    Intent readIntent = new Intent(Choose_module.this, Diary.class);
                    readIntent.putExtra("date",date);
                    startActivity(readIntent);
                }
                break;

            case R.id.btlist:
                if(cursor.moveToNext()) {
                    String no = cursor.getString(0);
                    String getdate = cursor.getString(1);
                    String img = cursor.getString(3);
                    String content = cursor.getString(4);
                    cursor.close();
                    db.close();

                    Intent readIntent = new Intent(Choose_module.this, Todolist.class);
                    readIntent.putExtra("no",no);
                    readIntent.putExtra("date",date);
                    readIntent.putExtra("img",img);
                    readIntent.putExtra("content", content);
                    readIntent.putExtra("FLAG", "UPDATESIGN");
                    Choose_module.UPDATEIS = "0";
                    startActivity(readIntent);
                }
                else {
                    Intent readIntent = new Intent(Choose_module.this, Todolist.class);
                    readIntent.putExtra("date",date);
                    startActivity(readIntent);
                }
                break;
            case R.id.btfood:
                if(cursor.moveToNext()) {
                    String no = cursor.getString(0);
                    String getdate = cursor.getString(1);
                    String img = cursor.getString(3);
                    String content = cursor.getString(4);
                    cursor.close();
                    db.close();

                    Intent readIntent = new Intent(Choose_module.this, select_meal.class);
                    readIntent.putExtra("no",no);
                    readIntent.putExtra("date",date);
                    readIntent.putExtra("img",img);
                    readIntent.putExtra("content", content);
                    readIntent.putExtra("FLAG", "UPDATESIGN");
                    Choose_module.UPDATEIS = "0";
                    startActivity(readIntent);
                }
                else {
                    Intent readIntent = new Intent(Choose_module.this, select_meal.class);
                    readIntent.putExtra("date",date);
                    startActivity(readIntent);
                }
                break;
            case R.id.bthealth:
                if(cursor.moveToNext()) {
                    String no = cursor.getString(0);
                    String getdate = cursor.getString(1);
                    String img = cursor.getString(3);
                    String content = cursor.getString(4);
                    cursor.close();
                    db.close();

                    Intent readIntent = new Intent(Choose_module.this, Exercise.class);
                    readIntent.putExtra("no",no);
                    readIntent.putExtra("date",date);
                    readIntent.putExtra("img",img);
                    readIntent.putExtra("content", content);
                    readIntent.putExtra("FLAG", "UPDATESIGN");
                    Choose_module.UPDATEIS = "0";
                    startActivity(readIntent);
                }
                else {
                    Intent readIntent = new Intent(Choose_module.this, Exercise.class);
                    readIntent.putExtra("date",date);
                    startActivity(readIntent);
                }
                break;
        }
    }
}