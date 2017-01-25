package com.lypeer.zybuluo.ui.fragment.viewpager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.OnItemClickListener;
import com.lypeer.zybuluo.mixture.activity.MainActivity;
import com.lypeer.zybuluo.mixture.core.MixtureKeys;
import com.lypeer.zybuluo.model.bean.VideoResponse;
import com.lypeer.zybuluo.presenter.viewpager.SearchPresenter;
import com.lypeer.zybuluo.ui.adapter.SearchAdapter;
import com.lypeer.zybuluo.ui.base.BaseFragment;
import com.lypeer.zybuluo.utils.ApiSignUtil;
import com.zhuge.analysis.stat.ZhugeSDK;

import butterknife.BindView;

/**
 * Created by lypeer on 2017/1/4.
 */

public class SearchFragment extends BaseFragment<SearchPresenter> {
    @BindView(R.id.fsv_search)
    FloatingSearchView mFsvSearch;
    @BindView(R.id.rv_result)
    RecyclerView mRvResult;

    private SearchAdapter mSearchAdapter;

    @Override
    protected SearchPresenter createPresenter() {
        return new SearchPresenter();
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_search;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        initSearchBar();
        initList();
    }

    private void initList() {
        mSearchAdapter = new SearchAdapter();
        mRvResult.setItemAnimator(new DefaultItemAnimator());
        mRvResult.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvResult.setAdapter(mSearchAdapter);

        mSearchAdapter.setOnClickListener(new OnItemClickListener<VideoResponse.BodyBean.VideoListBean>() {
            @Override
            public void onItemClick(VideoResponse.BodyBean.VideoListBean itemValue, int viewID, int position) {
                switch (viewID) {
                    case R.id.lly_container:
                        ZhugeSDK.getInstance().track(App.getAppContext(), "素材被点击");
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

    private void initSearchBar() {
        mFsvSearch.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                showProgressBar();
                getPresenter().search(currentQuery);
            }
        });
    }

    public void searchFail(String errorMessage) {
        hideProgressBar();
        showMessage(errorMessage);
    }

    public void searchSuccess(VideoResponse videoResponse) {
        hideProgressBar();
        if (videoResponse.getBody().getVideo_list().size() == 0) {
            showMessage(R.string.prompt_no_result);
        }
        mSearchAdapter.refreshData(videoResponse.getBody().getVideo_list());
    }
}
