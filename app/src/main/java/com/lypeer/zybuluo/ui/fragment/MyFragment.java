package com.lypeer.zybuluo.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.OnItemClickListener;
import com.lypeer.zybuluo.impl.OnProgressChangedListener;
import com.lypeer.zybuluo.model.bean.CreateShareLinkResponse;
import com.lypeer.zybuluo.model.bean.Video;
import com.lypeer.zybuluo.presenter.main.MyPresenter;
import com.lypeer.zybuluo.ui.activity.setting.SettingActivity;
import com.lypeer.zybuluo.ui.adapter.MyAdapter;
import com.lypeer.zybuluo.ui.base.BaseFragment;
import com.lypeer.zybuluo.ui.custom.google.GoogleCircleProgressView;
import com.lypeer.zybuluo.utils.FileUtil;
import com.lypeer.zybuluo.utils.meipai.MeiPai;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by lypeer on 2017/1/4.
 */

public class MyFragment extends BaseFragment<MyPresenter> implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.iv_setting)
    ImageView mIvSetting;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.googleProgress)
    GoogleCircleProgressView mGoogleProgress;
    @BindView(R.id.swipe_target)
    RecyclerView mSwipeTarget;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;

    private MyAdapter mAdapter;
    private int mCurrentPage = 1;
    private String mShareType = "";
    private PopupWindow mPopupWindow;

    @Override
    protected MyPresenter createPresenter() {
        return new MyPresenter();
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        getProgressDialog().setCancelable(false);
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
        mAdapter = new MyAdapter();
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


        mAdapter.setOnClickListener(new OnItemClickListener<Video>() {
            @Override
            public void onItemClick(Video itemValue, int viewID, int position) {
                switch (viewID) {
                    case R.id.lly_save:
                        save(itemValue);
                        break;
                    case R.id.lly_share:
                        showPpw(itemValue);
                        break;
                    case R.id.lly_delete:
                        delete(itemValue, position);
                        break;
                }
            }
        });
    }

    private void save(Video itemValue) {
        showProgressBar();
        if (FileUtil.saveToGallery(itemValue.getPath())) {
            hideProgressBar();
            showMessage(R.string.prompt_save_to_gallery_success);
        } else {
            hideProgressBar();
            showMessage(R.string.error_save_fail);
        }
    }

    private void showPpw(Video itemValue) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.ppw_share, null);
        initPpwView(view, itemValue);

        mPopupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mPopupWindow.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
    }

    private void initPpwView(View view, final Video video) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlerClick(view.getId(), video);
                mPopupWindow.dismiss();
                mPopupWindow = null;
            }
        };
        view.findViewById(R.id.lly_comment).setOnClickListener(listener);
        view.findViewById(R.id.lly_wechat).setOnClickListener(listener);
        view.findViewById(R.id.lly_qq).setOnClickListener(listener);
        view.findViewById(R.id.lly_meipai).setOnClickListener(listener);
        view.findViewById(R.id.lly_weibo).setOnClickListener(listener);


        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                mPopupWindow = null;
            }
        });
    }

    private void handlerClick(int id, Video video) {
        if (id == R.id.lly_meipai) {
            MeiPai meiPai = new MeiPai(getActivity());
            meiPai.share(video.getPath());
        } else {
            switch (id) {
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

            share(video);
        }
    }

    private void delete(Video itemValue, int position) {
        //setProgressMessage(App.getAppContext().getString(R.string.prompt_deleting));
        //showProgressBar();
        getPresenter().delete(itemValue, position);
    }

    private void share(Video target) {
        setProgressMessage(App.getAppContext().getString(R.string.prompt_sharing));
        showProgressBar();
        getPresenter().share(target, new OnProgressChangedListener() {
            @Override
            public void onProgressChanged(double currentProgress) {
                setProgressMessage("上传中，当前进度为：" + (int) (currentProgress * 100) + "%");
            }
        });

    }

    @Override
    public void onLoadMore() {
        getPresenter().loadMoreVideos(mCurrentPage);
    }

    @Override
    public void onRefresh() {
        mCurrentPage = 1;
        getPresenter().refreshVideos(mCurrentPage);
    }

    public void refreshVideosSuccess(List<Video> videoList) {
        Collections.reverse(videoList);
        mSwipeToLoadLayout.setRefreshing(false);
        mAdapter.refreshData(videoList);
    }


    public void refreshVideosFail(String errorMessage) {
        mSwipeToLoadLayout.setRefreshing(false);
        showMessage(errorMessage);
    }

    public void loadMoreVideosSuccess(List<Video> videoList) {
        mSwipeToLoadLayout.setLoadingMore(false);

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

    @OnClick(R.id.iv_setting)
    public void onClick() {
        Intent intent = new Intent(getActivity(), SettingActivity.class);
        startActivity(intent);
    }

    public void shareFail(String errorMessage) {
        hideProgressBar();
        showMessage(errorMessage);
    }

    public void shareSuccess(CreateShareLinkResponse shareLinkResponse, String videoUrl) {
        hideProgressBar();
        Log.e("MyFragment", "share success , response data is - > " + shareLinkResponse.toString());

        CreateShareLinkResponse.BodyBean bodyBean = shareLinkResponse.getBody();
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setUrl(bodyBean.getUrl());
        sp.setImageUrl(bodyBean.getThumb_nail());
        sp.setTitleUrl(bodyBean.getUrl());
        sp.setSite(App.getAppContext().getString(R.string.app_name));
        sp.setSiteUrl(bodyBean.getUrl());

        if (mShareType.equals(SinaWeibo.NAME)) {
            sp.setTitle(bodyBean.getWeibo_title() + "\t\t" + "大头秀－分享-" + shareLinkResponse.getBody().getUrl());
            sp.setText(bodyBean.getWeibo_title() + "\t\t" + "大头秀－分享-" + shareLinkResponse.getBody().getUrl());
        } else {
            sp.setTitle(bodyBean.getWeibo_title());
            sp.setText(bodyBean.getWeibo_title());
        }

        if (mShareType.equals(Wechat.NAME) || mShareType.equals(WechatMoments.NAME)) {
            sp.setShareType(Platform.SHARE_WEBPAGE);
        }

        Platform platform = ShareSDK.getPlatform(mShareType);
        platform.share(sp);
    }

    // 应用改版，已经不用这个面板了
    @Deprecated
    private void showSharePanel(final CreateShareLinkResponse response, final String videoUrl) {
        final OnekeyShare oks = new OnekeyShare();

        oks.setTitle(response.getBody().getWeibo_title());
        oks.setText(response.getBody().getWeibo_title());
        oks.setImageUrl(response.getBody().getThumb_nail());
        oks.setUrl(response.getBody().getUrl());
        oks.setTitleUrl(response.getBody().getUrl());
        oks.setSite(App.getAppContext().getString(R.string.app_name));
        oks.setSiteUrl(response.getBody().getUrl());

        Bitmap enableLogo = BitmapFactory.decodeResource(App.getAppContext().getResources(), R.drawable.ic_meipai);
        String label = "美拍";
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                MeiPai meiPai = new MeiPai(getActivity());
                meiPai.share(videoUrl);
            }
        };
        oks.setCustomerLogo(enableLogo, label, listener);

        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                if (platform.getName().equals(SinaWeibo.NAME)) {
                    paramsToShare.setTitle(response.getBody().getWeibo_title() + "\t\t" + "大头秀－分享-" + response.getBody().getUrl());
                    paramsToShare.setText(response.getBody().getWeibo_title() + "\t\t" + "大头秀－分享-" + response.getBody().getUrl());
                }
            }
        });

        oks.show(getActivity());
    }

    public void deleteSuccess(int position) {
        //hideProgressBar();
        mAdapter.removeData(position);
    }

    public void deleteFail(String errorMessage) {
        //hideProgressBar();
        showMessage(errorMessage);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && mSwipeToLoadLayout != null && mSwipeTarget != null && mAdapter != null)
            mSwipeToLoadLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter.getItemCount() > 0) {
                        mSwipeTarget.smoothScrollToPosition(0);
                    }
                    mSwipeToLoadLayout.setRefreshing(true);
                    onRefresh();
                }
            });
    }
}
