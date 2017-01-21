package com.lypeer.zybuluo.model.bean;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.ui.fragment.viewpager.BuriedFragment;
import com.lypeer.zybuluo.ui.fragment.viewpager.FilmTvFragment;
import com.lypeer.zybuluo.ui.fragment.viewpager.FunnyFragment;
import com.lypeer.zybuluo.ui.fragment.viewpager.HotFragment;
import com.lypeer.zybuluo.ui.fragment.viewpager.MvFragment;
import com.lypeer.zybuluo.ui.fragment.viewpager.SearchFragment;
import com.lypeer.zybuluo.ui.fragment.viewpager.SpringFestivalFragment;
import com.lypeer.zybuluo.ui.fragment.viewpager.VarietyFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lypeer on 2017/1/4.
 */

public class ViewPagerDb {
    private static final ArrayList<String> titles = new ArrayList<>();
    private static final ArrayList<Object> iconsNormal = new ArrayList<>();
    private static final ArrayList<Object> iconsSelected = new ArrayList<>();
    private static final ArrayList<Fragment> fragments = new ArrayList<>();


    public static ArrayList<String> getTitles() {
        if (titles.size() != 0) {
            return titles;
        }

        titles.add("热门");
        titles.add("春节");
        titles.add("搞笑");
        titles.add("MV");
        titles.add("影视");
        titles.add("综艺");
        titles.add("搜索");
        return titles;
    }

    public static ArrayList<Object> getIconsNormal() {
        if (iconsNormal.size() != 0) {
            return iconsNormal;
        }

        iconsNormal.add(R.drawable.ic_hot_normal);
        iconsNormal.add(R.drawable.ic_spring_festival_normal);
        iconsNormal.add(R.drawable.ic_funny_normal);
        iconsNormal.add(R.drawable.ic_mv_normal);
        iconsNormal.add(R.drawable.ic_film_tv_normal);
        iconsNormal.add(R.drawable.ic_variety_normal);
        iconsNormal.add(R.drawable.ic_search_normal);
        return iconsNormal;
    }

    public static ArrayList<Object> getIconsSelected() {
        if (iconsSelected.size() != 0) {
            return iconsSelected;
        }

        iconsSelected.add(R.drawable.ic_hot_selected);
        iconsSelected.add(R.drawable.ic_spring_festival_selected);
        iconsSelected.add(R.drawable.ic_funnny_selected);
        iconsSelected.add(R.drawable.ic_mv_selected);
        iconsSelected.add(R.drawable.ic_film_tv_selected);
        iconsSelected.add(R.drawable.ic_variety_selected);
        iconsSelected.add(R.drawable.ic_search_normal);
        return iconsSelected;
    }

    public static ArrayList<Fragment> getFragments() {
        if (fragments.size() != 0) {
            return fragments;
        }

        fragments.add(new HotFragment());
        fragments.add(new SpringFestivalFragment());
        fragments.add(new FunnyFragment());
        fragments.add(new MvFragment());
        fragments.add(new FilmTvFragment());
        fragments.add(new VarietyFragment());
        fragments.add(new SearchFragment());
        return fragments;
    }

    public static void init(ClassificationsBean classificationsBean) {
        if (classificationsBean == null) {
            return;
        }

        List<ClassificationsBean.BodyBean.ClassificationListBean> listBean = classificationsBean.getBody().getClassification_list();

        Collections.sort(listBean, new Comparator<ClassificationsBean.BodyBean.ClassificationListBean>() {
            @Override
            public int compare(ClassificationsBean.BodyBean.ClassificationListBean l, ClassificationsBean.BodyBean.ClassificationListBean r) {
                return l.getIndex() - r.getIndex();
            }
        });

        titles.add("热门");
        iconsNormal.add(R.drawable.ic_hot_normal);
        iconsSelected.add(R.drawable.ic_hot_selected);
        fragments.add(new HotFragment());

        for (ClassificationsBean.BodyBean.ClassificationListBean bean : listBean) {
            titles.add(bean.getName());
            iconsNormal.add(bean.getIcon());
            iconsSelected.add(bean.getSelect_icon());

            Fragment fragment = new BuriedFragment();

            Bundle bundle = new Bundle();
            bundle.putInt(BuriedFragment.KEY_TYPE, bean.getType());

            fragment.setArguments(bundle);
            fragments.add(fragment);
        }

        titles.add("搜索");
        iconsNormal.add(R.drawable.ic_search_normal);
        iconsSelected.add(R.drawable.ic_search_normal);
        fragments.add(new SearchFragment());
    }
}
