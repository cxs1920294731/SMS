package com.example.administrator.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import android.os.Handler;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.IntentFilter;
import android.text.format.Time;
//实现功能，把数据存到数据库中
public class MainActivity extends AppCompatActivity {
    EditText numbers, content1, content2, IntervaTime, StartTime, endTime, StartNum, EndNum;
    Button save, send, ip_num, test, result, set_content1, set_content2;
    dataFunction dataSql = new dataFunction();
    SQLiteDatabase db;
    MyData dehelper;
    readContacts read = new readContacts(this);
    saveSet saveset;
    TimerT timeCao;
    //记录需要群发消息的号码列表
    ArrayList<String> sendList = new ArrayList<String>();
    //记录不匹配的号码列表
    ArrayList<String> no_sendList = new ArrayList<String>();
    //总的列表
    ArrayList<String> num_list = new ArrayList<String>();
    //定义定时器
    Timer timer = new Timer();
    Handler myhandler;
    Time t = new Time();
    private static String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
    private static String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //显示要发送的号码
        dehelper = new MyData(this, "mySend.db3", null, 1);
        db = dehelper.getReadableDatabase();
        saveset= new saveSet(this,db);
        timeCao=new TimerT(this,db);
        //发送内容
        content1 = (EditText) findViewById(R.id.content1);
        content2 = (EditText) findViewById(R.id.content2);
        //间隔时间
        IntervaTime = (EditText) findViewById(R.id.IntervaTime);
        //开始和结束时间
        StartTime = (EditText) findViewById(R.id.StartTime);
        endTime = (EditText) findViewById(R.id.EndTime);
        //开始和结束的号码段
        StartNum = (EditText) findViewById(R.id.StartNum);
        EndNum = (EditText) findViewById(R.id.EndNum);
        //按钮
        save = (Button) findViewById(R.id.select);
        send = (Button) findViewById(R.id.send);
        ip_num = (Button) findViewById(R.id.ip_num);
        result = (Button) findViewById(R.id.result);
        test = (Button) findViewById(R.id.sendTest);
        set_content1 = (Button) findViewById(R.id.set_content1);
        set_content2 = (Button) findViewById(R.id.set_content2);
        final sendSMS sendsms=new sendSMS(this,db);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendsms.SendMsgIfSuc("15986816196", "nihao");
                //setContentView(R.layout.sendsms);
                timeCao.startTime();
            }
        });
        //点击保存按钮，保存到数据库中
        set_content1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = content1.getText().toString();
                saveText(1, text);
            }
        });
        set_content2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = content2.getText().toString();
                saveText(2, text);
            }
        });
        displayText();
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> send_list_y = new ArrayList<String>();
                ArrayList<String> send_list_n = new ArrayList<String>();
                final ArrayList<String> send_list = new ArrayList<String>();
                Cursor cursor1 = db.rawQuery("select * from send_result_table where id=1", null);
                try {
                    if (cursor1 != null) {
                        send_list_y.add("发送成功的号码");
                        while (cursor1.moveToNext()) {
                            // 得到手机号码
                            String sending = cursor1.getString(cursor1.getColumnIndex("num")).toString();
                            send_list_y.add(sending);
                        }
                    }
                    Cursor cursor2 = db.rawQuery("select * from send_result_table where id=0", null);
                    if (cursor2 != null) {
                        send_list_n.add("发送成功的号码");
                        while (cursor2.moveToNext()) {
                            // 得到手机号码
                            String sending = cursor2.getString(cursor2.getColumnIndex("num")).toString();
                            send_list_n.add(sending);
                        }
                    }
                    if (send_list_y != null) {
                        send_list.addAll(send_list_y);
                    }
                    if (send_list_n != null) {
                        send_list.addAll(send_list_n);
                    }
                    BaseAdapter adapter = new BaseAdapter() {
                        @Override
                        public int getCount() {
                            return send_list.size();
                        }

                        @Override
                        public Object getItem(int i) {
                            return i;
                        }
                        @Override
                        public long getItemId(int i) {
                            return i;
                        }
                        @Override
                        public View getView(int i, View view, ViewGroup viewGroup) {
                            String number = send_list.get(i);
                            TextView tv = new TextView(MainActivity.this);
                            tv.setText(number);
                            return tv;
                        }
                    };
                    View listView = getLayoutInflater().inflate(R.layout.list, null);
                    final ListView listView1 = (ListView) listView.findViewById(R.id.list1);
                    listView1.setAdapter(adapter);
                    new AlertDialog.Builder(MainActivity.this).setView(listView).setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                } catch (Exception e) {

                    String x;
                    x = "cao";
                }

            }
        });
        ip_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //查找手机上的通讯录
                num_list = read.read();
                BaseAdapter adapter = new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return num_list.size();
                    }
                    @Override
                    public Object getItem(int i) {
                        return i;
                    }
                    @Override
                    public long getItemId(int i) {
                        return i;
                    }

                    @Override
                    public View getView(int i, View view, ViewGroup viewGroup) {
                        String number = num_list.get(i);
                        TextView tv = new TextView(MainActivity.this);
                        tv.setText(number);
                        return tv;
                    }
                };
                View listView = getLayoutInflater().inflate(R.layout.list, null);
                final ListView listView1 = (ListView) listView.findViewById(R.id.list1);
                listView1.setAdapter(adapter);
                new AlertDialog.Builder(MainActivity.this).setView(listView).setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                read.saveNumber(db);
                            }
                        }).show();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer start_time = Integer.parseInt(StartTime.getText().toString());
                Integer end_time = Integer.parseInt(endTime.getText().toString());
                Integer inter_time = Integer.parseInt(IntervaTime.getText().toString());
                saveset.save(start_time, end_time, inter_time);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    final ArrayList<String> send_list = new ArrayList<String>();
                    if (is_empty("send_sms_table")) {

                        //没有发送完的情况
                        final Cursor cursor1 = db.rawQuery("select * from send_sms_table where is_send=0", null);

                        send_list.add("未发送的号码");
                        if (cursor1 != null) {
                            while (cursor1.moveToNext()) {
                                // 得到手机号码
                                String sms = cursor1.getString(cursor1.getColumnIndex("num")).toString();
                                send_list.add(sms);
                            }
                        }
                        BaseAdapter adapter = new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return send_list.size();
                            }

                            @Override
                            public Object getItem(int i) {
                                return i;
                            }

                            @Override
                            public long getItemId(int i) {
                                return i;
                            }

                            @Override
                            public View getView(int i, View view, ViewGroup viewGroup) {
                                String number = send_list.get(i);
                                TextView tv = new TextView(MainActivity.this);
                                tv.setText(number);
                                return tv;
                            }
                        };
                        View listView = getLayoutInflater().inflate(R.layout.list, null);
                        final ListView listView1 = (ListView) listView.findViewById(R.id.list1);
                        listView1.setAdapter(adapter);
                        new AlertDialog.Builder(MainActivity.this).setView(listView).setPositiveButton("继续发送",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        timeCao.startTime();
                                    }
                                }).setNegativeButton("清楚上次记录数据",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        db.execSQL("delete  from send_sms_table");
                                    }
                                }).show();
                    } else {
                        //已经发送完，或者为空
                        String start, end;
                        String mm = StartNum.getText().toString();
                        if (StartNum.getText().toString().length() == 0 || EndNum.getText().toString().length() == 0) {
                            Toast.makeText(MainActivity.this, "请确实是否填写号码段", Toast.LENGTH_LONG).show();
                        } else {
                            start = StartNum.getText().toString();
                            end = EndNum.getText().toString();
                            final Cursor cursor = read.sendList(db, start, end);
                            if (cursor != null) {
                                while (cursor.moveToNext()) {
                                    // 得到手机号码
                                    String sms = cursor.getString(cursor.getColumnIndex("monbile")).toString();
                                    String smsID = cursor.getString(cursor.getColumnIndexOrThrow("id")).toString();
                                    String smsName = cursor.getString(cursor.getColumnIndexOrThrow("name")).toString();
                                    send_list.add(smsID + "--" + smsName + "--" + sms);
                                }
                            }
                            BaseAdapter adapter = new BaseAdapter() {
                                @Override
                                public int getCount() {
                                    return send_list.size();
                                }

                                @Override
                                public Object getItem(int i) {
                                    return i;
                                }

                                @Override
                                public long getItemId(int i) {
                                    return i;
                                }

                                @Override
                                public View getView(int i, View view, ViewGroup viewGroup) {
                                    String number = send_list.get(i);
                                    TextView tv = new TextView(MainActivity.this);
                                    tv.setText(number);
                                    return tv;
                                }
                            };
                            View listView = getLayoutInflater().inflate(R.layout.list, null);
                            final ListView listView1 = (ListView) listView.findViewById(R.id.list1);
                            listView1.setAdapter(adapter);
                            new AlertDialog.Builder(MainActivity.this).setView(listView).setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            for (int y = 0; y < send_list.size(); y++) {
                                                //把合法的号码存入手机中
                                                String[] sms = send_list.get(y).split("--");
                                                String sql = "insert into send_sms_table VALUES (" + sms[2] + ",0)";
                                                db.execSQL(sql);
                                            }
                                            timeCao.startTime();
                                        }
                                    }).show();
                        }
                    }
                } catch (Exception e) {
                    String x;
                    x = "ao";
                }

            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSql.destroyData(db);
    }

    //判断是否为空发送完
    public boolean is_empty(String name) {
        //db.execSQL("delete from send_sms_table");
        Cursor cursor = db.rawQuery("select * from " + name + " where is_send = 0", null);
        String num, xl;
        if (cursor != null) {
            int x = cursor.getCount();
            if (cursor.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    //保存短信内容
    public void saveText(int id, String text) {
        try {
            String sql = "select * from save_text_table where id=" + id;
            String sqlInsert = "insert into save_text_table VALUES (" + id + ",'" + text + "')";
            String sqlUpdate = "update save_text_table set content='" + text + "' where id=" + id;
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    int count = cursor.getInt(0);
                    if (count > 0) {
                        db.execSQL(sqlUpdate);
                    } else {
                        db.execSQL(sqlInsert);
                    }
                } else {
                    db.execSQL(sqlInsert);
                }
                Toast.makeText(MainActivity.this, "保存成功"
                        , Toast.LENGTH_SHORT).show();
            } else {
                db.execSQL(sqlInsert);
            }
        } catch (Exception e) {
            String x;
            x = "ca9";
        }

    }

    public void displayText() {
        String text1 = "";
        String text2 = "";
        int i = 0;
        String in_time = "", start_time = "", end_time = "";
        Cursor cursor = null, cursor1 = null;
        try {
            cursor = db.rawQuery("select * from save_text_table", null);
            cursor1 = db.rawQuery("select * from set_table", null);
        } catch (Exception e) {
            String c = "";
            c = "cao";
        }

        if (cursor1 != null) {
            while (cursor1.moveToNext()) {
                //set start_time="+start_time+",end_time="+end_time+",interva_time="+inter_time+"

                start_time = cursor1.getString(cursor1.getColumnIndexOrThrow("start_time")).toString();
                end_time = cursor1.getString(cursor1.getColumnIndexOrThrow("end_time")).toString();
                in_time = cursor1.getString(cursor1.getColumnIndexOrThrow("interva_time")).toString();
            }
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (i == 0) {
                    text1 = cursor.getString(cursor.getColumnIndexOrThrow("content")).toString();
                }
                if (i == 1) {
                    text2 = cursor.getString(cursor.getColumnIndexOrThrow("content")).toString();
                }
                i++;
            }
        }
        IntervaTime.setText(in_time);
        //开始和结束时间
        StartTime.setText(start_time);
        endTime.setText(end_time);
        content1.setText(text1);
        content2.setText(text2);

        if (i < 2) {
            Toast.makeText(MainActivity.this, "请编辑短信内容"
                    , Toast.LENGTH_SHORT).show();
        }
    }

}

