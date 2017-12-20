package com.example.administrator.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by Administrator on 2017/12/19.
 */
public class MyData extends SQLiteOpenHelper {
    public MyData(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table set_table (id integer,start_time integer,end_time integer,interva_time integer)");
        sqLiteDatabase.execSQL("create table save_monbile_table (id INTEGER PRIMARY KEY AUTOINCREMENT,monbile varchar(225),name varchar(255))");
        sqLiteDatabase.execSQL("create table send_sms_table (num varchar(225),is_send integer)");
        sqLiteDatabase.execSQL("create table save_text_table (id integer,content varchar(225))");
        sqLiteDatabase.execSQL("create table send_result_table (id integer,num varchar(225),res varchar(255))");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
