package com.lypeer.zybuluo.ui.activity.setting;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
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
    @BindView(R.id.tv_terms)
    TextView mTvTerms;
    @BindView(R.id.btn_back)
    Button mBtnBack;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
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


    @OnClick(R.id.btn_back)
    public void onBackClick() {
        onBackPressed();
    }

    @OnClick(R.id.tv_terms)
    public void onTermsClick() {
        //@todo 点击事件
    }
}
