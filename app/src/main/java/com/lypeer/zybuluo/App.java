package com.lypeer.zybuluo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsOptions;
import com.growingio.android.sdk.collection.Configuration;
import com.growingio.android.sdk.collection.GrowingIO;
import com.lypeer.zybuluo.utils.Constants;
import com.lypeer.zybuluo.utils.SharePreferencesUtil;
import com.lypeer.zybuluo.utils.meipai.MeiPaiFactory;
import com.zhuge.analysis.stat.ZhugeSDK;

import cn.sharesdk.framework.ShareSDK;
import io.realm.Realm;
import io.realm.RealmConfiguration;


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
                .setChannel("不告诉你"));
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);
        SharePreferencesUtil.init();

        BugtagsOptions options = new BugtagsOptions.Builder().
                trackingCrashLog(true).
                trackingConsoleLog(true).
                trackingUserSteps(true).
                build();

        Bugtags.start("a7899237a27d3215dbdd5f90d4d43e86", this, Bugtags.BTGInvocationEventNone);

        //ZhugeSDK.getInstance().disablePhoneNumber();
        //ZhugeSDK.getInstance().disableAccounts();
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static Resources getRes() {
        return mContext.getResources();
    }
}
