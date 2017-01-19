package com.lypeer.zybuluo.utils;

import android.util.DisplayMetrics;
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
}
