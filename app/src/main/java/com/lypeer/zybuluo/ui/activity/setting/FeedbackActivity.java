package com.lypeer.zybuluo.ui.activity.setting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.presenter.setting.FeedbackPresenter;
import com.lypeer.zybuluo.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by lypeer on 2017/1/18.
 */

public class FeedbackActivity extends BaseActivity<FeedbackPresenter> {
    @BindView(R.id.tv_commit)
    TextView mTvCommit;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_content)
    EditText mEtContent;
    @BindView(R.id.et_contact_way)
    EditText mEtContactWay;

    @Override
    protected FeedbackPresenter createPresenter() {
        return new FeedbackPresenter();
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_feedback;
    }

    @OnClick(R.id.tv_commit)
    public void onCommitClick() {
        String content = mEtContent.getText().toString().trim();
        String contactWay = mEtContactWay.getText().toString().trim();

        if (TextUtils.isEmpty(contactWay) || TextUtils.isEmpty(content)) {
            showMessage(R.string.error_input_complete);
            return;
        }
        getPresenter().commit(content, contactWay);
    }

    public void commitSuccess() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_prompt)
                .setMessage(R.string.message_send_success)
                .setPositiveButton(R.string.prompt_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                })
                .show();
    }
}
