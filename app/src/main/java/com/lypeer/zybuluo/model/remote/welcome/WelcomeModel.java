package com.lypeer.zybuluo.model.remote.welcome;

import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.ApiService;
import com.lypeer.zybuluo.model.base.BaseModel;
import com.lypeer.zybuluo.model.bean.ClassificationsBean;
import com.lypeer.zybuluo.presenter.welcome.WelcomePresenter;
import com.lypeer.zybuluo.utils.Constants;
import com.lypeer.zybuluo.utils.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lypeer on 2017/1/21.
 */

public class WelcomeModel extends BaseModel<WelcomePresenter> {

    public WelcomeModel(WelcomePresenter welcomePresenter) {
        super(welcomePresenter);
    }

    @Override
    protected WelcomePresenter createPresenter() {
        return new WelcomePresenter();
    }

    public void requestClassifications() {
        RetrofitClient.buildService(ApiService.class)
                .getClassifications()
                .enqueue(new Callback<ClassificationsBean>() {
                    @Override
                    public void onResponse(Call<ClassificationsBean> call, Response<ClassificationsBean> response) {
                        if (response == null || response.body() == null) {
                            getPresenter().requestFail(App.getAppContext().getString(R.string.error_network));
                            return;
                        }

                        ClassificationsBean classificationsBean = response.body();
                        if (classificationsBean.getStatus() == Constants.StatusCode.STATUS_SUCCESS) {
                            getPresenter().requestSuccess(classificationsBean);
                        } else {
                            getPresenter().requestFail(App.getRes().getStringArray(R.array.status_error)[classificationsBean.getStatus()]);
                        }
                    }

                    @Override
                    public void onFailure(Call<ClassificationsBean> call, Throwable t) {
                        getPresenter().requestFail(t.getMessage());
                    }
                });
    }
}
