package com.lypeer.zybuluo.utils;

import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.ApiService;
import com.lypeer.zybuluo.impl.OnCheckUpdateInfoListener;
import com.lypeer.zybuluo.model.bean.UpdateInfoBean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lypeer on 2017/1/20.
 */

public class UpdateUtil {

    public static void checkUpdateInfo(final OnCheckUpdateInfoListener listener) {
        RetrofitClient.buildService(ApiService.class)
                .update()
                .enqueue(new Callback<UpdateInfoBean>() {
                    @Override
                    public void onResponse(Call<UpdateInfoBean> call, Response<UpdateInfoBean> response) {
                        if (response == null || response.body() == null) {
                            listener.fail(App.getAppContext().getString(R.string.error_some_problem));
                            return;
                        }

                        UpdateInfoBean updateInfoBean = response.body();
                        if (updateInfoBean.getStatus() == Constants.StatusCode.STATUS_SUCCESS) {
                            listener.success(hasUpdate(updateInfoBean.getBody().getUpdate().getAndroid_verison()), updateInfoBean);
                        }
                    }

                    @Override
                    public void onFailure(Call<UpdateInfoBean> call, Throwable t) {
                        listener.fail(App.getAppContext().getString(R.string.error_network));
                    }
                });
    }

    private static boolean hasUpdate(String nowVersion) {
        String currentVersion = ActivityController.getVersion();

        try {
            String[] currents = currentVersion.split("\\.");
            String[] nows = nowVersion.split("\\.");

            for (int i = 0; i < Math.min(nows.length, currents.length); i++) {
                if (Integer.valueOf(nows[i]) > Integer.valueOf(currents[i])) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
