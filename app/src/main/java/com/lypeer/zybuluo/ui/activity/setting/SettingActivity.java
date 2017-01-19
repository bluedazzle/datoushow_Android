package com.lypeer.zybuluo.ui.activity.setting;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.ui.base.BaseCustomActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lypeer on 2017/1/5.
 */

public class SettingActivity extends BaseCustomActivity {
    @BindView(R.id.tv_version)
    TextView mTvVersion;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rl_use_guide)
    RelativeLayout mRlUseGuide;
    @BindView(R.id.rl_invite_friends)
    RelativeLayout mRlInviteFriends;
    @BindView(R.id.rl_feedback)
    RelativeLayout mRlFeedback;
    @BindView(R.id.rl_service_terms)
    RelativeLayout mRlServiceTerms;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        try {
            mTvVersion.setText(getVersionName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得版本号的方法
     *
     * @return 当前应用的版本号
     * @throws Exception
     */
    private String getVersionName() throws Exception {
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        return packInfo.versionName;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_setting;
    }

    @OnClick(R.id.rl_service_terms)
    public void onTermsClick() {
        Intent intent = new Intent(this, TermsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rl_use_guide)
    public void onUseGuideClick() {
        Intent intent = new Intent(this, UseGuideActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rl_feedback)
    public void onFeedbackClick() {
        Intent intent = new Intent(this, FeedbackActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rl_invite_friends)
    public void onInviteFriendClick() {
        Intent intent = new Intent(this, InviteFriendsActivity.class);
        startActivity(intent);
    }
}
