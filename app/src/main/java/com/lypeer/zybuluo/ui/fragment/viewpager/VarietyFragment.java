package com.lypeer.zybuluo.ui.fragment.viewpager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.OnItemClickListener;
import com.lypeer.zybuluo.mixture.activity.MainActivity;
import com.lypeer.zybuluo.mixture.core.MixtureKeys;
import com.lypeer.zybuluo.model.bean.VideoResponse;
import com.lypeer.zybuluo.presenter.viewpager.VarietyPresenter;
import com.lypeer.zybuluo.ui.adapter.VarietyAdapter;
import com.lypeer.zybuluo.ui.base.BaseFragment;
import com.lypeer.zybuluo.ui.custom.google.GoogleCircleProgressView;
import com.lypeer.zybuluo.utils.ApiSignUtil;

import java.util.List;

import butterknife.BindView;

/**
 * Created by lypeer on 2017/1/4.
 */

public class VarietyFragment extends BaseFragment<VarietyPresenter> implements OnRefreshListener, OnLoadMoreListener {
    @BindView(R.id.googleProgress)
    GoogleCircleProgressView mGoogleProgress;
    @BindView(R.id.swipe_target)
    RecyclerView mSwipeTarget;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout mSwipeToLoadLayout;

    private VarietyAdapter mAdapter;
    private int mCurrentPage = 1;

    @Override
    protected VarietyPresenter createPresenter() {
        return new VarietyPresenter();
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_variety;
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
        mAdapter = new VarietyAdapter();
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
        intent.putExtra(MixtureKeys.KEY_VIDEO , target);
        startActivity(intent);
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
}
