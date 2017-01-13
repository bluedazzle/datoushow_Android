package com.lypeer.zybuluo.presenter.base;


import com.lypeer.zybuluo.model.base.BaseModel;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by lypeer on 16/8/30.
 */
public abstract class BasePresenter<V, M extends BaseModel> {

    protected Reference<V> mViewRef;
    private M mModel;

    public void attachView(V view) {
        mViewRef = new WeakReference<>(view);
    }

    protected M getModel(){
        if(mModel == null){
            mModel = createModel();
        }
        return mModel;
    }

    protected abstract M createModel();

    protected V getView() {
        if (isViewAttached())
            return mViewRef.get();
        else return null;
    }

    public boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }

    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }
}

