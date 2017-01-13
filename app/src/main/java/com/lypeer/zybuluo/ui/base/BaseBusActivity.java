package com.lypeer.zybuluo.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;


import com.lypeer.zybuluo.event.EmptyEvent;
import com.lypeer.zybuluo.presenter.base.BasePresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by lypeer on 2016/10/4.
 */
public abstract class BaseBusActivity<P extends BasePresenter> extends BaseActivity<P> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public abstract void onEvent(EmptyEvent event);
}
