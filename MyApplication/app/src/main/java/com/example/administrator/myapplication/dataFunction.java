package com.example.administrator.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2017/12/5.
 */

public class dataFunction {
//    public SQLiteDatabase creatDate(){
//        SQLiteDatabase db;
//
//        return db;
//    };
    //用于没有返回值的sql语句
    public void execDate(SQLiteDatabase db,String sql){
            db.execSQL(sql);
    }
    //用于查询语句的
    public Cursor queryData(SQLiteDatabase db,String sql){
        Cursor reslut = db.rawQuery(sql,null);
        return reslut;
    }
    public void destroyData(SQLiteDatabase db){
        if (db!=null&&db.isOpen()){
            db.close();
        }
    }
}
