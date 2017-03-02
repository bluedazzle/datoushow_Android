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

import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.event.BannerEvent;
import com.lypeer.zybuluo.event.EmptyEvent;
import com.lypeer.zybuluo.model.bean.ViewPagerDb;
import com.lypeer.zybuluo.presenter.main.AddPresenter;
import com.lypeer.zybuluo.ui.adapter.ViewPagerAdapter;
import com.lypeer.zybuluo.ui.base.BaseBusFragment;
import com.lypeer.zybuluo.utils.DataFormatter;
import com.lypeer.zybuluo.utils.ZhugeUtil;
import com.squareup.picasso.Picasso;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

/**
 * Created by lypeer on 2017/1/4.
 */

public class AddFragment extends BaseBusFragment<AddPresenter> {
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
        ViewPagerDb.init();
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
        textView.setTextColor(getResources().getColor(R.color.colorWhite));

        if (targetStatus == STATUS_NORMAL) {
            textView.setTextSize(DataFormatter.dipToPixels(4));

            Object iconNormal = ViewPagerDb.getIconsNormal().get(tab.getPosition());
            if (iconNormal instanceof Integer) {
                imageView.setImageDrawable(App.getRes().getDrawable((Integer) iconNormal));
            } else if (iconNormal instanceof String) {
                Picasso.with(App.getAppContext()).load((String) iconNormal).placeholder(imageView.getDrawable()).fit().centerInside().into(imageView);
            }
        } else if (targetStatus == STATUS_SELECTED) {
            textView.setTextSize(DataFormatter.dipToPixels(5));

            Object iconSelected = ViewPagerDb.getIconsSelected().get(tab.getPosition());
            if (iconSelected instanceof Integer) {
                imageView.setImageDrawable(App.getRes().getDrawable((Integer) iconSelected));
            } else if (iconSelected instanceof String) {

                Picasso.with(App.getAppContext()).load((String) iconSelected).placeholder(imageView.getDrawable()).fit().centerInside().into(imageView);
            }
        }
    }

    private void initViewPager() {
        mVpMain.setAdapter(new ViewPagerAdapter(getActivity().getSupportFragmentManager(), ViewPagerDb.getTitles(), ViewPagerDb.getFragments()));
        mTlMain.setupWithViewPager(mVpMain);
        mTlMain.setTabMode(TabLayout.MODE_SCROLLABLE);
        setupTabIcons();

        mVpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    ZhugeUtil.upload("单个素材分类被点击量", "分类名", "热门");
                } else if (position == ViewPagerDb.getTitles().size() + 1) {
                    ZhugeUtil.upload("单个素材分类被点击量", "分类名", "搜索");
                } else {
                    ZhugeUtil.upload("单个素材分类被点击量", "button名", ViewPagerDb.getTitles().get(position - 1));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
        textView.setTextColor(getResources().getColor(R.color.colorWhite));

        if (index == 0) {
            textView.setTextSize(DataFormatter.dipToPixels(5));

            Object iconSelected = ViewPagerDb.getIconsSelected().get(index);
            if (iconSelected instanceof Integer) {
                imageView.setImageDrawable(App.getRes().getDrawable((Integer) iconSelected));
            } else if (iconSelected instanceof String) {
                Picasso.with(App.getAppContext()).load((String) iconSelected).placeholder(imageView.getDrawable()).fit().centerInside().into(imageView);
            }
        } else {
            textView.setTextSize(DataFormatter.dipToPixels(4));

            Object iconNormal = ViewPagerDb.getIconsNormal().get(index);
            if (iconNormal instanceof Integer) {
                imageView.setImageDrawable(App.getRes().getDrawable((Integer) iconNormal));
            } else if (iconNormal instanceof String) {
                Picasso.with(App.getAppContext()).load((String) iconNormal).placeholder(imageView.getDrawable()).fit().centerInside().into(imageView);
            }
        }

        return view;
    }

    @Subscribe
    @Override
    public void onEvent(EmptyEvent event) {
        if (event == null) {
            return;
        }
        if (event instanceof BannerEvent) {
            BannerEvent bannerEvent = (BannerEvent) event;
            if (bannerEvent.getNav() == 0) {
                mVpMain.setCurrentItem(1);
            }
        }
    }
}
