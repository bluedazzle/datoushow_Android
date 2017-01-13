package com.lypeer.zybuluo.utils.meipai;

import android.content.Context;

import com.meitu.meipaimv.sdk.openapi.IMeipaiAPI;
import com.meitu.meipaimv.sdk.openapi.MeipaiAPIFactory;

/**
 * Created by lypeer on 2017/1/13.
 */

public class MeiPaiFactory {

    private static IMeipaiAPI mMeipaiAPI;

    private MeiPaiFactory() {
    }

    public static void init(Context context, String clientId) {
        synchronized (new Object()) {
            if (mMeipaiAPI != null) {
                return;
            }
            mMeipaiAPI = MeipaiAPIFactory.createMeipaiApi(context, clientId, true);
        }
    }

    public static IMeipaiAPI getInstance() {
        return mMeipaiAPI;
    }
}
