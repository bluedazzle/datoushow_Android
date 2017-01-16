package com.lypeer.zybuluo.presenter.viewpager;

import com.lypeer.zybuluo.model.bean.VideoResponse;
import com.lypeer.zybuluo.model.remote.viewpager.SearchModel;
import com.lypeer.zybuluo.presenter.base.BasePresenter;
import com.lypeer.zybuluo.ui.fragment.viewpager.SearchFragment;

/**
 * Created by lypeer on 2017/1/4.
 */

public class SearchPresenter extends BasePresenter<SearchFragment , SearchModel> {
    @Override
    protected SearchModel createModel() {
        return new SearchModel(this);
    }

    public void search(String currentQuery) {
        if(!isViewAttached()){
            return;
        }
        getModel().search(currentQuery);
    }

    public void searchFail(String errorMessage) {
        if(!isViewAttached()){
            return;
        }
        getView().searchFail(errorMessage);
    }

    public void searchSuccess(VideoResponse videoResponse) {
        if(!isViewAttached()){
            return;
        }
        getView().searchSuccess(videoResponse);
    }
}
