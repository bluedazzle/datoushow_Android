package com.lypeer.zybuluo.utils;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.lypeer.zybuluo.App;

/**
 * Created by lypeer on 2017/1/18.
 */

public class DataFormatter {

    public static float dipToPixels(float dipValue) {
        DisplayMetrics metrics = App.getRes().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static long string2Millisecond(double time){
        return string2Millisecond(String.valueOf(time));
    }

    public static long string2Millisecond(String time) {
        try {
            String origin = time.trim();
            long millisecond = 0;

            String tailString = origin.split("\\.")[1];
            if(tailString.length() > 3){
                tailString = tailString.substring(0 , 3);
            }else if(tailString.length() < 3){
                time = time.concat("0");
                return string2Millisecond(time);
            }

            long tail = Long.valueOf(tailString);
            String head = origin.split("\\.")[0];

            String[] timeArray = head.split(":");
            for (int i = 0; i < timeArray.length; i++) {
                millisecond += Math.pow(60, i) * Long.valueOf(timeArray[timeArray.length - i - 1]) * 1000;
            }
            return millisecond + tail;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
