package com.lypeer.zybuluo.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.OnItemClickListener;
import com.lypeer.zybuluo.impl.OnProgressChangedListener;
import com.lypeer.zybuluo.model.bean.CreateShareLinkResponse;
import com.lypeer.zybuluo.model.bean.Video;
import com.lypeer.zybuluo.model.bean.VideoResponse;
import com.lypeer.zybuluo.presenter.main.MyPresenter;
import com.lypeer.zybuluo.ui.activity.setting.SettingActivity;
import com.lypeer.zybuluo.ui.adapter.FunnyAdapter;
import com.lypeer.zybuluo.ui.adapter.MyAdapter;
import com.lypeer.zybuluo.ui.base.BaseFragment;
import com.lypeer.zybuluo.ui.custom.google.GoogleCircleProgressView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.UmengTool;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

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
    private SHARE_MEDIA mCurrentShareMedia;

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

        UmengTool.checkWx(getActivity());
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
                        break;
                    case R.id.lly_share:
                        share(itemValue);
                        break;
                    case R.id.lly_delete:
                        break;
                }
            }
        });
    }

    private void share(Video target) {
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

    public void shareSuccess(CreateShareLinkResponse shareLinkResponse) {
        hideProgressBar();
        Log.e("MyFragment", "share success , response data is - > " + shareLinkResponse.toString());
        showSharePanel(shareLinkResponse);
    }

    private void showSharePanel(CreateShareLinkResponse response) {
        UMImage umImage = new UMImage(getActivity(), response.getBody().getThumb_nail());
        umImage.setTitle(response.getBody().getWechat_title());

        new ShareAction(getActivity())
                .withTargetUrl(response.getBody().getUrl())
                .withMedia(umImage)
                .withText(response.getBody().getWechat_sub_title())
                .withTitle(response.getBody().getWechat_title())
                .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        Log.e("MyFragment", "ShareAction open onResult");
                        mCurrentShareMedia = share_media;
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        Log.e("MyFragment", "ShareAction open onError");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        Log.e("MyFragment", "ShareAction open onCancel");
                    }
                }).open();
    }
}
