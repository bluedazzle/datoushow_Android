package com.lypeer.zybuluo.model.bean;

import android.support.v4.app.Fragment;

import com.lypeer.zybuluo.ui.fragment.guide.Guide1Fragment;
import com.lypeer.zybuluo.ui.fragment.guide.Guide2Fragment;
import com.lypeer.zybuluo.ui.fragment.guide.Guide3Fragment;
import com.lypeer.zybuluo.ui.fragment.guide.Guide4Fragment;

import java.util.ArrayList;

/**
 * Created by lypeer on 2017/1/18.
 */

public class GuideVPDb{
    private static final ArrayList<String> titles = new ArrayList<>();

    public static ArrayList<String> getTitles() {
        if (titles.size() != 0) {
            return titles;
        }

        titles.add("guide1");
        titles.add("guide2");
        titles.add("guide3");
        titles.add("guide4");
        return titles;
    }

    public static ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new Guide1Fragment());
        fragments.add(new Guide2Fragment());
        fragments.add(new Guide3Fragment());
        fragments.add(new Guide4Fragment());
        return fragments;
    }
}
