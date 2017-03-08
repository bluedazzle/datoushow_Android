package com.lypeer.zybuluo.ui.activity.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.OnProgressChangedListener;
import com.lypeer.zybuluo.mixture.view.CircleProgressView;
import com.lypeer.zybuluo.model.bean.CreateShareLinkResponse;
import com.lypeer.zybuluo.presenter.share.SharePresenter;
import com.lypeer.zybuluo.ui.base.BaseActivity;
import com.lypeer.zybuluo.ui.custom.RatioLayout;
import com.lypeer.zybuluo.utils.ZhugeUtil;
import com.lypeer.zybuluo.utils.meipai.MeiPai;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by lypeer on 2017/1/17.
 */

public class ShareActivity extends BaseActivity<SharePresenter> {
    public static final String SHARE_KEY_PATH = "path";
    public static final String SHARE_KEY_ID = "id";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.imageView2)
    ImageView mImageView2;
    @BindView(R.id.lly_meipai)
    RatioLayout mLlyMeipai;
    @BindView(R.id.imageView)
    ImageView mImageView;
    @BindView(R.id.lly_weibo)
    RatioLayout mLlyWeibo;
    @BindView(R.id.lly_wechat)
    RatioLayout mLlyWechat;
    @BindView(R.id.lly_comment)
    RatioLayout mLlyComment;
    @BindView(R.id.lly_qq)
    RatioLayout mLlyQq;
    @BindView(R.id.btn_back)
    Button mBtnBack;
    @BindView(R.id.cp_progress)
    CircleProgressView mCpProgress;

    private String mPath = "";
    private int mId = -1;
    private String mShareType = "";

    @Override
    protected SharePresenter createPresenter() {
        return new SharePresenter();
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mCpProgress.setRoundProgressColor(App.getRes().getColor(R.color.colorDark));
        mCpProgress.setRoundColor(App.getRes().getColor(R.color.colorGray));

        Intent intent = getIntent();

        try {
            mPath = intent.getStringExtra(SHARE_KEY_PATH);
            mId = intent.getIntExtra(SHARE_KEY_ID, -1);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(R.string.error_data_wrong);
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_share;
    }

    @OnClick({R.id.lly_meipai, R.id.lly_weibo, R.id.lly_wechat, R.id.lly_comment, R.id.lly_qq})
    public void onClick(View view) {
        if (TextUtils.isEmpty(mPath) || mId == -1) {
            showMessage(R.string.error_data_wrong);
            return;
        }

        switch (view.getId()) {
            case R.id.lly_meipai:
                mShareType = App.getAppContext().getString(R.string.meipai);
                break;
            case R.id.lly_weibo:
                mShareType = SinaWeibo.NAME;
                break;
            case R.id.lly_wechat:
                mShareType = Wechat.NAME;
                break;
            case R.id.lly_comment:
                mShareType = WechatMoments.NAME;
                break;
            case R.id.lly_qq:
                mShareType = QQ.NAME;
                break;
        }

        ZhugeUtil.upload("不同渠道总分享量");
        ZhugeUtil.upload("我的主页总分享总量");
        ZhugeUtil.upload("我的主页单个享渠道分享总量", "渠道名", mShareType);
        ZhugeUtil.upload("单个素材分享总量", "id", mId + "");
        ZhugeUtil.upload("单个渠道分享总量", "渠道名", mShareType);

        share();
    }

    private void share() {
/*        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(App.getAppContext().getString(R.string.prompt_saving));*/
        mCpProgress.setVisibility(View.VISIBLE);
        mCpProgress.bringToFront();
        mCpProgress.setProgress(0, "视频已上传 0%");
        mCpProgress.setText("");
        getPresenter().share(mPath, mId, new OnProgressChangedListener() {
            @Override
            public void onProgressChanged(double currentProgress) {
                mCpProgress.setProgress((int) (currentProgress * 100), "视频已上传 " + (int) (currentProgress * 100) + "%");
            }
        });
    }

    @OnClick(R.id.btn_back)
    public void onClick() {
        onBackPressed();
    }

    public void shareFail(String errorMessage) {
        mCpProgress.setVisibility(View.GONE);
        showMessage(errorMessage);
    }

    public void shareSuccess(CreateShareLinkResponse response, String path) {

        if (mShareType.equals(App.getAppContext().getString(R.string.meipai))) {
            MeiPai meiPai = new MeiPai(this);
            meiPai.share(path);
        } else {
            CreateShareLinkResponse.BodyBean bodyBean = response.getBody();
            Platform.ShareParams sp = new Platform.ShareParams();
            sp.setUrl(bodyBean.getUrl());
            sp.setImageUrl(bodyBean.getThumb_nail());
            sp.setTitleUrl(bodyBean.getUrl());
            sp.setSite(App.getAppContext().getString(R.string.app_name));
            sp.setSiteUrl(bodyBean.getUrl());

            if (mShareType.equals(SinaWeibo.NAME)) {
                sp.setTitle(bodyBean.getWeibo_title() + "\t\t" + "大头秀－分享-" + response.getBody().getUrl());
                sp.setText(bodyBean.getWeibo_title() + "\t\t" + "大头秀－分享-" + response.getBody().getUrl());
            } else {
//                sp.setTitle(bodyBean.getWeibo_title());
//                sp.setText(bodyBean.getWeibo_title());
                if(bodyBean.getWechat_title().equals("")){
                    sp.setTitle(bodyBean.getWechat_sub_title());
                    sp.setText(bodyBean.getWechat_sub_title());
                }
                else {
                    sp.setTitle(bodyBean.getWechat_title());
                    sp.setText(bodyBean.getWechat_sub_title());
                }
            }

            if (mShareType.equals(Wechat.NAME) || mShareType.equals(WechatMoments.NAME)) {
                sp.setShareType(Platform.SHARE_WEBPAGE);
            }

            Platform platform = ShareSDK.getPlatform(mShareType);
            platform.share(sp);
        }

        mCpProgress.setVisibility(View.GONE);
    }
}
