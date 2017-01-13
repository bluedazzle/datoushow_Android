package com.lypeer.zybuluo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.growingio.android.sdk.collection.Configuration;
import com.growingio.android.sdk.collection.GrowingIO;
import com.lypeer.zybuluo.utils.Constants;
import com.lypeer.zybuluo.utils.meipai.MeiPaiFactory;

import cn.sharesdk.framework.ShareSDK;


/**
 * Created by lypeer on 2017/1/4.
 */

public class App extends Application {


    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        ShareSDK.initSDK(this);
        MeiPaiFactory.init(getAppContext(), Constants.ApiSign.V_MEIPAI);
        GrowingIO.startWithConfiguration(this, new Configuration()
                .useID()
                .trackAllFragments()
                .setChannel("XXX应用商店"));
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static Resources getRes() {
        return mContext.getResources();
    }
}
