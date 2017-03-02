package com.lypeer.zybuluo.ui.activity.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.event.PageChangeEvent;
import com.lypeer.zybuluo.model.bean.GuideVPDb;
import com.lypeer.zybuluo.ui.adapter.ViewPagerAdapter;
import com.lypeer.zybuluo.ui.base.BaseCustomActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

/**
 * Created by lypeer on 2017/1/18.
 */

public class UseGuideActivity extends BaseCustomActivity implements ViewPager.OnPageChangeListener {

    private int mPrePosition = 1;

    @BindView(R.id.vp_guide)
    ViewPager mVpGuide;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), GuideVPDb.getTitles(), GuideVPDb.getFragments());
        mVpGuide.setAdapter(adapter);

        FragmentManager manager = getSupportFragmentManager();
        mVpGuide.addOnPageChangeListener(this);
        mVpGuide.setOffscreenPageLimit(3);
        mVpGuide.postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new PageChangeEvent(0));
            }
        }, 1000);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_use_guide;
    }

    @Override
    protected void onDestroy() {
        //mVpGuide.removeOnPageChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mPrePosition != 1 && position == 0) {
            return;
        }
        mPrePosition = position;
        EventBus.getDefault().post(new PageChangeEvent(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
