package com.lypeer.zybuluo.ui.activity.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.model.bean.ClassificationsBean;
import com.lypeer.zybuluo.model.bean.ViewPagerDb;
import com.lypeer.zybuluo.presenter.base.BasePresenter;
import com.lypeer.zybuluo.presenter.welcome.WelcomePresenter;
import com.lypeer.zybuluo.ui.activity.MainActivity;
import com.lypeer.zybuluo.ui.activity.setting.UseGuideActivity;
import com.lypeer.zybuluo.ui.base.BaseActivity;
import com.lypeer.zybuluo.ui.base.BaseCustomActivity;
import com.lypeer.zybuluo.utils.SharePreferencesUtil;

import butterknife.BindView;

/**
 * Created by lypeer on 2017/1/18.
 */

public class WelcomeActivity extends BaseActivity<WelcomePresenter> {
    @BindView(R.id.iv_welcome)
    ImageView mIvWelcome;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {

        mIvWelcome.postDelayed(new Runnable() {
            @Override
            public void run() {
                judge();
            }
        }, 2000);
    }

    private void judge() {
        if (SharePreferencesUtil.isFirstLaunch()) {
            Intent intent = new Intent();
            intent.setClass(this, UseGuideActivity.class);
            startActivity(intent);
            finish();
        } else {
            getPresenter().requestClassifications();
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected WelcomePresenter createPresenter() {
        return new WelcomePresenter();
    }

    public void requestFail(String errorMessage) {
        gotoMainActivity();
    }

    public void requestSuccess(ClassificationsBean classificationsBean) {
        ViewPagerDb.init(classificationsBean);
        gotoMainActivity();
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
