package com.example.administrator.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/12/21.
 */

public class TimerT {
    private Time t = new Time();
    private Timer timer = new Timer();
    private Handler myhandler;
    private Context context;
    public SQLiteDatabase db;
    final private int timeunit= 1000;
    private sendSMS send;
    private saveSet redSet;
    public TimerT(final Context context,SQLiteDatabase db){
        this.context=context;
        this.db=db;
        this.send=new sendSMS(context,db);
        this.redSet=new saveSet(context,db);
        this.myhandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    String sending, text;
                    text = redSet.selectText();
                    sending=redSet.sendPone();
                    if (text==""||text==null){
                        Toast.makeText(context, text
                                , Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (sending == "" || sending == null){
                        timer.cancel();
                        return;
                    }
                    Toast.makeText(context, text
                            , Toast.LENGTH_SHORT).show();
                    //send.SendMsgIfSuc(sending, text);
                }
            }
        };
    }

    class mytask extends TimerTask {
        @Override
        public void run() {
            try {
                timer.cancel();
                //分为是否包含24
                Random rand = new Random();
                //random.nextInt(max)%(max-min+1) + min
                int time = redSet.get_inter_time();
                int i = 3, delay;
                if (time < 10) {
                    i = 0;
                }
                int max = time + i;
                int min = time - i;
                delay = rand.nextInt(max) % (i + 1) + min;
                if (isTime()){
                    myhandler.sendEmptyMessage(1);
                    timer = null;

                    timer = new Timer();
                    timeTask = null;
                    timeTask = new mytask();
                    timer.schedule(timeTask, delay * timeunit, 10000000);
                }
            } catch (Exception e) {
                String x = "cap";
            }
        }
    }

    private mytask timeTask=new mytask();
    public boolean isTime(){
        int[] a = new int[2];
        a = redSet.get_start_end_time();
        t.setToNow();
        Integer x = t.hour;
        if (a[0] > a[1]) {
            //包24小时
            if ((a[0] < x && a[1] > 24) || (x < a[1] && x >= 0)) {
                //timer.cancel();
                return true;
            }
        } else {
            //不包24
            if (a[0] < x && a[1] > x) {
                return true;
            }
        }
        return false;
    }
    //开始计时
    public void startTime(){
        timer.schedule(timeTask, 0, redSet.get_inter_time() * timeunit);
    }
}
