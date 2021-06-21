package kr.ac.pusan.cs.android.myapplication;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import static android.content.ContentValues.TAG;
import static androidx.core.app.ActivityCompat.requestPermissions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
public class MyService extends Service implements SensorEventListener {
    private int PHYISCAL_ACTIVITY ;
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    //TextView tvStepDetector;
    private int mStepDetector=0;
    private  float currentStepCount;
    private  float previousStepCount;

    private Sensor stepCountSensor;
    //TextView tvStepCount;
    private Date savedDate=new Date();
    public MyService() {
    }

    @Override
    public void onCreate() {
        sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        //sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        // DETECTOR
        if(sensorManager == null){
            Toast.makeText(this, "sensormanger error", Toast.LENGTH_SHORT).show();
        }


        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if(stepDetectorSensor == null) {
            Toast.makeText(this, "No Step Detect Sensor", Toast.LENGTH_SHORT).show();
            Log.v("detecsensor", "d: no senser");
        }

        // COUNTER

        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(stepCountSensor == null) {
            Toast.makeText(this, "No Step Detect Sensor", Toast.LENGTH_SHORT).show();
            Log.v("countsensor", "d: no senser");

        }

        Toast.makeText(this.getApplicationContext(),"service on", Toast.LENGTH_SHORT).show();
        //super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyService", "OnstartCommane() call");
        if (intent == null) return Service.START_STICKY;
        else {
            sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
            //sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
            // DETECTOR
            if(sensorManager == null){
                Toast.makeText(this, "sensormanger error", Toast.LENGTH_SHORT).show();
            }
            //else Toast.makeText(this, "sensormanger on", Toast.LENGTH_SHORT).show();

            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            if(stepDetectorSensor == null) {
                Toast.makeText(this, "No Step Detect Sensor", Toast.LENGTH_SHORT).show();
                Log.v("detecsensor", "d: no senser");
            }
            //else Toast.makeText(this, "sensordetect on", Toast.LENGTH_SHORT).show();
            // COUNTER

            stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if(stepCountSensor == null) {
                Toast.makeText(this, "No Step Detect Sensor", Toast.LENGTH_SHORT).show();
                Log.v("countsensor", "d: no senser");

            }
            //else Toast.makeText(this, "sensorcount on", Toast.LENGTH_SHORT).show();
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        Toast.makeText(this.getApplicationContext(),"service off", Toast.LENGTH_SHORT).show();
        sensorManager.unregisterListener(this);

        // super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(isNewDay()){
            previousStepCount = currentStepCount;
        }
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if(event.values[0] == 1.0f) {
                mStepDetector += event.values[0];
                //tvStepDetector.setText("Step Detect : " + String.valueOf(mStepDetector));
            }
        } else if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            //tvStepCount.setText("Step Count : " + String.valueOf(event.val
            currentStepCount = event.values[0];
        }
        //Toast.makeText(this.getApplicationContext(),"onSensorchanged", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent("CountData");
        intent.putExtra("detect", String.valueOf(mStepDetector));
        intent.putExtra("count", String.valueOf(currentStepCount-previousStepCount));
        //Toast.makeText(this, "curent count "+currentStepCount+" previous count "+previousStepCount, Toast.LENGTH_SHORT).show();

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private boolean isNewDay(){
        // Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN);

        Date date = beforeOneDay(new Date()); //현재
        String dstr = dateFormat.format(date);
        String sstr = dateFormat.format(savedDate); //이전
        if(dstr.equals(sstr)){ //다음날이면
            savedDate = new Date(); //업데이트
            return true;
        }
        else return false;
    }
    public Date beforeOneDay(Date d){
        //d 기준날짜를 입력한다.
        long dd=d.getTime();
        //밀리세컨드*60초*60분*24시간==하루
        return new Date(dd-1000*60*60*24);
    }
}