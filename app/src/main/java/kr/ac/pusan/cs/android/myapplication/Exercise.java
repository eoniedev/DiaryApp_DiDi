package kr.ac.pusan.cs.android.myapplication;


import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Exercise extends AppCompatActivity {
    MyDBHelper myDBHelper;
    SQLiteDatabase sqlDB;
    String event, weight;
    EditText editEvent, editHour, editMinute,editWeight;
    RecyclerView rv;
    RecyclerView.LayoutManager lm;
    private TextView text;

    private int mStepDetector;
    private int PHYISCAL_ACTIVITY ;
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private Sensor stepCountSensor;
    TextView tvStepDetector;
    TextView tvStepCount;

    TextView tvStep, tvWeight;
    String date, date2;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        getSupportActionBar().hide();

        Intent readIntent = getIntent();
        date = readIntent.getStringExtra("date");
        date2 = date.replace(".", "_");
        date2 = "day" + date2;
        text = (TextView) findViewById(R.id.header);
        text.setText(date);
        editEvent = findViewById(R.id.editEvent);
        editHour = findViewById(R.id.editHour);
        editMinute = findViewById(R.id.editMimute);

        myDBHelper = new MyDBHelper(this);
        sqlDB = myDBHelper.getWritableDatabase();
        sqlDB.execSQL("CREATE TABLE IF NOT EXISTS " + date2
                + " (id INTEGER PRIMARY KEY AUTOINCREMENT, EVENT TEXT DEFAULT '', HOUR INTEGER DEFAULT 0, MINUTE INTEGER DEFAULT 0);");
        setButton();
        display();
        display2();
        setWeight();
        stepCounter();
        Toast.makeText(getApplicationContext(), "움직이면 걸음수가 나타납니다!", Toast.LENGTH_SHORT).show();
    }

    public void setButton(){
        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    int hours, minutes;
                    event  = editEvent.getText().toString();
                    hours   = Integer.parseInt(editHour.getText().toString());
                    minutes = Integer.parseInt(editMinute.getText().toString());

                    sqlDB.execSQL("INSERT INTO exercise"
                            + " (date, EVENT, HOUR, MINUTE) values ('"
                            + date  + "', '"
                            + event  + "', '"
                            + hours   + "', '"
                            + minutes + "');");
                    MyRefresh();
                }catch(Exception e){
                    Toast.makeText(Exercise.this,"모든 정보를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    event = editEvent.getText().toString();
                    sqlDB.execSQL("delete from exercise where date='"+date+"' and EVENT='"+event+"'");
                    //sqlDB.delete("exercise", "EVENT = ?", new String[]{event});
                    MyRefresh();
                }catch (Exception e){
                    Toast.makeText(Exercise.this,"종목을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void display(){
        ArrayList<ExerciseInfo> exerciseInfos = new ArrayList<>();
        rv = findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        lm = new LinearLayoutManager(Exercise.this);
        rv.setLayoutManager(lm);

        sqlDB = myDBHelper.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("SELECT * FROM exercise WHERE date='"+date+"'", null);
        while(cursor.moveToNext()){
            exerciseInfos.add(new ExerciseInfo(cursor.getString(1),cursor.getString(2), cursor.getString(3)));
        }

        MyRVAdapter rva = new MyRVAdapter(exerciseInfos);
        rv.setAdapter(rva);
        cursor.close();
    }
    public void display2(){
        tvWeight = findViewById(R.id.tvWeight);
        tvStep = findViewById(R.id.tvStep);

        sqlDB = myDBHelper.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("SELECT * FROM WEIGHT WHERE date='" + date + "'", null);
        while(cursor.moveToNext()){
            weight = cursor.getString(1);
        }
        if(weight != null){
            tvWeight.setText(weight);
        }
        else{
            cursor = sqlDB.rawQuery("SELECT * FROM userInfo", null);
            while (cursor.moveToNext()) {
                weight = cursor.getString(5);
            }
            tvWeight.setText(weight);
        }
    }
    public void setWeight(){
        findViewById(R.id.btWeight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    AlertDialog.Builder builder = new AlertDialog.Builder(Exercise.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.activity_dialog, null);
                    builder.setView(layout);
                    builder.setTitle("체중을 입력하세요.");
                    editWeight = layout.findViewById(R.id.editWeight);

                    builder.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which) {
                            float Weight = Float.parseFloat(editWeight.getText().toString());
                            sqlDB = myDBHelper.getWritableDatabase();
                            Cursor cursor = sqlDB.rawQuery("SELECT * FROM WEIGHT WHERE date = '"+date+"'", null);
                            if (cursor != null){
                                sqlDB.execSQL("INSERT INTO WEIGHT VALUES('"+date+"', '"+Weight+"');");
                            }
                            else{
                                ContentValues val = new ContentValues();
                                val.put("weight", Weight);
                                sqlDB.update("WEIGHT", val, "date = ?", new String[]{date});
                            }
                            String temp = getDay();
                                if(date.equals(temp)){
                                    ContentValues val = new ContentValues();
                                    val.put("weight", Weight);
                                    sqlDB.update("userInfo", val, "id = ?", new String[]{"1"});
                                }
                            display2();
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
                    Toast.makeText(Exercise.this,"errororororor.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void stepCounter(){

        tvStepCount = findViewById(R.id.tvStep);
        //tvStepDetector = findViewById(R.id.tvStepDetector);

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("CountData"));
    }
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // intent ..
            String stepDetect = intent.getStringExtra("detect");
            String stepCount = intent.getStringExtra("count");
            //tvStepDetector.setText("Step Detect : " + stepDetect);
            tvStepCount.setText(stepCount);
        }
    };

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Exercise.this);
        builder
                .setMessage("페이지를 나가시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public String getDay() {
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.M.d");
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
    public void MyRefresh(){
        display();
        editEvent .setText("");
        editHour  .setText("");
        editMinute.setText("");
    }
}
