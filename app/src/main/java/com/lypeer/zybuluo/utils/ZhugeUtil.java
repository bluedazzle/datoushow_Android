package com.lypeer.zybuluo.utils;

import com.lypeer.zybuluo.App;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lypeer on 2017/1/24.
 */

public class ZhugeUtil {

    public static void upload(String title, String... args) {
        if (args.length % 2 != 0) {
            return;
        }

        JSONObject object = new JSONObject();

        try {
            for (int i = 0; i < args.length; ) {
                object.put(args[i], args[i + 1]);
                i += 2;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (object.length() == 0) {
            ZhugeSDK.getInstance().track(App.getAppContext(), title);
        } else {
            ZhugeSDK.getInstance().track(App.getAppContext(), title, object);
        }
    }
}
