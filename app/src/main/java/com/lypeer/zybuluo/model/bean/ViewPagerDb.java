package com.lypeer.zybuluo.model.bean;

import android.support.v4.app.Fragment;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.ui.fragment.viewpager.FilmTvFragment;
import com.lypeer.zybuluo.ui.fragment.viewpager.FunnyFragment;
import com.lypeer.zybuluo.ui.fragment.viewpager.HotFragment;
import com.lypeer.zybuluo.ui.fragment.viewpager.MvFragment;
import com.lypeer.zybuluo.ui.fragment.viewpager.SearchFragment;
import com.lypeer.zybuluo.ui.fragment.viewpager.VarietyFragment;

import java.util.ArrayList;

/**
 * Created by lypeer on 2017/1/4.
 */

public class ViewPagerDb {
    private static final ArrayList<String> titles = new ArrayList<>();
    private static final ArrayList<Integer> iconsNormal = new ArrayList<>();
    private static final ArrayList<Integer> iconsSelected = new ArrayList<>();


    public static ArrayList<String> getTitles() {
        if (titles.size() != 0) {
            return titles;
        }

        titles.add("热门");
        titles.add("MV");
        titles.add("影视");
        titles.add("综艺");
        titles.add("搞笑");
        titles.add("搜索");
        return titles;
    }

    public static ArrayList<Integer> getIconsNormal() {
        if (iconsNormal.size() != 0) {
            return iconsNormal;
        }

        iconsNormal.add(R.drawable.ic_hot_normal);
        iconsNormal.add(R.drawable.ic_mv_normal);
        iconsNormal.add(R.drawable.ic_film_tv_normal);
        iconsNormal.add(R.drawable.ic_variety_normal);
        iconsNormal.add(R.drawable.ic_funny_normal);
        iconsNormal.add(R.drawable.ic_search_normal);
        return iconsNormal;
    }

    public static ArrayList<Integer> getIconsSelected() {
        if (iconsSelected.size() != 0) {
            return iconsSelected;
        }

        ArrayList<Integer> iconsSelected = new ArrayList<>();
        iconsSelected.add(R.drawable.ic_hot_selected);
        iconsSelected.add(R.drawable.ic_mv_selected);
        iconsSelected.add(R.drawable.ic_film_tv_selected);
        iconsSelected.add(R.drawable.ic_variety_selected);
        iconsSelected.add(R.drawable.ic_funnny_selected);
        iconsSelected.add(R.drawable.ic_search_normal);
        return iconsSelected;
    }

    public static ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new HotFragment());
        fragments.add(new MvFragment());
        fragments.add(new FilmTvFragment());
        fragments.add(new VarietyFragment());
        fragments.add(new FunnyFragment());
        fragments.add(new SearchFragment());
        return fragments;
    }
}
