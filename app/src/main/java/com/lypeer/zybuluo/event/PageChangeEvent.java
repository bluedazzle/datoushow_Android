package com.lypeer.zybuluo.event;

/**
 * Created by lypeer on 2017/1/18.
 */

public class PageChangeEvent extends EmptyEvent{

    public PageChangeEvent(int currentFragment) {
        mCurrentFragment = currentFragment;
    }

    public int getCurrentFragment() {
        return mCurrentFragment;
    }

    public void setCurrentFragment(int currentFragment) {
        mCurrentFragment = currentFragment;
    }

    private int mCurrentFragment;
}
