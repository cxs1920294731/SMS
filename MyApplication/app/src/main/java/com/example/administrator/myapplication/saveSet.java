package com.example.administrator.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Administrator on 2017/12/20.
 */

public class saveSet {
    private Context context;
    private SQLiteDatabase db;
    public saveSet(Context context,SQLiteDatabase db){
        this.context=context;
        this.db=db;
    }
    public boolean save(Integer start_time,Integer end_time,Integer inter_time){
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
    //取出设置的时长
    public int get_inter_time() {
        Cursor cursor = db.rawQuery("select * from set_table", null);
        int inter = 20;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                inter = Integer.parseInt(cursor.getString(cursor.getColumnIndex("interva_time")).toString());
            }
        }
        return inter;
    }

    //取出设置id的开始与结束时间
    public int[] get_start_end_time() {
        int[] res = new int[2];
        Cursor cursor = db.rawQuery("select * from set_table", null);
        res[0] = 21;
        res[1] = 8;
        while (cursor.moveToNext()) {
            res[0] = Integer.parseInt(cursor.getString(cursor.getColumnIndex("start_time")).toString());
            res[1] = Integer.parseInt(cursor.getString(cursor.getColumnIndex("end_time")).toString());
        }
        return res;
    }
    //得到随机短信
    public String selectText() {
        try {
            String sql = "select * from save_text_table";
            ArrayList<String> Array_text = new ArrayList<String>();
            Cursor cursor = db.rawQuery(sql, null);
            int max = 0;
            while (cursor.moveToNext()) {
                String con = cursor.getString(cursor
                        .getColumnIndex("content"));
                if (con.length() > 0) {
                    Array_text.add(cursor.getString(cursor
                            .getColumnIndex("content")));
                    max++;
                }
            }
            Random rand = new Random();
            int i = rand.nextInt(max);
            return Array_text.get(i).toString();
        } catch (Exception e) {
            Toast.makeText(context, "请编辑短信内容"
                    , Toast.LENGTH_SHORT).show();
            return "";
        }

    }
    //返回发送的号码
    public String sendPone(){
        String sending="";
        Cursor cursor = db.rawQuery("select * from send_sms_table  where is_send=0 limit 0,1", null);
        if (cursor!=null){
            while (cursor.moveToNext()){
                sending = cursor.getString(cursor.getColumnIndex("num")).toString();
            }
        }
        return sending;
    }
}
