package com.lypeer.zybuluo.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.presenter.base.BasePresenter;


/**
 * Created by lypeer on 16/8/18.
 */
public abstract class BaseActivity<P extends BasePresenter> extends BaseCustomActivity {

    private P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = createPresenter();
        mPresenter.attachView(this);
        super.onCreate(savedInstanceState);
        if (mPresenter == null) {
            throw new IllegalArgumentException(getString(R.string.error_presenter_must_be_inited));
        }
    }

    protected P getPresenter() {
        return mPresenter;
    }

    protected abstract P createPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
