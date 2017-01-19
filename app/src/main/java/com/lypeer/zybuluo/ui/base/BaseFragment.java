package com.lypeer.zybuluo.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.lypeer.zybuluo.presenter.base.BasePresenter;

/**
 * Created by lypeer on 16/8/17.
 */
public abstract class BaseFragment<P extends BasePresenter> extends BaseCustomFragment {

    private P mPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPresenter = createPresenter();
        mPresenter.attachView(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected abstract P createPresenter();


    protected P getPresenter() {
        return mPresenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
