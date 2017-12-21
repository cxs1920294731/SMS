package com.example.administrator.myapplication;

import android.content.Context;
import android.os.Handler;
import android.text.format.Time;

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
    public TimerT(Context context){
        this.context=context;
    }
    class mytask extends TimerTask {
        @Override
        public void run() {
            try {
                timer.cancel();
                int[] a = new int[2];
                a = redSet.get_start_end_time();
                t.setToNow();
                Integer x = t.hour;
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
                if (a[0] > a[1]) {
                    //包24小时
                    if ((a[0] < x && a[1] > 24) || (x < a[1] && x >= 0)) {
                        //timer.cancel();
                        myhandler.sendEmptyMessage(1);
                        timer = null;
                        timeTask.cancel();
                        timer = new Timer();
                        timeTask = null;
                        timeTask = new mytask();
                        timer.schedule(timeTask, delay * 1000 * 60, 10000000);
                    }
                } else {
                    //不包24
                    if (a[0] < x && a[1] > x) {
                        myhandler.sendEmptyMessage(1);
                        //timer.cancel();
                        timer = null;
                        timeTask.cancel();
                        timer = new Timer();
                        timeTask = null;
                        timeTask = new mytask();
                        timer.schedule(timeTask, delay * 1000 * 60, 10000000);
                    }
                }
            } catch (Exception e) {
                String x = "cap";
            }
        }
    }
    private saveSet redSet=new saveSet(context);
    private mytask timeTask=new mytask();
    public boolean isTime(){
        
        return true;
    }

}
