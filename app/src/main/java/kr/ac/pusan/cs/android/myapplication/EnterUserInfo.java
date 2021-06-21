package kr.ac.pusan.cs.android.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EnterUserInfo extends AppCompatActivity {
    private int MY_PERMISSIONS_REQUEST_CAMERA  ;
    MyDBHelper myDBHelper;
    SQLiteDatabase sqlDB;

    Spinner spinnerGender;
    EditText editName, editAge, editHeight, editWeight;
    String gender, name;
    int age;
    Double height, weight;
    String date;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.M.d");

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enteruserinfo);
        getSupportActionBar().setTitle("개인정보입력");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDBHelper = new MyDBHelper(this);

        editName = findViewById(R.id.editName);
        editAge = findViewById(R.id.editAge);
        editHeight = findViewById(R.id.editHeight);
        editWeight = findViewById(R.id.editWeight);

        final String[] genders = {"남자", "여자"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, genders);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerGender.setAdapter(adapter);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = genders[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //옵션 메뉴를 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.enteruserinfo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                Intent intent1 = new Intent(EnterUserInfo.this, MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.btnOK:
                sqlDB = myDBHelper.getWritableDatabase();
                try{
                    name = editHeight.getText().toString();
                    age = Integer.parseInt(editAge.getText().toString());
                    height = Double.parseDouble(editHeight.getText().toString());
                    weight = Double.parseDouble(editWeight.getText().toString());

                    sqlDB.execSQL("INSERT INTO userInfo VALUES('1','"
                    +name+"', '"+age+"', '"+gender+"', '"+height+"', '"+weight+"');");
                    date = getDay();
                    sqlDB.execSQL("INSERT INTO WEIGHT VALUES('"+date+"', '"+weight+"');");
                    sqlDB.close();
                    Intent intent2 = new Intent(EnterUserInfo.this, Start.class);
                    startActivity(intent2);
                }catch (Exception e){
                    Toast.makeText(EnterUserInfo.this, "모든 정보를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getDay() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
}
