package com.lypeer.zybuluo.ui.activity.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.ui.base.BaseCustomActivity;

import butterknife.BindView;

/**
 * Created by lypeer on 2017/1/16.
 */

public class TermsActivity extends BaseCustomActivity {
    @BindView(R.id.wv_terms)
    WebView mWvTerms;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        mWvTerms.loadUrl("file:///android_asset/terms.html");
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_terms;
    }
}
