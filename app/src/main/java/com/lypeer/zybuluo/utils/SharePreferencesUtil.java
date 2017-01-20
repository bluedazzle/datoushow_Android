package com.lypeer.zybuluo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lypeer.zybuluo.App;

/**
 * Created by lypeer on 2017/1/18.
 */

public class SharePreferencesUtil {

    private static SharedPreferences sSharedPreferences = null;
    private static final String KEY_IS_FIRST_LAUNCH = "isFirstLaunch";
    private static final String KEY_IS_USER_LIKE_SUBTITLE = "isUserLikeSubtitle";

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

    public static void setIsUserLikeSubtitle(boolean isUserLikeSubtitle) {
        sSharedPreferences.edit().putBoolean(KEY_IS_USER_LIKE_SUBTITLE, isUserLikeSubtitle).apply();
    }

    public static boolean isUserLikeSubtitle() {
        if (!sSharedPreferences.contains(KEY_IS_USER_LIKE_SUBTITLE)){
            return true;
        }else {
            return sSharedPreferences.getBoolean(KEY_IS_USER_LIKE_SUBTITLE , true);
        }
    }
}
