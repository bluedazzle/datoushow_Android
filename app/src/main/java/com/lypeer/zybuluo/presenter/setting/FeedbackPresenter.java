package com.lypeer.zybuluo.presenter.setting;

import com.lypeer.zybuluo.model.remote.setting.FeedbackModel;
import com.lypeer.zybuluo.presenter.base.BasePresenter;
import com.lypeer.zybuluo.ui.activity.setting.FeedbackActivity;

/**
 * Created by lypeer on 2017/1/18.
 */

public class FeedbackPresenter extends BasePresenter<FeedbackActivity , FeedbackModel> {
    @Override
    protected FeedbackModel createModel() {
        return new FeedbackModel(this);
    }

    public void commit(String content, String contactWay) {
        if(!isViewAttached()){
            return;
        }
        getModel().commit(content , contactWay);
    }

    public void commitSuccess() {
        if(!isViewAttached()){
            return;
        }
        getView().commitSuccess();
    }
}
