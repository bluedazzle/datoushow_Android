package com.lypeer.zybuluo.presenter.welcome;

import com.lypeer.zybuluo.model.bean.ClassificationsBean;
import com.lypeer.zybuluo.model.remote.welcome.WelcomeModel;
import com.lypeer.zybuluo.presenter.base.BasePresenter;
import com.lypeer.zybuluo.ui.activity.welcome.WelcomeActivity;

/**
 * Created by lypeer on 2017/1/21.
 */

public class WelcomePresenter extends BasePresenter<WelcomeActivity , WelcomeModel> {
    @Override
    protected WelcomeModel createModel() {
        return new WelcomeModel(this);
    }

    public void requestClassifications() {
        if(!isViewAttached()){
            return;
        }
        getModel().requestClassifications();
    }

    public void requestFail(String erroeMessage) {
        if(!isViewAttached()){
            return;
        }
        getView().requestFail(erroeMessage);
    }

    public void requestSuccess(ClassificationsBean classificationsBean) {
        if(!isViewAttached()){
            return;
        }
        getView().requestSuccess(classificationsBean);
    }
}
