package kr.ac.pusan.cs.android.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

public class insert_food_2 extends AppCompatActivity {
    MyDBHelper helper;
    SQLiteDatabase db;
    TextView nowDay;
    Button savebutton;
    Button searchbutton;
    EditText name, kcal;
    String type, date;
    ListView list;

    private TextView text;

    ArrayList<String> info = new ArrayList<String>();
    ArrayList<String> infoName = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_food_2);
        getSupportActionBar().hide();
        savebutton = findViewById(R.id.saveButton);
        searchbutton = findViewById(R.id.searchButton);
        text = (TextView) findViewById(R.id.header);

        name = findViewById(R.id.foodName);
        kcal = findViewById(R.id.calorie);
        list = (ListView) findViewById(R.id.foodList);

        Intent readIntent = getIntent();
        type = readIntent.getStringExtra("type");
        date = readIntent.getStringExtra("date");

        text.setText(date);

        helper = new MyDBHelper(this);
        db = helper.getWritableDatabase();
        printListView();

        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                String foodString = name.getText().toString();
                String kcalString = kcal.getText().toString();
                values.put("date", date);
                values.put("foodname", foodString);
                values.put("kcal", kcalString);
                db.insert(type, null, values);
                printListView();
                name.setText(null);
                kcal.setText(null);

            }
        });
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foodString = name.getText().toString();
                try {
                    //SearchCalorie search = new SearchCalorie(foodString);
                    //String calorie = search.search();
                    SearchCalorie search = new SearchCalorie(foodString);
                    String calorie = search.search();
                    if(calorie != "not found")
                        kcal.setText(calorie);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String [] s = {date, infoName.get(position)};
                db.delete(type, "date=? AND foodname=?", s);
                //infoName.remove(position);
                printListView();

            }
        });
    }

    private void printListView(){
        Cursor cursor = db.rawQuery("SELECT * FROM "+type+" WHERE date = '"+date+"'", null);
        info.clear();
        infoName.clear();
        if (cursor != null){
            while(cursor.moveToNext()){
                String foodname = cursor.getString(1);
                String calorie = cursor.getString(2);
                infoName.add(foodname);
                info.add(foodname+" : "+calorie+"kcal");


            }
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, info);

        list.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(insert_food_2.this);
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
}