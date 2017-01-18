package com.lypeer.zybuluo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lypeer.zybuluo.App;

/**
 * Created by lypeer on 2017/1/18.
 */

public class SharePreferencesUtil {

    private static SharedPreferences sSharedPreferences = null;
    private static String KEY_IS_FIRST_LAUNCH = "isFirstLaunch";

    public static void init() {
        if (sSharedPreferences == null) {
            sSharedPreferences = App.getAppContext().getSharedPreferences("DatouShow_SharePreferences", Context.MODE_PRIVATE);
        }
    }

    public static boolean isFirstLaunch() {
        return !sSharedPreferences.contains(KEY_IS_FIRST_LAUNCH) && sSharedPreferences.getBoolean(KEY_IS_FIRST_LAUNCH, true);
    }

    public static void launched() {
        sSharedPreferences.edit().putBoolean(KEY_IS_FIRST_LAUNCH, false).apply();
    }
}
