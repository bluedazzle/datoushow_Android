package com.lypeer.zybuluo.model.base;


import com.lypeer.zybuluo.presenter.base.BasePresenter;

/**
 * Created by lypeer on 16/8/30.
 */
public abstract class BaseModel<P extends BasePresenter> {

    private P mPresenter = null;

    public BaseModel(P p){
        mPresenter = p;
    }

    protected abstract P createPresenter();

    protected P getPresenter() {
        synchronized (this) {
            if (mPresenter == null) {
                mPresenter = createPresenter();
            }
        }
        return mPresenter;
    }
}
