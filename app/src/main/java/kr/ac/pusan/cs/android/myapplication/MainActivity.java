package kr.ac.pusan.cs.android.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private int  PHYISCAL_ACTIVITY;
    private  int MY_PERMISSIONS_REQUEST_CAMERA  ;
    MyDBHelper myDBHelper;
    SQLiteDatabase sqlDB;
    String key;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        myDBHelper = new MyDBHelper(this);
        permissionCount();
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
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permissionCount(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PHYISCAL_ACTIVITY);
        }

        Intent intent = new Intent(MainActivity.this, MyService.class);
        startService(intent);
    }
}
