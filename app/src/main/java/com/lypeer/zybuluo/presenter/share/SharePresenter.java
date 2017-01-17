package com.lypeer.zybuluo.presenter.share;

import com.lypeer.zybuluo.impl.OnProgressChangedListener;
import com.lypeer.zybuluo.model.bean.CreateShareLinkResponse;
import com.lypeer.zybuluo.model.remote.share.ShareModel;
import com.lypeer.zybuluo.presenter.base.BasePresenter;
import com.lypeer.zybuluo.ui.activity.share.ShareActivity;

/**
 * Created by lypeer on 2017/1/17.
 */

public class SharePresenter extends BasePresenter<ShareActivity , ShareModel> {
    @Override
    protected ShareModel createModel() {
        return new ShareModel(this);
    }

    public void share(String path, int id , OnProgressChangedListener listener) {
        if(!isViewAttached()){
            return;
        }
        getModel().share(path , id , listener);
    }

    public void shareFail(String errorMessage) {
        if(!isViewAttached()){
            return;
        }
        getView().shareFail(errorMessage);
    }

    public void shareSuccess(CreateShareLinkResponse response, String path) {
        if(!isViewAttached()){
            return;
        }
        getView().shareSuccess(response , path);
    }
}
