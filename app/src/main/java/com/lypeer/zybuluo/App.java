package com.lypeer.zybuluo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.lypeer.zybuluo.utils.Constants;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * Created by lypeer on 2017/1/4.
 */

public class App extends Application {

    {
        PlatformConfig.setWeixin(Constants.ApiSign.K_WECHAT, Constants.ApiSign.V_WECHAT);
        PlatformConfig.setSinaWeibo(Constants.ApiSign.K_WEIBO, Constants.ApiSign.V_WEIBO);
        PlatformConfig.setQQZone(Constants.ApiSign.K_QQ, Constants.ApiSign.V_QQ);
    }

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        Config.isUmengSina = true;
        UMShareAPI.get(this);
        Config.isJumptoAppStore = true;
        Config.DEBUG = true;
        Config.REDIRECT_URL = "http://datoushow.com";
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static Resources getRes() {
        return mContext.getResources();
    }
}
