package com.lypeer.zybuluo.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by lypeer on 1/18/2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    /**
     * 每一个选项页的标题的list
     */
    private List<String> titleList;

    /**
     * 每一个选项卡的fragment的list
     */
    private List<Fragment> fragmentList;

    public ViewPagerAdapter(FragmentManager fm , List<String> titleList , List<Fragment> fragmentList) {
        super(fm);
        this.titleList = titleList;
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

}
