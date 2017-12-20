package com.example.administrator.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/12/20.
 */

public class saveSet {
    private Context context;
    public saveSet(Context context){
        this.context=context;
    }
    public boolean save(SQLiteDatabase db,Integer start_time,Integer end_time,Integer inter_time){
        try{
            String sql="";
            String sql1="";
            //把数设置的数据导入数据库
            Cursor cursor=db.rawQuery("select * from set_table where id=1",null);
            if (cursor!=null){
                if (cursor.getCount()>0){
                    sql="update set_table set start_time="+start_time+",end_time="+end_time+",interva_time="+inter_time+" where id=1";
                    db.execSQL(sql);
                }else {
                    sql="insert into set_table VALUES (1,"+start_time+","+end_time+","+inter_time+")";
                    db.execSQL(sql);
                }
            }else {
                sql="insert into set_table VALUES (1,"+start_time+","+end_time+","+inter_time+")";
                db.execSQL(sql);
            }
            Toast.makeText(context, "保存成功"
                    , Toast.LENGTH_SHORT).show();
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
