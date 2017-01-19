package com.lypeer.zybuluo.ui.activity.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.ui.activity.MainActivity;
import com.lypeer.zybuluo.ui.activity.setting.UseGuideActivity;
import com.lypeer.zybuluo.ui.base.BaseCustomActivity;
import com.lypeer.zybuluo.utils.SharePreferencesUtil;

import butterknife.BindView;

/**
 * Created by lypeer on 2017/1/18.
 */

public class WelcomeActivity extends BaseCustomActivity {
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
        Intent intent = new Intent();
        if (SharePreferencesUtil.isFirstLaunch()) {
            intent.setClass(this, UseGuideActivity.class);
        } else {
            intent.setClass(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_welcome;
    }
}
