package com.lypeer.zybuluo.ui.fragment.viewpager;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.event.BannerEvent;
import com.lypeer.zybuluo.event.EmptyEvent;
import com.lypeer.zybuluo.impl.OnItemClickListener;
import com.lypeer.zybuluo.mixture.activity.MainActivity;
import com.lypeer.zybuluo.mixture.core.MixtureKeys;
import com.lypeer.zybuluo.model.bean.BannerResponse;
import com.lypeer.zybuluo.model.bean.VideoResponse;
import com.lypeer.zybuluo.presenter.viewpager.HotPresenter;
import com.lypeer.zybuluo.ui.adapter.HotAdapter;
import com.lypeer.zybuluo.ui.adapter.viewholder.HotBannerVH;
import com.lypeer.zybuluo.ui.base.BaseBusFragment;
import com.lypeer.zybuluo.ui.custom.google.GoogleCircleProgressView;
import com.lypeer.zybuluo.utils.ApiSignUtil;
import com.lypeer.zybuluo.utils.Constants;
import com.lypeer.zybuluo.utils.ZhugeUtil;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by lypeer on 2017/1/4.
 */

public class HotFragment extends BaseBusFragment<HotPresenter> implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.googleProgress)
    GoogleCircleProgressView mGoogleProgress;
    @BindView(R.id.swipe_target)
    RecyclerView mSwipeTarget;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;

    private HotAdapter mAdapter;
    private int mCurrentPage = 1;
    private PopupWindow mPopupWindow;

    @Override
    protected HotPresenter createPresenter() {
        return new HotPresenter();
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_hot;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        initList();
        mSwipeToLoadLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeToLoadLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    private void initList() {
        mAdapter = new HotAdapter();
        mAdapter.hasHeader(true);
        mAdapter.setHeaderVH(new HotBannerVH(getActivity(), mAdapter.getParent()));

        mSwipeTarget.setItemAnimator(new DefaultItemAnimator());
        mSwipeTarget.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSwipeTarget.setAdapter(mAdapter);
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mSwipeToLoadLayout.setOnLoadMoreListener(this);

        mSwipeTarget.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!ViewCompat.canScrollVertically(recyclerView, 1)) {
                        mSwipeToLoadLayout.setLoadingMore(true);
                    }
                }
            }
        });

        mAdapter.setOnClickListener(new OnItemClickListener<VideoResponse.BodyBean.VideoListBean>() {
            @Override
            public void onItemClick(VideoResponse.BodyBean.VideoListBean itemValue, int viewID, int position) {
                switch (viewID) {
                    case R.id.lly_container:
                        ZhugeUtil.upload("列表中素材被点击", "素材名称", itemValue.getTitle(), "素材ID", itemValue.getId() + "");
                        gotoMakeVideo(itemValue);
                        break;
                }
            }
        });
    }

    private void gotoMakeVideo(VideoResponse.BodyBean.VideoListBean target) {
        String timeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
        String sign = ApiSignUtil.getSign(timeStamp);

        String dataUrl = "http://datoushow.com/api/v1/video/" + target.getId() + "?&timestamp=" + timeStamp + "&sign=" + sign;

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra(MixtureKeys.KEY_VIDEO_PATH, target.getUrl());
        intent.putExtra(MixtureKeys.KEY_DATA_PATH, dataUrl);
        intent.putExtra(MixtureKeys.KEY_VIDEO, target);
        startActivity(intent);
    }

    @Override
    public void onLoadMore() {
        getPresenter().loadMoreVideos(mCurrentPage);
        ZhugeSDK.getInstance().track(App.getAppContext(), "上滑刷新总量");

    }

    @Override
    public void onRefresh() {
        mCurrentPage = 1;
        getPresenter().refreshVideos(mCurrentPage);
        getPresenter().refreshBanner();
    }

    public void refreshVideosSuccess(VideoResponse body) {
        mSwipeToLoadLayout.setRefreshing(false);
        mAdapter.refreshData(body.getBody().getVideo_list());
    }


    public void refreshVideosFail(String errorMessage) {
        mSwipeToLoadLayout.setRefreshing(false);
        showMessage(errorMessage);
    }

    public void loadMoreVideosSuccess(VideoResponse videoResponse) {
        mSwipeToLoadLayout.setLoadingMore(false);

        List<VideoResponse.BodyBean.VideoListBean> videoList = videoResponse.getBody().getVideo_list();
        if (videoList == null || videoList.size() == 0) {
            showMessage(R.string.prompt_no_more);
        } else {
            mCurrentPage++;
            mAdapter.addData(videoList);
        }
    }

    public void loadMoreVideosFail(String errorMessage) {
        mSwipeToLoadLayout.setLoadingMore(false);
        showMessage(errorMessage);
    }

    public void refreshBannerFail(String errorMessage) {
        showMessage(errorMessage);
    }

    public void refreshBannerSuccess(BannerResponse bannerResponse) {
        mAdapter.setHeaderValue(bannerResponse);
    }

    @Subscribe
    @Override
    public void onEvent(EmptyEvent event) {
        if (event == null) {
            return;
        }
        if (event instanceof BannerEvent) {
            BannerEvent bannerEvent = (BannerEvent) event;
            if (bannerEvent.getNav() == 1) {
                showPpw();
            }
        }
    }

    private void showPpw() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.ppw_invite_friends, null);
        initPpwView(view);

        mPopupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mPopupWindow.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
    }

    private void initPpwView(View view) {
        ImageView ivLogo = (ImageView) view.findViewById(R.id.iv_logo);
        ivLogo.bringToFront();
        ivLogo.requestLayout();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlerClick(view.getId());
                mPopupWindow.dismiss();
                mPopupWindow = null;
            }
        };
        view.findViewById(R.id.lly_wechat_comment).setOnClickListener(listener);
        view.findViewById(R.id.lly_wechat).setOnClickListener(listener);
        view.findViewById(R.id.lly_qq).setOnClickListener(listener);
        view.findViewById(R.id.lly_copy_link).setOnClickListener(listener);


        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZhugeSDK.getInstance().track(App.getAppContext(), "邀请好友弹窗关闭");
                mPopupWindow.dismiss();
                mPopupWindow = null;
            }
        });
    }

    private void handlerClick(int id) {
        if (id == R.id.lly_copy_link) {
            onCopyLinkClick();
        } else {
            onInviteClick(id);
        }
    }

    private void onInviteClick(int id) {

        Platform.ShareParams sp = new Platform.ShareParams();

        sp.setUrl(Constants.InviteData.URL);
        sp.setImageUrl(Constants.InviteData.LOGO_URL);
        sp.setTitleUrl(Constants.InviteData.URL);
        sp.setSiteUrl(Constants.InviteData.URL);

        sp.setSite(App.getAppContext().getString(R.string.app_name));
        sp.setTitle(Constants.InviteData.TITLE);
        sp.setText(Constants.InviteData.TEXT);

        String shareType = "";
        switch (id) {
            case R.id.lly_wechat:
                shareType = Wechat.NAME;
                break;
            case R.id.lly_wechat_comment:
                shareType = WechatMoments.NAME;
                break;
            case R.id.lly_qq:
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

        ZhugeUtil.upload("各邀请渠道点击总量");
        ZhugeUtil.upload("邀请好友弹框中单个渠道点击量" , "渠道名" , shareType);
    }

    public void onCopyLinkClick() {
        ZhugeUtil.upload("邀请好友弹框中单个渠道点击量" , "渠道名" , "复制链接");

        ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(Constants.InviteData.COPY_LINK);
        showMessage(R.string.prompt_copy_success);
    }
}
