package com.lypeer.zybuluo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.model.bean.ViewPagerDb;
import com.lypeer.zybuluo.presenter.main.AddPresenter;
import com.lypeer.zybuluo.ui.adapter.ViewPagerAdapter;
import com.lypeer.zybuluo.ui.base.BaseFragment;

import butterknife.BindView;

/**
 * Created by lypeer on 2017/1/4.
 */

public class AddFragment extends BaseFragment<AddPresenter> {
    @BindView(R.id.tl_main)
    TabLayout mTlMain;
    @BindView(R.id.vp_main)
    ViewPager mVpMain;

    private static final int STATUS_NORMAL = 1024;
    private static final int STATUS_SELECTED = 1025;

    @Override
    protected AddPresenter createPresenter() {
        return new AddPresenter();
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_add;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        initViewPager();
        initToolbar();
    }

    private void initToolbar() {
        mTlMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeTabStatus(tab, STATUS_SELECTED);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                changeTabStatus(tab, STATUS_NORMAL);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void changeTabStatus(TabLayout.Tab tab, int targetStatus) {
        View view = tab.getCustomView();
        assert view != null;
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);
        TextView textView = (TextView) view.findViewById(R.id.tv_name);

        if (targetStatus == STATUS_NORMAL) {
            textView.setTextColor(getResources().getColor(R.color.colorGray));
            imageView.setImageDrawable(getActivity().getResources().getDrawable(ViewPagerDb.getIconsNormal().get(tab.getPosition())));
        } else if (targetStatus == STATUS_SELECTED) {
            textView.setTextColor(getResources().getColor(R.color.colorRed));
            imageView.setImageDrawable(getActivity().getResources().getDrawable(ViewPagerDb.getIconsSelected().get(tab.getPosition())));
        }
    }

    private void initViewPager() {
        mVpMain.setAdapter(new ViewPagerAdapter(getActivity().getSupportFragmentManager(), ViewPagerDb.getTitles(), ViewPagerDb.getFragments()));
        mTlMain.setupWithViewPager(mVpMain);
        mTlMain.setTabMode(TabLayout.MODE_SCROLLABLE);
        setupTabIcons();
    }

    private void setupTabIcons() {
        for (int i = 0; i < ViewPagerDb.getTitles().size(); i++) {
            mTlMain.getTabAt(i).setCustomView(getTabView(i));
        }
    }

    @NonNull
    private View getTabView(int index) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_tab, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_name);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);

        textView.setText(ViewPagerDb.getTitles().get(index));

        if (index == 0) {
            textView.setTextColor(getResources().getColor(R.color.colorRed));
            imageView.setImageDrawable(getActivity().getResources().getDrawable(ViewPagerDb.getIconsSelected().get(index)));
        } else {
            textView.setTextColor(getResources().getColor(R.color.colorGray));
            imageView.setImageDrawable(getActivity().getResources().getDrawable(ViewPagerDb.getIconsNormal().get(index)));
        }

        return view;
    }
}
