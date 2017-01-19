package com.lypeer.zybuluo.ui.activity.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.ui.base.BaseCustomActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lypeer on 2017/1/16.
 */

public class TermsActivity extends BaseCustomActivity {
    @BindView(R.id.wv_terms)
    WebView mWvTerms;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mWvTerms.loadUrl("file:///android_asset/terms.html");
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_terms;
    }
}
