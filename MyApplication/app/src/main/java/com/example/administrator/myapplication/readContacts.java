package com.example.administrator.myapplication;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/12/20.
 */

public class readContacts {
    private Context context;
    private Cursor cursor_num;
    public readContacts(Context context){
        this.context=context;
    }
    public ArrayList<String> read(){
        ArrayList<String> res=new ArrayList<String>();
        ArrayList<String> sendList=new ArrayList<String>();
        ArrayList<String> no_sendList=new ArrayList<String>();
        cursor_num=context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, android.provider.ContactsContract.Contacts.SORT_KEY_PRIMARY);
        int i=0;
        if (cursor_num != null) {
            while (cursor_num.moveToNext()) {
                i++;
                // 得到手机号码
                String phoneNumber = cursor_num.getString(cursor_num
                        .getColumnIndex(ContactsContract
                                .CommonDataKinds.Phone.NUMBER))
                        .replace("-", "")
                        .replace(" ", "")
                        .replace("+", "");
                String PhoneID = cursor_num.getString(cursor_num.getColumnIndex(android.provider.ContactsContract.Contacts._ID));
                String phoneName = cursor_num.getString(cursor_num.getColumnIndex(android.provider.ContactsContract.Contacts.DISPLAY_NAME));
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (is_cell_num(phoneNumber)){
                    sendList.add(i+"--"+phoneName+"--"+phoneNumber);
                }else {
                    no_sendList.add(i+"--"+phoneName+"--"+phoneNumber);
                }
            }
            res.add("不符合条件的号码");
            res.addAll(no_sendList);
            res.add("符合条件的号码");
            res.addAll(sendList);
        }
        return res;
    }
    public Boolean saveNumber(SQLiteDatabase db){
        try{
            db.execSQL("delete from save_monbile_table");
            if (cursor_num!=null){
                cursor_num.moveToFirst();
                while (cursor_num.moveToNext()){
                    String phoneNumber = cursor_num.getString(cursor_num
                            .getColumnIndex(ContactsContract
                                    .CommonDataKinds.Phone.NUMBER))
                            .replace("-", "")
                            .replace(" ", "")
                            .replace("+", "");
                    String PhoneID = cursor_num.getString(cursor_num.getColumnIndex(android.provider.ContactsContract.Contacts._ID));
                    String phoneName = cursor_num.getString(cursor_num.getColumnIndex(android.provider.ContactsContract.Contacts.DISPLAY_NAME));
                    // 当手机号码为空的或者为空字段 跳过当前循环
                    if (is_cell_num(phoneNumber)){
                        //insert into set_table VALUES (1,"+start_time+","+end_time+","+inter_time+")"
                        db.execSQL("insert into save_monbile_table (monbile,name) values ('"+phoneNumber+"','"+phoneName+"')");
                        //sendList.add(i+"--"+phoneName+"--"+phoneNumber);
                    }else {
                        //no_sendList.add(i+"--"+phoneName+"--"+phoneNumber);
                    }
                }
            }
            Toast.makeText(context, "保存成功"
                    , Toast.LENGTH_SHORT).show();
            return true;
        }catch (Exception e){
            Toast.makeText(context, "保存失败"
                    , Toast.LENGTH_SHORT).show();
            return true;
        }
    }
    public Cursor sendList(SQLiteDatabase db,String start,String end){
        Cursor cursor=null;
        int startID=0,endID=0;
        Cursor cursor1=db.rawQuery("select * from save_monbile_table where name='"+start+"'",null);
        startID=getFirst(cursor1);
        Cursor cursor2=db.rawQuery("select * from save_monbile_table where name='"+end+"'",null);
        endID=getFirst(cursor2);
        cursor=db.rawQuery("select * from save_monbile_table where id>="+startID+" and id<="+endID,null);
        return cursor;
    }
    public Boolean is_cell_num(String mobiles){
        Pattern p = Pattern.compile("^(0|86|17951)?(13[0-9]|15[0-9]|17[0-9]|18[0-9]|14[0-9])[0-9]{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
    //得到最后一个数据
    private int getFirst(Cursor cursor){
        int res=-1;
        String st="";
        if (cursor.getCount()>0){
            while (cursor.moveToNext()){
                res=cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            }
            //res=Integer.parseInt(st);
        }
        return res;
    }
}
