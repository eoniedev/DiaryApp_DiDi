package kr.ac.pusan.cs.android.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    MyDBHelper myDBHelper;
    SQLiteDatabase sqlDB;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        myDBHelper = new MyDBHelper(this);

        getSupportActionBar().hide();
        findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlDB = myDBHelper.getWritableDatabase();
                Cursor cursor = sqlDB.rawQuery("SELECT * FROM userInfo", null);

                while(cursor.moveToNext()){
                    key = cursor.getString(0);
                }

                if(key != null){
                    Intent intent = new Intent(MainActivity.this, Start.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(MainActivity.this, EnterUserInfo.class);
                    startActivity(intent);
                }
                cursor.close();
                sqlDB.close();
            }
        });
    }
}
