package com.lypeer.zybuluo.event;

/**
 * Created by lypeer on 2017/1/18.
 */

public class BannerEvent extends EmptyEvent {

    private int mNav;

    public int getNav() {
        return mNav;
    }

    public void setNav(int nav) {
        mNav = nav;
    }

    public BannerEvent(int nav) {

        mNav = nav;
    }
}
