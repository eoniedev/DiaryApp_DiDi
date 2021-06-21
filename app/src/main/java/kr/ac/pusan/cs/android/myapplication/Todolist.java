package kr.ac.pusan.cs.android.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Todolist extends AppCompatActivity {
    EditText ettodo;
    Context context;
    Button store;
    //ListView todolist;
    private String date, updateFlag = "NON", id, TODO;
    MyDBHelper helper;
    SQLiteDatabase db;
    private TextView text;
    ArrayList<String> settodo = new ArrayList<String>();
    ArrayList<Integer> check = new ArrayList<Integer>();
    Todolistitem item;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_todolist);
        getSupportActionBar().hide();
        ListView listView;
        Todolistadapter adapter;
        adapter = new Todolistadapter();
        listView = (ListView) findViewById(R.id.todoList);
        ettodo = findViewById(R.id.ettodo);
        text = (TextView) findViewById(R.id.header);
        //todolist = findViewById(R.id.todolist);
        Intent intent = getIntent();
        date = intent.getExtras().getString("date");
        text.setText(date);
        try {
            helper = new MyDBHelper(Todolist.this);
            db = helper.getWritableDatabase();
        } catch (SQLException e) {
            db = helper.getReadableDatabase();
        }
        listView.setAdapter(adapter);
        printtodo(adapter);
        store = (Button) findViewById(R.id.btnsave);
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todo = ettodo.getText().toString();
                inserttodo(todo);
                Toast.makeText(getApplicationContext(),"추가되었습니다.",Toast.LENGTH_SHORT).show();
                printtodo(adapter);
                adapter.notifyDataSetChanged();
                ettodo.setText("");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data = settodo.get(position);
                StringBuffer buffer = new StringBuffer();
                buffer.append("DELETE FROM todolist WHERE date = #date# and todo = #todo#");
                String query = buffer.toString();
                query = query.replace("#date#", "'" + date + "'");
                query = query.replace("#todo#", "'" + data + "'");
                //query = query.replace("#num#", "'" + adapter.ge + "'");
                db.execSQL(query);

                adapter.notifyDataSetChanged();

                printtodo(adapter);
            }
        });
        Toast.makeText(getApplicationContext(), "일정을 작성하세요.", Toast.LENGTH_SHORT).show();

    }

    private void printtodo(Todolistadapter adapter) {
        StringBuffer buffer = new StringBuffer();
        int count = 0;
        buffer.append("SELECT * FROM todolist WHERE date = #date#");
        String query = buffer.toString();
        query = query.replace("#date#", "'" + date + "'");
        Cursor cursor;
        cursor = db.rawQuery(query, null);
        adapter.setItem();
        settodo.clear();
        if(cursor != null) {
            while(cursor.moveToNext()) {
                int getcheck = cursor.getInt(2);
                //Toast.makeText(getApplicationContext(), Integer.toString(count)+ " : "+Integer.toString(getcheck), Toast.LENGTH_SHORT).show();
                String gettodo = cursor.getString(1);
                adapter.addItem(gettodo, getcheck);
                settodo.add(gettodo);
            }
            count++;
        }
    }
    private void checkupdate(Todolistadapter adapter) {
        int count = adapter.getCount();
        int tmpcheck = 0;
        //Toast.makeText(getApplicationContext(),Integer.toString(count),Toast.LENGTH_SHORT).show();
        for(int i = 0; i < count; i++) {
            tmpcheck = adapter.getcheck(i);
            /*
            StringBuffer buffer = new StringBuffer();
            buffer.append("UPDATE todolist ");
            buffer.append("SET num = #num# ");
            buffer.append("WHERE date = #date#");
            String query = buffer.toString();
            Toast.makeText(getApplicationContext(),Integer.toString(tmpcheck),Toast.LENGTH_SHORT).show();
            query = query.replace("#num#", "'" + tmpcheck + "'");
            query = query.replace("#date#", "'" + date + "'");
            db.execSQL(query);*/
            ContentValues values = new ContentValues();

            values.put("num", tmpcheck);
            String [] s = {date};
            db.update("todolist", values, "date=?",s);
        }
    }
    private void inserttodo(String todo) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("INSERT INTO todolist (");
        buffer.append("date, todo, num )");
        buffer.append("VALUES (?, ?, ?)");
        db.execSQL(buffer.toString(), new Object[]{date, todo, 0});
    }
}
