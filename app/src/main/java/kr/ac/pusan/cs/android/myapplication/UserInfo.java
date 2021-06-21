package kr.ac.pusan.cs.android.myapplication;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserInfo extends AppCompatActivity {
    MyDBHelper myDBHelper;
    SQLiteDatabase sqlDB;
    TextView textName, textAge, textGender, textHeghit, textWeight;
    String name, age, gender, height, weight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo);

        getSupportActionBar().setTitle("개인정보");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDBHelper = new MyDBHelper(this);
        textName = findViewById(R.id.textName);
        textAge = findViewById(R.id.textAge);
        textGender = findViewById(R.id.textGender);
        textHeghit = findViewById(R.id.textHeight);
        textWeight = findViewById(R.id.textWeight);

        sqlDB = myDBHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM userInfo", null);

        while (cursor.moveToNext()) {
            name = cursor.getString(1);
            age = cursor.getString(2);
            gender = cursor.getString(3);
            height = cursor.getString(4);
            weight = cursor.getString(5);
        }

        textName.setText(name);
        textAge.setText(age);
        textGender.setText(gender);
        textHeghit.setText(height + " CM");
        textWeight.setText(weight + " KG");
        cursor.close();
        sqlDB.close();
        setEdit();
        sqlDB.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.userinfo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.btnInit:
                sqlDB = myDBHelper.getWritableDatabase();
                myDBHelper.onUpgrade(sqlDB,1,2);
                sqlDB.close();
                Toast.makeText(UserInfo.this,"개인 정보 초기화",Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(UserInfo.this, MainActivity.class);
                startActivity(intent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void setEdit(){
        findViewById(R.id.btnEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserInfo.this);
                    builder.setTitle("재입력 하시겠습니까?");
                    builder.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(UserInfo.this, EnterUserInfo.class);
                            sqlDB = myDBHelper.getWritableDatabase();
                            sqlDB.execSQL("delete from userInfo where id='1'");
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "취소했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();

                }catch(Exception e){
                    Toast.makeText(UserInfo.this,"errororororor.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
