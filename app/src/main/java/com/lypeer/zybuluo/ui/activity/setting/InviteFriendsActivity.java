package com.lypeer.zybuluo.ui.activity.setting;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.model.bean.CreateShareLinkResponse;
import com.lypeer.zybuluo.ui.base.BaseCustomActivity;
import com.lypeer.zybuluo.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

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
        Platform.ShareParams sp = new Platform.ShareParams();

        sp.setUrl(Constants.InviteData.URL);
        sp.setImageUrl(Constants.InviteData.URL);
        sp.setTitleUrl(Constants.InviteData.URL);
        sp.setSiteUrl(Constants.InviteData.URL);

        sp.setSite(App.getAppContext().getString(R.string.app_name));
        sp.setTitle(Constants.InviteData.TITLE);
        sp.setText(Constants.InviteData.TEXT);

        String shareType = "";
        switch (view.getId()) {
            case R.id.rl_wechat:
                shareType = Wechat.NAME;
                break;
            case R.id.rl_wechat_comment:
                shareType = WechatMoments.NAME;
                break;
            case R.id.rl_qq:
                shareType = QQ.NAME;
                break;
        }

        if (TextUtils.isEmpty(shareType)) {
            showMessage(R.string.error_data_wrong);
            return;
        }
        if (shareType.equals(Wechat.NAME) || shareType.equals(WechatMoments.NAME)) {
            sp.setShareType(Platform.SHARE_WEBPAGE);
        }

        Platform platform = ShareSDK.getPlatform(shareType);
        platform.share(sp);
    }

    @OnClick(R.id.rl_copy_link)
    public void onCopyLinkClick() {
        ClipboardManager cmb = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(Constants.InviteData.COPY_LINK);
        showMessage(R.string.prompt_copy_success);
    }
}
