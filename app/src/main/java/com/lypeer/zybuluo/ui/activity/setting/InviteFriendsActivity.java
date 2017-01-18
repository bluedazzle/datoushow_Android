package com.lypeer.zybuluo.ui.activity.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.ui.base.BaseCustomActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lypeer on 2017/1/18.
 */

public class InviteFriendsActivity extends BaseCustomActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.iv_wechat)
    ImageView mIvWechat;
    @BindView(R.id.rl_wechat)
    RelativeLayout mRlWechat;
    @BindView(R.id.iv_wechat_comment)
    ImageView mIvWechatComment;
    @BindView(R.id.rl_wechat_comment)
    RelativeLayout mRlWechatComment;
    @BindView(R.id.iv_qq)
    ImageView mIvQq;
    @BindView(R.id.rl_qq)
    RelativeLayout mRlQq;
    @BindView(R.id.iv_copy_link)
    ImageView mIvCopyLink;
    @BindView(R.id.rl_copy_link)
    RelativeLayout mRlCopyLink;

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
        return R.layout.activity_invite_friends;
    }

    @OnClick({R.id.rl_wechat, R.id.rl_wechat_comment, R.id.rl_qq})
    public void onInviteClick(View view) {
        switch (view.getId()) {
            case R.id.rl_wechat:
                break;
            case R.id.rl_wechat_comment:
                break;
            case R.id.rl_qq:
                break;
        }
    }

    @OnClick(R.id.rl_copy_link)
    public void onCopyLinkClick() {

    }
}
