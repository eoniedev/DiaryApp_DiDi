package kr.ac.pusan.cs.android.myapplication;

import android.Manifest;
import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Start extends TabActivity implements View.OnClickListener, OnDateSelectedListener {
    private int PHYISCAL_ACTIVITY ;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    Calendar calendar = Calendar.getInstance();
    MaterialCalendarView material;
    MyDBHelper helper;
    SQLiteDatabase db;
    ListView lv;
    //String name, age;
    public static String UPDATEIS = "X";
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1001;
    private LineChart chart;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabHost tabHost = getTabHost();
        TabHost.TabSpec tabSpec1 = tabHost.newTabSpec("record").setIndicator("메인화면");
        tabSpec1.setContent(R.id.mainRecord);
        tabHost.addTab(tabSpec1);
        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("personal").setIndicator("마이페이지");
        tabSpec2.setContent(R.id.mainPersonal);
        tabHost.addTab(tabSpec2);
        tabHost.setCurrentTab(0);
        Intent inIntent = getIntent();
        int mainNo = inIntent.getIntExtra("mainNo", 0);
        if (mainNo == 0) {
            tabHost.setCurrentTab(0);
        } else if (mainNo == 1) {
            tabHost.setCurrentTab(1);
        }
        String[] personal = {"개인정보"};
        ListView list1 = findViewById(R.id.listView1);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, personal);
        list1.setAdapter(adapter1);
        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        Intent intent1 = new Intent(Start.this, UserInfo.class);
                        startActivity(intent1);
                        break;
                }
            }
        });

        //mydiary
        View textView = (View) findViewById(R.id.icHeader);
        TextView title =(TextView) textView.findViewById(R.id.header);
        Button back = (Button) findViewById(R.id.back);
        Button home = (Button) findViewById(R.id.home);
        title.setText("Calendar");
        material = (MaterialCalendarView) findViewById(R.id.calendarView);
        material.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        material.addDecorators(oneDayDecorator);
        material.setOnDateChangedListener(this);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 승인이 필요합니다.", Toast.LENGTH_LONG).show();
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "카메라 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
                Toast.makeText(this, "카메라 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            }
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                setLineChart();
            }
        });
        //permissionCount();

        //chart
        setLineChart();

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permissionCount(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PHYISCAL_ACTIVITY);
        }

        Intent intent = new Intent(Start.this, MyService.class);
        startService(intent);
    }
    @Override
    public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
        try {
            helper = new MyDBHelper(Start.this);
            db = helper.getReadableDatabase();
        } catch (SQLException e) {
            Toast.makeText(this, "db error.",Toast.LENGTH_LONG).show();
        }
        int year = date.getYear();
        int month =date.getMonth()+1;
        int day = date.getDay();
        String enter = ".";
        CalendarDay calendaryDay1= date.from(year, month, day);
        widget.setSelectedDate(calendaryDay1);
        Intent readIntent = new Intent(Start.this, Choose_module.class);
        readIntent.putExtra("date",year+enter+month+enter+day);
        //readIntent.putExtra("Name",name);
        Toast.makeText(getApplicationContext(),year+enter+month+enter+day , Toast.LENGTH_SHORT).show();
        startActivity(readIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "승인이 허가되어 있습니다.", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(this, "아직 승인받지 않았습니다.", Toast.LENGTH_LONG).show();
                return;
        }

    }

    @Override
    public void onClick(View v) {

    }

    public void setLineChart(){
        chart = (LineChart)findViewById((R.id.lineChart));
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setViewPortOffsets(0, 0, 0, 0);

        helper = new MyDBHelper(Start.this);
        db = helper.getReadableDatabase();
        float weight = 0, temp = 0;
        String date;

        Cursor cursor = db.rawQuery("SELECT * FROM userInfo", null);
        while (cursor.moveToNext()) {
            temp = Float.parseFloat(cursor.getString(5));
        }
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            date = getDay(i);
            String str = null;
            cursor = db.rawQuery("SELECT * FROM WEIGHT WHERE date='" + date + "'", null);
            while(cursor.moveToNext()){
                str = cursor.getString(1);
            }
            if(str != null){
                weight = Float.parseFloat(str);
            }
            else
                weight = temp;
            values.add(new Entry(i, weight));
        }

        LineDataSet set1;
        set1 = new LineDataSet(values, "");

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets

        // create a data object with the data sets
        LineData data = new LineData(dataSets);

        // black lines and points
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);

        // set data
        chart.setData(data);
    }
    public String getDay(int i) {
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.M.d");
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);
        mDate.setDate(mDate.getDate()-i);
        return mFormat.format(mDate);
    }
}