package kr.ac.pusan.cs.android.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DIDI.db";
    private static final int DATABASE_VERSION = 1;
    public MyDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //할일
        db.execSQL("CREATE TABLE todolist (date CHAR(20), todo CHAR(30), num INTEGER DEFAULT 0); ");
        //식단
        db.execSQL("CREATE TABLE if not exists breakfast (date CHAR(20) ,foodname CHAR(30) , kcal INTEGER); ");
        db.execSQL("CREATE TABLE if not exists lunch (date CHAR(20) ,foodname CHAR(30) , kcal INTEGER); ");
        db.execSQL("CREATE TABLE if not exists dinner (date CHAR(20) ,foodname CHAR(30) , kcal INTEGER); ");
        db.execSQL("CREATE TABLE if not exists dateInfo(date CHAR(10) PRIMARY KEY, rekcal INTEGERT, inkcal INTEGER, carbohydrate INTEGER, protein INTEGER, fat INTEGER);");
        //운동
        db.execSQL("CREATE TABLE if not exists WEIGHT (date CHAR(20), weight DOUBLE);");
        db.execSQL("CREATE TABLE if not exists exercise (date CHAR(20), EVENT TEXT DEFAULT '', HOUR INTEGER DEFAULT 0, MINUTE INTEGER DEFAULT 0);");
        //개인정보
        db.execSQL("CREATE TABLE if not exists userInfo (id CHAR(1) PRIMARY KEY, name CHAR(20), age INTEGER, gender CHAR(10), height DOUBLE, weight DOUBLE);");
        //일기
        db.execSQL("CREATE TABLE if not exists diary (\n" +
                " dno INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                " ddate TEXT, \n" +
                " dtitle TEXT, \n" +
                " dimgpath TEXT, \n" +
                " dcontent TEXT\n" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS todolist;");
        db.execSQL("DROP TABLE IF EXISTS breakfast;");
        db.execSQL("DROP TABLE IF EXISTS lunch;");
        db.execSQL("DROP TABLE IF EXISTS dinner;");
        db.execSQL("DROP TABLE IF EXISTS dateInfo;");
        db.execSQL("DROP TABLE IF EXISTS WEIGHT;");
        db.execSQL("DROP TABLE IF EXISTS exercise;");
        db.execSQL("DROP TABLE IF EXISTS userInfo;");
        db.execSQL("DROP TABLE IF EXISTS diary;");
        db.execSQL("CREATE TABLE todolist (date CHAR(20), todo CHAR(30), num INTEGER DEFAULT 0); ");
        db.execSQL("CREATE TABLE breakfast (date CHAR(20) ,foodname CHAR(30) , kcal INTEGER); ");
        db.execSQL("CREATE TABLE lunch (date CHAR(20) ,foodname CHAR(30) , kcal INTEGER); ");
        db.execSQL("CREATE TABLE dinner (date CHAR(20) ,foodname CHAR(30) , kcal INTEGER); ");
        db.execSQL("CREATE TABLE dateInfo(date CHAR(10) PRIMARY KEY, rekcal INTEGERT, inkcal INTEGER, carbohydrate INTEGER, protein INTEGER, fat INTEGER);");
        db.execSQL("CREATE TABLE WEIGHT (date CHAR(20), weight DOUBLE);");
        db.execSQL("CREATE TABLE exercise (date CHAR(20), EVENT TEXT DEFAULT '', HOUR INTEGER DEFAULT 0, MINUTE INTEGER DEFAULT 0);");
        db.execSQL("CREATE TABLE userInfo (id CHAR(1) PRIMARY KEY, name CHAR(20), age INTEGER, gender CHAR(10), height DOUBLE, weight DOUBLE);");
        db.execSQL("CREATE TABLE diary (\n" +
                " dno INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                " ddate TEXT, \n" +
                " dtitle TEXT, \n" +
                " dimgpath TEXT, \n" +
                " dcontent TEXT\n" +
                ");");}
}
